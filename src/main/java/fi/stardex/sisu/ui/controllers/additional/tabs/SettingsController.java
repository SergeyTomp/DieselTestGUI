package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class SettingsController {

    @FXML
    private CheckBox checkBoxFlowVisible;

    @FXML
    private CheckBox fastMeasurementCheckBox;

    @FXML
    private CheckBox fastCodingCheckBox;

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

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    public ComboBox<InjectorChannel> getComboInjectorConfig() {
        return comboInjectorConfig;
    }

    public CheckBox getCheckBoxFlowVisible() {
        return checkBoxFlowVisible;
    }

    @PostConstruct
    private void init() {

        checkBoxFlowVisible.setSelected(prefs.getBoolean("checkBoxFlowVisibleSelected", true));

        autoResetCheckBox.setSelected(prefs.getBoolean("autoResetCheckBoxSelected", true));

        fastCodingCheckBox.setSelected(prefs.getBoolean("fastCodingCheckBoxSelected", false));

        fastMeasurementCheckBox.setSelected(prefs.getBoolean("fastMeasurementCheckBoxSelected", false));

        comboInjectorConfig.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL,InjectorChannel.MULTI_CHANNEL));
        comboInjectorConfig.getSelectionModel().selectFirst();

        sensor1500RadioButton.setSelected(prefs.getBoolean("sensor1500RadioButtonSelected", true));

        sensor1800RadioButton.setSelected(prefs.getBoolean("sensor1800RadioButtonSelected", false));

        sensor2000RadioButton.setSelected(prefs.getBoolean("sensor2000RadioButtonSelected", false));

        sensor2200RadioButton.setSelected(prefs.getBoolean("sensor2200RadioButtonSelected", false));

        sensor2400RadioButton.setSelected(prefs.getBoolean("sensor2400RadioButtonSelected", false));

        checkBoxFlowVisible.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("checkBoxFlowVisibleSelected", newValue));

        autoResetCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("autoResetCheckBoxSelected", newValue));

        fastCodingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("fastCodingCheckBoxSelected", newValue));

        fastMeasurementCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("fastMeasurementCheckBoxSelected", newValue));

        sensor1500RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor1500RadioButtonSelected", newValue));

        sensor1800RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor1800RadioButtonSelected", newValue));

        sensor2000RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2000RadioButtonSelected", newValue));

        sensor2200RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2200RadioButtonSelected", newValue));

        sensor2400RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> prefs.putBoolean("sensor2400RadioButtonSelected", newValue));

    }

}
