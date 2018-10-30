package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
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

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class SettingsController {


    private I18N i18N;

//    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

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

    // Fast Coding

    @FXML private CheckBox fastCodingCheckBox;

    // DIMAS

    @FXML private CheckBox isDIMASCheckBox;

    // Flow Visible

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

    private HighPressureSectionController highPressureSectionController;

//    private InjectorChannel SINGLE;
//    private InjectorChannel MULTY;
//    private StringProperty single = new SimpleStringProperty(SINGLE.getCHANNEL_QTY());
//    private StringProperty multi = new SimpleStringProperty(MULTY.getCHANNEL_QTY());

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setHighPressureSectionController(HighPressureSectionController highPressureSectionController) {
        this.highPressureSectionController = highPressureSectionController;
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

    public ComboBox<Locales> getLanguagesConfigComboBox() {
        return languagesConfigComboBox;
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

    public ObjectProperty<Integer> pressMultiplierPropertyProperty() {
        return pressMultiplierProperty;
    }

    @PostConstruct
    private void init() {
//        StringProperty single = new SimpleStringProperty(SINGLE.getCHANNEL_QTY());
//        StringProperty multi = new SimpleStringProperty(MULTY.getCHANNEL_QTY());

        bindingI18N();
        sensor1500RadioButton.setUserData(1500);
        sensor1800RadioButton.setUserData(1800);
        sensor2000RadioButton.setUserData(2000);
        sensor2200RadioButton.setUserData(2200);
        sensor2200RadioButton.setUserData(2400);
        setupPressureSensor();
        setupCheckBoxPreference(fastCodingCheckBox, "fastCodingCheckBoxSelected", false);
        setupCheckBoxPreference(isDIMASCheckBox, "isDIMASCheckBoxSelected", true);
        setupCheckBoxPreference(flowVisibleCheckBox, "checkBoxFlowVisibleSelected", true);
        regulatorsConfigComboBox.setItems(FXCollections.observableArrayList("3", "2", "1"));
        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));
        languagesConfigComboBox.setItems(FXCollections.observableArrayList(Locales.RUSSIAN, Locales.ENGLISH, Locales.KOREAN));
        flowOutputDimensionsComboBox.setItems(FXCollections.observableArrayList(Dimension.LIMIT, Dimension.PLUS_OR_MINUS));
        setupComboBoxesPreferences();
        configRegulatorsInvolved(Integer.parseInt(regulatorsConfigComboBox.valueProperty().getValue()));
        regulatorsConfigComboBox.valueProperty().addListener(new RegulatorsConfigListener());

    }

    private void bindingI18N() {

        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        fastCodingCheckBox.textProperty().bind(i18N.createStringBinding("settings.fastCoding.CheckBox"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        flowOutputDimensionLabel.textProperty().bind(i18N.createStringBinding("settings.FlowOutputDimension.ComboBox"));
        isDIMASCheckBox.textProperty().bind(i18N.createStringBinding("settings.isDIMAS.CheckBox"));
        flowVisibleCheckBox.textProperty().bind(i18N.createStringBinding("settings.flowVisible.CheckBox"));
        sensor1500RadioButton.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.1500.radiobutton"));
        sensor1800RadioButton.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.1800.radiobutton"));
        sensor2000RadioButton.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.2000.radiobutton"));
        sensor2200RadioButton.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.2200.radiobutton"));
        sensor2400RadioButton.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.2400.radiobutton"));
//        single.bind(i18N.createStringBinding("main.channelQty.SINGLE_CHANNEL"));
//        multi.bind(i18N.createStringBinding("main.channelQty.MULTY_CHANNEL"));
    }

    private void setupPressureSensor() {

        sensor1500RadioButton.setSelected(rootPrefs.getBoolean("sensor1500RadioButtonSelected", true));
        sensor1800RadioButton.setSelected(rootPrefs.getBoolean("sensor1800RadioButtonSelected", false));
        sensor2000RadioButton.setSelected(rootPrefs.getBoolean("sensor2000RadioButtonSelected", false));
        sensor2200RadioButton.setSelected(rootPrefs.getBoolean("sensor2200RadioButtonSelected", false));
        sensor2400RadioButton.setSelected(rootPrefs.getBoolean("sensor2400RadioButtonSelected", false));

        sensor1500RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean("sensor1500RadioButtonSelected", newValue));
        sensor1800RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean("sensor1800RadioButtonSelected", newValue));
        sensor2000RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean("sensor2000RadioButtonSelected", newValue));
        sensor2200RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean("sensor2200RadioButtonSelected", newValue));
        sensor2400RadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean("sensor2400RadioButtonSelected", newValue));

        ReadOnlyObjectProperty<Toggle> pressureSensorToggleGroupProperty = pressureSensorToggleGroup.selectedToggleProperty();

        Integer maxPressure = (Integer) pressureSensorToggleGroupProperty.getValue().getUserData();
        pressMultiplierProperty.setValue(maxPressure);
        highPressureSectionController.getPressReg1Spinner().setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, maxPressure, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));

        pressureSensorToggleGroupProperty.addListener((observable, oldValue, newValue) -> {
            pressMultiplierProperty.setValue((Integer)(newValue.getUserData()));
            highPressureSectionController.getPressReg1Spinner().setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, (Integer)newValue.getUserData(), PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));
        });
    }

    private void setupCheckBoxPreference(CheckBox checkBox, String prefsKey, boolean prefsValue) {

        checkBox.setSelected(rootPrefs.getBoolean(prefsKey, prefsValue));
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean(prefsKey, newValue));

    }

    private void setupComboBoxesPreferences() {

        regulatorsConfigComboBox.getSelectionModel().select(rootPrefs.get("regulatorsConfigSelected", "3"));
        regulatorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put("regulatorsConfigSelected", newValue));


        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(rootPrefs.get("injectorsConfigSelected", InjectorChannel.SINGLE_CHANNEL.name())));
        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put("injectorsConfigSelected", newValue.name()));
