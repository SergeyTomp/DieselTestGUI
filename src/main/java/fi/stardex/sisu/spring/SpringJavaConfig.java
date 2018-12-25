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
import fi.stardex.sisu.measurement.Measurements;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.pdf.PDFService;
import fi.stardex.sisu.persistence.CheckAndInitializeBD;
import fi.stardex.sisu.persistence.orm.CSVSUpdater;
import fi.stardex.sisu.persistence.orm.Manufacturer;
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
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.CodingController;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.RLCController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.FlowReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.RLC_ReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.ConnectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.ArrayList;
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
    public RegisterProvider flowRegisterProvider(ModbusConnect flowModbusConnect, ConnectionController connectionController,
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
    public ModbusRegisterProcessor ultimaModbusWriter(List<Updater> updatersList, RegisterProvider ultimaRegisterProvider) {
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
    public ModbusRegisterProcessor flowModbusWriter(List<Updater> updatersList, RegisterProvider flowRegisterProvider,
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
                                updaters.stream().filter(updater -> updater instanceof FlowMasterUpdater).forEach(Platform::runLater);
                                break;
                            case STREAM:
                                updaters.stream().filter(updater -> updater instanceof FlowStreamUpdater).forEach(Platform::runLater);
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
    public ModbusRegisterProcessor standModbusWriter(List<Updater> updatersList, RegisterProvider standRegisterProvider,
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
    public InjectorSectionUpdater injectorSectionUpdater(VoltageController voltageController) {
        return new InjectorSectionUpdater(voltageController);
    }

    @Bean
    @Autowired
    public FlowMasterUpdater flowMasterUpdater(FlowController flowController,
                                               InjectorSectionController injectorSectionController,
                                               FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                               InjConfigurationModel injConfigurationModel,
                                               InstantFlowState instantFlowState) {
        return new FlowMasterUpdater(flowController, injectorSectionController, flowFirmwareVersion, injConfigurationModel, instantFlowState);
    }

    @Bean
    @Autowired
    public FlowStreamUpdater flowStreamUpdater(FlowController flowController,
                                               InjectorSectionController injectorSectionController,
                                               FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                               InjConfigurationModel injConfigurationModel,
                                               InstantFlowState instantFlowState) {
        return new FlowStreamUpdater(flowController, injectorSectionController, flowFirmwareVersion, injConfigurationModel, instantFlowState);
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
    public CheckAndInitializeBD checkAndInitializeBD(ManufacturerRepository manufacturerRepository,
                                                     DataSource dataSource) {
        return new CheckAndInitializeBD(manufacturerRepository, dataSource);
    }

    @Bean
    @DependsOn("checkAndInitializeBD")
    @Autowired
    public ListView<Manufacturer> manufacturerList(ManufacturerRepository manufacturerRepository, MainSectionController mainSectionController) {
        Iterable<Manufacturer> manufacturers = manufacturerRepository.findAll();
        List<Manufacturer> listOfManufacturers = new ArrayList<>();
        manufacturers.forEach(listOfManufacturers::add);

        ObservableList<Manufacturer> observableList = FXCollections.observableList(listOfManufacturers);
        ListView<Manufacturer> manufacturerList = mainSectionController.getManufacturerListView();
        manufacturerList.setItems(observableList);

        return manufacturerList;
    }

    @Bean
    @Lazy
    @Autowired
    public Enabler enabler(MainSectionController mainSectionController,
                           InjectorSectionController injectorSectionController,
                           RLCController rlcController,
                           VoltageController voltageController,
                           FlowController flowController,
                           FlowReportController flowReportController,
                           GUI_TypeController gui_typeController,
                           FlowReportModel flowReportModel) {
        return new Enabler(mainSectionController,
                injectorSectionController,
                rlcController,
                voltageController,
                flowController, flowReportController,
                gui_typeController.getGui_typeComboBox(),
                flowReportModel);
    }

    @Bean
    @Autowired
    public FlowResolver flowResolver(MainSectionController mainSectionController,
                                     FlowController flowController,
                                     FlowViewModel flowViewModel) {
        return new FlowResolver(mainSectionController.getTestListView().getSelectionModel(), flowController, flowViewModel);
    }

    @Bean
    @Autowired
    public CSVSUpdater csvsUpdater(ManufacturerRepository manufacturerRepository,
                                   VoltAmpereProfileRepository voltAmpereProfileRepository,
                                   InjectorsRepository injectorsRepository,
                                   InjectorTestRepository injectorTestRepository) {
        return new CSVSUpdater(manufacturerRepository, voltAmpereProfileRepository, injectorsRepository, injectorTestRepository);
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
                                     Enabler enabler,
                                     CodingController codingController,
                                     ISADetectionController isaDetectionController,
                                     CodingReportModel codingReportModel,
                                     FlowReportModel flowReportModel,
                                     HighPressureSectionPwrState highPressureSectionPwrState,
                                     PressureRegulatorOneModel pressureRegulatorOneModel,
                                     HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        return new Measurements(mainSectionController, testBenchSectionController,
                injectorSectionController, enabler, codingController,
                isaDetectionController, codingReportModel, flowReportModel,
                highPressureSectionPwrState,
                pressureRegulatorOneModel,
                highPressureSectionUpdateModel);
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
                                 RLC_ReportController rlc_reportController,
                                 DelayReportModel delayReportModel,
                                 RLC_ReportModel rlc_reportModel,
                                 CodingReportModel codingReportModel,
                                 FlowReportModel flowReportModel) {
        PDFService pdfService = new PDFService();
        pdfService.setI18N(i18N);
        pdfService.setDesktopFiles(desktopFiles);
        pdfService.setRlc_reportController(rlc_reportController);
        pdfService.setLanguageModel(languageModel);
        pdfService.setDelayReportModel(delayReportModel);
        pdfService.setRlc_reportModel(rlc_reportModel);
        pdfService.setCodingReportModel(codingReportModel);
        pdfService.setFlowReportModel(flowReportModel);
        return pdfService;
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
    public BoostUModel boostU_state() {
        return new BoostUModel();
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
                                           FlowViewModel flowViewModel) {
        return new FlowReportModel(flowViewModel, flowValuesModel, deliveryFlowRangeModel,
                deliveryFlowUnitsModel, backFlowRangeModel, backFlowUnitsModel);
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
                                               PumpModel pumpModel){
        return new PumpTestListModel(pumpTestRepository, pumpModel);
    }

    @Bean
    public PumpTestModeModel pumpTestModeModel(){
        return new PumpTestModeModel();
    }
}
