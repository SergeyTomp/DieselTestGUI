package fi.stardex.sisu.spring;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.charts.ChartTaskFour;
import fi.stardex.sisu.charts.ChartTaskOne;
import fi.stardex.sisu.charts.ChartTaskThree;
import fi.stardex.sisu.charts.ChartTaskTwo;
import fi.stardex.sisu.connect.ConnectProcessor;
import fi.stardex.sisu.connect.InetAddressWrapper;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.measurement.CrTestManager;
import fi.stardex.sisu.measurement.PumpTestManager;
import fi.stardex.sisu.measurement.TestManagerFactory;
import fi.stardex.sisu.measurement.UisTestManager;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.cr.*;
import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.model.uis.*;
import fi.stardex.sisu.model.updateModels.*;
import fi.stardex.sisu.pdf.PDFService;
import fi.stardex.sisu.persistence.CheckAndInitializeBD;
import fi.stardex.sisu.persistence.orm.CSVSUpdater;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.persistence.repos.pump.*;
import fi.stardex.sisu.persistence.repos.uis.*;
import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.StandControlsService;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.*;
import fi.stardex.sisu.ui.controllers.common.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.MainSectionController;
import fi.stardex.sisu.ui.controllers.cr.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.tabs.settings.ConnectionController;
import fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController;
import fi.stardex.sisu.ui.controllers.pumps.CalibrationTestErrorController;
import fi.stardex.sisu.ui.controllers.pumps.SCVCalibrationController;
import fi.stardex.sisu.ui.controllers.pumps.main.PumpTestListController;
import fi.stardex.sisu.ui.controllers.pumps.main.StartButtonController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpHighPressureSectionPwrController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpRegulatorSectionTwoController;
import fi.stardex.sisu.ui.controllers.uis.MainSectionUisController;
import fi.stardex.sisu.ui.controllers.uis.UisInjectorSectionController;
import fi.stardex.sisu.ui.updaters.*;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.DesktopFiles;
import fi.stardex.sisu.util.converters.FlowResolver;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.BackFlowRescaler;
import fi.stardex.sisu.util.rescalers.DeliveryRescaler;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import fi.stardex.sisu.version.StandFirmwareVersion;
import fi.stardex.sisu.version.UltimaFirmwareVersion;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.FirmwareVersion;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.*;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions.*;
import static fi.stardex.sisu.version.UltimaFirmwareVersion.UltimaVersions;
import static fi.stardex.sisu.version.UltimaFirmwareVersion.UltimaVersions.*;

@Configuration
@Import(JavaFXSpringConfigure.class)
@EnableScheduling
public class SpringJavaConfig {

    @Bean
    public ConnectProcessor connectProcessor() {
        return new ConnectProcessor();
    }

    @Bean
    public InetAddressWrapper inetAddressWrapper() {
        return new InetAddressWrapper();
    }

    @Bean
    @Autowired
    public ModbusConnect ultimaModbusConnect(ConnectionController connectionController, ConnectProcessor connectProcessor, Devices devices, StatusBarWrapper statusBar,
                                             InetAddressWrapper inetAddressWrapper) {
        return new ModbusConnect(connectionController.getUltimaConnect(), connectProcessor, devices, statusBar, Device.ULTIMA, inetAddressWrapper);
    }

    @Bean
    @Autowired
    public ModbusConnect flowModbusConnect(ConnectionController connectionController, ConnectProcessor connectProcessor, Devices devices, StatusBarWrapper statusBar,
                                           InetAddressWrapper inetAddressWrapper) {
        return new ModbusConnect(connectionController.getFlowMeterConnect(), connectProcessor, devices, statusBar, Device.MODBUS_FLOW, inetAddressWrapper);
    }

    @Bean
    @Autowired
    public ModbusConnect standModbusConnect(ConnectionController connectionController, ConnectProcessor connectProcessor, Devices devices, StatusBarWrapper statusBar,
                                            InetAddressWrapper inetAddressWrapper) {
        return new ModbusConnect(connectionController.getStandConnect(), connectProcessor, devices, statusBar, Device.MODBUS_STAND, inetAddressWrapper);
    }

    @Bean
    public Devices devices() {
        return new Devices();
    }

    @Bean
    @Autowired
    public I18N i18N(Preferences rootPrefs) {
        return new I18N(rootPrefs);
    }

    @Bean
    public Preferences rootPrefs() {
        return Preferences.userRoot();
    }

