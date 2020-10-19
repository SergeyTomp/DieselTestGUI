package fi.stardex.sisu.ui.controllers.cr.tabs.settings;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.cr.CrSettingsModel;
import fi.stardex.sisu.settings.*;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import java.util.prefs.Preferences;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER_DF;

public class SettingsController {

    @FXML private StackPane limitControlsStackPane;
    @FXML private StackPane limitLabelsStackPane;
    @FXML private Spinner<Integer> pumpRpmLimitSpinner;
    @FXML private Spinner<Integer> heuiMaxPressureSpinner;
    @FXML private Label pumpRpmLimitLabel;
    @FXML private Label heuiMaxPressureLabel;
    @FXML private Label firmwareUpdateLabel;
    @FXML private Button firmwareUpdateButton;
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
    private CrSettingsModel crSettingsModel;
    private Preferences rootPrefs;

    public void setFlowModbusConnect(ModbusConnect flowModbusConnect) {
        this.flowModbusConnect = flowModbusConnect;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }
    public void setCrSettingsModel(CrSettingsModel crSettingsModel) {
        this.crSettingsModel = crSettingsModel;
    }
    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
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

        firmwareUpdateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                crSettingsModel.firmwareUpdateProperty().setValue(true);
                crSettingsModel.firmwareUpdateProperty().setValue(false);
            }
        });

        hideUpdate();

        heuiMaxPressureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(150, 300, 300, 10));
        pumpRpmLimitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2000, 2000, 100));
        crSettingsModel.heuiMaxPressureProperyProperty().bind(heuiMaxPressureSpinner.valueProperty());
        crSettingsModel.pumpMaxRpmPropertyProperty().bind(pumpRpmLimitSpinner.valueProperty());
        heuiMaxPressureSpinner.getValueFactory().valueProperty().addListener((observableValue, oldValue, newValue) -> rootPrefs.putInt("heuiMaxPressure", newValue));
        pumpRpmLimitSpinner.getValueFactory().valueProperty().addListener((observableValue, oldValue, newValue) -> rootPrefs.putInt("pumpRpmLimit", newValue));
        heuiMaxPressureSpinner.getValueFactory().setValue(rootPrefs.getInt("heuiMaxPressure", 300));
        pumpRpmLimitSpinner.getValueFactory().setValue(rootPrefs.getInt("pumpRpmLimit", 2000));
        SpinnerManager.setupIntegerSpinner(heuiMaxPressureSpinner);
        SpinnerManager.setupIntegerSpinner(pumpRpmLimitSpinner);


        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        flowOutputDimensionLabel.textProperty().bind(i18N.createStringBinding("settings.FlowOutputDimension.ComboBox"));
        diffFmSettingsLabel.textProperty().bind(i18N.createStringBinding("differentialFM.calibrationButton"));
    }

    private void hideUpdate() {
        firmwareUpdateLabel.setVisible(false);
        firmwareUpdateButton.setVisible(false);
    }
}
