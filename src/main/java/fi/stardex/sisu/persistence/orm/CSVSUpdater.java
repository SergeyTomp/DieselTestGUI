package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.CheckAndInitializeBD;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.orm.interfaces.*;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.persistence.repos.pump.PumpModelService;
import fi.stardex.sisu.persistence.repos.pump.PumpProducerService;
import fi.stardex.sisu.persistence.repos.pump.PumpTestService;
import fi.stardex.sisu.persistence.repos.uis.UisProducerService;
import fi.stardex.sisu.persistence.repos.uis.UisTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@PropertySource("classpath:properties/app.properties")
public class CSVSUpdater {

    private Logger logger = LoggerFactory.getLogger(CheckAndInitializeBD.class);

    private ManufacturerRepository manufacturerRepository;

    private VoltAmpereProfileRepository voltAmpereProfileRepository;

    private InjectorsRepository injectorsRepository;

    private InjectorTestRepository injectorTestRepository;

    private ProducerService producerService;

    private ModelService modelService;

    private TestService testService;

    private VapService vapService;

    private PumpProducerService pumpProducerService;
    private PumpModelService pumpModelService;
    private PumpTestService pumpTestService;

    private static final String NEW_LINE_SEPARATOR = "\n";

    private static final String COMMA_DELIMITER = ",";

    @Value("${stardex.custom_csvs.manufacturers.header}")
    private String custom_manufacturers_header;

    @Value("${stardex.custom_csvs.manufacturersUis.header}")
    private String custom_manufacturersUis_header;

    @Value("${stardex.custom_csvs.voltAmpereProfiles.header}")
    private String custom_voap_header;

    @Value("${stardex.custom_csvs.voltAmpereProfilesUis.header}")
    private String custom_vapUis_header;

    @Value("${stardex.custom_csvs.injectorsUis.header}")
    private String custom_injectorUis_header;

    @Value("${stardex.custom_csvs.injectorUisTests.header}")
    private String custom_injectorUis_tests_header;

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

    @Value("${stardex.custom_csvs.manufacturersUis}")
    private String customManufacturersUis;

    @Value("${stardex.custom_csvs.voltAmpereProfiles}")
    private String customVOAP;

    @Value("${stardex.custom_csvs.voltAmpereProfilesUis}")
    private String customVapUis;

    @Value("${stardex.custom_csvs.injectors}")
    private String customInjectors;

    @Value("${stardex.custom_csvs.injectorsUis}")
    private String customInjectorsUis;

    @Value("${stardex.custom_csvs.injectorTests}")
    private String customInjectorTests;

    @Value("${stardex.custom_csvs.injectorTestsUis}")
    private String customInjectorUisTests;

    @Value("${stardex.custom_csvs.manufacturersPump}")
    private String customPumpProducer;

    @Value("${stardex.custom_csvs.manufacturersPump.header}")
    private String customPumpProducer_header;

    @Value("${stardex.custom_csvs.pumps}")
    private String customPump;

    @Value("${stardex.custom_csvs.pumps.header}")
    private String customPump_header;

    @Value("${stardex.custom_csvs.pumpTests}")
    private String customPumpTest;

    @Value("${stardex.custom_csvs.pumpTests.header}")
    private String customPumpTest_header;

