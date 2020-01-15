package fi.stardex.sisu.ui.controllers.cr.tabs.settings;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.settings.*;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER_DF;

public class SettingsController {

    @FXML private StackPane firmwareButton;
    @FXML private StackPane differentialFlowMeterButton;
    @FXML private HBox pressureSensor;
    @FXML private HBox injConfiguration;
    @FXML private HBox language;
    @FXML private HBox flowView;
    @FXML private HBox regulatorsQTY;
    @FXML private CheckBox fastCoding;
    @FXML private CheckBox dimasGuiEdition;
    @FXML private CheckBox instantFlow;
    @FXML private GridPane settingsGridPane;
    @FXML private Label regulatorsConfigLabel;
    @FXML private Label injectorsConfigLabel;
    @FXML private Label languagesLabel;
    @FXML private Label flowOutputDimensionLabel;
    @FXML private Label pressureSensorLabel;
    @FXML private Label diffFmSettingsLabel;

    @FXML private DimasGuiEditionController dimasGuiEditionController;
    @FXML private FastCodingController fastCodingController;
    @FXML private FlowViewController flowViewController;
    @FXML private InjConfigurationController injConfigurationController;
    @FXML private LanguageController languageController;
    @FXML private PressureSensorController pressureSensorController;
    @FXML private RegulatorsQTYController regulatorsQTYController;
    @FXML private InstantFlowController instantFlowController;
    @FXML private FirmwareButtonController firmwareButtonController;
    @FXML private DifferentialFlowMeterButtonController differentialFlowMeterButtonController;

    protected I18N i18N;
    private ModbusConnect flowModbusConnect;
    private FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion;


    public void setFlowModbusConnect(ModbusConnect flowModbusConnect) {
        this.flowModbusConnect = flowModbusConnect;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

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
    public FirmwareButtonController getFirmwareButtonController() {
        return firmwareButtonController;
    }
    public DifferentialFlowMeterButtonController getDifferentialFlowMeterButtonController() {
        return differentialFlowMeterButtonController;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }

    @PostConstruct
    private void init() {

        differentialFlowMeterButton.setDisable(true);
        flowModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                differentialFlowMeterButton.setDisable(!(flowFirmwareVersion.getVersions() == MASTER_DF));

            } else {
                differentialFlowMeterButton.setDisable(true);
            }
        });

        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        flowOutputDimensionLabel.textProperty().bind(i18N.createStringBinding("settings.FlowOutputDimension.ComboBox"));
        diffFmSettingsLabel.textProperty().bind(i18N.createStringBinding("differentialFM.calibrationButton"));
    }
}
