package fi.stardex.sisu.spring;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.charts.*;
import fi.stardex.sisu.connect.ConnectProcessor;
import fi.stardex.sisu.connect.InetAddressWrapper;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.measurement.Measurements;
import fi.stardex.sisu.measurement.PumpMeasurementManager;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.model.updateModels.InjectorSectionUpdateModel;
import fi.stardex.sisu.model.updateModels.PiezoRepairUpdateModel;
import fi.stardex.sisu.pdf.PDFService;
import fi.stardex.sisu.persistence.CheckAndInitializeBD;
import fi.stardex.sisu.persistence.orm.CSVSUpdater;
import fi.stardex.sisu.persistence.repos.HEUI.ManufacturerHeuiRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.persistence.repos.pump.ManufacturerPumpRepository;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import fi.stardex.sisu.persistence.repos.pump.PumpTestRepository;
import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.*;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.CodingController;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.ConnectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.controllers.pumps.CalibrationTestErrorController;
import fi.stardex.sisu.ui.controllers.pumps.SCVCalibrationController;
import fi.stardex.sisu.ui.controllers.pumps.main.PumpTestListController;
import fi.stardex.sisu.ui.controllers.pumps.main.StartButtonController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpHighPressureSectionPwrController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpRegulatorSectionTwoController;
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

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.FirmwareVersion;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.*;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions.STAND;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions.UNKNOWN;
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
                        if (firmwareVersionNumber == 0x1122) {
                            standFirmwareVersion.setVersions(STAND);
                        } else {
                            standFirmwareVersion.setVersions(UNKNOWN);
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
                                                    FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        return new ModbusRegisterProcessor(flowRegisterProvider, ModbusMapFlow.values()) {

            @Override
            public boolean add(ModbusMap reg, Object value) {

                boolean added = super.add(reg, value);

                if (reg instanceof ModbusMapStand) {

                    switch ((ModbusMapStand) reg) {

                        case TargetRPMStandFM:
                        case RotationDirectionStandFM:
                        case RotationStandFM:
                        case FanTurnOnStandFM:
                            ((ModbusMapStand) reg).setSyncWriteRead(true);
                            break;
                        default:
                            break;

                    }

                }

                return added;

            }

            @Override
            protected void initThread() {

                List<Updater> updaters = addUpdaters(updatersList, Device.MODBUS_FLOW);
                Thread loopThread = new Thread(new ProcessExecutor() {

                    ModbusMapStand[] standRegisters = ModbusMapStand.values();

                    @Override
                    protected boolean isStand(ModbusMap register) {
                        return register.isAutoUpdate() && ((ModbusMapStand) register).isStandFMRegister();
                    }

                    @Override
                    protected void readAll() {

                        FlowVersions version = flowFirmwareVersion.getVersions();

                        if (version == STAND_FM || version == STAND_FM_4_CH)
                            Arrays.stream(standRegisters).filter(this::isStand).forEach(registerProvider::read);

                        super.readAll();
                    }

                    @Override
                    protected void updateAll() {

                        FlowVersions version = flowFirmwareVersion.getVersions();
                        switch (version) {
                            case MASTER:
                                updaters.stream().filter(updater -> updater instanceof FlowMasterUpdater
                                        || updater instanceof PumpFlowUpdater).forEach(Platform::runLater);
                                break;
                            case STREAM:
                                updaters.stream().filter(updater -> updater instanceof FlowStreamUpdater
                                        || updater instanceof PumpFlowUpdater).forEach(Platform::runLater);
                                break;
                            case STAND_FM:
                                updaters.stream().filter(updater -> updater instanceof FlowMasterUpdater
                                        || updater instanceof TestBenchSectionUpdater).forEach(Platform::runLater);
                                break;
                            case STAND_FM_4_CH:
                                updaters.stream().filter(updater -> updater instanceof FlowStreamUpdater
                                        || updater instanceof TestBenchSectionUpdater).forEach(Platform::runLater);
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
                                                     FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        return new ModbusRegisterProcessor(standRegisterProvider, ModbusMapStand.values()) {

            @Override
            public boolean add(ModbusMap reg, Object value) {

                boolean added = super.add(reg, value);

                switch ((ModbusMapStand) reg) {

                    case TargetRPM:
                    case RotationDirection:
                    case Rotation:
                    case FanTurnOn:
                        ((ModbusMapStand) reg).setSyncWriteRead(true);
                        break;
                    default:
                        break;

                }

                return added;

            }

            @Override
            protected void initThread() {
                List<Updater> updaters = addUpdaters(updatersList, Device.MODBUS_STAND);
                Thread loopThread = new Thread(new ProcessExecutor() {

                    private boolean isNotStand(ModbusMap register) {
                        return !isStand(register);
                    }

                    @Override
                    protected boolean isStand(ModbusMap register) {
                        return register.isAutoUpdate() && ((ModbusMapStand) register).isStandFMRegister();
                    }

                    @Override
                    protected void readAll() {

                        FlowVersions version = flowFirmwareVersion.getVersions();

                        if (version == STAND_FM || version == STAND_FM_4_CH)
                            Arrays.stream(readArray).filter(this::isStand).forEach(registerProvider::read);
                        else
                            Arrays.stream(readArray).filter(this::isNotStand).forEach(registerProvider::read);

                    }

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
    public TestBenchSectionUpdater testBenchSectionUpdater(TestBenchSectionController testBenchSectionController,
                                                           FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        return new TestBenchSectionUpdater(testBenchSectionController, flowFirmwareVersion);
    }

    @Bean
    @Autowired
    public TachometerUltimaUpdater tachometerUltimaUpdater(TestBenchSectionController testBenchSectionController,
                                                           FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                                           ModbusConnect standModbusConnect) {
        return new TachometerUltimaUpdater(testBenchSectionController, flowFirmwareVersion, standModbusConnect.connectedProperty());
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
    @Qualifier("piesoRepairTask")
    @Scope("prototype")
    public PiezoRepairTask piezoRepairTask() {
        return new PiezoRepairTask();
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

//    @Bean
//    @DependsOn("checkAndInitializeBD")
//    @Autowired
//    public ListView<Manufacturer> manufacturerList(ManufacturerRepository manufacturerRepository,
//                                                   MainSectionController mainSectionController) {
//        Iterable<Manufacturer> manufacturers = manufacturerRepository.findAll();
//        List<Manufacturer> listOfManufacturers = new ArrayList<>();
//        manufacturers.forEach(listOfManufacturers::add);

//        ObservableList<Manufacturer> observableList = FXCollections.observableList(listOfManufacturers);
//        ListView<Manufacturer> manufacturerList = mainSectionController.getManufacturerListView();
//        manufacturerList.setItems(observableList);
//
//        return manufacturerList;
//    }

//    @Bean
//    @Lazy
//    @Autowired
//    public Enabler enabler(MainSectionController mainSectionController,
//                           InjectorSectionController injectorSectionController,
//                           VoltageController voltageController,
//                           FlowController flowController,
//                           FlowReportController flowReportController,
//                           GUI_TypeController gui_typeController,
//                           FlowReportModel flowReportModel) {
//        return new Enabler(mainSectionController,
//                injectorSectionController,
//                voltageController,
//                flowController, flowReportController,
//                gui_typeController.getGui_typeComboBox(),
//                flowReportModel);
//    }

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
                                   ManufacturerHeuiRepository manufacturerHeuiRepository) {
        return new CSVSUpdater(manufacturerRepository, voltAmpereProfileRepository, injectorsRepository, injectorTestRepository, manufacturerHeuiRepository);
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
    public Measurements measurements(MainSectionController mainSectionController,
                                     TestBenchSectionController testBenchSectionController,
                                     InjectorSectionController injectorSectionController,
                                     ISADetectionController isaDetectionController,
                                     CodingReportModel codingReportModel,
                                     FlowReportModel flowReportModel,
                                     HighPressureSectionPwrState highPressureSectionPwrState,
                                     PressureRegulatorOneModel pressureRegulatorOneModel,
                                     HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                     MainSectionModel mainSectionModel,
                                     InjectorControllersState injectorControllersState) {
        return new Measurements(mainSectionController, testBenchSectionController,
                injectorSectionController,
                isaDetectionController, codingReportModel, flowReportModel,
                highPressureSectionPwrState,
                pressureRegulatorOneModel,
                highPressureSectionUpdateModel,
                mainSectionModel,
                injectorControllersState);
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
                                 PumpReportModel pumpReportModel) {
        PDFService pdfService = new PDFService();
        pdfService.setI18N(i18N);
        pdfService.setDesktopFiles(desktopFiles);
        pdfService.setLanguageModel(languageModel);
        pdfService.setDelayReportModel(delayReportModel);
        pdfService.setRlc_reportModel(rlc_reportModel);
        pdfService.setCodingReportModel(codingReportModel);
        pdfService.setFlowReportModel(flowReportModel);
        pdfService.setPumpReportModel(pumpReportModel);
        return pdfService;
    }

    @Bean
    @Autowired
    public PumpMeasurementManager pumpMeasurementManager(PumpTestListModel pumpTestListModel,
                                                         PumpsStartButtonState pumpsStartButtonState,
                                                         PumpTestModel pumpTestModel,
                                                         TargetRpmModel targetRpmModel,
                                                         ModbusRegisterProcessor flowModbusWriter,
                                                         PumpReportModel pumpReportModel,
                                                         PumpTestModeModel pumpTestModeModel,
                                                         TestBenchSectionPwrState testBenchSectionPwrState,
                                                         CurrentRpmModel currentRpmModel,
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
                                                         TestBenchSectionController testBenchSectionController) {
        PumpMeasurementManager pumpMeasurementManager = new PumpMeasurementManager();
        pumpMeasurementManager.setPumpsStartButtonState(pumpsStartButtonState);
        pumpMeasurementManager.setPumpTestListModel(pumpTestListModel);
        pumpMeasurementManager.setPumpTestModel(pumpTestModel);
        pumpMeasurementManager.setTargetRpmModel(targetRpmModel);
        pumpMeasurementManager.setFlowModbusWriter(flowModbusWriter);
        pumpMeasurementManager.setPumpReportModel(pumpReportModel);
        pumpMeasurementManager.setPumpTestModeModel(pumpTestModeModel);
        pumpMeasurementManager.setTestBenchSectionPwrState(testBenchSectionPwrState);
        pumpMeasurementManager.setCurrentRpmModel(currentRpmModel);
        pumpMeasurementManager.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpMeasurementManager.setPumpTimeProgressModel(pumpTimeProgressModel);
        pumpMeasurementManager.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        pumpMeasurementManager.setTestListView(pumpTestListController.getTestListView());
        pumpMeasurementManager.setScvCalibrationModel(scvCalibrationModel);
        pumpMeasurementManager.setScvCalibrationController(scvCalibrationController);
        pumpMeasurementManager.setPumpRegulatorSectionTwoController(pumpRegulatorSectionTwoController);
        pumpMeasurementManager.setCalibrationTestErrorController(calibrationTestErrorController);
        pumpMeasurementManager.setPumpModel(pumpModel);
        pumpMeasurementManager.setStartButtonController(startButtonController);
        pumpMeasurementManager.setPumpHighPressureSectionPwrController(pumpHighPressureSectionPwrController);
        pumpMeasurementManager.setTestBenchSectionController(testBenchSectionController);
        return pumpMeasurementManager;
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
    @Autowired
    public ManufacturerPumpModel manufacturerPumpModel(ManufacturerPumpRepository manufacturerPumpRepository) {
        return new ManufacturerPumpModel(manufacturerPumpRepository);
    }

    @Bean
    @Autowired
    public PumpModel pumpModel(PumpRepository pumpRepository,
                               ManufacturerPumpModel manufacturerPumpModel,
                               CustomPumpState customPumpState) {
        return new PumpModel(pumpRepository, manufacturerPumpModel, customPumpState);
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
    public TargetRpmModel targetRpmModel() {
        return new TargetRpmModel();
    }

    @Bean
    public PumpTestSpeedModel pumpTestSpeedModel() {
        return new PumpTestSpeedModel();
    }

    @Bean
    public TestBenchSectionPwrState testBenchSectionPwrState() {
        return new TestBenchSectionPwrState();
    }

    @Bean
    public CurrentRpmModel currentRpmModel() {
        return new CurrentRpmModel();
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

//    @Bean
//    @Autowired
//    public FxListSelection pumpTestListSelectionModel(PumpTestListController pumpTestListController) {
//        return new ListSelection(pumpTestListController.getTestListView().getSelectionModel());
//    }
}
