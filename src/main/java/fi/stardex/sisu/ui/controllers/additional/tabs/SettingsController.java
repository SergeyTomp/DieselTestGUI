package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.settings.*;
import fi.stardex.sisu.states.FastCodingState;
import fi.stardex.sisu.states.InjectorTypeToggleState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;

public class SettingsController {

    private I18N i18N;
    @FXML private GridPane settingsGridPane;
    @FXML private Label regulatorsConfigLabel;
    @FXML private Label injectorsConfigLabel;
    @FXML private Label languagesLabel;
    @FXML private Label flowOutputDimensionLabel;
    @FXML private Label pressureSensorLabel;

    @FXML private DimasGuiEditionController dimasGuiEditionController;
    @FXML private FastCodingController fastCodingController;
    @FXML private FlowViewController flowViewController;
    @FXML private InjConfigurationController injConfigurationController;
    @FXML private LanguageController languageController;
    @FXML private PressureSensorController pressureSensorController;
    @FXML private RegulatorsQTYController regulatorsQTYController;
    @FXML private InstantFlowController instantFlowController;

    public DimasGuiEditionController getDimasGuiEditionController() {
        return dimasGuiEditionController;
    }

    public FastCodingController getFastCodingController() {
        return fastCodingController;
    }

    public FlowViewController getFlowViewController() {
        return flowViewController;
    }

    public InjConfigurationController getInjConfigurationController() {
        return injConfigurationController;
    }

    public LanguageController getLanguageController() {
        return languageController;
    }

    public PressureSensorController getPressureSensorController() {
        return pressureSensorController;
    }

    public RegulatorsQTYController getRegulatorsQTYController() {
        return regulatorsQTYController;
    }

    public InstantFlowController getInstantFlowController() {
        return instantFlowController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init() {
        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        flowOutputDimensionLabel.textProperty().bind(i18N.createStringBinding("settings.FlowOutputDimension.ComboBox"));
    }
}
