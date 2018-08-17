package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController.Languages.*;

public class SettingsController {

    private I18N i18N;

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    // Pressure sensor

    @FXML private Label pressureSensorLabel;

    @FXML private RadioButton sensor1500RadioButton;

    @FXML private RadioButton sensor1800RadioButton;

    @FXML private RadioButton sensor2000RadioButton;

    @FXML private RadioButton sensor2200RadioButton;

    @FXML private RadioButton sensor2400RadioButton;

    // Auto Reset

    @FXML private CheckBox autoResetCheckBox;

    // Fast Coding

    @FXML private CheckBox fastCodingCheckBox;

    // Fast Measurement

    @FXML
    private CheckBox fastMeasurementCheckBox;

    // DIMAS

    @FXML private Label isDIMASLabel;

    @FXML private CheckBox isDIMASCheckBox;

    // Flow Visible

    @FXML private Label flowVisibleLabel;

    @FXML private CheckBox flowVisibleCheckBox;

    // Regulators configuration

    @FXML private Label regulatorsConfigLabel;

    @FXML private ComboBox<String> regulatorsConfigComboBox;

    // Injectors configuration

    @FXML private Label injectorsConfigLabel;

    @FXML private ComboBox<InjectorChannel> injectorsConfigComboBox;

    // Languages

    public enum Languages {
        RUSSIAN, ENGLISH, KOREAN
    }

    @FXML private Label languagesLabel;

    @FXML private ComboBox<Languages> languagesConfigComboBox;

    // Flow output dimensions

    @FXML private Label flowOutputDimensionLabel;

    @FXML private ComboBox<Dimension> flowOutputDimensionsComboBox;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public ComboBox<InjectorChannel> getInjectorsConfigComboBox() {
        return injectorsConfigComboBox;
    }

    public CheckBox getFlowVisibleCheckBox() {
        return flowVisibleCheckBox;
    }

    public ComboBox<Dimension> getFlowOutputDimensionsComboBox() {
        return flowOutputDimensionsComboBox;
    }

    @PostConstruct
    private void init() {

        bindingI18N();

        setupPressureSensor();

        setupCheckBoxPreference(autoResetCheckBox, "autoResetCheckBoxSelected", true);

        setupCheckBoxPreference(fastCodingCheckBox, "fastCodingCheckBoxSelected", false);

        setupCheckBoxPreference(fastMeasurementCheckBox, "fastMeasurementCheckBoxSelected", false);

        setupCheckBoxPreference(isDIMASCheckBox, "isDIMASCheckBoxSelected", true);

        setupCheckBoxPreference(flowVisibleCheckBox, "checkBoxFlowVisibleSelected", true);

        regulatorsConfigComboBox.setItems(FXCollections.observableArrayList("3", "2", "1"));

        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));

        languagesConfigComboBox.setItems(FXCollections.observableArrayList(RUSSIAN, ENGLISH, KOREAN));

        flowOutputDimensionsComboBox.setItems(FXCollections.observableArrayList(Dimension.LIMIT, Dimension.PLUS_OR_MINUS));

        setupComboBoxesPreferences();

    }

    private void bindingI18N() {

        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        autoResetCheckBox.textProperty().bind(i18N.createStringBinding("settings.autoReset.CheckBox"));
        fastCodingCheckBox.textProperty().bind(i18N.createStringBinding("settings.fastCoding.CheckBox"));
        fastMeasurementCheckBox.textProperty().bind(i18N.createStringBinding("settings.fastMeasurement.CheckBox"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        flowOutputDimensionLabel.textProperty().bind(i18N.createStringBinding("settings.FlowOutputDimension.ComboBox"));
        isDIMASLabel.textProperty().bind(i18N.createStringBinding("settings.isDIMAS.CheckBox"));
        flowVisibleLabel.textProperty().bind(i18N.createStringBinding("settings.flowVisible.CheckBox"));

    }

    private void setupPressureSensor() {

        sensor1500RadioButton.setSelected(prefs.getBoolean("sensor1500RadioButtonSelected", true));
        sensor1800RadioButton.setSelected(prefs.getBoolean("sensor1800RadioButtonSelected", false));
        sensor2000RadioButton.setSelected(prefs.getBoolean("sensor2000RadioButtonSelected", false));
        sensor2200RadioButton.setSelected(prefs.getBoolean("sensor2200RadioButtonSelected", false));
        sensor2400RadioButton.setSelected(prefs.getBoolean("sensor2400RadioButtonSelected", false));

        sensor1500RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor1500RadioButtonSelected", newValue));
        sensor1800RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor1800RadioButtonSelected", newValue));
        sensor2000RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2000RadioButtonSelected", newValue));
        sensor2200RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2200RadioButtonSelected", newValue));
        sensor2400RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2400RadioButtonSelected", newValue));

    }

    private void setupCheckBoxPreference(CheckBox checkBox, String prefsKey, boolean prefsValue) {

        checkBox.setSelected(prefs.getBoolean(prefsKey, prefsValue));
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean(prefsKey, newValue));

    }

    private void setupComboBoxesPreferences() {

        regulatorsConfigComboBox.getSelectionModel().select(prefs.get("regulatorsConfigSelected", "3"));
        regulatorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> prefs.put("regulatorsConfigSelected", newValue));


        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(prefs.get("injectorsConfigSelected", InjectorChannel.SINGLE_CHANNEL.name())));
        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> prefs.put("injectorsConfigSelected", newValue.name()));

        languagesConfigComboBox.getSelectionModel().select(Languages.valueOf(prefs.get("languageSelected", ENGLISH.name())));
        languagesConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            prefs.put("languageSelected", newValue.name());
            i18N.setLocale(Locales.getLocale(newValue.name()));
        });

        flowOutputDimensionsComboBox.getSelectionModel().select(Dimension.valueOf(prefs.get("flowOutputDimensionSelected", Dimension.LIMIT.name())));
        flowOutputDimensionsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                prefs.put("flowOutputDimensionSelected", newValue.name()));


    }

}
