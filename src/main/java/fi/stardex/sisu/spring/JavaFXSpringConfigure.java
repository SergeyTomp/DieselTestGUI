package fi.stardex.sisu.spring;

import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.RootLayoutController;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.cr.CRSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.Enabler;
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
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ResourceBundle;

@Configuration
public class JavaFXSpringConfigure {

    private final I18N i18N;
    private final UTF8Control utf8Control;

    private final Logger logger = LoggerFactory.getLogger(JavaFXSpringConfigure.class);

    @Autowired
    public JavaFXSpringConfigure(I18N i18N) {
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
    public ViewHolder testBench() {
        return loadView("/fxml/sections/CR/TestBenchSection.fxml");
    }

    @Bean
    public TestBenchController testBenchController() {
        return (TestBenchController) testBench().getController();
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
