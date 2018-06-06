package fi.stardex.sisu.spring;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.charts.ChartTask;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.connect.ConnectProcessor;
import fi.stardex.sisu.connect.InetAddressWrapper;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.leds.ActiveLeds;
import fi.stardex.sisu.parts.PiezoCoilToggleGroup;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.updaters.HighPressureSectionUpdater;
import fi.stardex.sisu.ui.updaters.InjectorSectionUpdater;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.UltimaFirmwareVersion;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.LinkedList;
import java.util.List;

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
                                UltimaFirmwareVersion.setUltimaFirmwareVersion(UltimaFirmwareVersion.MULTI_CHANNEL_FIRMWARE_WO_FILTER);
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
            protected void setupFirmwareVersionListener() {
                // TODO: implement listener
            }
        };
    }

    @Bean
    @Autowired
    public RegisterProvider standRegisterProvider(ModbusConnect standModbusConnect) {
        return new RegisterProvider(standModbusConnect) {
            @Override
            protected void setupFirmwareVersionListener() {
                // TODO: implement listener
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor ultimaModbusWriter(List<Updater> updatersList, RegisterProvider ultimaRegisterProvider) {
        final List<Updater> updaters = new LinkedList<>();
        updatersList.forEach(updater -> {
            Module module = updater.getClass().getAnnotation(Module.class);
            for (Device device : module.value()) {
                if (device == Device.ULTIMA)
                    updaters.add(updater);
            }
        });
        return new ModbusRegisterProcessor(ultimaRegisterProvider, ModbusMapUltima.values(), "Ultima register processor", updaters);
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor flowModbusWriter(RegisterProvider flowRegisterProvider) {
        return new ModbusRegisterProcessor(flowRegisterProvider,  null, "Flow register processor", null);
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor standModbusWriter(RegisterProvider standRegisterProvider) {
        return new ModbusRegisterProcessor(standRegisterProvider,  null, "Stand register processor", null);
    }

    @Bean
    @Autowired
    public HighPressureSectionUpdater highPressureSectionUpdater(HighPressureSectionController highPressureSectionController) {
        return new HighPressureSectionUpdater(highPressureSectionController);
    }

    @Bean
    @Autowired
    public InjectorSectionUpdater injectorSectionUpdater(VoltageController voltageController, InjectorSectionController injectorSectionController) {
        return new InjectorSectionUpdater(voltageController, injectorSectionController);
    }

    @Bean
    @Autowired
    public TimerTasksManager timerTasksManager(ApplicationContext applicationContext) {
        return new TimerTasksManager(applicationContext);
    }

    @Bean
    @Scope("prototype")
    @Autowired
    public ChartTask chartTaskOne(VoltageController voltageController, ModbusRegisterProcessor ultimaModbusWriter,
                                  PiezoCoilToggleGroup piezoCoilToggleGroup) {
        return new ChartTask(ultimaModbusWriter, piezoCoilToggleGroup) {
            @Override
            public ModbusMapUltima getCurrentGraph() {
                return ModbusMapUltima.Current_graph1;
            }

            @Override
            public ModbusMapUltima getCurrentGraphFrameNum() {
                return ModbusMapUltima.Current_graph1_frame_num;
            }

            @Override
            public ModbusMapUltima getCurrentGraphUpdate() {
                return ModbusMapUltima.Current_graph1_update;
            }

            @Override
            protected ObservableList<XYChart.Data<Double, Double>> getData() {
                return voltageController.getData1();
            }
        };
    }

    @Bean
    @Scope("prototype")
    @Autowired
    public ChartTask chartTaskTwo(VoltageController voltageController, ModbusRegisterProcessor ultimaModbusWriter,
                                  PiezoCoilToggleGroup piezoCoilToggleGroup) {
        return new ChartTask(ultimaModbusWriter, piezoCoilToggleGroup) {
            @Override
            public ModbusMapUltima getCurrentGraph() {
                return ModbusMapUltima.Current_graph2;
            }

            @Override
            public ModbusMapUltima getCurrentGraphFrameNum() {
                return ModbusMapUltima.Current_graph2_frame_num;
            }

            @Override
            public ModbusMapUltima getCurrentGraphUpdate() {
                return ModbusMapUltima.Current_graph2_update;
            }

            @Override
            protected ObservableList<XYChart.Data<Double, Double>> getData() {
                return voltageController.getData2();
            }
        };
    }

    @Bean
    @Scope("prototype")
    @Autowired
    public ChartTask chartTaskThree(VoltageController voltageController, ModbusRegisterProcessor ultimaModbusWriter,
                                    PiezoCoilToggleGroup piezoCoilToggleGroup) {
        return new ChartTask(ultimaModbusWriter, piezoCoilToggleGroup) {
            @Override
            public ModbusMapUltima getCurrentGraph() {
                return ModbusMapUltima.Current_graph3;
            }

            @Override
            public ModbusMapUltima getCurrentGraphFrameNum() {
                return ModbusMapUltima.Current_graph3_frame_num;
            }

            @Override
            public ModbusMapUltima getCurrentGraphUpdate() {
                return ModbusMapUltima.Current_graph3_update;
            }

            @Override
            protected ObservableList<XYChart.Data<Double, Double>> getData() {
                return voltageController.getData3();
            }
        };
    }

    @Bean
    @Scope("prototype")
    @Autowired
    public ChartTask chartTaskFour(VoltageController voltageController, ModbusRegisterProcessor ultimaModbusWriter,
                                   PiezoCoilToggleGroup piezoCoilToggleGroup) {
        return new ChartTask(ultimaModbusWriter, piezoCoilToggleGroup) {
            @Override
            public ModbusMapUltima getCurrentGraph() {
                return ModbusMapUltima.Current_graph4;
            }

            @Override
            public ModbusMapUltima getCurrentGraphFrameNum() {
                return ModbusMapUltima.Current_graph4_frame_num;
            }

            @Override
            public ModbusMapUltima getCurrentGraphUpdate() {
                return ModbusMapUltima.Current_graph4_update;
            }

            @Override
            protected ObservableList<XYChart.Data<Double, Double>> getData() {
                return voltageController.getData4();
            }
        };
    }

    @Bean
    @Autowired
    public PiezoCoilToggleGroup piezoCoilToggleGroup(InjectorSectionController injectorSectionController) {
        return new PiezoCoilToggleGroup(injectorSectionController);
    }

    @Bean
    @Autowired
    public ActiveLeds activeLeds(InjectorSectionController injectorSectionController) {
        return new ActiveLeds(injectorSectionController.getLedControllers());
    }
}