//        injectorsConfigComboBox.getSelectionModel().select(rootPrefs.get("injectorsConfigSelected", InjectorChannel.SINGLE_CHANNEL.getCHANNEL_QTY()));
//        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put("injectorsConfigSelected", newValue));



        languagesConfigComboBox.getSelectionModel().select(Locales.valueOf(rootPrefs.get("Language", Locales.ENGLISH.name())));
        languagesConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        rootPrefs.put("Language", newValue.name());
//            prefs.put("Language", newValue.name());
        i18N.setLocale(Locales.getLocale(newValue.name()));
        });

        flowOutputDimensionsComboBox.getSelectionModel().select(Dimension.valueOf(rootPrefs.get("flowOutputDimensionSelected", Dimension.LIMIT.name())));
        flowOutputDimensionsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                rootPrefs.put("flowOutputDimensionSelected", newValue.name()));


    }

    private class RegulatorsConfigListener implements ChangeListener<String>{
        @Override
        public void changed(ObservableValue<? extends String> observable, String  oldValue, String  newValue) {
            switch (Integer.parseInt(newValue)){
                case 1:
                    configRegulatorsInvolved(1);
                    break;
                case 2:
                    configRegulatorsInvolved(2);
                    break;
                case 3:
                    configRegulatorsInvolved(3);
                    break;
                default:
                    configRegulatorsInvolved(3);
                    break;
            }
        }
    }

    private void configRegulatorsInvolved(int number) {
        switch (number){
            case 1:
                highPressureSectionController.setVisibleRegulator2(false);
                highPressureSectionController.setVisibleRegulator3(false);
                break;
            case 2:
                highPressureSectionController.setVisibleRegulator2(true);
                highPressureSectionController.setVisibleRegulator3(false);
                break;
            case 3:
                highPressureSectionController.setVisibleRegulator2(true);
                highPressureSectionController.setVisibleRegulator3(true);
                break;
            default:
                highPressureSectionController.setVisibleRegulator2(true);
                highPressureSectionController.setVisibleRegulator3(true);
                break;
        }

    }

}