    public CSVSUpdater(ManufacturerRepository manufacturerRepository,
                       VoltAmpereProfileRepository voltAmpereProfileRepository,
                       InjectorsRepository injectorsRepository,
                       InjectorTestRepository injectorTestRepository,
                       UisProducerService producerService,
                       ModelService modelService,
                       TestService testService,
                       VapService vapService,
                       PumpProducerService pumpProducerService,
                       PumpModelService pumpModelService,
                       PumpTestService pumpTestService) {

        this.manufacturerRepository = manufacturerRepository;
        this.voltAmpereProfileRepository = voltAmpereProfileRepository;
        this.injectorsRepository = injectorsRepository;
        this.injectorTestRepository = injectorTestRepository;
        this.producerService = producerService;
        this.modelService = modelService;
        this.testService = testService;
        this.vapService = vapService;
        this.pumpTestService = pumpTestService;
        this.pumpModelService = pumpModelService;
        this.pumpProducerService = pumpProducerService;
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
                    case "ManufacturerUIS":
                        updateCustomCSV(new File(pathToCSVSDirectory, customManufacturersUis), custom_manufacturersUis_header, producerService);
                        break;
                    case "InjectorUIS":
                        updateCustomCSV(new File(pathToCSVSDirectory, customInjectorsUis), custom_injectorUis_header, modelService);
                        break;
                    case "InjectorUisVAP":
                        updateCustomCSV(new File(pathToCSVSDirectory, customVapUis), custom_vapUis_header, vapService);
                        break;
                    case "InjectorUisTest":
                        updateCustomCSV(new File(pathToCSVSDirectory, customInjectorUisTests), custom_injectorUis_tests_header, testService);
                        break;
                    case "ManufacturerPump":
                        updateCustomCSV(new File(pathToCSVSDirectory, customPumpProducer), customPumpProducer_header, pumpProducerService);
                        break;
                    case "Pump":
                        updateCustomCSV(new File(pathToCSVSDirectory, customPump), customPump_header, pumpModelService);
                        break;
                    case "PumpTest":
                        updateCustomCSV(new File(pathToCSVSDirectory, customPumpTest), customPumpTest_header, pumpTestService);
                        break;
                    default:
                        break;
                }
            }
        }));
    }

    private void updateCustomCSV(File file, String header, PumpTestService testService) {

        List<? extends Test> testList = testService.findAllByIsCustom(true);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter((file)))) {

            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!testList.isEmpty()) {
                testList.forEach(test ->{

                    PumpTest pumpTest = (PumpTest) test;
                    try{
                        writer.append("").append(COMMA_DELIMITER)
                                .append(pumpTest.getModel().getModelCode()).append(COMMA_DELIMITER)
                                .append(String.valueOf(pumpTest.getTestName().getId())).append(COMMA_DELIMITER)
                                .append(String.valueOf(pumpTest.isVacuum())).append(COMMA_DELIMITER)
                                .append(pumpTest.getAdjustingTime().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getMeasuringTime() == null ? "" : pumpTest.getMeasuringTime().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getMotorSpeed().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getTargetPressure().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getMinDirectFlow() == null ? "" : pumpTest.getMinDirectFlow().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getMaxDirectFlow() == null ? "" : pumpTest.getMaxDirectFlow().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getMinBackFlow() == null ? "" : pumpTest.getMinBackFlow().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getMaxBackFlow() == null ? "" : pumpTest.getMaxBackFlow().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getRegulatorCurrent() == null ? "" : pumpTest.getRegulatorCurrent().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getPcvCurrent() == null ? "" : pumpTest.getPcvCurrent().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getCalibrationMinI() == null ? "" : pumpTest.getCalibrationMinI().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getCalibrationMaxI() == null ? "" : pumpTest.getCalibrationMaxI().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getCalibrationI1() == null ? "" : pumpTest.getCalibrationI1().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getCalibrationI2() == null ? "" : pumpTest.getCalibrationI2().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.getCalibrationIoffset() == null ? "" : pumpTest.getCalibrationIoffset().toString()).append(COMMA_DELIMITER)
                                .append(pumpTest.isCustom().toString()).append(NEW_LINE_SEPARATOR);
                    }catch (IOException ex){logger.error("IO Exception for Test file update process occurred!", ex);}
                });
            }
        }catch (IOException ex) { logger.error("IO Exception for Test file update process occurred!", ex);}

    }

    private void updateCustomCSV(File file, String header, PumpModelService modelService) {

        List<? extends Model> customModelsList = modelService.findByIsCustom(true);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customModelsList.isEmpty()) {
                customModelsList.forEach(( model -> {
                    try {
                        Pump pump = (Pump) model;
                        writer.append(model.getModelCode()).append(COMMA_DELIMITER)
                                .append(pump.getManufacturer().getManufacturerName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(pump.isCustom())).append(COMMA_DELIMITER)
                                .append(String.valueOf(pump.getFeedPressure())).append(COMMA_DELIMITER)
                                .append(pump.getPumpRotation().name()).append(COMMA_DELIMITER)
                                .append(pump.getPumpRegulatorConfig().name()).append(COMMA_DELIMITER)
                                .append(pump.getPumpPressureControl().name()).append(COMMA_DELIMITER)
                                .append(pump.getPumpRegulatorType().name()).append(COMMA_DELIMITER)
                                .append(pump.getScvMaxCurr() == null ? "" : pump.getScvMaxCurr().toString()).append(COMMA_DELIMITER)
                                .append(pump.getScvMinCurr() == null ? "" : pump.getScvMinCurr().toString()).append(COMMA_DELIMITER)
                                .append(pump.getScvCurrInj() == null ? "" : pump.getScvCurrInj().toString()).append(NEW_LINE_SEPARATOR);
                    }catch (IOException ex) { logger.error("IO Exception for Models file update process occurred!", ex); }
                }));
            }
        } catch (IOException ex) { logger.error("IO Exception for Models file update process occurred!", ex); }
    }

    private void updateCustomCSV(File file, String header, PumpProducerService producerService) {


        List<? extends Producer> customProducersList = producerService.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customProducersList.isEmpty()) {
                customProducersList.forEach(manufacturer -> {
                    try {
                        writer.append(manufacturer.getManufacturerName()).append(COMMA_DELIMITER)
                                 .append(String.valueOf(manufacturer.isCustom())).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occurred!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occurred!", ex);
        }

    }

    private void updateCustomCSV(File file, String header, TestService testService) {

        List<InjectorUisTest> customTestList = new ArrayList<>();

        if (testService instanceof UisTestService) {
            customTestList.addAll(testService.findAllByIsCustom(true).stream().map(test -> (InjectorUisTest)test).collect(Collectors.toList()));
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter((file)))) {

            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customTestList.isEmpty()) {
                customTestList.forEach(test ->{

                    try{
                        String vap = test.getVoltAmpereProfile() == null ? "" : test.getVoltAmpereProfile().getProfileName();
                        writer.append("").append(COMMA_DELIMITER)
                                .append(test.getModel().getModelCode()).append(COMMA_DELIMITER)
                                .append(test.getTestName().getId().toString()).append(COMMA_DELIMITER)
                                .append(test.getMotorSpeed() == null ? "" : test.getMotorSpeed().toString()).append(COMMA_DELIMITER)
                                .append(test.getTargetPressure() == null ? "" : test.getTargetPressure().toString()).append(COMMA_DELIMITER)
                                .append(test.getAngle_1() == null ? "" : test.getAngle_1().toString()).append(COMMA_DELIMITER)
                                .append(test.getAngle_2() == null ? "" : test.getAngle_2().toString()).append(COMMA_DELIMITER)
                                .append(test.getDoubleCoilOffset() == null ? "" : test.getDoubleCoilOffset().toString()).append(COMMA_DELIMITER)
                                .append(test.getTotalPulseTime1() == null ? "" : test.getTotalPulseTime1().toString()).append(COMMA_DELIMITER)
                                .append(test.getTotalPulseTime2() == null ? "" : test.getTotalPulseTime2().toString()).append(COMMA_DELIMITER)
                                .append(test.getNominalFlow() == null ? "" : test.getNominalFlow().toString()).append(COMMA_DELIMITER)
                                .append(test.getFlowRange() == null ? "" : test.getFlowRange().toString()).append(COMMA_DELIMITER)
                                .append(test.getAdjustingTime() == null ? "" : test.getAdjustingTime().toString()).append(COMMA_DELIMITER)
                                .append(test.getMeasuringTime() == null ? "" : test.getMeasuringTime().toString()).append(COMMA_DELIMITER)
                                .append(vap).append(COMMA_DELIMITER)
                                .append(test.getBip() == null ? "" : test.getBip().toString()).append(COMMA_DELIMITER)
                                .append(test.getBipRange() == null ? "" : test.getBipRange().toString()).append(COMMA_DELIMITER)
                                .append(test.getRackPosition() == null ? "" : test.getRackPosition().toString()).append(COMMA_DELIMITER)
                                .append(test.isCustom().toString()).append(NEW_LINE_SEPARATOR);
                    }catch (IOException ex){logger.error("IO Exception for Test file update process occurred!", ex);}
                });
            }
        }catch (IOException ex) { logger.error("IO Exception for Test file update process occurred!", ex);}
    }

    private void updateCustomCSV(File file, String header, ModelService modelService) {

        List<? extends Model> customModelsList = modelService.findByIsCustom(true);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customModelsList.isEmpty()) {
                customModelsList.forEach(model -> {
                    try {

                        writer.append(model.getModelCode()).append(COMMA_DELIMITER)
                                .append(model.getManufacturer().getManufacturerName()).append(COMMA_DELIMITER)
                                .append(model.getVAP().getProfileName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(model.isCustom())).append(NEW_LINE_SEPARATOR);
                    }catch (IOException ex) { logger.error("IO Exception for Models file update process occurred!", ex); }
                });
            }
        } catch (IOException ex) { logger.error("IO Exception for Models file update process occurred!", ex); }

    }

    private void updateCustomCSV(File file, String header, VapService vapService) {

        List<? extends VAP> customVapList = vapService.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customVapList.isEmpty()) {
                customVapList.forEach(vap -> {
                    try {

                        writer.append(vap.getProfileName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(((InjectorUisVAP) vap).getCamType())).append(COMMA_DELIMITER)
                                .append(String.valueOf(((InjectorUisVAP) vap).getInletPressure())).append(COMMA_DELIMITER)
                                .append(vap.getInjectorType().name()).append(COMMA_DELIMITER)
                                .append(vap.getInjectorSubType().name()).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getBoostDisable())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getBoostU())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getBoostI())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getBatteryU())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getFirstI())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getFirstW())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getSecondI())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getBoostI2())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getFirstI2())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getFirstW2())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getSecondI2())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.getNegativeU())).append(COMMA_DELIMITER)
                                .append(((InjectorUisVAP) vap).getBipPWM() == null ? "" : String.valueOf(((InjectorUisVAP) vap).getBipPWM())).append(COMMA_DELIMITER)
                                .append(((InjectorUisVAP) vap).getBipWindow() == null ? "" : String.valueOf(((InjectorUisVAP) vap).getBipWindow())).append(COMMA_DELIMITER)
                                .append(String.valueOf(vap.isCustom())).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception for VAP file update process occurred!", ex);
                    }
                });
            }

        } catch (IOException ex) { logger.error("IO Exception for VAP file update process occurred!", ex); }

    }

    private void updateCustomCSV(File file, String header, ManufacturerRepository repository) {

        List<Manufacturer> customManufacturersList = repository.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customManufacturersList.isEmpty()) {
                customManufacturersList.forEach(manufacturer -> {
                    try {
                        writer.append(manufacturer.getManufacturerName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(manufacturer.isCustom())).append(COMMA_DELIMITER)
                                .append(manufacturer.getCommonRail().toString()).append(COMMA_DELIMITER)
                                .append(manufacturer.getHeui().toString()).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occurred!", ex);
                    }

                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occurred!", ex);
        }

    }

    private void updateCustomCSV(File file, String header, ProducerService producerService) {

        List<? extends Producer> customProducersList = producerService.findByIsCustom(true);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(header).append(NEW_LINE_SEPARATOR);
            if (!customProducersList.isEmpty()) {
                customProducersList.forEach(manufacturer -> {
                    try {
                        writer.append(manufacturer.getManufacturerName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(manufacturer.getDisplayOrder())).append(COMMA_DELIMITER)
                                .append(String.valueOf(manufacturer.isCustom())).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occurred!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occurred!", ex);
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
                                .append(voap.getInjectorType().getTypeName()).append(COMMA_DELIMITER)
                                .append(String.valueOf(voap.isCustom())).append(COMMA_DELIMITER)
                                .append(voap.getBoostU().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBatteryU().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBoostI().toString()).append(COMMA_DELIMITER)
                                .append(voap.getFirstI().toString()).append(COMMA_DELIMITER)
                                .append(voap.getFirstW().toString()).append(COMMA_DELIMITER)
                                .append(voap.getSecondI().toString()).append(COMMA_DELIMITER)
                                .append(voap.getNegativeU().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBoostDisable().toString()).append(COMMA_DELIMITER)
                                .append(voap.isDoubleCoil().toString()).append(COMMA_DELIMITER)
                                .append(voap.getBoostI2().toString()).append(COMMA_DELIMITER)
                                .append(voap.getFirstI2().toString()).append(COMMA_DELIMITER)
                                .append(voap.getFirstW2().toString()).append(COMMA_DELIMITER)
                                .append(voap.getSecondI2().toString()).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occurred!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occurred!", ex);
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
                        String coefficient = (injector.getCoefficient() == null) ? "" : injector.getCoefficient().toString();
                        writer.append(injector.getInjectorCode()).append(COMMA_DELIMITER)
                                .append(injector.getManufacturer().toString()).append(COMMA_DELIMITER)
                                .append(injector.getVoltAmpereProfile().toString()).append(COMMA_DELIMITER)
                                .append(codetypeValue).append(COMMA_DELIMITER)
                                .append(calibrationIdValue).append(COMMA_DELIMITER)
                                .append(coefficient).append(COMMA_DELIMITER)
                                .append(injector.isCustom().toString()).append(COMMA_DELIMITER)
                                .append(injector.isHeui().toString()).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occurred!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occurred!", ex);
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
                        String voltAmpereProfileValue = (injectorTest.getVoltAmpereProfile() == null) ? "" : injectorTest.getVoltAmpereProfile().toString();
                        String injectionRate = (injectorTest.getInjectionRate() == null) ? "" : injectorTest.getInjectionRate().toString();
                        String nominalFlow = (injectorTest.getNominalFlow() == null) ? "" : injectorTest.getNominalFlow().toString();
                        String flowRange = (injectorTest.getFlowRange() == null) ? "" : injectorTest.getFlowRange().toString();
                        writer.append("").append(COMMA_DELIMITER)
                                .append(injectorTest.getInjector().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getTestName().getId().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getMotorSpeed().toString()).append(COMMA_DELIMITER)
                                .append(injectorTest.getSettedPressure().toString()).append(COMMA_DELIMITER)
                                .append(adjustingTimeValue).append(COMMA_DELIMITER)
                                .append(injectorTest.getMeasurementTime().toString()).append(COMMA_DELIMITER)
                                .append(injectionRate).append(COMMA_DELIMITER)
                                .append(injectorTest.getTotalPulseTime().toString()).append(COMMA_DELIMITER)
                                .append(nominalFlow).append(COMMA_DELIMITER)
                                .append(flowRange).append(COMMA_DELIMITER)
                                .append(voltAmpereProfileValue).append(COMMA_DELIMITER)
                                .append(injectorTest.isCustom().toString()).append(NEW_LINE_SEPARATOR);
                    } catch (IOException ex) {
                        logger.error("IO Exception occurred!", ex);
                    }
                });
            }
        } catch (IOException ex) {
            logger.error("IO Exception occurred!", ex);
        }

    }

    private String getStringFromInteger(Function<Test, Integer> integerFunction, Test test) {

        if(test == null) return null;
        if(integerFunction.apply(test) == null) return "";
        return integerFunction.apply(test).toString();
    }

    private String getStringFromDouble(Function<Test, Double> doubleFunction, Test test) {

        if(test == null) return null;
        if(doubleFunction.apply(test) == null) return "";
        return doubleFunction.apply(test).toString();
    }

}
