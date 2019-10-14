package fi.stardex.sisu.ui.controllers.uis;

import fi.stardex.sisu.model.uis.UisSettingsModel;
import fi.stardex.sisu.util.enums.uis.RpmSource;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
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

public class UisSettingsController {

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

    private Preferences rootPrefs;
    private I18N i18N;
    private UisSettingsModel uisSettingsModel;
    private static final String PREF_KEY_FLOW = "checkBoxFlowVisibleSelected";
    private static final String PREF_KEY_PRESSURE = "pressureSensorSelected";
    private static final String PREF_KEY_OFFSET = "angleOffsetSelected";
    private static final String PREF_KEY_RPM = "rpmSourceSelected";

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

    @PostConstruct
    public void init() {

        rpmSourceComboBox.getItems().setAll(RpmSource.values());
        rpmSourceComboBox.getItems().sort(Comparator.comparingInt(RpmSource::getOrder));
        rpmSourceComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> rootPrefs.put(PREF_KEY_RPM, newValue.name()));
        uisSettingsModel.rpmSourceProperty().bind(rpmSourceComboBox.getSelectionModel().selectedItemProperty());
        rpmSourceComboBox.getSelectionModel().select(RpmSource.valueOf(rootPrefs.get(PREF_KEY_RPM, EXTERNAL.name())));

        languagesComboBox.setItems(FXCollections.observableArrayList(Locales.values()));
        uisSettingsModel.languageProperty().bind(languagesComboBox.getSelectionModel().selectedItemProperty());
        languagesComboBox.getSelectionModel().select(Locales.valueOf(rootPrefs.get("Language", Locales.ENGLISH.name())));
        languagesComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            rootPrefs.put("Language", newValue.name());
            i18N.setLocale(Locales.getLocale(newValue.name()));
        });

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
        firmwareButton.setOnAction(actionEvent -> uisSettingsModel.getFirmwareVersionButton().fire());
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
    }
}
