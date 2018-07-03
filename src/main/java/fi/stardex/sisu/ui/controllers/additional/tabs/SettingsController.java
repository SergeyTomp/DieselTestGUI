package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.combobox_values.Languages;
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

public class SettingsController {

    @FXML
    private ComboBox<Dimension> comboFlowOutputDimension;

    @FXML
    private Label languagesLabel;

    @FXML
    private Label injectorsConfigLabel;

    @FXML
    private Label regulatorsConfigLabel;

    @FXML
    private ComboBox<String> comboRegulatorsConfig;

    @FXML
    private ComboBox<Languages> comboLanguageConfig;

    @FXML
    private CheckBox checkBoxFlowVisible;

    @FXML
    private CheckBox fastMeasurementCheckBox;

    @FXML
    private CheckBox fastCodingCheckBox;

    @FXML
    private Label pressureSensorLabel;

    @FXML
    private RadioButton sensor1500RadioButton;

    @FXML
    private RadioButton sensor1800RadioButton;

    @FXML
    private RadioButton sensor2000RadioButton;

    @FXML
    private RadioButton sensor2200RadioButton;

    @FXML
    private RadioButton sensor2400RadioButton;

    @FXML
    private ComboBox<InjectorChannel> comboInjectorConfig;

    @FXML
    private CheckBox autoResetCheckBox;

    private I18N i18N;

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public ComboBox<InjectorChannel> getComboInjectorConfig() {
        return comboInjectorConfig;
    }

    public CheckBox getCheckBoxFlowVisible() {
        return checkBoxFlowVisible;
    }

    @PostConstruct
    private void init() {

        bindingI18N();

        comboFlowOutputDimension.setItems(FXCollections.observableArrayList(Dimension.LIMIT, Dimension.PLUS_OR_MINUS));

        comboRegulatorsConfig.setItems(FXCollections.observableArrayList("3", "2", "1"));

        comboLanguageConfig.setItems(FXCollections.observableArrayList(Languages.RUSSIAN, Languages.ENGLISH, Languages.KOREAN));

        autoResetCheckBox.setSelected(prefs.getBoolean("autoResetCheckBoxSelected", true));


        comboInjectorConfig.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));
//        comboInjectorConfig.getSelectionModel().selectFirst();

        sensor1500RadioButton.setSelected(prefs.getBoolean("sensor1500RadioButtonSelected", true));

        sensor1800RadioButton.setSelected(prefs.getBoolean("sensor1800RadioButtonSelected", false));

        sensor2000RadioButton.setSelected(prefs.getBoolean("sensor2000RadioButtonSelected", false));

        sensor2200RadioButton.setSelected(prefs.getBoolean("sensor2200RadioButtonSelected", false));

        sensor2400RadioButton.setSelected(prefs.getBoolean("sensor2400RadioButtonSelected", false));

        comboInjectorConfig.getSelectionModel().select(InjectorChannel.valueOf(prefs.get("injectorsConfigSelected", InjectorChannel.SINGLE_CHANNEL.name())));

        comboInjectorConfig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> prefs.put("injectorsConfigSelected", newValue.name()));

//        comboRegulatorsConfig.getSelectionModel().select(prefs.getInt("regulatorsConfigSelected", 3));
        comboRegulatorsConfig.getSelectionModel().select(prefs.get("regulatorsConfigSelected", "3"));

        comboRegulatorsConfig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                prefs.put("regulatorsConfigSelected", newValue));

        comboLanguageConfig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            prefs.put("languageSelected", newValue.name());
            i18N.setLocale(Locales.getLocale(newValue.name()));
        });

        checkBoxFlowVisible.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("checkBoxFlowVisibleSelected", newValue));

        fastCodingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("fastCodingCheckBoxSelected", newValue));

        fastMeasurementCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("fastMeasurementCheckBoxSelected", newValue));

        sensor1500RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor1500RadioButtonSelected", newValue));

        sensor1800RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor1800RadioButtonSelected", newValue));

        sensor2000RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2000RadioButtonSelected", newValue));

        sensor2200RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2200RadioButtonSelected", newValue));

        sensor2400RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2400RadioButtonSelected", newValue));

        comboLanguageConfig.getSelectionModel().select(Languages.valueOf(prefs.get("languageSelected", Languages.ENGLISH.name())));

        autoResetCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("autoResetCheckBoxSelected", newValue));

        checkBoxFlowVisible.setSelected(prefs.getBoolean("checkBoxFlowVisibleSelected", true));

        fastCodingCheckBox.setSelected(prefs.getBoolean("fastCodingCheckBoxSelected", false));

        fastMeasurementCheckBox.setSelected(prefs.getBoolean("fastMeasurementCheckBoxSelected", false));
    }

    private void bindingI18N() {
        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        autoResetCheckBox.textProperty().bind(i18N.createStringBinding("settings.autoReset.CheckBox"));
        fastCodingCheckBox.textProperty().bind(i18N.createStringBinding("settings.fastCoding.CheckBox"));
        fastMeasurementCheckBox.textProperty().bind(i18N.createStringBinding("settings.fastMeasurement.CheckBox"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.Label"));
    }

}
