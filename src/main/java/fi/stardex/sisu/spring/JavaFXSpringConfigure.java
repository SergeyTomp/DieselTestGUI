package fi.stardex.sisu.spring;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.parts.PiezoCoilToggleGroup;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.RootLayoutController;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.CRSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.FirmwareDataConverter;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
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
    public MainSectionController mainSectionController() {
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
    public ConnectionController connectionController(AdditionalSectionController additionalSectionController) {
        return additionalSectionController.getConnectionController();
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
        return additionalSectionController.getSettingsController();
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
                                                                   PiezoCoilToggleGroup piezoCoilToggleGroup,
                                                                   FirmwareDataConverter firmwareDataConverter) {
        VoltAmpereProfileController voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog().getController();
        voltAmpereProfileController.setUltimaModbusWriter(ultimaModbusWriter);
        voltAmpereProfileController.setPiezoCoilToggleGroup(piezoCoilToggleGroup);
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
