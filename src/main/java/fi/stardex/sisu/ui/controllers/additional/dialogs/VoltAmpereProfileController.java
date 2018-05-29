package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

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
    private Button cancelBtn;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private Stage stage;

    private Spinner<Integer> widthCurrentSignal;

    private boolean boostToggleButtonEnabled = true;

    private VoltageController voltageController;

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

        firstWSpinner.setEditable(true);
        boostISpinner.setEditable(true);
        firstISpinner.setEditable(true);
        secondISpinner.setEditable(true);
        batteryUSpinner.setEditable(true);
        negativeU1Spinner.setEditable(true);
        negativeU2Spinner.setEditable(true);
        boostUSpinner.setEditable(true);

        setupApplyButton();

//        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
//            if((newValue >= 0) && (newValue <= 3000) && (!(newValue == WIDTH_SPINNER_FAKE_VALUE)))
//                System.err.println("Value is: " + newValue);
//        });

        new VAPSettingsChangeListener();
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
            sendVAPRegisters();
            voltageController.refreshVoltageLabels(boostUSpinner.getValue(), firstWSpinner.getValue(), firstISpinner.getValue(), secondISpinner.getValue());
            stage.close();
        });
    }

    private class VAPSettingsChangeListener implements ChangeListener<Number> {

        VAPSettingsChangeListener() {
            boostUSpinner.valueProperty().addListener(this);
            batteryUSpinner.valueProperty().addListener(this);
            negativeU1Spinner.valueProperty().addListener(this);
            negativeU2Spinner.valueProperty().addListener(this);
            boostISpinner.valueProperty().addListener(this);
            firstISpinner.valueProperty().addListener(this);
            secondISpinner.valueProperty().addListener(this);
            firstWSpinner.valueProperty().addListener(this);
            widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((newValue >= 0) && (newValue <= 3000) && (!(newValue == InjectorSectionController.WIDTH_SPINNER_FAKE_VALUE)))
                    sendVAPRegisters();
            });
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

        }
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
}