    @Bean
    @Autowired
    public RegisterProvider ultimaRegisterProvider(ModbusConnect ultimaModbusConnect, FirmwareVersion<UltimaVersions> ultimaFirmwareVersion) {
        return new RegisterProvider(ultimaModbusConnect) {
            @Override
            public void setupFirmwareVersionListener() {

                BooleanProperty connectedProperty = ultimaModbusConnect.connectedProperty();

                if (!connectedProperty.get())
                    ultimaFirmwareVersion.setVersions(UltimaVersions.NO_VERSION);

                connectedProperty.addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        int firmwareVersionNumber = (int) read(FirmwareVersion);
                        switch (firmwareVersionNumber) {
                            case 221:
                                ultimaFirmwareVersion.setVersions(WITHOUT_A);
                                break;
                            case 238:
                                ultimaFirmwareVersion.setVersions(WITH_A);
                                break;
                            case 241:
                                ultimaFirmwareVersion.setVersions(WITHOUT_F);
                                break;
                        }
                    } else {
                        ultimaFirmwareVersion.setVersions(UltimaVersions.NO_VERSION);
                    }
                });
            }
        };
    }

    @Bean
    @Autowired
    public RegisterProvider flowRegisterProvider(ModbusConnect flowModbusConnect,
                                                 ConnectionController connectionController,
                                                 FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        return new RegisterProvider(flowModbusConnect) {
            @Override
            public void setupFirmwareVersionListener() {

                BooleanProperty connectedProperty = flowModbusConnect.connectedProperty();

                if (!connectedProperty.get())
                    flowFirmwareVersion.setVersions(FlowVersions.NO_VERSION);

                connectedProperty.addListener((observable, oldValue, newValue) -> {
                    TextField standIPField = connectionController.getStandIPField();
                    TextField standPortField = connectionController.getStandPortField();
                    if (newValue) {
                        int firmwareVersionNumber = (int) read(ModbusMapFlow.FirmwareVersion);
                        switch (firmwareVersionNumber) {
                            case 0xAACC:
                                flowFirmwareVersion.setVersions(MASTER);
                                standIPField.setDisable(false);
                                standPortField.setDisable(false);
                                break;
                            case 0xDDFF:
                                flowFirmwareVersion.setVersions(MASTER_DF);
                                standIPField.setDisable(false);
                                standPortField.setDisable(false);
                                break;
                            case 0xAABB:
                                flowFirmwareVersion.setVersions(STREAM);
                                standIPField.setDisable(false);
                                standPortField.setDisable(false);
                                break;
                            case 0xBBCC:
                                flowFirmwareVersion.setVersions(STAND_FM);
                                standIPField.setDisable(true);
                                standPortField.setDisable(true);
                                break;
                            case 0xCCDD:
                                flowFirmwareVersion.setVersions(STAND_FM_4_CH);
                                standIPField.setDisable(true);
                                standPortField.setDisable(true);
                                break;
                        }
                    } else {
                        flowFirmwareVersion.setVersions(FlowVersions.NO_VERSION);
                        standIPField.setDisable(false);
                        standPortField.setDisable(false);
                    }
                });

            }
        };
    }

    @Bean
    @Autowired
    public RegisterProvider standRegisterProvider(ModbusConnect standModbusConnect, FirmwareVersion<StandVersions> standFirmwareVersion) {
        return new RegisterProvider(standModbusConnect) {
            @Override
            public void setupFirmwareVersionListener() {

                BooleanProperty connectedProperty = standModbusConnect.connectedProperty();

                if (!connectedProperty.get())
                    standFirmwareVersion.setVersions(StandVersions.NO_VERSION);

                connectedProperty.addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        int firmwareVersionNumber = (int) read(ModbusMapStand.FirmwareVersion);
                        switch (firmwareVersionNumber) {

                            case 0x1122:
                                standFirmwareVersion.setVersions(STAND);
                                break;
                            case 0x2233:
                                standFirmwareVersion.setVersions(STAND_FORTE);
                                break;
                            default:
                                standFirmwareVersion.setVersions(UNKNOWN);
                                break;
                        }
                    } else {
                        standFirmwareVersion.setVersions(StandVersions.NO_VERSION);
                    }
                });
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor ultimaModbusWriter(List<Updater> updatersList,
                                                      RegisterProvider ultimaRegisterProvider) {
        return new ModbusRegisterProcessor(ultimaRegisterProvider, ModbusMapUltima.values()) {

            @Override
            protected void initThread() {
                List<Updater> updaters = addUpdaters(updatersList, Device.ULTIMA);
                Thread loopThread = new Thread(new ProcessExecutor() {
                    @Override
                    protected void updateAll() {
                        updaters.forEach(Platform::runLater);
                    }
                });
                setLoopThread(loopThread);
                loopThread.setName("Ultima register processor");
                loopThread.setDaemon(true);
                loopThread.start();
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor flowModbusWriter(List<Updater> updatersList,
                                                    RegisterProvider flowRegisterProvider,
                                                    FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                                    StandControlsService standControlsService) {
        return new ModbusRegisterProcessor(flowRegisterProvider, ModbusMapFlow.values(), standControlsService) {

            @Override
            protected void setRegisterSetChangeListener(){

                controlsService.controlsChangeProperty().addListener((observableValue, oldValue, newValue) -> {

                        isPaused = true;

                        List<ModbusMap> standFmRegisters = controlsService.getControls().stream()
                                .map(c -> (ModbusMapStand)c.getRegister())
                                .filter(ModbusMapStand::isStandFMRegister)
                                .collect(Collectors.toList());

                        standFmRegisters.addAll(Arrays.asList(ModbusMapFlow.values()));
                        readArray = standFmRegisters.toArray(ModbusMap[]::new);
                        isPaused = false;
                 });
            }

            @Override
            protected void initThread() {

                List<Updater> updaters = addUpdaters(updatersList, Device.MODBUS_FLOW);
                setRegisterSetChangeListener();
                Thread loopThread = new Thread(new ProcessExecutor() {

//                    ModbusMapStand[] standRegisters = ModbusMapStand.values();

//                    @Override
//                    protected boolean isStand(ModbusMap register) {
//                        return register.isAutoUpdate() && ((ModbusMapStand) register).isStandFMRegister();
//                    }

//                    @Override
//                    protected void readAll() {

//                        FlowVersions version = flowFirmwareVersion.getVersions();
//
//                        if (version == STAND_FM || version == STAND_FM_4_CH)
//                            Arrays.stream(standRegisters).filter(this::isStand).forEach(registerProvider::read);

//                        super.readAll();
//                    }

                    @Override
                    protected void updateAll() {

                        FlowVersions version = flowFirmwareVersion.getVersions();
                        switch (version) {
                            case MASTER:
                                updaters.stream()
                                        .filter(updater ->
                                        updater instanceof FlowMasterUpdater
                                        || updater instanceof PumpFlowUpdater
                                        || updater instanceof UisFlowUpdater)
                                        .forEach(Platform::runLater);
                                break;
                            case MASTER_DF:
                                updaters.stream()
                                        .filter(updater ->
                                                updater instanceof FlowMasterUpdater
                                                        || updater instanceof PumpFlowUpdater
                                                        || updater instanceof UisFlowUpdater
                                                        || updater instanceof DifferentialFmUpdateModel)
                                        .forEach(Platform::runLater);
                                break;
                            case STREAM:
                                updaters.stream()
                                        .filter(updater ->
                                        updater instanceof FlowStreamUpdater
                                        || updater instanceof UisFlowUpdater
                                        || updater instanceof PumpFlowUpdater)
                                        .forEach(Platform::runLater);
                                break;
                            case STAND_FM:
                                updaters.stream()
                                        .filter(updater ->
                                        updater instanceof FlowMasterUpdater
                                        || updater instanceof UisFlowUpdater
                                        || updater instanceof TestBenchSectionUpdateModel)
                                        .forEach(Platform::runLater);
                                break;
                            case STAND_FM_4_CH:
                                updaters.stream()
                                        .filter(updater ->
                                        updater instanceof FlowStreamUpdater
                                        || updater instanceof UisFlowUpdater
                                        || updater instanceof TestBenchSectionUpdateModel)
                                        .forEach(Platform::runLater);
                                break;
                            default:
                                break;
                        }

                    }
                });
                setLoopThread(loopThread);
                loopThread.setName("Flow register processor");
                loopThread.setDaemon(true);
                loopThread.start();
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor standModbusWriter(List<Updater> updatersList,
                                                     RegisterProvider standRegisterProvider,
                                                     StandControlsService standControlsService) {
        return new ModbusRegisterProcessor(standRegisterProvider, ModbusMapStand.values(), standControlsService) {

            @Override
            protected void setRegisterSetChangeListener(){

                controlsService.controlsChangeProperty().addListener((observableValue, oldValue, newValue) -> {
                        isPaused = true;
                        readArray = controlsService.getControls().stream()
                                .map(c -> (ModbusMapStand)c.getRegister())
                                .filter(modbusMapStand -> !modbusMapStand.isStandFMRegister()).toArray(ModbusMap[]::new);
                        isPaused = false;
                });
            }

            @Override
            protected void initThread() {
                List<Updater> updaters = addUpdaters(updatersList, Device.MODBUS_STAND);

                setRegisterSetChangeListener();

                Thread loopThread = new Thread(new ProcessExecutor() {

//                    private boolean isNotStand(ModbusMap register) {
//                        return !isStand(register);
//                    }

//                    @Override
//                    protected boolean isStand(ModbusMap register) {
//                        return register.isAutoUpdate() && ((ModbusMapStand) register).isStandFMRegister();
//                    }

//                    @Override
//                    protected void readAll() {

//                        FlowVersions version = flowFirmwareVersion.getVersions();
//
//                        if (version == STAND_FM || version == STAND_FM_4_CH){
//                            Arrays.stream(readArray).filter(this::isStand).forEach(registerProvider::read);
//                        }
//
//                        else{
//                            Arrays.stream(readArray).filter(this::isNotStand).forEach(registerProvider::read);
//                        }
//                        Arrays.stream(readArray).filter(reg -> !isStand(reg)).forEach(registerProvider::read);

//                    }

                    @Override
                    protected void updateAll() {
                        updaters.forEach(Platform::runLater);
                    }
                });
                setLoopThread(loopThread);
                loopThread.setName("Stand register processor");
                loopThread.setDaemon(true);
                loopThread.start();
            }
        };
    }

    @Bean
    @Autowired
    public InjectorSectionUpdateModel injectorSectionUpdateModel(VoltAmpereProfileDialogModel voltAmpereProfileDialogModel) {
        InjectorSectionUpdateModel injectorSectionUpdateModel = new InjectorSectionUpdateModel();
        injectorSectionUpdateModel.setVoltAmpereProfileDialogModel(voltAmpereProfileDialogModel);
        return injectorSectionUpdateModel;
    }

    @Bean
    @Autowired
    public FlowMasterUpdater flowMasterUpdater(FlowController flowController,
                                               FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                               InjConfigurationModel injConfigurationModel,
                                               InstantFlowState instantFlowState,
                                               InjectorControllersState injectorControllersState,
                                               InjectorSectionPwrState injectorSectionPwrState) {
        return new FlowMasterUpdater(flowController,
                flowFirmwareVersion,
                injConfigurationModel,
                instantFlowState,
                injectorControllersState,
                injectorSectionPwrState);
    }

    @Bean
    @Autowired
    public FlowStreamUpdater flowStreamUpdater(FlowController flowController,
                                               FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                               InjConfigurationModel injConfigurationModel,
                                               InstantFlowState instantFlowState,
                                               InjectorControllersState injectorControllersState,
                                               InjectorSectionPwrState injectorSectionPwrState) {
        return new FlowStreamUpdater(flowController,
                flowFirmwareVersion,
                injConfigurationModel,
                instantFlowState,
                injectorControllersState,
                injectorSectionPwrState);
    }

    @Bean
    @Autowired
    public PumpFlowUpdater pumpFlowUpdater(PumpFlowValuesModel pumpDeliveryFlowValuesModel,
                                           PumpFlowValuesModel pumpBackFlowValuesModel,
                                           PumpFlowTemperaturesModel pumpDeliveryFlowTemperaturesModel,
                                           PumpFlowTemperaturesModel pumpBackFlowTemperaturesModel,
                                           FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                           InstantFlowState instantFlowState,
                                           PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState,
                                           FlowRangeModel pumpDeliveryFlowRangeModel,
                                           FlowRangeModel pumpBackFlowRangeModel) {
        PumpFlowUpdater pumpFlowUpdater = new PumpFlowUpdater();
        pumpFlowUpdater.setPumpDeliveryFlowValuesModel(pumpDeliveryFlowValuesModel);
        pumpFlowUpdater.setPumpBackFlowValuesModel(pumpBackFlowValuesModel);
        pumpFlowUpdater.setPumpDeliveryFlowTemperaturesModel(pumpDeliveryFlowTemperaturesModel);
        pumpFlowUpdater.setPumpBackFlowTemperaturesModel(pumpBackFlowTemperaturesModel);
        pumpFlowUpdater.setFlowFirmwareVersion(flowFirmwareVersion);
        pumpFlowUpdater.setInstantFlowState(instantFlowState);
        pumpFlowUpdater.setPumpHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpFlowUpdater.setPumpDeliveryFlowRangeModel(pumpDeliveryFlowRangeModel);
        pumpFlowUpdater.setPumpBackFlowRangeModel(pumpBackFlowRangeModel);
        return pumpFlowUpdater;
    }

    @Bean
    @Autowired
    public TestBenchSectionUpdateModel testBenchSectionUpdateModel(StandControlsService standControlsService) {
        TestBenchSectionUpdateModel testBenchSectionUpdateModel = new TestBenchSectionUpdateModel();
        testBenchSectionUpdateModel.setControlsService(standControlsService);
        return testBenchSectionUpdateModel;
    }

    @Bean
    @Autowired
    public TachometerUltimaUpdateModel tachometerUltimaUpdateModel(FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                                                   ModbusConnect standModbusConnect) {
        return new TachometerUltimaUpdateModel(flowFirmwareVersion, standModbusConnect.connectedProperty());
    }

    @Bean
    @Qualifier("chartTaskOne")
    @Scope("prototype")
    public ChartTaskOne chartTaskOne() {
        return new ChartTaskOne();
    }

    @Bean
    @Qualifier("ChartTaskTwo")
    @Scope("prototype")
    public ChartTaskTwo chartTaskTwo() {
        return new ChartTaskTwo();
    }

    @Bean
    @Qualifier("chartTaskThree")
    @Scope("prototype")
    public ChartTaskThree chartTaskThree() {
        return new ChartTaskThree();
    }

    @Bean
    @Qualifier("chartTaskFour")
    @Scope("prototype")
    public ChartTaskFour chartTaskFour() {
        return new ChartTaskFour();
    }

    @Bean
    public DelayCalculator delayCalculator() {
        return new DelayCalculator();
    }

    @Bean
    public Rescaler deliveryRescaler() {
        return new DeliveryRescaler();
    }

    @Bean
    public Rescaler backFlowRescaler() {
        return new BackFlowRescaler();
    }

    @Bean
    public Rescaler pumpDeliveryRescaler() {
        return new DeliveryRescaler();
    }

    @Bean
    public Rescaler pumpBackFlowRescaler() {
        return new BackFlowRescaler();
    }

    @Bean
    public CheckAndInitializeBD checkAndInitializeBD(ManufacturerRepository manufacturerRepository,
                                                     DataSource dataSource) {
        return new CheckAndInitializeBD(manufacturerRepository, dataSource);
    }


    @Bean
    @Autowired
    public FlowResolver flowResolver(FlowController flowController,
                                     FlowViewModel flowViewModel,
                                     MainSectionModel mainSectionModel) {
        return new FlowResolver(flowController, flowViewModel, mainSectionModel);
    }

    @Bean
    @Autowired
    public CSVSUpdater csvsUpdater(ManufacturerRepository manufacturerRepository,
                                   VoltAmpereProfileRepository voltAmpereProfileRepository,
                                   InjectorsRepository injectorsRepository,
                                   InjectorTestRepository injectorTestRepository,
                                   UisProducerService producerService,
                                   UisModelService modelService,
                                   UisTestService testService,
                                   UisVapService vapService,
                                   PumpProducerService pumpProducerService,
                                   PumpModelService pumpModelService,
                                   PumpTestService pumpTestService) {
        return new CSVSUpdater(manufacturerRepository,
                voltAmpereProfileRepository,
                injectorsRepository,
                injectorTestRepository,
                producerService,
                modelService,
                testService,
                vapService,
                pumpProducerService,
                pumpModelService,
                pumpTestService);
    }

    @Bean
    public FirmwareVersion<UltimaVersions> ultimaFirmwareVersion() {
        return new UltimaFirmwareVersion<>(WITH_A);
    }

    @Bean
    public FirmwareVersion<FlowVersions> flowFirmwareVersion() {
        return new FlowFirmwareVersion<>(MASTER);
    }

    @Bean
    public FirmwareVersion<StandVersions> standFirmwareVersion() {
        return new StandFirmwareVersion<>(STAND);
    }

    @Bean
    @Autowired
    public CrTestManager crTestManager(MainSectionController mainSectionController,
                                       TestBenchSectionController testBenchSectionController,
                                       InjectorSectionController injectorSectionController,
                                       ISADetectionController isaDetectionController,
                                       CodingReportModel codingReportModel,
                                       FlowReportModel flowReportModel,
                                       HighPressureSectionPwrState highPressureSectionPwrState,
                                       PressureRegulatorOneModel pressureRegulatorOneModel,
                                       HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                       MainSectionModel mainSectionModel,
                                       InjectorControllersState injectorControllersState,
                                       TestBenchSectionModel testBenchSectionModel) {
        return new CrTestManager(mainSectionController, testBenchSectionController,
                injectorSectionController,
                isaDetectionController, codingReportModel, flowReportModel,
                highPressureSectionPwrState,
                pressureRegulatorOneModel,
                highPressureSectionUpdateModel,
                mainSectionModel,
                injectorControllersState,
                testBenchSectionModel);
    }

    private List<Updater> addUpdaters(List<Updater> updatersList, Device targetDevice) {
        List<Updater> updaters = new LinkedList<>();
        updatersList.forEach(updater -> {
            Module module = updater.getClass().getAnnotation(Module.class);
            for (Device device : module.value()) {
                if (device == targetDevice)
                    updaters.add(updater);
            }
        });
        return updaters;
    }


    @Bean
    public DesktopFiles desktopFiles() {
        return new DesktopFiles();
    }

    @Bean
    @Autowired
    public PDFService pdfService(I18N i18N,
                                 DesktopFiles desktopFiles,
                                 LanguageModel languageModel,
                                 DelayReportModel delayReportModel,
                                 RLC_ReportModel rlc_reportModel,
                                 CodingReportModel codingReportModel,
                                 FlowReportModel flowReportModel,
                                 PumpReportModel pumpReportModel,
                                 UisFlowModel uisFlowModel,
                                 UisDelayModel uisDelayModel,
                                 UisRlcModel uisRlcModel,
                                 UisBipModel uisBipModel) {
        PDFService pdfService = new PDFService();
        pdfService.setI18N(i18N);
        pdfService.setDesktopFiles(desktopFiles);
        pdfService.setLanguageModel(languageModel);
        pdfService.setDelayReportModel(delayReportModel);
        pdfService.setRlc_reportModel(rlc_reportModel);
        pdfService.setCodingReportModel(codingReportModel);
        pdfService.setFlowReportModel(flowReportModel);
        pdfService.setPumpReportModel(pumpReportModel);
        pdfService.setUisFlowModel(uisFlowModel);
        pdfService.setUisDelayModel(uisDelayModel);
        pdfService.setUisRlcModel(uisRlcModel);
        pdfService.setUisBipModel(uisBipModel);
        return pdfService;
    }

    @Bean
    @Autowired
    public PumpTestManager pumpTestManager(PumpTestListModel pumpTestListModel,
                                           PumpsStartButtonState pumpsStartButtonState,
                                           PumpTestModel pumpTestModel,
                                           ModbusRegisterProcessor flowModbusWriter,
                                           PumpReportModel pumpReportModel,
                                           PumpTestModeModel pumpTestModeModel,
                                           HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                           PumpTimeProgressModel pumpTimeProgressModel,
                                           PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel,
                                           PumpTestListController pumpTestListController,
                                           SCVCalibrationModel scvCalibrationModel,
                                           SCVCalibrationController scvCalibrationController,
                                           PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController,
                                           CalibrationTestErrorController calibrationTestErrorController,
                                           PumpModel pumpModel,
                                           StartButtonController startButtonController,
                                           PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController,
                                           TestBenchSectionController testBenchSectionController,
                                           TestBenchSectionModel testBenchSectionModel) {
        PumpTestManager pumpTestManager = new PumpTestManager();
        pumpTestManager.setPumpsStartButtonState(pumpsStartButtonState);
        pumpTestManager.setPumpTestListModel(pumpTestListModel);
        pumpTestManager.setPumpTestModel(pumpTestModel);
        pumpTestManager.setFlowModbusWriter(flowModbusWriter);
        pumpTestManager.setPumpReportModel(pumpReportModel);
        pumpTestManager.setPumpTestModeModel(pumpTestModeModel);
        pumpTestManager.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpTestManager.setPumpTimeProgressModel(pumpTimeProgressModel);
        pumpTestManager.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        pumpTestManager.setTestListView(pumpTestListController.getTestListView());
        pumpTestManager.setScvCalibrationModel(scvCalibrationModel);
        pumpTestManager.setScvCalibrationController(scvCalibrationController);
        pumpTestManager.setPumpRegulatorSectionTwoController(pumpRegulatorSectionTwoController);
        pumpTestManager.setCalibrationTestErrorController(calibrationTestErrorController);
        pumpTestManager.setPumpModel(pumpModel);
        pumpTestManager.setStartButtonController(startButtonController);
        pumpTestManager.setPumpHighPressureSectionPwrController(pumpHighPressureSectionPwrController);
        pumpTestManager.setTestBenchSectionController(testBenchSectionController);
        pumpTestManager.setTestBenchSectionModel(testBenchSectionModel);
        return pumpTestManager;
    }

    @Bean
    @Autowired
    public PumpModelService pumpModelService(PumpRepository pumpRepository) {
        return new PumpModelService(pumpRepository);
    }

    @Bean
    @Autowired
    public PumpProducerService pumpProducerService(ManufacturerPumpRepository manufacturerPumpRepository) {
        return new PumpProducerService(manufacturerPumpRepository);
    }

    @Bean
    @Autowired
    public PumpTestService pumpTestService(PumpTestRepository pumpTestRepository) {
        return new PumpTestService(pumpTestRepository);
    }

    // --------------------------------------State-----------------------------------------------

    @Bean
    public DimasGUIEditionState dimasGUIEditionState() {
        return new DimasGUIEditionState();
    }

    @Bean
    public FastCodingState fastCodingState() {
        return new FastCodingState();
    }

    @Bean
    public InstantFlowState instantFlowState() {
        return new InstantFlowState();
    }

    @Bean
    public CustomPumpState customPumpState() {
        return new CustomPumpState();
    }

    @Bean
    public BoostUadjustmentState boostUadjustmentState() {
        return new BoostUadjustmentState();
    }

    @Bean
    public HighPressureSectionPwrState highPressureSectionPwrState() {
        return new HighPressureSectionPwrState();
    }

    @Bean
    public PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState() {
        return new PumpHighPressureSectionPwrState();
    }

    @Bean
    public PumpsStartButtonState pumpsStartButtonState() {
        return new PumpsStartButtonState();
    }

    @Bean
    public InjectorSectionPwrState injectorSectionPwrState() {
        return new InjectorSectionPwrState();
    }

    // --------------------------------------Model-----------------------------------------------

    @Bean
    public ManufacturerPumpModel manufacturerPumpModel() {
        return new ManufacturerPumpModel();
    }

    @Bean
    public PumpModel pumpModel() {
        return new PumpModel();
    }

    @Bean
    public PumpTestModel pumpTestModel(){
        return new PumpTestModel();
    }

    @Bean
    public InjectorTypeModel injectorTypeModel() {
        return new InjectorTypeModel();
    }

    @Bean
    public RegulatorsQTYModel regulatorsQTYModel() {
        return new RegulatorsQTYModel();
    }

    @Bean
    public VoltAmpereProfileDialogModel voltAmpereProfileDialogModel() {
        return new VoltAmpereProfileDialogModel();
    }

    @Bean
    public PressureSensorModel pressureSensorModel() {
        return new PressureSensorModel();
    }

    @Bean
    public LanguageModel languageModel() {
        return new LanguageModel();
    }

    @Bean
    public InjConfigurationModel injConfigurationModel() {
        return new InjConfigurationModel();
    }

    @Bean
    public FlowViewModel flowViewModel() {
        return new FlowViewModel();
    }

    @Bean
    @Lazy
    public DelayReportModel delayReportModel() {
        return new DelayReportModel();
    }

    @Bean
    @Lazy
    public RLC_ReportModel rlc_reportModel() {
        return new RLC_ReportModel();
    }

    @Bean
    @Lazy
    public CodingReportModel codingReportModel() {
        return new CodingReportModel();
    }

    @Bean
    @Lazy
    @Autowired
    public FlowReportModel flowReportModel(FlowValuesModel flowValuesModel,
                                           BackFlowRangeModel backFlowRangeModel,
                                           BackFlowUnitsModel backFlowUnitsModel,
                                           DeliveryFlowRangeModel deliveryFlowRangeModel,
                                           DeliveryFlowUnitsModel deliveryFlowUnitsModel,
                                           FlowViewModel flowViewModel,
                                           InjectorControllersState injectorControllersState,
                                           InjectorTestModel injectorTestModel) {
        return new FlowReportModel(flowViewModel, flowValuesModel, deliveryFlowRangeModel,
                deliveryFlowUnitsModel, backFlowRangeModel, backFlowUnitsModel, injectorControllersState, injectorTestModel);
    }

    @Bean
    @Autowired
    public PumpReportModel pumpReportModel(FlowRangeModel pumpDeliveryFlowRangeModel,
                                           FlowRangeModel pumpBackFlowRangeModel,
                                           FlowUnitsModel pumpDeliveryFlowUnitsModel,
                                           FlowUnitsModel pumpBackFlowUnitsModel,
                                           PumpTestModel pumpTestModel,
                                           PumpModel pumpModel) {

        PumpReportModel pumpReportModel = new PumpReportModel();
        pumpReportModel.setDeliveryFlowRangeModel(pumpDeliveryFlowRangeModel);
        pumpReportModel.setBackFlowRangeModel(pumpBackFlowRangeModel);
        pumpReportModel.setDeliveryFlowUnitsModel(pumpDeliveryFlowUnitsModel);
        pumpReportModel.setBackFlowUnitsModel(pumpBackFlowUnitsModel);
        pumpReportModel.setPumpTestModel(pumpTestModel);
        pumpReportModel.setPumpModel(pumpModel);
        return pumpReportModel;
    }

    @Bean
    @Lazy
    public FlowValuesModel flowValuesModel() {
        return new FlowValuesModel();
    }

    @Bean
    @Lazy
    public BackFlowRangeModel backFlowRangeModel() {
        return new BackFlowRangeModel();
    }

    @Bean
    @Lazy
    public BackFlowUnitsModel backFlowUnitsModel() {
        return new BackFlowUnitsModel();
    }

    @Bean
    @Lazy
    public DeliveryFlowRangeModel deliveryFlowRangeModel() {
        return new DeliveryFlowRangeModel();
    }

    @Bean
    @Lazy
    public DeliveryFlowUnitsModel deliveryFlowUnitsModel() {
        return new DeliveryFlowUnitsModel();
    }

    @Bean
    @Autowired
    public HighPressureSectionUpdateModel highPressureSectionUpdateModel(PressureSensorModel pressureSensorModel,
                                                                         RegulationModesModel regulationModesModel) {
        return new HighPressureSectionUpdateModel(pressureSensorModel, regulationModesModel);
    }

    @Bean
    public InjectorTestModel injectorTestModel() {
        return new InjectorTestModel();
    }

    @Bean
    public PressureRegulatorOneModel pressureRegulatorOneModel() {
        return new PressureRegulatorOneModel();
    }

    @Bean
    public RegulationModesModel regulationModesModel() {
        return new RegulationModesModel();
    }

    @Bean
    public PumpTimeProgressModel pumpTimeProgressModel() {
        return new PumpTimeProgressModel();
    }

    @Bean
    @Autowired
    public PumpTestListModel pumpTestListModel(PumpTestRepository pumpTestRepository,
                                               PumpModel pumpModel,
                                               AutoTestListLastChangeModel autoTestListLastChangeModel){
        PumpTestListModel pumpTestListModel = new PumpTestListModel(pumpTestRepository, pumpModel);
        pumpTestListModel.setAutoTestListLastChangeModel(autoTestListLastChangeModel);
        return  pumpTestListModel;
    }

    @Bean
    public CustomPumpTestDialogModel custompumpTestDialogModel() {
        return new CustomPumpTestDialogModel();
    }

    @Bean
    public PumpTestModeModel pumpTestModeModel(){
        return new PumpTestModeModel();
    }

    @Bean
    public AutoTestListLastChangeModel autoTestListLastChangeModel(){
        return new AutoTestListLastChangeModel();
    }

    @Bean
    public PumpFlowValuesModel pumpDeliveryFlowValuesModel(){
        return new PumpFlowValuesModel();
    }

    @Bean
    public PumpFlowValuesModel pumpBackFlowValuesModel(){
        return new PumpFlowValuesModel();
    }

    @Bean
    public PumpFlowTemperaturesModel pumpDeliveryFlowTemperaturesModel(){
        return new PumpFlowTemperaturesModel();
    }

    @Bean
    public PumpFlowTemperaturesModel pumpBackFlowTemperaturesModel(){
        return new PumpFlowTemperaturesModel();
    }

    @Bean
    public CustomPumpProducerDialogModel customPumpProducerDialogModel() {
        return new CustomPumpProducerDialogModel();
    }

    @Bean
    public FlowRangeModel pumpDeliveryFlowRangeModel(){
        return new FlowRangeModel();
    }

    @Bean
    public FlowRangeModel pumpBackFlowRangeModel(){
        return new FlowRangeModel();
    }

    @Bean
    public FlowUnitsModel pumpDeliveryFlowUnitsModel() {
        return new FlowUnitsModel();
    }

    @Bean
    public FlowUnitsModel pumpBackFlowUnitsModel() {
        return new FlowUnitsModel();
    }

    @Bean
    public PiezoRepairModel piezoRepairModel() {
        return new PiezoRepairModel();
    }

    @Bean
    public PumpTestSpeedModel pumpTestSpeedModel() {
        return new PumpTestSpeedModel();
    }

    @Bean
    public PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel() {
        return new PumpPressureRegulatorOneModel();
    }

    @Bean
    public PiezoRepairUpdateModel piezoRepairUpdateModel() {
        return new PiezoRepairUpdateModel();
    }

    @Bean
    public SCVCalibrationModel scvCalibrationModel() {
        return new SCVCalibrationModel();
    }

    @Bean
    public GUI_TypeModel gui_typeModel() {
        return new GUI_TypeModel();
    }

    @Bean
    public CoilOnePulseParametersModel coilOnePulseParametersModel() {
        return new CoilOnePulseParametersModel();
    }

    @Bean
    public CoilTwoPulseParametersModel coilTwoPulseParametersModel() {
        return new CoilTwoPulseParametersModel();
    }

    @Bean
    public InjectorModel injectorModel() {
        return new InjectorModel();
    }

    @Bean
    public InjectorControllersState injectorControllersState() {
        return new InjectorControllersState();
    }

    @Bean
    public ProducerRepositoryModel producerRepositoryModel() {
        return new ProducerRepositoryModel();
    }

    @Bean
    public ManufacturerMenuDialogModel manufacturerMenuDialogModel() {
        return new ManufacturerMenuDialogModel();
    }

    @Bean
    @Autowired
    public MainSectionModel mainSectionModel(ManufacturerRepository manufacturerRepository) {
        return new MainSectionModel(manufacturerRepository);
    }

    @Bean
    public VoltageTabModel voltageTabModel() {
        return new VoltageTabModel();
    }

    @Bean
    public NewEditInjectorDialogModel newEditInjectorDialogModel() {
        return new NewEditInjectorDialogModel();
    }

    @Bean
    public TestBenchSectionModel testBenchSectionModel() {
        return new TestBenchSectionModel();
    }

    @Bean
    public MainSectionUisModel mainSectionUisModel() {
        return new MainSectionUisModel();
    }

    @Bean
    public CustomModelDialogModel customInjectorDialogModel() {
        return new CustomModelDialogModel();
    }

    @Bean
    public CustomTestDialogModel customTestDialogModel() {
        return new CustomTestDialogModel();
    }

    @Bean
    public CustomProducerDialogModel customManufacturerDialogModel() {
        return new CustomProducerDialogModel();
    }

    @Bean
    @Autowired
    public UisModelService uisModelService(InjectorUisRepository injectorUisRepository,
                                           ReferenceUisRepository referenceUisRepository) {
        return new UisModelService(injectorUisRepository, referenceUisRepository);
    }

    @Bean
    @Autowired
    public UisProducerService uisProducerService(ManufacturerUisRepository manufacturerUisRepository) {
        return new UisProducerService(manufacturerUisRepository);
    }

    @Bean
    @Autowired
    public UisTestService uisTestService(InjectorUisTestRepository injectorUisTestRepository) {
        return new UisTestService(injectorUisTestRepository);
    }

    @Bean
    @Autowired
    public UisVapService uisVapService(InjectorUisVapRepository injectorUisVapRepository) {
        return new UisVapService(injectorUisVapRepository);
    }

    @Bean
    public CustomVapUisDialogModel customVapUisDialogModel() {
        return new CustomVapUisDialogModel();
    }

    @Bean
    @Autowired
    public UisTestNameService uisTestNameService(InjectorUisTestNameRepository injectorUisTestNameRepository) {
        return new UisTestNameService(injectorUisTestNameRepository);
    }

    @Bean
    public UisSettingsModel uisSettingsModel() {
        return new UisSettingsModel();
    }

    @Bean
    public UisInjectorSectionModel uisInjectorSectionModel() {
        return new UisInjectorSectionModel();
    }

    @Bean
    public Step3Model step3Model() {
        return new Step3Model();
    }

    @Bean
    public TabSectionModel tabSectionModel() {
        return new TabSectionModel();
    }

    @Bean
    public DiffFlowUpdateModel diffFlowUpdateModel() {
        return new DiffFlowUpdateModel();
    }

    @Bean
    public UisTabSectionModel uisTabSectionModel() {
        return new UisTabSectionModel();
    }

    @Bean
    @Autowired
    public UisHardwareUpdateModel uisHardwareUpdateModel(MainSectionUisModel mainSectionUisModel,
                                                         GUI_TypeModel gui_typeModel,
                                                         RegulationModesModel regulationModesModel,
                                                         UisSettingsModel uisSettingsModel) {
        UisHardwareUpdateModel uisHardwareUpdateModel = new UisHardwareUpdateModel();
        uisHardwareUpdateModel.setMainSectionUisModel(mainSectionUisModel);
        uisHardwareUpdateModel.setGui_typeModel(gui_typeModel);
        uisHardwareUpdateModel.setRegulationModesModel(regulationModesModel);
        uisHardwareUpdateModel.setUisSettingsModel(uisSettingsModel);
        return uisHardwareUpdateModel;
    }

    @Bean
    public UisVoltageTabModel uisVoltageTabModel() {
        return new UisVoltageTabModel();
    }

    @Bean
    public UisVapModel uisVapDialogModel() {
        return new UisVapModel();
    }

    @Bean
    public ChartTaskDataModel chartTaskDataModel() {
        return new ChartTaskDataModel();
    }

    @Bean
    @Autowired
    public UisDelayModel uisDelayModel(MainSectionUisModel mainSectionUisModel,
                                       UisInjectorSectionModel uisInjectorSectionModel) {
        UisDelayModel uisDelayModel = new UisDelayModel();
        uisDelayModel.setMainSectionUisModel(mainSectionUisModel);
        uisDelayModel.setUisInjectorSectionModel(uisInjectorSectionModel);
        return uisDelayModel;
    }

    @Bean
    @Autowired
    public UisRlcModel uisRlcReportModel(MainSectionUisModel mainSectionUisModel,
                                         UisInjectorSectionModel uisInjectorSectionModel) {
        UisRlcModel uisRlcModel = new UisRlcModel();
        uisRlcModel.setMainSectionUisModel(mainSectionUisModel);
        uisRlcModel.setUisInjectorSectionModel(uisInjectorSectionModel);
        return uisRlcModel;
    }

    @Bean
    @Autowired
    public UisFlowModel uisFlowModel(MainSectionUisModel mainSectionUisModel,
                                     UisInjectorSectionModel uisInjectorSectionModel) {
        UisFlowModel uisFlowModel = new UisFlowModel();
        uisFlowModel.setMainSectionUisModel(mainSectionUisModel);
        uisFlowModel.setUisInjectorSectionModel(uisInjectorSectionModel);
        return uisFlowModel;
    }

    @Bean
    @Autowired
    public UisFlowUpdater uisFlowUpdater(FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        UisFlowUpdater uisFlowUpdater = new UisFlowUpdater();
        uisFlowUpdater.setFlowFirmwareVersion(flowFirmwareVersion);
        return uisFlowUpdater;
    }

    @Bean
    @Autowired
    public UisBipModel uisBipModel(MainSectionUisModel mainSectionUisModel,
                                   UisInjectorSectionModel uisInjectorSectionModel) {
        UisBipModel uisBipModel = new UisBipModel();
        uisBipModel.setMainSectionUisModel(mainSectionUisModel);
        uisBipModel.setUisInjectorSectionModel(uisInjectorSectionModel);
        return uisBipModel;
    }

    @Bean
    @Autowired
    public UisTestManager uisTestManager(MainSectionUisController mainSectionUisController,
                                         UisInjectorSectionController uisInjectorSectionController,
                                         TestBenchSectionController testBenchSectionController,
                                         MainSectionUisModel mainSectionUisModel,
                                         UisInjectorSectionModel uisInjectorSectionModel,
                                         TestBenchSectionModel testBenchSectionModel,
                                         UisHardwareUpdateModel uisHardwareUpdateModel,
                                         ModbusRegisterProcessor flowModbusWriter,
                                         UisTestTimingModel uisTestTimingModel,
                                         I18N i18N) {
        UisTestManager uisTestManager = new UisTestManager();
        uisTestManager.setMainSectionUisController(mainSectionUisController);
        uisTestManager.setUisInjectorSectionController(uisInjectorSectionController);
        uisTestManager.setTestBenchSectionController(testBenchSectionController);
        uisTestManager.setMainSectionUisModel(mainSectionUisModel);
        uisTestManager.setUisInjectorSectionModel(uisInjectorSectionModel);
        uisTestManager.setTestBenchSectionModel(testBenchSectionModel);
        uisTestManager.setUisHardwareUpdateModel(uisHardwareUpdateModel);
        uisTestManager.setFlowModbusWriter(flowModbusWriter);
        uisTestManager.setUisTestTimingModel(uisTestTimingModel);
        uisTestManager.setI18N(i18N);
        return uisTestManager;
    }

    @Bean
    public UisTestTimingModel uisTestTimingModel() {
        return new UisTestTimingModel();
    }

    @Bean
    public CrTestTimingModel crTestTimingModel() {
        return new CrTestTimingModel();
    }

    @Bean
    public PumpTestTimingModel pumpTestTimingModel() {
        return new PumpTestTimingModel();
    }

    @Bean
    @Autowired
    public TimingModelFactory timingModelFactory(CrTestTimingModel crTestTimingModel,
                                                 PumpTestTimingModel pumpTestTimingModel,
                                                 UisTestTimingModel uisTestTimingModel) {
        TimingModelFactory timingModelFactory = new TimingModelFactory();
        timingModelFactory.setCrTestTimingModel(crTestTimingModel);
        timingModelFactory.setPumpTestTimingModel(pumpTestTimingModel);
        timingModelFactory.setUisTestTimingModel(uisTestTimingModel);
        return timingModelFactory;
    }

    @Bean
    @Autowired
    public TestManagerFactory testManagerFactory(CrTestManager crTestManager,
                                                 PumpTestManager pumpTestManager,
                                                 UisTestManager uisTestManager) {
        TestManagerFactory testManagerFactory = new TestManagerFactory();
        testManagerFactory.setCrTestManager(crTestManager);
        testManagerFactory.setPumpTestManager(pumpTestManager);
        testManagerFactory.setUisTestManager(uisTestManager);
        return testManagerFactory;
    }

    @Bean
    public CrSettingsModel crSettingsModel() {
        return new CrSettingsModel();
    }

    @Bean
    public DifferentialFmUpdateModel differentialFmUpdateModel() {
        return new DifferentialFmUpdateModel();
    }

    @Bean
    @Autowired
    public StandControlsService standControlsService(FirmwareVersion<StandVersions> standFirmwareVersion,
                                                     FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                                     GUI_TypeModel gui_typeModel) {
        StandControlsService standControlsFactory = new StandControlsService();
        standControlsFactory.setStandFirmwareVersion(standFirmwareVersion);
        standControlsFactory.setFlowFirmwareVersion(flowFirmwareVersion);
        standControlsFactory.setGui_typeModel(gui_typeModel);
        return standControlsFactory;
    }

    @Bean
    public CustomPumpDialogModel customPumpDialogModel() {
        return new CustomPumpDialogModel();
    }

    @Bean
    @Autowired
    public PumpTestNameService pumpTestNameService(PumpTestNameRepository pumpTestNameRepository) {
        return new PumpTestNameService(pumpTestNameRepository);
    }

//    @Bean
//    @Autowired
//    public FxListSelection pumpTestListSelectionModel(PumpTestListController pumpTestListController) {
//        return new ListSelection(pumpTestListController.getTestListView().getSelectionModel());
//    }
}
