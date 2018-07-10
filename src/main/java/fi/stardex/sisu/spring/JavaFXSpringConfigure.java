package fi.stardex.sisu.spring;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.RootLayoutController;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import fi.stardex.sisu.ui.controllers.cr.CRSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditInjectorDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditTestDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditVOAPDialogController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.StardexVersion;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ResourceBundle;

@Configuration
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigure {

    private final I18N i18N;
    private final UTF8Control utf8Control;

    private final Logger logger = LoggerFactory.getLogger(JavaFXSpringConfigure.class);

    public JavaFXSpringConfigure (I18N i18N) {
        this.i18N = i18N;
        this.utf8Control = new UTF8Control();
    }

    @Bean
    public ViewHolder rootLayout() {
        return loadView("/fxml/RootLayout.fxml");
    }

    @Bean
    public RootLayoutController rootLayoutController() {
        return (RootLayoutController) rootLayout().getController();
    }

    @Bean
    public ViewHolder mainSection() {
        return loadView("/fxml/sections/Main/MainSection.fxml");
    }

    @Bean
    @Autowired
    public MainSectionController mainSectionController(ApplicationConfigHandler applicationConfigHandler,
                                                       ApplicationAppearanceChanger applicationAppearanceChanger,
                                                       @Lazy ModbusRegisterProcessor flowModbusWriter) {
        return (MainSectionController) mainSection().getController();
    }

    @Bean
    public ViewHolder crSection() {
        return loadView("/fxml/sections/CR/CRSection.fxml");
    }

    @Bean
    public CRSectionController crSectionController() {
        return (CRSectionController) crSection().getController();
    }

    @Bean
    @Autowired
    public TestBenchSectionController testBenchController(CRSectionController crSectionController) {
        return crSectionController.getTestBenchSectionController();
    }

    @Bean
    @Autowired
    public HighPressureSectionController highPressureSectionController(CRSectionController crSectionController) {
        return crSectionController.getHighPressureSectionController();
    }

    @Bean
    @Autowired
    public InjectorSectionController injectorSectionController(SettingsController settingsController,
                                                               @Lazy ModbusRegisterProcessor ultimaModbusWriter,
                                                               TimerTasksManager timerTasksManager) {
        InjectorSectionController injectorSectionController = crSectionController().getInjectorSectionController();
        injectorSectionController.setSettingsController(settingsController);
        injectorSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        injectorSectionController.setTimerTasksManager(timerTasksManager);
        return injectorSectionController;
    }

    @Bean
    public ViewHolder uisSection() {
        return loadView("/fxml/sections/UIS/UISSection.fxml");
    }

    @Bean
    public ViewHolder additionalSection() {
        return loadView("/fxml/sections/Additional/AdditionalSection.fxml");
    }

    @Bean
    public AdditionalSectionController additionalSectionController() {
        return (AdditionalSectionController) additionalSection().getController();
    }

    @Bean
    @Autowired
    public FlowController flowController(AdditionalSectionController additionalSectionController) {
        return additionalSectionController.getFlowController();
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery1Controller(FlowController flowController, Rescaler deliveryRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerDelivery1Controller = flowController.getBeakerDelivery1Controller();
        beakerDelivery1Controller.setTextField(flowController.getDelivery1TextField());
        beakerDelivery1Controller.setRescaler(deliveryRescaler);
        beakerDelivery1Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerDelivery1Controller.setName("Delivery1");
        return beakerDelivery1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery2Controller(FlowController flowController, Rescaler deliveryRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerDelivery2Controller = flowController.getBeakerDelivery2Controller();
        beakerDelivery2Controller.setTextField(flowController.getDelivery2TextField());
        beakerDelivery2Controller.setRescaler(deliveryRescaler);
        beakerDelivery2Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerDelivery2Controller.setName("Delivery2");
        return beakerDelivery2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery3Controller(FlowController flowController, Rescaler deliveryRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerDelivery3Controller = flowController.getBeakerDelivery3Controller();
        beakerDelivery3Controller.setTextField(flowController.getDelivery3TextField());
        beakerDelivery3Controller.setRescaler(deliveryRescaler);
        beakerDelivery3Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerDelivery3Controller.setName("Delivery3");
        return beakerDelivery3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery4Controller(FlowController flowController, Rescaler deliveryRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerDelivery4Controller = flowController.getBeakerDelivery4Controller();
        beakerDelivery4Controller.setTextField(flowController.getDelivery4TextField());
        beakerDelivery4Controller.setRescaler(deliveryRescaler);
        beakerDelivery4Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerDelivery4Controller.setName("Delivery4");
        return beakerDelivery4Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow1Controller(FlowController flowController, Rescaler backFlowRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerBackFlow1Controller = flowController.getBeakerBackFlow1Controller();
        beakerBackFlow1Controller.setTextField(flowController.getBackFlow1TextField());
        beakerBackFlow1Controller.setRescaler(backFlowRescaler);
        beakerBackFlow1Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerBackFlow1Controller.setName("Backflow1");
        return beakerBackFlow1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow2Controller(FlowController flowController, Rescaler backFlowRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerBackFlow2Controller = flowController.getBeakerBackFlow2Controller();
        beakerBackFlow2Controller.setTextField(flowController.getBackFlow2TextField());
        beakerBackFlow2Controller.setRescaler(backFlowRescaler);
        beakerBackFlow2Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerBackFlow2Controller.setName("Backflow2");
        return beakerBackFlow2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow3Controller(FlowController flowController, Rescaler backFlowRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerBackFlow3Controller = flowController.getBeakerBackFlow3Controller();
        beakerBackFlow3Controller.setTextField(flowController.getBackFlow3TextField());
        beakerBackFlow3Controller.setRescaler(backFlowRescaler);
        beakerBackFlow3Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerBackFlow3Controller.setName("Backflow3");
        return beakerBackFlow3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow4Controller(FlowController flowController, Rescaler backFlowRescaler,
                                                      FirmwareDataConverter firmwareDataConverter) {
        BeakerController beakerBackFlow4Controller = flowController.getBeakerBackFlow4Controller();
        beakerBackFlow4Controller.setTextField(flowController.getBackFlow4TextField());
        beakerBackFlow4Controller.setRescaler(backFlowRescaler);
        beakerBackFlow4Controller.setFirmwareDataConverter(firmwareDataConverter);
        beakerBackFlow4Controller.setName("Backflow4");
        return beakerBackFlow4Controller;
    }

    @Bean
    @Autowired
    public ConnectionController connectionController(AdditionalSectionController additionalSectionController,
                                                     ApplicationConfigHandler applicationConfigHandler) {
        ConnectionController connectionController = additionalSectionController.getConnectionController();
        connectionController.setApplicationConfigHandler(applicationConfigHandler);
        return connectionController;
    }

    @Bean
    @Autowired
    public VoltageController voltageController(AdditionalSectionController additionalSectionController,
                                               FirmwareDataConverter firmwareDataConverter,
                                               InjectorSectionController injectorSectionController) {
        VoltageController voltageController = additionalSectionController.getVoltageController();
        voltageController.setVoltAmpereProfileDialog(voltAmpereProfileDialog());
        voltageController.setParentController(additionalSectionController);
        voltageController.setInjectorSectionController(injectorSectionController);
        voltageController.setFirmwareDataConverter(firmwareDataConverter);
        return voltageController;
    }

    @Bean
    @Autowired
    public DelayController delayController(AdditionalSectionController additionalSectionController) {
        return additionalSectionController.getDelayController();
    }

    @Bean
    @Autowired
    public SettingsController settingsController(AdditionalSectionController additionalSectionController) {
        SettingsController settingsController = additionalSectionController.getSettingsController();
        settingsController.setI18N(i18N);
        return settingsController;
    }

    @Bean(value = "voltAmpereProfileDialog")
    public ViewHolder voltAmpereProfileDialog() {
        return loadView("/fxml/sections/Additional/dialogs/voltAmpereProfileDialog.fxml");
    }

    @Bean
    @Autowired
    public VoltAmpereProfileController voltAmpereProfileController(ModbusRegisterProcessor ultimaModbusWriter,
                                                                   InjectorSectionController injectorSectionController,
                                                                   VoltageController voltageController,
                                                                   FirmwareDataConverter firmwareDataConverter) {
        VoltAmpereProfileController voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog().getController();
        voltAmpereProfileController.setUltimaModbusWriter(ultimaModbusWriter);
        voltAmpereProfileController.setInjectorSectionController(injectorSectionController);
        voltAmpereProfileController.setWidthSpinner(injectorSectionController.getWidthCurrentSignal());
        voltAmpereProfileController.setVoltageController(voltageController);
        voltAmpereProfileController.setFirmwareDataConverter(firmwareDataConverter);
        return voltAmpereProfileController;
    }

    @Bean
    @Autowired
    public ApplicationAppearanceChanger applicationAppearanceChanger(ViewHolder crSection, ViewHolder uisSection,
                                                                     ViewHolder additionalSection, RootLayoutController rootLayoutController) {
        return new ApplicationAppearanceChanger(crSection.getView(), uisSection.getView(),
                additionalSection.getView(), rootLayoutController.getSectionLayout());
    }

    @Bean
    @Autowired
    public StatusBarWrapper statusBar(Devices devices) {
        return new StatusBarWrapper(devices, "Ready", "Device not connected", StardexVersion.VERSION);
    }

    @Bean
    public ViewHolder manufacturerMenuDialog() {
        return loadView("/fxml/dialogs/ManufacturerMenuDialog.fxml");
    }

    @Bean
    public ManufacturerMenuDialogController manufacturerMenuDialogController() {
        return (ManufacturerMenuDialogController) manufacturerMenuDialog().getController();
    }

    @Bean
    public ViewHolder newEditVOAPDialog() {
        return loadView("/fxml/dialogs/NewEditVOAPDialog.fxml");
    }

    @Bean
    public NewEditVOAPDialogController newEditVOAPDialogController() {
        return (NewEditVOAPDialogController) newEditVOAPDialog().getController();
    }

    @Bean
    public ViewHolder newEditInjectorDialog() {
        return loadView("/fxml/dialogs/NewEditInjectorDialog.fxml");
    }

    @Bean
    public NewEditInjectorDialogController newEditInjectorDialogController() {
        return (NewEditInjectorDialogController) newEditInjectorDialog().getController();
    }

    @Bean
    public ViewHolder newEditTestDialog() {
        return loadView("/fxml/dialogs/NewEditTestDialog.fxml");
    }

    @Bean
    public NewEditTestDialogController newEditTestDialogController() {
        return (NewEditTestDialogController) newEditTestDialog().getController();
    }

    private ViewHolder loadView(String url) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url), ResourceBundle.getBundle("properties.labels", i18N.getLocale(), utf8Control));
        ViewHolder viewHolder = new ViewHolder();
        try {
            viewHolder.setView(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Exception while load view {}", url, e);
        }
        viewHolder.setController(fxmlLoader.getController());
        return viewHolder;
    }
}
