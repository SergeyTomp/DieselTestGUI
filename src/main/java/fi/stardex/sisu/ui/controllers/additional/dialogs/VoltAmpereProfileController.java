package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class VoltAmpereProfileController {

    @FXML private Spinner<Integer> firstWSpinner;

    @FXML private Spinner<Integer> batteryUSpinner;

    @FXML private Spinner<Double> boostISpinner;

    @FXML private Spinner<Integer> boostUSpinner;

    @FXML private Spinner<Double> firstISpinner;

    @FXML private Spinner<Integer> negativeUSpinner;

    @FXML private Spinner<Double> secondISpinner;

    @FXML private ToggleButton enableBoostToggleButton;

    @FXML private Button applyButton;

    @FXML private Button cancelButton;

    @FXML private Label firstWlabel;

    @FXML private Label batteryUlabel;

    @FXML private Label boostIlabel;

    @FXML private Label boostUlabel;

    @FXML private Label firstIlabel;

    @FXML private Label negativeUlabel;

    @FXML private Label secondIlabel;

    private I18N i18N;

    private Spinner<Integer> widthCurrentSignal;

    private int firstWSavedValue;

    private double boostISavedValue;

    private double firstISavedValue;

    private double secondISavedValue;

    private int batteryUSavedValue;

    private int negativeUSavedValue;

    private int boostUSavedValue;

    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private Stage stage;

    private VoltageController voltageController;

    private List<Spinner> listOfVAPSpinners = new ArrayList<>();

    private InjectorSectionController injectorSectionController;

    public Spinner<Integer> getFirstWSpinner() {
        return firstWSpinner;
    }

    public Spinner<Integer> getBatteryUSpinner() {
        return batteryUSpinner;
    }

    public Spinner<Double> getBoostISpinner() {
        return boostISpinner;
    }

    public Spinner<Integer> getBoostUSpinner() {
        return boostUSpinner;
    }

    public Spinner<Double> getFirstISpinner() {
        return firstISpinner;
    }

    public Spinner<Integer> getNegativeUSpinner() {
        return negativeUSpinner;
    }

    public Spinner<Double> getSecondISpinner() {
        return secondISpinner;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getApplyButton() {
        return applyButton;
    }

    public ToggleButton getEnableBoostToggleButton() {
        return enableBoostToggleButton;
    }

    public InjectorSectionController getInjectorSectionController() {
        return injectorSectionController;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setWidthSpinner(Spinner<Integer> widthCurrentSignal) {
        this.widthCurrentSignal = widthCurrentSignal;
    }

    public void setVoltageController(VoltageController voltageController) {
        this.voltageController = voltageController;
    }

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    // FIXME: при изменении значения в спиннере которое равно значению с прошивки красным перестают гореть оба значения, хотя значение спиннера еще не было подтверждено нажатием Apply
    @PostConstruct
    private void init() {

        setupEnableBoostToggleButton();

        setupVAPSpinners();

        setupPiezoCoilToggleGroupListener();

        setupApplyButton();

        setupCancelButton();

        setupWidthCurrentSignalListener();

        setupSpinnerStyleWhenValueChangedListener();

        bindingI18N();

    }

    private void setupEnableBoostToggleButton() {

        enableBoostToggleButton.setSelected(true);
        enableBoostToggleButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUdisable"));
        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
            enableBoostToggleButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUdisable"));
        }
        else {
            enableBoostToggleButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUenable"));
        }});
    }

    private void setupVAPSpinners() {

        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(FIRST_W_SPINNER_MIN,
                FIRST_W_SPINNER_MAX,
                FIRST_W_SPINNER_INIT,
                FIRST_W_SPINNER_STEP));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(BOOST_I_SPINNER_MIN,
                BOOST_I_SPINNER_MAX,
                BOOST_I_SPINNER_INIT,
                BOOST_I_SPINNER_STEP));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(FIRST_I_SPINNER_MIN,
                FIRST_I_SPINNER_MAX,
                FIRST_I_SPINNER_INIT,
                FIRST_I_SPINNER_STEP));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(SECOND_I_SPINNER_MIN,
                SECOND_I_SPINNER_MAX,
                SECOND_I_SPINNER_INIT,
                SECOND_I_SPINNER_STEP));
        batteryUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BATTERY_U_SPINNER_MIN,
                BATTERY_U_SPINNER_MAX,
                BATTERY_U_SPINNER_INIT,
                BATTERY_U_SPINNER_STEP));
        negativeUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(NEGATIVE_U_SPINNER_MIN,
                NEGATIVE_U_SPINNER_MAX,
                NEGATIVE_U_SPINNER_INIT,
                NEGATIVE_U_SPINNER_STEP));
        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                BOOST_U_SPINNER_MAX,
                BOOST_U_SPINNER_INIT,
                BOOST_U_SPINNER_STEP));

        SpinnerManager.setupIntegerSpinner(widthCurrentSignal);
        SpinnerManager.setupIntegerSpinner(firstWSpinner);
        SpinnerManager.setupDoubleSpinner(boostISpinner);
        SpinnerManager.setupDoubleSpinner(firstISpinner);
        SpinnerManager.setupDoubleSpinner(secondISpinner);
        SpinnerManager.setupIntegerSpinner(batteryUSpinner);
        SpinnerManager.setupIntegerSpinner(negativeUSpinner);
        SpinnerManager.setupIntegerSpinner(boostUSpinner);

        listOfVAPSpinners.add(widthCurrentSignal);
        listOfVAPSpinners.add(firstWSpinner);
        listOfVAPSpinners.add(boostISpinner);
        listOfVAPSpinners.add(firstISpinner);
        listOfVAPSpinners.add(secondISpinner);
        listOfVAPSpinners.add(batteryUSpinner);
        listOfVAPSpinners.add(negativeUSpinner);
        listOfVAPSpinners.add(boostUSpinner);

        listOfVAPSpinners.forEach(e -> e.setEditable(true));

}

    private void setupPiezoCoilToggleGroupListener() {

        injectorSectionController.getPiezoCoilToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == injectorSectionController.getPiezoRadioButton()) {
                boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                        BOOST_U_SPINNER_MAX_PIEZO,
                        BOOST_U_SPINNER_INIT,
                        BATTERY_U_SPINNER_STEP));
            } else {
                if (convertDataToInt(voltageController.getVoltage().getText()) > BOOST_U_SPINNER_MAX)
                    ultimaModbusWriter.add(ModbusMapUltima.Boost_U, BOOST_U_SPINNER_INIT);
                boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                        BOOST_U_SPINNER_MAX,
                        BOOST_U_SPINNER_INIT,
                        BOOST_U_SPINNER_STEP));
            }
        });

    }

    private void setupApplyButton() {

        applyButton.setOnAction(event -> {
            listOfVAPSpinners.forEach(e -> e.increment(0));
            sendVAPRegisters();
            if (stage != null)
                stage.close();
        });

    }

    // FIXME: есть баги, не пишется старое значение при закрытии окна при горящем tooltip
    private void setupCancelButton() {

        cancelButton.setOnAction(event -> {
            firstWSpinner.getValueFactory().setValue(firstWSavedValue);
            boostISpinner.getValueFactory().setValue(boostISavedValue);
            firstISpinner.getValueFactory().setValue(firstISavedValue);
            secondISpinner.getValueFactory().setValue(secondISavedValue);
            batteryUSpinner.getValueFactory().setValue(batteryUSavedValue);
            negativeUSpinner.getValueFactory().setValue(negativeUSavedValue);
            boostUSpinner.getValueFactory().setValue(boostUSavedValue);
            stage.close();
        });

    }

    private void setupWidthCurrentSignalListener() {

        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue >= WIDTH_CURRENT_SIGNAL_SPINNER_MIN) &&
                    (newValue <= WIDTH_CURRENT_SIGNAL_SPINNER_MAX)) {
                sendVAPRegisters();
            }
        });

    }

    private void setupSpinnerStyleWhenValueChangedListener() {

        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(widthCurrentSignal, voltageController.getWidth(), newValue));

        firstWSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(firstWSpinner, voltageController.getFirstWidth(), newValue));

        boostISpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(boostISpinner, voltageController.getBoostI(), newValue));

        firstISpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(firstISpinner, voltageController.getFirstCurrent(), newValue));

        secondISpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(secondISpinner, voltageController.getSecondCurrent(), newValue));

        batteryUSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(batteryUSpinner, voltageController.getBatteryU(), newValue));

        negativeUSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(negativeUSpinner, voltageController.getNegativeU(), newValue));

        boostUSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                setDefaultStyle(boostUSpinner, voltageController.getVoltage(), newValue));

    }

    private void setDefaultStyle(Spinner<? extends Number> spinner, Label label, Number newValue) {

        if (newValue.toString().equals(label.getText())) {
            spinner.getEditor().setStyle(null);
            label.setStyle(null);
        }

    }

    private void sendVAPRegisters() {

        int negativeValue = negativeUSpinner.getValue();
        double boostIValue = boostISpinner.getValue();
        double firstIValue = firstISpinner.getValue();
        double secondIValue = secondISpinner.getValue();
        int firstWValue = firstWSpinner.getValue();
        int widthValue = widthCurrentSignal.getValue();
        boolean boostToggleButtonSelected = enableBoostToggleButton.isSelected();

        firstIValue = (boostIValue - firstIValue >= 0.5) ? firstIValue : boostIValue - 0.5;
        secondIValue = (firstIValue - secondIValue >= 0.5) ? secondIValue : firstIValue - 0.5;
        if ((widthValue - firstWValue <= MAX_DELTA_WIDTH_TO_FIRST_WIDTH)) {
            firstWValue = widthValue - MAX_DELTA_WIDTH_TO_FIRST_WIDTH;
            secondIValue = firstIValue - 0.6d;
        }

        ultimaModbusWriter.add(Boost_U, boostUSpinner.getValue());
        ultimaModbusWriter.add(Battery_U, batteryUSpinner.getValue());
        ultimaModbusWriter.add(Negative_U, negativeValue);
        ultimaModbusWriter.add(Negative_U2, 12); // Not relevant, but necessary for hardware correct initialisation
        ultimaModbusWriter.add(BoostIBoardOne, (int) (boostIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstIBoardOne, (int) (firstIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(SecondIBoardOne, (int) (secondIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstWBoardOne, firstWValue);
        ultimaModbusWriter.add(WidthBoardOne, widthValue);
        ultimaModbusWriter.add(BoostIBoardTwo, (int) (boostIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstIBoardTwo, (int) (firstIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(SecondIBoardTwo, (int) (secondIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstWBoardTwo, firstWValue);
        ultimaModbusWriter.add(WidthBoardTwo, widthValue);
        ultimaModbusWriter.add(BoostIBoardThree, (int) (boostIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstIBoardThree, (int) (firstIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(SecondIBoardThree, (int) (secondIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstWBoardThree, firstWValue);
        ultimaModbusWriter.add(WidthBoardThree, widthValue);
        ultimaModbusWriter.add(BoostIBoardFour, (int) (boostIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstIBoardFour, (int) (firstIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(SecondIBoardFour, (int) (secondIValue * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(FirstWBoardFour, firstWValue);
        ultimaModbusWriter.add(WidthBoardFour, widthValue);
        ultimaModbusWriter.add(StartOnBatteryUOne, boostToggleButtonSelected);
        ultimaModbusWriter.add(StartOnBatteryUTwo, boostToggleButtonSelected);
        ultimaModbusWriter.add(StartOnBatteryUThree, boostToggleButtonSelected);
        ultimaModbusWriter.add(StartOnBatteryUFour, boostToggleButtonSelected);
    }

    public void saveValues() {

        firstWSavedValue = firstWSpinner.getValue();
        boostISavedValue = boostISpinner.getValue();
        firstISavedValue = firstISpinner.getValue();
        secondISavedValue = secondISpinner.getValue();
        batteryUSavedValue = batteryUSpinner.getValue();
        negativeUSavedValue = negativeUSpinner.getValue();
        boostUSavedValue = boostUSpinner.getValue();

    }

    private void bindingI18N() {
        firstWlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.firstW"));
        batteryUlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.batteryU"));
        boostIlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostI"));
        boostUlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostU"));
        firstIlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.firstI"));
        negativeUlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.neagtiveU1"));
        secondIlabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.secondI"));
        applyButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.apply"));
        cancelButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
    }
}