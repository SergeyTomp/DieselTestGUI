package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.util.Pair;
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
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class SettingsController {


    private I18N i18N;

//    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    private Preferences rootPrefs;

    // Pressure sensor
    @FXML private ToggleGroup pressureSensorToggleGroup;

    @FXML private Label pressureSensorLabel;

    @FXML private ComboBox<Integer> pressureSensorComboBox;

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

    // Connect

    @FXML private TextField ultimaIPField;

    @FXML private TextField flowMeterIPField;

    @FXML private TextField standIPField;

    @FXML private TextField ultimaPortField;

    @FXML private TextField flowMeterPortField;

    @FXML private TextField standPortField;

    @FXML private Button acceptButton;

    @FXML private Label ultimaLabel;

    @FXML private Label flowMeterLabel;

    @FXML private Label standLabel;

    @FXML private Label ipAddressLabel;

    @FXML private Label portLabel;

    private HighPressureSectionController highPressureSectionController;

//    private InjectorChannel SINGLE;
//    private InjectorChannel MULTY;
//    private StringProperty single = new SimpleStringProperty(SINGLE.getCHANNEL_QTY());
//    private StringProperty multi = new SimpleStringProperty(MULTY.getCHANNEL_QTY());

    private Pair<String, String> ultimaConnect = new Pair<>();

    private Pair<String, String> flowMeterConnect = new Pair<>();

    private Pair<String, String> standConnect = new Pair<>();

    private final String ipRegex = makePartialIPRegex();

    public TextField getStandIPField() {
        return standIPField;
    }

    public TextField getStandPortField() {
        return standPortField;
    }

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

    public Pair<String, String> getUltimaConnect() {
        return ultimaConnect;
    }

    public Pair<String, String> getFlowMeterConnect() {
        return flowMeterConnect;
    }

    public Pair<String, String> getStandConnect() {
        return standConnect;
    }

    public ComboBox getPressureSensorComboBox() {
        return pressureSensorComboBox;
    }

    public ObjectProperty<Integer> pressMultiplierPropertyProperty() {
        return pressMultiplierProperty;
    }


    @PostConstruct
    private void init() {
//        StringProperty single = new SimpleStringProperty(SINGLE.getCHANNEL_QTY());
//        StringProperty multi = new SimpleStringProperty(MULTY.getCHANNEL_QTY());

        bindingI18N();
        setupSettingsControls();
        setupConnectionControls();
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
        ultimaLabel.textProperty().bind(i18N.createStringBinding("link.ultima.label"));
        flowMeterLabel.textProperty().bind(i18N.createStringBinding("link.flowmeter.label"));
        standLabel.textProperty().bind(i18N.createStringBinding("link.stand.label"));
        ipAddressLabel.textProperty().bind(i18N.createStringBinding("link.ipAddress.label"));
        portLabel.textProperty().bind(i18N.createStringBinding("link.port.label"));
        acceptButton.textProperty().bind(i18N.createStringBinding("link.accept.button"));
//        single.bind(i18N.createStringBinding("main.channelQty.SINGLE_CHANNEL"));
//        multi.bind(i18N.createStringBinding("main.channelQty.MULTY_CHANNEL"));
    }

    private void setupConnectionControls(){
        ultimaIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));
        flowMeterIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));
        standIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));

        String portRegex = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$";

        ultimaPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));
        flowMeterPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));
        standPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));

        setPairValues();

        ultimaIPField.setText(ultimaConnect.getKey());
        flowMeterIPField.setText(flowMeterConnect.getKey());
        standIPField.setText(standConnect.getKey());

        ultimaPortField.setText(ultimaConnect.getValue());
        flowMeterPortField.setText(flowMeterConnect.getValue());
        standPortField.setText(standConnect.getValue());

        acceptButton.setOnMouseClicked(event -> {

            rootPrefs.put("UltimaIP", ultimaIPField.getText());
            rootPrefs.put("FlowIP", flowMeterIPField.getText());
            rootPrefs.put("StandIP", standIPField.getText());

            rootPrefs.put("UltimaPort", ultimaPortField.getText());
            rootPrefs.put("FlowPort", flowMeterPortField.getText());
            rootPrefs.put("StandPort", standPortField.getText());

            setPairValues();

        });
    }

    private void setupSettingsControls(){
        pressureSensorComboBox.setItems(FXCollections.observableArrayList(1500, 1800, 2000, 2200, 2400));
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

    private void setupPressureSensor() {

        pressureSensorComboBox.getSelectionModel().select(new Integer(rootPrefs.getInt("pressureSensorSelected", 1500)));
        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putInt("pressureSensorSelected", newValue));

        Integer maxPressure = pressureSensorComboBox.getSelectionModel().selectedItemProperty().getValue();
        pressMultiplierProperty.setValue(maxPressure);
        highPressureSectionController.getPressReg1Spinner().setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, maxPressure, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));

        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            pressMultiplierProperty.setValue(newValue);
            highPressureSectionController.getPressReg1Spinner().setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, newValue, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));
        }));
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

        languagesConfigComboBox.getSelectionModel().select(Locales.valueOf(rootPrefs.get("Language", Locales.ENGLISH.name())));
        languagesConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        rootPrefs.put("Language", newValue.name());
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

    private void setPairValues() {

        ultimaConnect.setKey(rootPrefs.get("UltimaIP", "192.168.10.206"));
        flowMeterConnect.setKey(rootPrefs.get("FlowIP", "192.168.10.201"));
        standConnect.setKey(rootPrefs.get("StandIP", "192.168.10.202"));

        ultimaConnect.setValue(rootPrefs.get("UltimaPort", "502"));
        flowMeterConnect.setValue(rootPrefs.get("FlowPort", "502"));
        standConnect.setValue(rootPrefs.get("StandPort", "502"));

    }
    
    private String makePartialIPRegex() {

        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))" ;
        String subsequentPartialBlock = "(\\."+partialBlock+")" ;
        String ipAddress = partialBlock+"?"+subsequentPartialBlock+"{0,3}";
        return "^"+ipAddress ;

    }

    private UnaryOperator<TextFormatter.Change> getFilter(String regex) {
        return new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change c) {
                String text = c.getControlNewText();
                if (text.matches(regex)) {
                    return c;
                } else {
                    return null;
                }
            }
        };
    }

}
