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
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.updaters.*;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.converters.FlowResolver;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentInjectorTestsObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import fi.stardex.sisu.util.rescalers.BackFlowRescaler;
import fi.stardex.sisu.util.rescalers.DeliveryRescaler;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import fi.stardex.sisu.version.StandFirmwareVersion;
import fi.stardex.sisu.version.UltimaFirmwareVersion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Configuration
@Import(JavaFXSpringConfigure.class)
@EnableScheduling
public class SpringJavaConfig {

    private static final Logger logger = LoggerFactory.getLogger(SpringJavaConfig.class);

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
    public ApplicationConfigHandler applicationConfigHandler() {
        return new ApplicationConfigHandler();
    }

    @Bean
    @Autowired
    public I18N i18N(ApplicationConfigHandler applicationConfigHandler) {
        return new I18N(applicationConfigHandler);
    }

    @Bean
    @Autowired
    public RegisterProvider ultimaRegisterProvider(ModbusConnect ultimaModbusConnect) {
        return new RegisterProvider(ultimaModbusConnect) {
            @Override
            public void setupFirmwareVersionListener() {
                ultimaModbusConnect.connectedPropertyProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        int firmwareVersionNumber = (int) read(ModbusMapUltima.FirmwareVersion);
                        switch (firmwareVersionNumber) {
                            case 221:
                                UltimaFirmwareVersion.setUltimaFirmwareVersion(UltimaFirmwareVersion.MULTI_CHANNEL_FIRMWARE_WO_ACTIVATION);
                                break;
                            case 238:
                                UltimaFirmwareVersion.setUltimaFirmwareVersion(UltimaFirmwareVersion.MULTI_CHANNEL_FIRMWARE_W_ACTIVATION);
                                break;
                            case 241:
                                UltimaFirmwareVersion.setUltimaFirmwareVersion(UltimaFirmwareVersion.MULTI_CHANNEL_FIRMWARE_WO_FILTER);
                                break;
                            default:
                                logger.error("Wrong Ultima firmware version!");
                                break;
                        }
                    }
                });
            }
        };
    }

    @Bean
    @Autowired
    public RegisterProvider flowRegisterProvider(ModbusConnect flowModbusConnect) {
        return new RegisterProvider(flowModbusConnect) {
            @Override
            public void setupFirmwareVersionListener() {
                flowModbusConnect.connectedPropertyProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        int firmwareVersionNumber = (int) read(ModbusMapFlow.FirmwareVersion);
                        switch (firmwareVersionNumber) {
                            case 0xAACC:
                                FlowFirmwareVersion.setFlowFirmwareVersion(FlowFirmwareVersion.FLOW_MASTER);
                                break;
                            case 0xAABB:
                                FlowFirmwareVersion.setFlowFirmwareVersion(FlowFirmwareVersion.FLOW_STREAM);
                                break;
                            default:
                                FlowFirmwareVersion.setFlowFirmwareVersion(null);
                                logger.error("Wrong Flow firmware version!");
                                break;
                        }
                    }
                });
            }
        };
    }

    @Bean
    @Autowired
    public RegisterProvider standRegisterProvider(ModbusConnect standModbusConnect) {
        return new RegisterProvider(standModbusConnect) {
            @Override
            public void setupFirmwareVersionListener() {
                standModbusConnect.connectedPropertyProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        int firmwareVersionNumber = (int) read(ModbusMapStand.FirmwareVersion);
                        switch (firmwareVersionNumber) {
                            case 0x1122:
                                StandFirmwareVersion.setStandFirmwareVersion(StandFirmwareVersion.STAND);
                                break;
                            default:
                                StandFirmwareVersion.setStandFirmwareVersion(null);
                                logger.error("Wrong Stand firmware version!");
                                break;
                        }
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
                loopThread.start();
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor flowModbusWriter(List<Updater> updatersList, RegisterProvider flowRegisterProvider) {
        return new ModbusRegisterProcessor(flowRegisterProvider, ModbusMapFlow.values()) {
            @Override
            protected void initThread() {
                List<Updater> updaters = addUpdaters(updatersList, Device.MODBUS_FLOW);
                Thread loopThread = new Thread(new ProcessExecutor() {
                    @Override
                    protected void updateAll() {
                        for (Updater updater : updaters) {
                            if ((updater instanceof FlowMasterUpdater) && (FlowFirmwareVersion.getFlowFirmwareVersion() == FlowFirmwareVersion.FLOW_MASTER))
                                Platform.runLater(updater);
                            else if ((updater instanceof FlowStreamUpdater) && (FlowFirmwareVersion.getFlowFirmwareVersion() == FlowFirmwareVersion.FLOW_STREAM))
                                Platform.runLater(updater);
                        }
                    }
                });
                setLoopThread(loopThread);
                loopThread.setName("Flow register processor");
                loopThread.start();
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor standModbusWriter(List<Updater> updatersList, RegisterProvider standRegisterProvider) {
        return new ModbusRegisterProcessor(standRegisterProvider, ModbusMapStand.values()) {

            @Override
            public boolean add(ModbusMap reg, Object value) {
                if (reg == ModbusMapStand.TargetRPM)
                    ((ModbusMapStand) reg).setSyncWriteRead(true);
                else if (reg == ModbusMapStand.RotationDirection)
                    ((ModbusMapStand) reg).setSyncWriteRead(true);
                else if (reg == ModbusMapStand.Rotation)
                    ((ModbusMapStand) reg).setSyncWriteRead(true);
                else if (reg == ModbusMapStand.FanTurnOn)
                    ((ModbusMapStand) reg).setSyncWriteRead(true);
                return super.add(reg, value);
            }

            @Override
            protected void initThread() {
                List<Updater> updaters = addUpdaters(updatersList, Device.MODBUS_STAND);
                Thread loopThread = new Thread(new ProcessExecutor() {
                    @Override
                    protected void updateAll() {
                        updaters.forEach(Platform::runLater);
                    }
                });
                setLoopThread(loopThread);
                loopThread.setName("Stand register processor");
                loopThread.start();
            }
        };
    }

    @Bean
    @Autowired
    public HighPressureSectionUpdater highPressureSectionUpdater(HighPressureSectionController highPressureSectionController) {
        return new HighPressureSectionUpdater(highPressureSectionController);
    }

    @Bean
    @Autowired
    public InjectorSectionUpdater injectorSectionUpdater(VoltageController voltageController, DataConverter dataConverter) {
        return new InjectorSectionUpdater(voltageController, dataConverter);
    }

    @Bean
    @Autowired
    public FlowMasterUpdater flowMasterUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                                               SettingsController settingsController, DataConverter dataConverter) {
        return new FlowMasterUpdater(flowController, injectorSectionController, settingsController, dataConverter);
    }

    @Bean
    @Autowired
    public FlowStreamUpdater flowStreamUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                                               SettingsController settingsController, DataConverter dataConverter) {
        return new FlowStreamUpdater(flowController, injectorSectionController, settingsController, dataConverter);
    }

    @Bean
    @Autowired
    public TestBenchSectionUpdater testBenchSectionUpdater(TestBenchSectionController testBenchSectionController) {
        return new TestBenchSectionUpdater(testBenchSectionController);
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
    public DataConverter dataConverter() {
        return new DataConverter();
    }

    @Bean
    public Rescaler deliveryRescaler() {
        return new DeliveryRescaler();
    }

    @Bean
    public Rescaler backFlowRescaler() {
        return new BackFlowRescaler();
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
    public Enabler enabler(MainSectionController mainSectionController) {
        return new Enabler(mainSectionController);
    }

    @Bean
    @Autowired
    public FlowResolver flowResolver(MainSectionController mainSectionController, SettingsController settingsController,
                                     FlowController flowController, DataConverter dataConverter) {
        return new FlowResolver(mainSectionController, settingsController, flowController, dataConverter);
    }

    @Bean
    @Autowired
    public CSVSUpdater csvsUpdater(ManufacturerRepository manufacturerRepository,
                                   VoltAmpereProfileRepository voltAmpereProfileRepository,
                                   InjectorsRepository injectorsRepository,
                                   InjectorTestRepository injectorTestRepository) {
        return new CSVSUpdater(manufacturerRepository, voltAmpereProfileRepository, injectorsRepository, injectorTestRepository);
    }

}
