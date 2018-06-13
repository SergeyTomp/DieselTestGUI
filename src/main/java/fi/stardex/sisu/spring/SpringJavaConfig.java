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
import fi.stardex.sisu.leds.ActiveLeds;
import fi.stardex.sisu.parts.PiezoCoilToggleGroup;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.updaters.FlowUpdater;
import fi.stardex.sisu.ui.updaters.HighPressureSectionUpdater;
import fi.stardex.sisu.ui.updaters.InjectorSectionUpdater;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import fi.stardex.sisu.version.UltimaFirmwareVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
                        int firmwareVersionNumber = (int) read(ModbusMapFlow.FlowMeterVersion);
                        switch (firmwareVersionNumber) {
                            case 43724:
                                FlowFirmwareVersion.setFlowFirmwareVersion(FlowFirmwareVersion.FLOW_MASTER);
                                break;
                            case 43707:
                                FlowFirmwareVersion.setFlowFirmwareVersion(FlowFirmwareVersion.FLOW_STREAM);
                                break;
                            default:
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
            protected void setupFirmwareVersionListener() {
                // TODO: implement listener
            }
        };
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor ultimaModbusWriter(List<Updater> updatersList, RegisterProvider ultimaRegisterProvider) {
        return new ModbusRegisterProcessor(ultimaRegisterProvider, ModbusMapUltima.values(),
                "Ultima register processor", addUpdaters(updatersList, Device.ULTIMA));
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor flowModbusWriter(List<Updater> updatersList, RegisterProvider flowRegisterProvider) {
        return new ModbusRegisterProcessor(flowRegisterProvider, ModbusMapFlow.values(),
                "Flow register processor", addUpdaters(updatersList, Device.MODBUS_FLOW));
    }

    @Bean
    @Autowired
    public ModbusRegisterProcessor standModbusWriter(RegisterProvider standRegisterProvider) {
        return new ModbusRegisterProcessor(standRegisterProvider, null, "Stand register processor", null);
    }

    @Bean
    @Autowired
    public HighPressureSectionUpdater highPressureSectionUpdater(HighPressureSectionController highPressureSectionController) {
        return new HighPressureSectionUpdater(highPressureSectionController);
    }

    @Bean
    @Autowired
    public InjectorSectionUpdater injectorSectionUpdater(VoltageController voltageController, FirmwareDataConverter firmwareDataConverter) {
        return new InjectorSectionUpdater(voltageController, firmwareDataConverter);
    }

    // TODO: Test bean
    @Bean
    public FlowUpdater flowUpdater() {
        return new FlowUpdater();
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
    @Autowired
    public PiezoCoilToggleGroup piezoCoilToggleGroup(InjectorSectionController injectorSectionController) {
        return new PiezoCoilToggleGroup(injectorSectionController);
    }

    @Bean
    @Autowired
    public ActiveLeds activeLeds(InjectorSectionController injectorSectionController) {
        return new ActiveLeds(injectorSectionController.getLedControllers());
    }

    @Bean
    public FirmwareDataConverter firmwareDataConverter() {
        return new FirmwareDataConverter();
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
