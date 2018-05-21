package fi.stardex.sisu.spring;

import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.RootLayoutController;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.CRSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.Enabler;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import fi.stardex.sisu.util.storage.CurrentVAPStorage;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.StardexVersion;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.ResourceBundle;

@Configuration
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
    public InjectorSectionController injectorSectionController(CRSectionController crSectionController) {
        return crSectionController.getInjectorSectionController();
    }

    @Bean
    @Autowired
    public LedController led1Controller(InjectorSectionController injectorSectionController) {
        LedController ledBeaker1Controller = injectorSectionController.getLedBeaker1Controller();
        ledBeaker1Controller.setNumber(1);
        ledBeaker1Controller.setInjectorSectionController(injectorSectionController);
        return ledBeaker1Controller;
    }

    @Bean
    @Autowired
    public LedController led2Controller(InjectorSectionController injectorSectionController) {
        LedController ledBeaker2Controller = injectorSectionController.getLedBeaker2Controller();
        ledBeaker2Controller.setNumber(2);
        ledBeaker2Controller.setInjectorSectionController(injectorSectionController);
        return ledBeaker2Controller;
    }

    @Bean
    @Autowired
    public LedController led3Controller(InjectorSectionController injectorSectionController) {
        LedController ledBeaker3Controller = injectorSectionController.getLedBeaker3Controller();
        ledBeaker3Controller.setNumber(3);
        ledBeaker3Controller.setInjectorSectionController(injectorSectionController);
        return ledBeaker3Controller;
    }

    @Bean
    @Autowired
    public LedController led4Controller(InjectorSectionController injectorSectionController) {
        LedController ledBeaker4Controller = injectorSectionController.getLedBeaker4Controller();
        ledBeaker4Controller.setNumber(4);
        ledBeaker4Controller.setInjectorSectionController(injectorSectionController);
        return ledBeaker4Controller;
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
    public ConnectionController connectionController() {
        return additionalSectionController().getConnectionController();
    }

    @Bean
    public VoltageController voltageController() {
        return additionalSectionController().getVoltageController();
    }

    @Bean
    public DelayController delayController() {
        return additionalSectionController().getDelayController();
    }

    @Bean(value = "voltAmpereProfileDialog")
    public ViewHolder voltAmpereProfileDialog() {
        return loadView("/fxml/sections/Additional/dialogs/voltAmpereProfileDialog.fxml");
    }

    @Bean
    public VoltAmpereProfileController voltAmpereProfileController() {
        return (VoltAmpereProfileController) voltAmpereProfileDialog().getController();
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
    public Enabler enabler() {
        return new Enabler();

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
