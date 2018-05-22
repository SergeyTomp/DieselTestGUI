package fi.stardex.sisu.spring;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.charts.ChartTask;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.connect.ConnectProcessor;
import fi.stardex.sisu.connect.InetAddressWrapper;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.listeners.FrequencySpinnerListener;
import fi.stardex.sisu.listeners.InjectorConfigComboBoxListener;
import fi.stardex.sisu.listeners.InjectorTypeListener;
import fi.stardex.sisu.listeners.LedBeakerListener;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.updaters.HighPressureSectionUpdater;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.storage.CurrentVAPStorage;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.wrappers.LedControllerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
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
        return new RegisterProvider(ultimaModbusConnect);
    }

    @Bean
    @Autowired
    public RegisterProvider flowRegisterProvider(ModbusConnect flowModbusConnect) {
        return new RegisterProvider(flowModbusConnect);
    }

    @Bean
    @Autowired
    public RegisterProvider standRegisterProvider(ModbusConnect standModbusConnect) {
        return new RegisterProvider(standModbusConnect);
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
        return new ModbusRegisterProcessor(flowRegisterProvider, null, "Flow register processor", null);
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
    public CurrentVAPStorage currentVAPStorage(ModbusRegisterProcessor ultimaModbusWriter) {
        return new CurrentVAPStorage(ultimaModbusWriter);
    }

    @Bean
    @Autowired
    public TimerTasksManager timerTasksManager(InjectorSectionController injectorSectionController) {
        return new TimerTasksManager(injectorSectionController);
    }

    @Bean
    @Scope("prototype")
    @Autowired
    public ChartTask chartTask(VoltageController voltageController, ModbusRegisterProcessor ultimaModbusWriter, InjectorSectionController injectorSectionController) {
        return new ChartTask(voltageController, ultimaModbusWriter, injectorSectionController);
    }

    @Bean
    @Autowired
    public FrequencySpinnerListener frequencySpinnerListener(InjectorSectionController injectorSectionController,
                                                             InjectorSwitchManager injectorSwitchManager) {
        return new FrequencySpinnerListener(injectorSectionController, injectorSwitchManager);
    }

    @Bean
    @Autowired
    public InjectorTypeListener injectorTypeListener(InjectorSectionController injectorSectionController,
                                                     InjectorSwitchManager injectorSwitchManager) {
        return new InjectorTypeListener(injectorSectionController, injectorSwitchManager);
    }

    @Bean
    @Autowired
    public LedBeakerListener ledBeaker1Listener(LedController led1Controller, InjectorSwitchManager injectorSwitchManager) {
        LedBeakerListener ledBeaker1Listener = new LedBeakerListener(led1Controller, 0);
        ledBeaker1Listener.setManager(injectorSwitchManager);
        return ledBeaker1Listener;
    }

    @Bean
    @Autowired
    public LedBeakerListener ledBeaker2Listener(LedController led2Controller, InjectorSwitchManager injectorSwitchManager) {
        LedBeakerListener ledBeaker2Listener = new LedBeakerListener(led2Controller, 1);
        ledBeaker2Listener.setManager(injectorSwitchManager);
        return ledBeaker2Listener;
    }

    @Bean
    @Autowired
    public LedBeakerListener ledBeaker3Listener(LedController led3Controller, InjectorSwitchManager injectorSwitchManager) {
        LedBeakerListener ledBeaker3Listener = new LedBeakerListener(led3Controller, 2);
        ledBeaker3Listener.setManager(injectorSwitchManager);
        return ledBeaker3Listener;
    }

    @Bean
    @Autowired
    public LedBeakerListener ledBeaker4Listener(LedController led4Controller, InjectorSwitchManager injectorSwitchManager) {
        LedBeakerListener ledBeaker4Listener = new LedBeakerListener(led4Controller, 3);
        ledBeaker4Listener.setManager(injectorSwitchManager);
        return ledBeaker4Listener;
    }

    @Bean
    @Autowired
    public InjectorConfigComboBoxListener injectorConfigComboBoxListener(InjectorSectionController injectorSectionController,
                                                                         SettingsController settingsController) {
        return new InjectorConfigComboBoxListener(injectorSectionController, settingsController);
    }

    @Bean
    @Autowired
    public LedControllerWrapper ledControllerWrapper(List<LedController> ledControllers) {
        return new LedControllerWrapper(ledControllers);
    }

    @Bean
    @Autowired
    public InjectorSwitchManager injectorSwitchManager(ModbusRegisterProcessor ultimaModbusWriter,
                                                       LedControllerWrapper ledControllerWrapper,
                                                       InjectorSectionController injectorSectionController,
                                                       SettingsController settingsController) {
        return new InjectorSwitchManager(ultimaModbusWriter, ledControllerWrapper, injectorSectionController, settingsController);
    }

}
