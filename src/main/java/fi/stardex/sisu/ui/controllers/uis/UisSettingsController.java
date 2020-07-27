package fi.stardex.sisu.ui.controllers.uis;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisSettingsModel;
import fi.stardex.sisu.util.enums.Dimension;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.uis.RpmSource;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
import fi.stardex.sisu.util.listeners.LocaleChangeListener;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.uis.RpmSource.EXTERNAL;
import static fi.stardex.sisu.util.i18n.Locales.ENGLISH;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER_DF;

public class UisSettingsController {

    @FXML private  Spinner<Integer> slaveMotorSpinner;
    @FXML private  Label slaveMotorLabel;
    @FXML private ComboBox <Dimension>  rangeViewComboBox;
    @FXML private Label rangeViewLabel;
    @FXML private Button firmwareButton;
    @FXML private GridPane settingsUisGridPane;
    @FXML private Label pressureSensorLabel;
    @FXML private ComboBox<Integer> pressureSensorComboBox;
    @FXML private Label languagesLabel;
    @FXML private ComboBox<Locales> languagesComboBox;
    @FXML private Label sensorAngleLabel;
    @FXML private Label rpmSourceLabel;
    @FXML private Spinner<Integer> sensorAngleSpinner;
    @FXML private ComboBox<RpmSource> rpmSourceComboBox;
    @FXML private CheckBox instantFlowCheckBox;
    @FXML private Label diffFmSettingsLabel;
    @FXML private Button diffFmSettingsButton;

    private Preferences rootPrefs;
    private I18N i18N;
    private UisSettingsModel uisSettingsModel;
    private ModbusConnect flowModbusConnect;
    private FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion;
    private MainSectionUisModel mainSectionUisModel;
    private Boolean localeChange = false;

    private static final String PREF_KEY_FLOW = "checkBoxFlowVisibleSelected";
    private static final String PREF_KEY_PRESSURE = "pressureSensorSelected";
    private static final String PREF_KEY_OFFSET = "angleOffsetSelected";
    private static final String PREF_KEY_RANGE_VIEW = "flowOutputDimensionSelected";
    private static final String PREF_KEY_SLAVE_RPM = "slaveRpmSelected";

    private Alert alert;
    private StringProperty yesButton = new SimpleStringProperty();
    private StringProperty noButton = new SimpleStringProperty();
    private StringProperty alertString = new SimpleStringProperty();
    private boolean alertProcessing;

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisSettingsModel(UisSettingsModel uisSettingsModel) {
        this.uisSettingsModel = uisSettingsModel;
    }
    public void setFlowModbusConnect(ModbusConnect flowModbusConnect) {
        this.flowModbusConnect = flowModbusConnect;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }

