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
import java.util.ArrayList;
import java.util.List;

@PropertySource("classpath:properties/app.properties")
public class CheckAndInitializeBD {

    private Logger logger = LoggerFactory.getLogger(CheckAndInitializeBD.class);
    private ManufacturerRepository manufacturerRepository;
    private DataSource dataSource;

    private static final String COMMA_DELIMITER = ",";

    private static final String NEW_LINE_SEPARATOR = "\n";

    @Value("${stardex.custom_csvs.directory}")
    private String customCSVSDirectory;

    @Value("${stardex.directory}")
    private String directory;

    @Value("${stardex.version.file.name}")
    private String versionFileName;

    @Value("${stardex.version}")
    private String version;

    @Value("${stardex.custom_csvs.manufacturers}")
    private String customManufacturers;

    @Value("${stardex.custom_csvs.injectorTypes}")
    private String customInjectorTypes;

    @Value("${stardex.custom_csvs.voltAmpereProfiles}")
    private String customVOAP;

    @Value("${stardex.custom_csvs.injectors}")
    private String customInjectors;

    @Value("${stardex.custom_csvs.testNames}")
    private String customTestNames;

    @Value("${stardex.custom_csvs.injectorTests}")
    private String customInjectorTests;

    public CheckAndInitializeBD(ManufacturerRepository manufacturerRepository, DataSource dataSource) {
        this.manufacturerRepository = manufacturerRepository;
        this.dataSource = dataSource;
    }


    @PostConstruct
    private void check() {

        try {
            if (!isCurrentVersion()) {
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

    }

    private void checkCustomCSVS() {

        File fileDir = new File(System.getProperty("user.home") + File.separator + directory + File.separator + customCSVSDirectory);

        fileDir.mkdirs();

        File customManufacturersFile = new File(fileDir, customManufacturers);

        File customInjectorTypesFile = new File(fileDir, customInjectorTypes);

        File customVOAPFile = new File(fileDir, customVOAP);

        File customInjectorsFile = new File(fileDir, customInjectors);

        File customTestNamesFile = new File(fileDir, customTestNames);

        File customInjectorTestsFile = new File(fileDir, customInjectorTests);

//        List<File> listOfCustomCSVSFiles = new ArrayList<>();
//
//        listOfCustomCSVSFiles.add(customManufacturersFile);
//
//        listOfCustomCSVSFiles.add(customInjectorTypesFile);
//
//        listOfCustomCSVSFiles.add(customVOAPFile);
//
//        listOfCustomCSVSFiles.add(customInjectorsFile);
//
//        listOfCustomCSVSFiles.add(customTestNamesFile);
//
//        listOfCustomCSVSFiles.add(customInjectorTestsFile);
//
//        listOfCustomCSVSFiles.forEach(file -> {
//            if (!file.exists() || !file.isFile()) {
//
//            }
//        });

    }

    private boolean isCurrentVersion() throws IOException {

        boolean isCurrent;

        File fileDir = new File(System.getProperty("user.home") + File.separator + directory);

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
        deleteTables();
        fillTables();
    }

    private void deleteTables() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/db/scripts/delete_tables.sql")));
            RunScript.execute(dataSource.getConnection(), reader);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void fillTables() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/db/scripts/fill_tables.sql")));
            RunScript.execute(dataSource.getConnection(), reader);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
