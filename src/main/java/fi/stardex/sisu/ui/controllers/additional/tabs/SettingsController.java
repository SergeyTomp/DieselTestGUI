package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class SettingsController {


    private I18N i18N;

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    private Preferences rootPrefs;

    // Pressure sensor
    @FXML private ToggleGroup pressureSensorToggleGroup;

    @FXML private Label pressureSensorLabel;

    @FXML private RadioButton sensor1500RadioButton;

    @FXML private RadioButton sensor1800RadioButton;

    @FXML private RadioButton sensor2000RadioButton;

    @FXML private RadioButton sensor2200RadioButton;

    @FXML private RadioButton sensor2400RadioButton;

    private ObjectProperty<Integer> pressMultiplierProperty = new SimpleObjectProperty<>();

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

    @FXML private Label languagesLabel;

    @FXML private ComboBox<Locales> languagesConfigComboBox;

    // Flow output dimensions

    @FXML private Label flowOutputDimensionLabel;

    @FXML private ComboBox<Dimension> flowOutputDimensionsComboBox;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
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

    public RadioButton getSensor1500RadioButton() {
        return sensor1500RadioButton;
    }

    public RadioButton getSensor1800RadioButton() {
        return sensor1800RadioButton;
    }

    public RadioButton getSensor2000RadioButton() {
        return sensor2000RadioButton;
    }

    public RadioButton getSensor2200RadioButton() {
        return sensor2200RadioButton;
    }

    public RadioButton getSensor2400RadioButton() {
        return sensor2400RadioButton;
    }

    public Integer getPressMultiplierProperty() {
        return pressMultiplierProperty.get();
    }

    public ObjectProperty<Integer> pressMultiplierPropertyProperty() {
        return pressMultiplierProperty;
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

        languagesConfigComboBox.setItems(FXCollections.observableArrayList(Locales.RUSSIAN, Locales.ENGLISH, Locales.KOREAN));

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

        if(pressureSensorToggleGroup.selectedToggleProperty().getValue() == sensor1500RadioButton){
            pressMultiplierProperty.setValue(1500);
        }else if(pressureSensorToggleGroup.selectedToggleProperty().getValue() == sensor1800RadioButton){
            pressMultiplierProperty.setValue(1800);
        }else if(pressureSensorToggleGroup.selectedToggleProperty().getValue() == sensor2000RadioButton){
            pressMultiplierProperty.setValue(2000);
        }else if(pressureSensorToggleGroup.selectedToggleProperty().getValue() == sensor2200RadioButton){
            pressMultiplierProperty.setValue(2200);
        }else pressMultiplierProperty.setValue(2400);


        pressureSensorToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == sensor1500RadioButton) {
                    pressMultiplierProperty.setValue(1500);
                } else if (newValue == sensor1800RadioButton) {
                    pressMultiplierProperty.setValue(1800);
                } else if (newValue == sensor2000RadioButton) {
                    pressMultiplierProperty.setValue(2000);
                } else if (newValue == sensor2200RadioButton) {
                    pressMultiplierProperty.setValue(2200);
                } else {
                    pressMultiplierProperty.setValue(2400);
                }
            }
        });
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

        languagesConfigComboBox.getSelectionModel().select(Locales.valueOf(prefs.get("Language", Locales.ENGLISH.name())));
        languagesConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            rootPrefs.put("Language", newValue.name());
            i18N.setLocale(Locales.getLocale(newValue.name()));
        });

        flowOutputDimensionsComboBox.getSelectionModel().select(Dimension.valueOf(prefs.get("flowOutputDimensionSelected", Dimension.LIMIT.name())));
        flowOutputDimensionsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                prefs.put("flowOutputDimensionSelected", newValue.name()));


    }

}
