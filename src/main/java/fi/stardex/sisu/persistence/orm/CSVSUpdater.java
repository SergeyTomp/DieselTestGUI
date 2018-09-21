package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.CheckAndInitializeBD;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@PropertySource("classpath:properties/app.properties")
public class CSVSUpdater {

    private Logger logger = LoggerFactory.getLogger(CheckAndInitializeBD.class);

    private ManufacturerRepository manufacturerRepository;

    private VoltAmpereProfileRepository voltAmpereProfileRepository;

    private InjectorsRepository injectorsRepository;

    private InjectorTestRepository injectorTestRepository;

    private static final String NEW_LINE_SEPARATOR = "\n";

    private static final String COMMA_DELIMITER = ",";

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

    @Value("${stardex.custom_csvs.manufacturers}")
    private String customManufacturers;

    @Value("${stardex.custom_csvs.voltAmpereProfiles}")
    private String customVOAP;

    @Value("${stardex.custom_csvs.injectors}")
    private String customInjectors;

    @Value("${stardex.custom_csvs.injectorTests}")
    private String customInjectorTests;

    public CSVSUpdater(ManufacturerRepository manufacturerRepository,
                       VoltAmpereProfileRepository voltAmpereProfileRepository,
                       InjectorsRepository injectorsRepository,
                       InjectorTestRepository injectorTestRepository) {

        this.manufacturerRepository = manufacturerRepository;
        this.voltAmpereProfileRepository = voltAmpereProfileRepository;
        this.injectorsRepository = injectorsRepository;
        this.injectorTestRepository = injectorTestRepository;

    }

    @PreDestroy
    private void destroy() {

        String pathToCSVSDirectory = System.getProperty("user.home") + File.separator + directoryName + File.separator + customCSVSDirectoryName;

        EntityUpdates.getMapOfEntityUpdates().forEach(((entityClassName, updated) -> {
            if (updated) {
                switch (entityClassName) {
                    case "Manufacturer":
                        updateCustomCSV(new File(pathToCSVSDirectory, customManufacturers), custom_manufacturers_header, manufacturerRepository);
                        break;
                    case "VoltAmpereProfile":
                        updateCustomCSV(new File(pathToCSVSDirectory, customVOAP), custom_voap_header, voltAmpereProfileRepository);
                        break;
                    case "Injector":
                        updateCustomCSV(new File(pathToCSVSDirectory, customInjectors), custom_injectors_header, injectorsRepository);
                        break;
                    case "InjectorTest":
                        updateCustomCSV(new File(pathToCSVSDirectory, customInjectorTests), custom_injector_tests_header, injectorTestRepository);
                        break;
                    default:
                        break;
                }
            }
        }));

    }

    private void updateCustomCSV(File file, String header, ManufacturerRepository repository) {

        List<Manufacturer> customManufacturersList = repository.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customManufacturersList.isEmpty()) {
                customManufacturersList.forEach(manufacturer -> {
                    try {
                        writer.append(manufacturer.getManufacturerName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(manufacturer.isCustom())).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occured!", ex);
                    }

                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occured!", ex);
        }

    }

    private void updateCustomCSV(File file, String header, VoltAmpereProfileRepository repository) {

        List<VoltAmpereProfile> customVOAPList = repository.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customVOAPList.isEmpty()) {
                customVOAPList.forEach(voap -> {
                    try {
                        writer.append(voap.getProfileName()).append(COMMA_DELIMITER)
                                .append(voap.getInjectorType().toString()).append(COMMA_DELIMITER)
                                .append(String.valueOf(voap.isCustom())).append(COMMA_DELIMITER)
                                .append(voap.getBoostU().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBatteryU().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBoostI().toString()).append(COMMA_DELIMITER)
                                .append(voap.getFirstI().toString()).append(COMMA_DELIMITER)
                                .append(voap.getFirstW().toString()).append(COMMA_DELIMITER)
                                .append(voap.getSecondI().toString()).append(COMMA_DELIMITER)
                                .append(voap.getNegativeU().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBoostDisable().toString()).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occured!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occured!", ex);
        }
    }

    private void updateCustomCSV(File file, String header, InjectorsRepository repository) {

        List<Injector> customInjectorsList = repository.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customInjectorsList.isEmpty()) {
                customInjectorsList.forEach(injector -> {
                    try {
                        String codetypeValue = injector.getCodetype().toString();
                        String calibrationIdValue = (injector.getCalibrationId() == null) ? "" : injector.getCalibrationId();
                        String coefficient = injector.getCoefficient().toString();
                        writer.append(injector.getInjectorCode()).append(COMMA_DELIMITER)
                                .append(injector.getManufacturer().toString()).append(COMMA_DELIMITER)
                                .append(injector.getVoltAmpereProfile().toString()).append(COMMA_DELIMITER)
                                .append(codetypeValue).append(COMMA_DELIMITER)
                                .append(calibrationIdValue).append(COMMA_DELIMITER)
                                .append(coefficient).append(COMMA_DELIMITER)
                                .append(injector.isCustom().toString()).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occured!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occured!", ex);
        }

    }

    private void updateCustomCSV(File file, String header, InjectorTestRepository repository) {

        List<InjectorTest> customInjectorTestsList = repository.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customInjectorTestsList.isEmpty()) {
                customInjectorTestsList.forEach(injectorTest -> {
                    try {
                        String adjustingTimeValue = (injectorTest.getAdjustingTime() == null) ? "" : injectorTest.getAdjustingTime().toString();
                        String codefieldValue = (injectorTest.getCodefield() == null) ? "" : injectorTest.getCodefield();
                        String voltAmpereProfileValue = (injectorTest.getVoltAmpereProfile() == null) ? "" : injectorTest.getVoltAmpereProfile().toString();
                        writer.append(injectorTest.getId().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getInjector().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getTestName().getId().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getMotorSpeed().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getSettedPressure().toString()).append(COMMA_DELIMITER)
                                .append(adjustingTimeValue).append(COMMA_DELIMITER)
                                .append(injectorTest.getMeasurementTime().toString()).append(COMMA_DELIMITER)
                                .append(codefieldValue).append(COMMA_DELIMITER)
                                .append(injectorTest.getInjectionRate().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getTotalPulseTime().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getNominalFlow().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getFlowRange().toString()).append(COMMA_DELIMITER)
                                .append(voltAmpereProfileValue).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occured!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occured!", ex);
        }

    }

}
