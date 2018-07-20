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

    private File customCSVSDirectoryFile;

    private static final String NEW_LINE_SEPARATOR = "\n";

    private static final String CUSTOM_MANUFACTURERS_HEADER = "manufacturer_name,is_custom";

    private static final String CUSTOM_INJECTOR_TYPES_HEADER = "type_name,injector_type";

    private static final String CUSTOM_VOAP_HEADER =
            "profile_name,injector_type,is_custom,boost_u,battery_u,boost_i,first_i,first_w,second_i,negative_u,boost_disable";

    private static final String CUSTOM_INJECTORS_HEADER =
            "injector_code,manufacturerName,volt_ampere_profile,codetype,calibration_id,checksum_m,k_coefficient,is_custom";

    private static final String CUSTOM_TEST_NAMES_HEADER = "id,test_name,measurement";

    private static final String CUSTOM_INJECTOR_TESTS_HEADER =
            "id,injector_code,test_name,motor_speed,setted_pressure,adjusting_time,measurement_time,codefield," +
                    "injection_rate,total_pulse_time,nominal_flow,flow_range,volt_ampere_profile";

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

        stardexDirectory = new File(System.getProperty("user.home"), directoryName);

        customCSVSDirectoryFile = new File(stardexDirectory, customCSVSDirectoryName);

        if (!customCSVSDirectoryFile.exists() || !customCSVSDirectoryFile.isDirectory())
            customCSVSDirectoryFile.mkdirs();

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

        File customManufacturersFile = new File(customCSVSDirectoryFile, customManufacturers);

        File customInjectorTypesFile = new File(customCSVSDirectoryFile, customInjectorTypes);

        File customVOAPFile = new File(customCSVSDirectoryFile, customVOAP);

        File customInjectorsFile = new File(customCSVSDirectoryFile, customInjectors);

        File customTestNamesFile = new File(customCSVSDirectoryFile, customTestNames);

        File customInjectorTestsFile = new File(customCSVSDirectoryFile, customInjectorTests);

        if (!customManufacturersFile.exists() || !customManufacturersFile.isFile())
            createCustomCSV(customManufacturersFile, CUSTOM_MANUFACTURERS_HEADER);

        if (!customInjectorTypesFile.exists() || !customInjectorTypesFile.isFile())
            createCustomCSV(customInjectorTypesFile, CUSTOM_INJECTOR_TYPES_HEADER);

        if (!customVOAPFile.exists() || !customVOAPFile.isFile())
            createCustomCSV(customVOAPFile, CUSTOM_VOAP_HEADER);

        if (!customInjectorsFile.exists() || !customInjectorsFile.isFile())
            createCustomCSV(customInjectorsFile, CUSTOM_INJECTORS_HEADER);

        if (!customTestNamesFile.exists() || !customTestNamesFile.isFile())
            createCustomCSV(customTestNamesFile, CUSTOM_TEST_NAMES_HEADER);

        if (!customInjectorTestsFile.exists() || !customInjectorTestsFile.isFile())
            createCustomCSV(customInjectorTestsFile, CUSTOM_INJECTOR_TESTS_HEADER);

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
