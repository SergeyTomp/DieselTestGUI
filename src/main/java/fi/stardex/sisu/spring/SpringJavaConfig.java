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
import fi.stardex.sisu.persistence.CheckAndInitializeBD;
import fi.stardex.sisu.persistence.orm.CSVSUpdater;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.store.FlowReport;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.updaters.*;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.VisualUtils;
import fi.stardex.sisu.util.converters.FlowResolver;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentInjectorTestsObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
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

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;
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
                        switch (version) {
                            case STAND_FM:
                                Arrays.stream(standRegisters).filter(this::isStand).forEach(registerProvider::read);
                                super.readAll();
                                break;
                            default:
                                super.readAll();
                                break;
                        }

                    }

                    @Override
                    protected void updateAll() {

                        FlowVersions version = flowFirmwareVersion.getVersions();
                        switch (version) {
                            case MASTER:
                                updaters.stream().filter(updater -> updater instanceof FlowMasterUpdater).forEach(Platform::runLater);
                                break;
                            case STREAM:
                                updaters.stream().filter(updater -> updater instanceof FlowMasterUpdater).forEach(Platform::runLater);
                                break;
                            case STAND_FM:
                                updaters.stream().filter(updater -> updater instanceof FlowMasterUpdater
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

                boolean isStandFMVersion = (flowFirmwareVersion.getVersions() == STAND_FM);

                switch ((ModbusMapStand) reg) {
                    case TargetRPM:
                        reg = isStandFMVersion ? TargetRPMStandFM : TargetRPM;
                        ((ModbusMapStand) reg).setSyncWriteRead(true);
                        break;
                    case RotationDirection:
                        reg = isStandFMVersion ? RotationDirectionStandFM : RotationDirection;
                        ((ModbusMapStand) reg).setSyncWriteRead(true);
                        break;
                    case Rotation:
                        reg = isStandFMVersion ? RotationStandFM : Rotation;
                        ((ModbusMapStand) reg).setSyncWriteRead(true);
                        break;
                    case FanTurnOn:
                        reg = isStandFMVersion ? FanTurnOnStandFM : FanTurnOn;
                        ((ModbusMapStand) reg).setSyncWriteRead(true);
                        break;
                    case PumpTurnOn:
                        reg = isStandFMVersion ? PumpTurnOnStandFM : PumpTurnOn;
                        break;
                    case PumpAutoMode:
                        reg = isStandFMVersion ? PumpAutoModeStandFM : PumpAutoMode;
                        break;
                }

                return super.add(reg, value);

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
                        switch (version) {
                            case STAND_FM:
                                Arrays.stream(readArray).filter(this::isStand).forEach(registerProvider::read);
                                break;
                            default:
                                Arrays.stream(readArray).filter(this::isNotStand).forEach(registerProvider::read);
                                break;
                        }

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
    public HighPressureSectionUpdater highPressureSectionUpdater(HighPressureSectionController highPressureSectionController,
                                                                 AdditionalSectionController additionalSectionController) {
        return new HighPressureSectionUpdater(highPressureSectionController, additionalSectionController);
    }

    @Bean
    @Autowired
    public InjectorSectionUpdater injectorSectionUpdater(VoltageController voltageController) {
        return new InjectorSectionUpdater(voltageController);
    }

    @Bean
    @Autowired
    public FlowMasterUpdater flowMasterUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                                               SettingsController settingsController, FirmwareVersion<FlowVersions> flowFirmwareVersion, Tests tests) {
        return new FlowMasterUpdater(flowController, injectorSectionController, settingsController, flowFirmwareVersion, tests);
    }

    @Bean
    @Autowired
    public FlowStreamUpdater flowStreamUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                                               SettingsController settingsController, FirmwareVersion<FlowVersions> flowFirmwareVersion, Tests tests) {
        return new FlowStreamUpdater(flowController, injectorSectionController, settingsController, flowFirmwareVersion, tests);
    }

    @Bean
    @Autowired
    public TestBenchSectionUpdater testBenchSectionUpdater(TestBenchSectionController testBenchSectionController,
                                                           VisualUtils visualUtils, FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        return new TestBenchSectionUpdater(testBenchSectionController, visualUtils, flowFirmwareVersion);
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
    public CurrentManufacturerObtainer currentManufacturerObtainer() {
        return new CurrentManufacturerObtainer();
    }

    @Bean
    public CurrentInjectorObtainer currentInjectorObtainer() {
        return new CurrentInjectorObtainer();
    }

    @Bean
    public CurrentInjectorTestsObtainer currentInjectorTestObtainer() {
        return new CurrentInjectorTestsObtainer();
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
    public Enabler enabler(MainSectionController mainSectionController, InjectorSectionController injectorSectionController,
                           RLCController rlcController, VoltageController voltageController) {
        return new Enabler(mainSectionController, injectorSectionController, rlcController, voltageController);
    }

    @Bean
    @Autowired
    public FlowResolver flowResolver(MainSectionController mainSectionController, SettingsController settingsController, FlowController flowController) {
        return new FlowResolver(mainSectionController.getTestListView().getSelectionModel(),
                settingsController.getFlowOutputDimensionsComboBox().getSelectionModel(), flowController);
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
    public VisualUtils visualUtils() {
        return new VisualUtils();
    }

    @Bean
    public FirmwareVersion<UltimaVersions> ultimaFirmwareVersion() {
        return new UltimaFirmwareVersion<>(WITH_A);
    }

    @Bean
    @Autowired
    public FirmwareVersion<FlowVersions> flowFirmwareVersion(TestBenchSectionController testBenchSectionController) {
        return new FlowFirmwareVersion<>(MASTER, testBenchSectionController.getTestBenchStartToggleButton());
    }

    @Bean
    @Autowired
    public FirmwareVersion<StandVersions> standFirmwareVersion(TestBenchSectionController testBenchSectionController) {
        return new StandFirmwareVersion<>(STAND, testBenchSectionController.getTestBenchStartToggleButton());
    }

    @Bean
    @Autowired
    public Measurements measurements(MainSectionController mainSectionController,
                                     TestBenchSectionController testBenchSectionController,
                                     HighPressureSectionController highPressureSectionController,
                                     InjectorSectionController injectorSectionController,
                                     FlowReportController flowReportController,
                                     Tests tests, Enabler enabler, FlowReport flowReport) {
        return new Measurements(mainSectionController, testBenchSectionController, highPressureSectionController,
                injectorSectionController, flowReportController, tests, enabler, flowReport);
    }

    @Bean
    @Autowired
    public FlowReport flowReport(FlowReportController flowReportController, MainSectionController mainSectionController,
                                 FlowController flowController, SettingsController settingsController) {
        return new FlowReport(flowReportController, mainSectionController, flowController, settingsController);
    }

    @Bean
    public Tests tests() {
        return new Tests();
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

}
