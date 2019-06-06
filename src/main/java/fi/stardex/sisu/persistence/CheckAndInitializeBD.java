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

    private File stardexDirectory;

    private File customCSVSDirectory;

    private static final String NEW_LINE_SEPARATOR = "\n";

    @Value("${stardex.custom_csvs.manufacturers.header}")
    private String custom_manufacturers_header;

    @Value("${stardex.custom_csvs.voltAmpereProfiles.header}")
    private String custom_voap_header;

    @Value("${stardex.custom_csvs.injectors.header}")
    private String custom_injectors_header;

    @Value("${stardex.custom_csvs.injectorTests.header}")
    private String custom_injector_tests_header;

    @Value("${stardex.custom_csvs.directory}")
    private String customCSVSDirectoryName;

    @Value("${stardex.directory}")
    private String directoryName;

    @Value("${stardex.version.file.name}")
    private String versionFileName;

    @Value("${stardex.version}")
    private String version;

    @Value("${stardex.custom_csvs.manufacturers}")
    private String customManufacturers;

    @Value("${stardex.custom_csvs.manufacturersHeui}")
    private String customManufacturersHeui;

    @Value("${stardex.custom_csvs.voltAmpereProfiles}")
    private String customVOAP;

    @Value("${stardex.custom_csvs.injectors}")
    private String customInjectors;

    @Value("${stardex.custom_csvs.injectorTests}")
    private String customInjectorTests;

    public CheckAndInitializeBD(ManufacturerRepository manufacturerRepository, DataSource dataSource) {

        this.manufacturerRepository = manufacturerRepository;
        this.dataSource = dataSource;

    }

    @PostConstruct
    private void check() {

        stardexDirectory = new File(System.getProperty("user.home"), directoryName);

        customCSVSDirectory = new File(stardexDirectory, customCSVSDirectoryName);

        if (!customCSVSDirectory.exists() || !customCSVSDirectory.isDirectory())
            customCSVSDirectory.mkdirs();

        try {
            checkCustomCSVS();
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
            } else {
                logger.error("IO Exception occured!", e);
            }
        }

    }

    private void checkCustomCSVS() throws IOException {

        File customManufacturersFile = new File(customCSVSDirectory, customManufacturers);

        File customVOAPFile = new File(customCSVSDirectory, customVOAP);

        File customInjectorsFile = new File(customCSVSDirectory, customInjectors);

        File customInjectorTestsFile = new File(customCSVSDirectory, customInjectorTests);

        File customManufacturersHeuiFile = new File(customCSVSDirectory, customManufacturersHeui);

        if (!customManufacturersFile.exists() || !customManufacturersFile.isFile())
            createCustomCSV(customManufacturersFile, custom_manufacturers_header);

        if (!customVOAPFile.exists() || !customVOAPFile.isFile())
            createCustomCSV(customVOAPFile, custom_voap_header);

        if (!customInjectorsFile.exists() || !customInjectorsFile.isFile())
            createCustomCSV(customInjectorsFile, custom_injectors_header);

        if (!customInjectorTestsFile.exists() || !customInjectorTestsFile.isFile())
            createCustomCSV(customInjectorTestsFile, custom_injector_tests_header);

        if (!customManufacturersHeuiFile.exists() || !customManufacturersHeuiFile.isFile())
            createCustomCSV(customManufacturersHeuiFile, custom_manufacturers_header);
    }

    private void createCustomCSV(File file, String header) throws IOException {

        file.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
        }

    }

    private boolean isCurrentVersion() throws IOException {

        boolean isCurrent;

        File file = new File(stardexDirectory, versionFileName);

        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (!line.equals(version)) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write(version);
                    }
                    isCurrent = false;
                } else
                    isCurrent = true;
            }
        } else {
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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
