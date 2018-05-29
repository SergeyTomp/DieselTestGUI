package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.util.SpinnerManager;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class VoltAmpereProfileController {

    @FXML
    private Spinner<Integer> firstWSpinner;

    @FXML
    private Spinner<Integer> batteryUSpinner;

    @FXML
    private Spinner<Double> boostISpinner;

    @FXML
    private Spinner<Integer> boostUSpinner;

    @FXML
    private Spinner<Double> firstISpinner;

    @FXML
    private Spinner<Integer> negativeU1Spinner;

    @FXML
    private Spinner<Double> secondISpinner;

    @FXML
    private Spinner<Integer> negativeU2Spinner;

    @FXML
    private ToggleButton enableBoostToggleButton;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    private int firstWValue;

    private double boostIValue;

    private double firstIValue;

    private double secondIValue;

    private int batteryUValue;

    private int negativeU1Value;

    private int negativeU2Value;

    private int boostUValue;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private Stage stage;

    private Spinner<Integer> widthCurrentSignal;

    private boolean boostToggleButtonEnabled = true;

    private VoltageController voltageController;

    private List<Spinner> listOfVAPSpinners = new ArrayList<>();

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

    public Button getCancelButton() {
        return cancelButton;
    }

    @PostConstruct
    private void init() {

        setupEnableBoostToggleButton();

        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(91, 15510, 500, 10));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(3, 25, 21.5, 0.1));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2, 25, 15, 0.1));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 25, 5.5, 0.1));
        batteryUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(11, 32, 20, 1));
        negativeU1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(17, 100, 48, 1));
        negativeU2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(12, 70, 36, 1));
        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(40, 75, 60, 1));

        SpinnerManager.setupSpinner(firstWSpinner, 500, 15503, new CustomTooltip());
        SpinnerManager.setupSpinner(boostISpinner, 21.5, 21.51, new CustomTooltip());
        SpinnerManager.setupSpinner(firstISpinner, 15, 15.01, new CustomTooltip());
        SpinnerManager.setupSpinner(secondISpinner, 5.5, 5.51, new CustomTooltip());
        SpinnerManager.setupSpinner(batteryUSpinner, 20, 13, new CustomTooltip());
        SpinnerManager.setupSpinner(negativeU1Spinner, 48, 94, new CustomTooltip());
        SpinnerManager.setupSpinner(negativeU2Spinner, 36, 67, new CustomTooltip());
        SpinnerManager.setupSpinner(boostUSpinner, 60, 71, new CustomTooltip());

        listOfVAPSpinners.add(firstWSpinner);
        listOfVAPSpinners.add(boostISpinner);
        listOfVAPSpinners.add(firstISpinner);
        listOfVAPSpinners.add(secondISpinner);
        listOfVAPSpinners.add(batteryUSpinner);
        listOfVAPSpinners.add(negativeU1Spinner);
        listOfVAPSpinners.add(negativeU2Spinner);
        listOfVAPSpinners.add(boostUSpinner);
        listOfVAPSpinners.add(widthCurrentSignal);

        listOfVAPSpinners.forEach(e -> e.setEditable(true));

        setupApplyButton();

        setupCancelButton();

        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue >= 0) && (newValue <= 3000) && (!(newValue == 2993)))
                sendVAPRegisters();
        });

    }

    private void setupEnableBoostToggleButton() {
        enableBoostToggleButton.setSelected(boostToggleButtonEnabled);
        enableBoostToggleButton.setText("Boost_U enabled");
        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                enableBoostToggleButton.setText("Boost_U enabled");

            } else {
                enableBoostToggleButton.setText("Boost_U disabled");
            }
            boostToggleButtonEnabled = newValue;
        });
    }

    private void setupApplyButton() {
        applyButton.setOnAction(event -> {
            listOfVAPSpinners.forEach(e -> e.increment(0));
            sendVAPRegisters();
            voltageController.refreshVoltageLabels(boostUSpinner.getValue(), firstWSpinner.getValue(), firstISpinner.getValue(), secondISpinner.getValue());
            stage.close();
        });
    }

    // TODO: есть баги, не пишется старое значение при закрытии окна при горящем tooltip
    private void setupCancelButton() {
        cancelButton.setOnAction(event -> {
            firstWSpinner.getValueFactory().setValue(firstWValue);
            boostISpinner.getValueFactory().setValue(boostIValue);
            firstISpinner.getValueFactory().setValue(firstIValue);
            secondISpinner.getValueFactory().setValue(secondIValue);
            batteryUSpinner.getValueFactory().setValue(batteryUValue);
            negativeU1Spinner.getValueFactory().setValue(negativeU1Value);
            negativeU2Spinner.getValueFactory().setValue(negativeU2Value);
            boostUSpinner.getValueFactory().setValue(boostUValue);
            stage.close();
        });
    }

    private void sendVAPRegisters() {
        ultimaModbusWriter.add(ModbusMapUltima.Boost_U, boostUSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Battery_U, batteryUSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Negative_U1, negativeU1Spinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Negative_U2, negativeU2Spinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardOne, boostISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardOne, firstISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardOne, secondISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardOne, firstWSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardOne, widthCurrentSignal.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardTwo, boostISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardTwo, firstISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardTwo, secondISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardTwo, firstWSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardTwo, widthCurrentSignal.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardThree, boostISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardThree, firstISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardThree, secondISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardThree, firstWSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardThree, widthCurrentSignal.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardFour, boostISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardFour, firstISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardFour, secondISpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardFour, firstWSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardFour, widthCurrentSignal.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUOne, boostToggleButtonEnabled);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUTwo, boostToggleButtonEnabled);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUThree, boostToggleButtonEnabled);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUFour, boostToggleButtonEnabled);
        System.err.println("boostUSpinner: " + boostUSpinner.getValue());
        System.err.println("batteryUSpinner: " + batteryUSpinner.getValue());
        System.err.println("negativeU1Spinner: " + negativeU1Spinner.getValue());
        System.err.println("negativeU2Spinner: " + negativeU2Spinner.getValue());
        System.err.println("boostISpinner: " + boostISpinner.getValue());
        System.err.println("firstISpinner: " + firstISpinner.getValue());
        System.err.println("secondISpinner: " + secondISpinner.getValue());
        System.err.println("firstWSpinner: " + firstWSpinner.getValue());
        System.err.println("widthCurrentSignal: " + widthCurrentSignal.getValue());
        System.err.println("StartOnBatteryUOne: " + boostToggleButtonEnabled);
        System.err.println("StartOnBatteryUTwo: " + boostToggleButtonEnabled);
        System.err.println("StartOnBatteryUThree: " + boostToggleButtonEnabled);
        System.err.println("StartOnBatteryUFour: " + boostToggleButtonEnabled);
    }

    public void saveValues() {
        firstWValue = firstWSpinner.getValue();
        boostIValue = boostISpinner.getValue();
        firstIValue = firstISpinner.getValue();
        secondIValue = secondISpinner.getValue();
        batteryUValue = batteryUSpinner.getValue();
        negativeU1Value = negativeU1Spinner.getValue();
        negativeU2Value = negativeU2Spinner.getValue();
        boostUValue = boostUSpinner.getValue();
    }
}