package fi.stardex.sisu.persistence;

import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;

@PropertySource("classpath:properties/app.properties")
public class CheckAndInitializeBD {

    private Logger logger = LoggerFactory.getLogger(CheckAndInitializeBD.class);
    private ManufacturerRepository manufacturerRepository;
    private DataSource dataSource;

    @Value("${stardex.version.directory}")
    private String versionDirectory;

    @Value("${stardex.version.file.name}")
    private String versionFileName;

    @Value("${stardex.version}")
    private String version;

    public CheckAndInitializeBD(ManufacturerRepository manufacturerRepository, DataSource dataSource) {
        this.manufacturerRepository = manufacturerRepository;
        this.dataSource = dataSource;
    }


    @PostConstruct
    private void check() {

        try {
            if (isCurrentVersion()) {
                updateDB();
            } else {
                if (manufacturerRepository.count() == 0) {
                    fillTables();
                } else {
                    logger.info("Tables filled out");
                }
            }
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                logger.error("Version file not found!", e);
                throw new RuntimeException(e);
            }
        }

//        if (manufacturerRepository.count() == 0) {
//            fillTables();
//        } else {
//            System.err.println("Tables filled out");
//        }

    }

    private boolean isCurrentVersion() throws IOException {

        boolean isCurrent;

        File fileDir = new File(System.getProperty("user.home") + File.separator + versionDirectory);

        fileDir.mkdirs();

        File file = new File(fileDir, versionFileName);

        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (!line.equals(version)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write("");
                        writer.write(version);
                    }
                    isCurrent = false;
                } else
                    isCurrent = true;
            }
        } else {
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("");
                writer.write(version);
            }
            isCurrent = false;
        }

        return isCurrent;
    }

    private void updateDB() {
        dropTables();
        fillTables();
    }

    private void fillTables() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/db/scripts/fill_tables.sql")));
            RunScript.execute(dataSource.getConnection(), reader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        String filePath = getClass().getResource("/db/csv/manufacturers.csv").getPath();
//        sessionFactory.openSession().createQuery("INSERT INTO MANUFACTURER(ID, MANUFACTURER, DISPLAY_ORDER) SELECT manufacturer FROM CSVREADER('"+
//               filePath +"')");


    }

    private void dropTables() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/db/scripts/drop_tables.sql")));
            RunScript.execute(dataSource.getConnection(), reader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
