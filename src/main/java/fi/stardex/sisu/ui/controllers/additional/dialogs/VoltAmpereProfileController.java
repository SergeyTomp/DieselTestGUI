package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.parts.PiezoCoilToggleGroup;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.util.FirmwareDataConverter;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.spinners.WidthSpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private Spinner<Integer> widthCurrentSignal;

    @FXML
    private ToggleButton enableBoostToggleButton;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;

    private int firstWSavedValue;

    private double boostISavedValue;

    private double firstISavedValue;

    private double secondISavedValue;

    private int batteryUSavedValue;

    private int negativeU1SavedValue;

    private int negativeU2SavedValue;

    private int boostUSavedValue;

    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private Stage stage;

    private boolean boostToggleButtonDisabled = true;

    private VoltageController voltageController;

    private List<Spinner> listOfVAPSpinners = new ArrayList<>();

    private WidthSpinnerValueObtainer widthCurrentSignalValueObtainer = new WidthSpinnerValueObtainer(300);

    private PiezoCoilToggleGroup piezoCoilToggleGroup;

    private FirmwareDataConverter firmwareDataConverter;

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

    public Spinner<Integer> getNegativeU1Spinner() {
        return negativeU1Spinner;
    }

    public Spinner<Double> getSecondISpinner() {
        return secondISpinner;
    }

    public Spinner<Integer> getNegativeU2Spinner() {
        return negativeU2Spinner;
    }

    public Spinner<Integer> getWidthCurrentSignal() {
        return widthCurrentSignal;
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

    public void setPiezoCoilToggleGroup(PiezoCoilToggleGroup piezoCoilToggleGroup) {
        this.piezoCoilToggleGroup = piezoCoilToggleGroup;
    }

    public void setFirmwareDataConverter(FirmwareDataConverter firmwareDataConverter) {
        this.firmwareDataConverter = firmwareDataConverter;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    @PostConstruct
    private void init() {

        setupEnableBoostToggleButton();

        //TODO: Boost U для Piezo/PiezoDelphi [30; 350]
        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(90, 15500, 500, 10));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(3, 25.5, 21.5, 0.1));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2, 25.5, 15, 0.1));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 25.5, 5.5, 0.1));
        batteryUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(11, 32, 20, 1));
        negativeU1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(17, 121, 48, 1));
        negativeU2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(12, 70, 36, 1));
        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 75, 60, 1));

        piezoCoilToggleGroup.getPiezoCoilToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == piezoCoilToggleGroup.getPiezoRadioButton()) {
                boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 350, 60, 1));
            } else {
                if (firmwareDataConverter.convertDataToInt(voltageController.getVoltage().getText()) > 75)
                    ultimaModbusWriter.add(ModbusMapUltima.Boost_U, 60);
                boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 75, 60, 1));
            }
        });

        SpinnerManager.setupSpinner(widthCurrentSignal, 300, 120, 15500, new CustomTooltip(), widthCurrentSignalValueObtainer);
        SpinnerManager.setupSpinner(firstWSpinner, 500, 90, 15500, new CustomTooltip(), new SpinnerValueObtainer(500));
        SpinnerManager.setupSpinner(boostISpinner, 21.5, 21.51, new CustomTooltip(), new SpinnerValueObtainer(21.5));
        SpinnerManager.setupSpinner(firstISpinner, 15, 15.01, new CustomTooltip(), new SpinnerValueObtainer(15));
        SpinnerManager.setupSpinner(secondISpinner, 5.5, 5.51, new CustomTooltip(), new SpinnerValueObtainer(5.5));
        SpinnerManager.setupSpinner(batteryUSpinner, 20, 11, 32, new CustomTooltip(), new SpinnerValueObtainer(20));
        SpinnerManager.setupSpinner(negativeU1Spinner, 48, 17, 121, new CustomTooltip(), new SpinnerValueObtainer(48));
        SpinnerManager.setupSpinner(negativeU2Spinner, 36, 12, 70, new CustomTooltip(), new SpinnerValueObtainer(36));
        SpinnerManager.setupSpinner(boostUSpinner, 60, 30, 75, new CustomTooltip(), new SpinnerValueObtainer(60));

        listOfVAPSpinners.add(widthCurrentSignal);
        listOfVAPSpinners.add(firstWSpinner);
        listOfVAPSpinners.add(boostISpinner);
        listOfVAPSpinners.add(firstISpinner);
        listOfVAPSpinners.add(secondISpinner);
        listOfVAPSpinners.add(batteryUSpinner);
        listOfVAPSpinners.add(negativeU1Spinner);
        listOfVAPSpinners.add(negativeU2Spinner);
        listOfVAPSpinners.add(boostUSpinner);

        listOfVAPSpinners.forEach(e -> e.setEditable(true));

        setupApplyButton();

        setupCancelButton();

        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue >= 120) && (newValue <= 15500) && (!(newValue == widthCurrentSignalValueObtainer.getGeneratedFakeValue()))) {
                sendVAPRegisters();
            }
        });

        setupSpinnerStyleWhenValueChangedListener();
    }

    private void setupSpinnerStyleWhenValueChangedListener() {
        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label widthLabel = voltageController.getWidth();
            if (newValue.toString().equals(widthLabel.getText()))
                setDefaultStyle(widthCurrentSignal, widthLabel);
        });

        firstWSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label firstWLabel = voltageController.getFirstWidth();
            if (newValue.toString().equals(firstWLabel.getText()))
                setDefaultStyle(firstWSpinner, firstWLabel);
        });

        boostISpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label boostILabel = voltageController.getBoostI();
            if (newValue.toString().equals(boostILabel.getText()))
                setDefaultStyle(boostISpinner, boostILabel);
        });

        firstISpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label firstILabel = voltageController.getFirstCurrent();
            if (newValue.toString().equals(firstILabel.getText()))
                setDefaultStyle(firstISpinner, firstILabel);
        });

        secondISpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label secondILabel = voltageController.getSecondCurrent();
            if (newValue.toString().equals(secondILabel.getText()))
                setDefaultStyle(secondISpinner, secondILabel);
        });

        batteryUSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label batteryULabel = voltageController.getBatteryU();
            if (newValue.toString().equals(batteryULabel.getText()))
                setDefaultStyle(batteryUSpinner, batteryULabel);
        });

        negativeU1Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label negativeU1Label = voltageController.getNegativeU1();
            if (newValue.toString().equals(negativeU1Label.getText()))
                setDefaultStyle(negativeU1Spinner, negativeU1Label);
        });

        negativeU2Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label negativeU2Label = voltageController.getNegativeU2();
            if (newValue.toString().equals(negativeU2Label.getText()))
                setDefaultStyle(negativeU2Spinner, negativeU2Label);
        });

        boostUSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label boostULabel = voltageController.getVoltage();
            if (newValue.toString().equals(boostULabel.getText()))
                setDefaultStyle(boostUSpinner, boostULabel);
        });
    }

    private void setDefaultStyle(Spinner<? extends Number> spinner, Label label) {
        spinner.getEditor().setStyle(null);
        label.setStyle(null);
    }

    private void setupEnableBoostToggleButton() {
        enableBoostToggleButton.setSelected(boostToggleButtonDisabled);
        enableBoostToggleButton.setText("Boost_U disabled");
        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                enableBoostToggleButton.setText("Boost_U disabled");
            } else {
                enableBoostToggleButton.setText("Boost_U enabled");
            }
            boostToggleButtonDisabled = newValue;
        });
    }

    // FIXME: не меняется на графике boost enabled/disabled при нажатии Apply, только после перезапуска инжекторной секции
    private void setupApplyButton() {
        applyButton.setOnAction(event -> {
            listOfVAPSpinners.forEach(e -> e.increment(0));
            sendVAPRegisters();
            stage.close();
        });
    }

    // TODO: есть баги, не пишется старое значение при закрытии окна при горящем tooltip
    private void setupCancelButton() {
        cancelButton.setOnAction(event -> {
            firstWSpinner.getValueFactory().setValue(firstWSavedValue);
            boostISpinner.getValueFactory().setValue(boostISavedValue);
            firstISpinner.getValueFactory().setValue(firstISavedValue);
            secondISpinner.getValueFactory().setValue(secondISavedValue);
            batteryUSpinner.getValueFactory().setValue(batteryUSavedValue);
            negativeU1Spinner.getValueFactory().setValue(negativeU1SavedValue);
            negativeU2Spinner.getValueFactory().setValue(negativeU2SavedValue);
            boostUSpinner.getValueFactory().setValue(boostUSavedValue);
            stage.close();
        });
    }

    private void sendVAPRegisters() {

        int negative1Value = negativeU1Spinner.getValue();
        int negative2Value = negativeU2Spinner.getValue();
        double boostIValue = boostISpinner.getValue();
        double firstIValue = firstISpinner.getValue();
        double secondIValue = secondISpinner.getValue();
        int firstWValue = firstWSpinner.getValue();
        int widthValue = widthCurrentSignal.getValue();

        negative1Value = (negative1Value - negative2Value >= 5) ? negative1Value : negative1Value + 5;
        firstIValue = (boostIValue - firstIValue >= 0.5) ? firstIValue : firstIValue - 0.5;
        secondIValue = (firstIValue - secondIValue >= 0.5) ? secondIValue : secondIValue - 0.5;
        firstWValue = (widthValue - firstWValue >= 30) ? firstWValue : firstWValue - 30;

        ultimaModbusWriter.add(ModbusMapUltima.Boost_U, boostUSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Battery_U, batteryUSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Negative_U1, negative1Value);
        ultimaModbusWriter.add(ModbusMapUltima.Negative_U2, negative2Value);
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardOne, boostIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardOne, firstIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardOne, secondIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardOne, firstWValue);
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardOne, widthValue);
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardTwo, boostIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardTwo, firstIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardTwo, secondIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardTwo, firstWValue);
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardTwo, widthValue);
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardThree, boostIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardThree, firstIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardThree, secondIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardThree, firstWValue);
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardThree, widthValue);
        ultimaModbusWriter.add(ModbusMapUltima.BoostIBoardFour, boostIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardFour, firstIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.SecondIBoardFour, secondIValue * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(ModbusMapUltima.FirstWBoardFour, firstWValue);
        ultimaModbusWriter.add(ModbusMapUltima.WidthBoardFour, widthValue);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUOne, boostToggleButtonDisabled);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUTwo, boostToggleButtonDisabled);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUThree, boostToggleButtonDisabled);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUFour, boostToggleButtonDisabled);
        System.err.println("boostUSpinner: " + boostUSpinner.getValue());
        System.err.println("batteryUSpinner: " + batteryUSpinner.getValue());
        System.err.println("negativeU1Spinner: " + negative1Value);
        System.err.println("negativeU2Spinner: " + negative2Value);
        System.err.println("boostISpinner: " + boostIValue);
        System.err.println("firstISpinner: " + firstIValue);
        System.err.println("secondISpinner: " + secondIValue);
        System.err.println("firstWSpinner: " + firstWValue);
        System.err.println("widthCurrentSignal: " + widthValue);
        System.err.println("StartOnBatteryUOne: " + boostToggleButtonDisabled);
        System.err.println("StartOnBatteryUTwo: " + boostToggleButtonDisabled);
        System.err.println("StartOnBatteryUThree: " + boostToggleButtonDisabled);
        System.err.println("StartOnBatteryUFour: " + boostToggleButtonDisabled);
    }

    public void saveValues() {
        firstWSavedValue = firstWSpinner.getValue();
        boostISavedValue = boostISpinner.getValue();
        firstISavedValue = firstISpinner.getValue();
        secondISavedValue = secondISpinner.getValue();
        batteryUSavedValue = batteryUSpinner.getValue();
        negativeU1SavedValue = negativeU1Spinner.getValue();
        negativeU2SavedValue = negativeU2Spinner.getValue();
        boostUSavedValue = boostUSpinner.getValue();
    }
}