    @PostConstruct
    public void init() {

        rangeViewComboBox.setItems(FXCollections.observableArrayList(Dimension.LIMIT, Dimension.PLUS_OR_MINUS));
        uisSettingsModel.getFlowRangeViewProperty().bind(rangeViewComboBox.valueProperty());
        rangeViewComboBox.getSelectionModel().select(Dimension.valueOf(rootPrefs.get(PREF_KEY_RANGE_VIEW, Dimension.LIMIT.name())));
        rangeViewComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY_RANGE_VIEW, newValue.name()));

        rpmSourceComboBox.getItems().setAll(RpmSource.values());
        rpmSourceComboBox.getItems().sort(Comparator.comparingInt(RpmSource::getOrder));
        rpmSourceComboBox.getSelectionModel().select(EXTERNAL);
        uisSettingsModel.rpmSourceProperty().setValue(EXTERNAL);
        uisSettingsModel.rpmSourceProperty().bind(rpmSourceComboBox.getSelectionModel().selectedItemProperty());

        languagesComboBox.setItems(FXCollections.observableArrayList(Locales.values()));
        uisSettingsModel.languageProperty().bind(languagesComboBox.getSelectionModel().selectedItemProperty());
        languagesComboBox.getSelectionModel().select(Locales.valueOf(rootPrefs.get("Language", ENGLISH.name())));
        languagesComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!localeChange) {
                rootPrefs.put("Language", newValue.name());
                i18N.setLocale(Locales.getLocale(newValue.name()));
            }
        });
        i18N.localeProperty().addListener(new LocaleChangeListener(languagesComboBox, localeChange));

        uisSettingsModel.instantFlowProperty().bind(instantFlowCheckBox.selectedProperty());
        instantFlowCheckBox.setSelected(rootPrefs.getBoolean(PREF_KEY_FLOW, true));
        instantFlowCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> rootPrefs.putBoolean(PREF_KEY_FLOW, newValue));

        pressureSensorComboBox.setItems(FXCollections.observableArrayList(1500, 1800, 2000, 2200, 2400, 2700));
        uisSettingsModel.pressureSensorProperty().bind(pressureSensorComboBox.getSelectionModel().selectedItemProperty());
        pressureSensorComboBox.getSelectionModel().select(Integer.valueOf(rootPrefs.getInt(PREF_KEY_PRESSURE, 2000)));
        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(alertProcessing) return;
            showAlert();
            if (alert.getResult() != ButtonType.YES) {

                alertProcessing = true;
                pressureSensorComboBox.getSelectionModel().select(oldValue);
                alertProcessing = false;
                return;
            }
            rootPrefs.putInt(PREF_KEY_PRESSURE, newValue);
        });

        sensorAngleSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(ANGLE_OFFSET_SPINNER_MIN,
                                                                                                ANGLE_OFFSET_SPINNER_MAX,
                                                                                                ANGLE_OFFSET_SPINNER_INIT,
                                                                                                ANGLE_OFFSET_SPINNER_STEP));
        uisSettingsModel.angleOffsetProperty().bind(sensorAngleSpinner.valueProperty());
        sensorAngleSpinner.getValueFactory().setValue(rootPrefs.getInt(PREF_KEY_OFFSET, 70));
        sensorAngleSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> rootPrefs.putInt(PREF_KEY_OFFSET, newValue));

        slaveMotorSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3000, 1000, 50));
        uisSettingsModel.slaveMotorRPMProperty().bind(slaveMotorSpinner.valueProperty());
        slaveMotorSpinner.getValueFactory().setValue(rootPrefs.getInt(PREF_KEY_SLAVE_RPM, 1000));
        slaveMotorSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> rootPrefs.putInt(PREF_KEY_SLAVE_RPM, newValue));
        slaveMotorSpinner.setDisable(true);
        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue)
                -> slaveMotorSpinner.setDisable(newValue == null || newValue.getVoltAmpereProfile().getInjectorSubType() != InjectorSubType.HPI));

        firmwareButton.setOnAction(actionEvent -> uisSettingsModel.getFirmwareVersionButton().fire());
        diffFmSettingsButton.setOnAction(actionEvent -> uisSettingsModel.getDifferentialFmSettingsButton().fire());
        diffFmSettingsButton.setDisable(true);
        flowModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                diffFmSettingsButton.setDisable(!(flowFirmwareVersion.getVersions() == MASTER_DF));
            } else {
                diffFmSettingsButton.setDisable(true);
            }
        });
        bindingI18N();
    }

    private void showAlert() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);
            alert.initStyle(StageStyle.DECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);
        }
        ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());
        ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).textProperty().setValue(noButton.get());
        alert.setContentText(alertString.get());
        alert.showAndWait();
    }

    private void bindingI18N() {

        instantFlowCheckBox.textProperty().bind(i18N.createStringBinding("settings.flowVisible.CheckBox"));
        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        sensorAngleLabel.textProperty().bind(i18N.createStringBinding("settings.angleOffset.spinner"));
        rpmSourceLabel.textProperty().bind(i18N.createStringBinding("settings.rpmSource.CheckBox"));
        alertString.bind(i18N.createStringBinding("alert.pressureSensorChange"));
        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        noButton.bind((i18N.createStringBinding("alert.noButton")));
        diffFmSettingsLabel.textProperty().bind((i18N.createStringBinding("differentialFM.calibrationButton")));
        diffFmSettingsButton.textProperty().bind((i18N.createStringBinding("h4.tab.settings")));
        rangeViewLabel.textProperty().bind((i18N.createStringBinding("settings.FlowOutputDimension.ComboBox")));
        slaveMotorLabel.textProperty().bind((i18N.createStringBinding("settings.slaveMotor.Spinner")));
        firmwareButton.textProperty().bind((i18N.createStringBinding("settings.firmware.Button")));
    }
}
