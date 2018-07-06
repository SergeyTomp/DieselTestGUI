package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.spinners.WidthSpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
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
    private Spinner<Integer> negativeUSpinner;

    @FXML
    private Spinner<Double> secondISpinner;

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

    private int negativeUSavedValue;

    private int boostUSavedValue;

    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private Stage stage;

    private VoltageController voltageController;

    private List<Spinner> listOfVAPSpinners = new ArrayList<>();

    private WidthSpinnerValueObtainer widthCurrentSignalValueObtainer = new WidthSpinnerValueObtainer(300);

    private InjectorSectionController injectorSectionController;

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

    public Spinner<Integer> getNegativeUSpinner() {
        return negativeUSpinner;
    }

    public Spinner<Double> getSecondISpinner() {
        return secondISpinner;
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

    public void setFirmwareDataConverter(FirmwareDataConverter firmwareDataConverter) {
        this.firmwareDataConverter = firmwareDataConverter;
    }

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
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

    // FIXME: при изменении значения в спиннере которое равно значению с прошивки красным перестают гореть оба значения, хотя значение спиннера еще не было подтверждено нажатием Apply
    @PostConstruct
    private void init() {

        setupEnableBoostToggleButton();

        setupVAPSpinners();

        injectorSectionController.getPiezoCoilToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == injectorSectionController.getPiezoRadioButton()) {
                boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 350, 60, 1));
            } else {
                if (firmwareDataConverter.convertDataToInt(voltageController.getVoltage().getText()) > 75)
                    ultimaModbusWriter.add(ModbusMapUltima.Boost_U, 60);
                boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 75, 60, 1));
            }
        });

        setupApplyButton();

        setupCancelButton();

        widthCurrentSignal.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue >= 120) && (newValue <= 15500) && (!(newValue == widthCurrentSignalValueObtainer.getGeneratedFakeValue()))) {
                sendVAPRegisters();
            }
        });

        setupSpinnerStyleWhenValueChangedListener();

    }

    private void setupEnableBoostToggleButton() {

        enableBoostToggleButton.setSelected(true);
        enableBoostToggleButton.setText("Boost_U disabled");
        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                enableBoostToggleButton.setText("Boost_U disabled");
            } else {
                enableBoostToggleButton.setText("Boost_U enabled");
            }
        });

    }

    private void setupVAPSpinners() {

        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(90, 15500, 500, 10));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(3, 25.5, 21.5, 0.1));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2, 25.5, 15, 0.1));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 25.5, 5.5, 0.1));
        batteryUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(11, 32, 20, 1));
        negativeUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(17, 121, 48, 1));
        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 75, 60, 1));

        SpinnerManager.setupSpinner(widthCurrentSignal, 300, 120, 15500, new CustomTooltip(), widthCurrentSignalValueObtainer);
        SpinnerManager.setupSpinner(firstWSpinner, 500, 90, 15500, new CustomTooltip(), new SpinnerValueObtainer(500));
        SpinnerManager.setupSpinner(boostISpinner, 21.5, 21.51, new CustomTooltip(), new SpinnerValueObtainer(21.5));
        SpinnerManager.setupSpinner(firstISpinner, 15, 15.01, new CustomTooltip(), new SpinnerValueObtainer(15));
        SpinnerManager.setupSpinner(secondISpinner, 5.5, 5.51, new CustomTooltip(), new SpinnerValueObtainer(5.5));
        SpinnerManager.setupSpinner(batteryUSpinner, 20, 11, 32, new CustomTooltip(), new SpinnerValueObtainer(20));
        SpinnerManager.setupSpinner(negativeUSpinner, 48, 17, 121, new CustomTooltip(), new SpinnerValueObtainer(48));
        SpinnerManager.setupSpinner(boostUSpinner, 60, 30, 75, new CustomTooltip(), new SpinnerValueObtainer(60));

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

        negativeUSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            Label negativeULabel = voltageController.getNegativeU();
            if (newValue.toString().equals(negativeULabel.getText()))
                setDefaultStyle(negativeUSpinner, negativeULabel);
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

    private void sendVAPRegisters() {

        int negativeValue = negativeUSpinner.getValue();
        double boostIValue = boostISpinner.getValue();
        double firstIValue = firstISpinner.getValue();
        double secondIValue = secondISpinner.getValue();
        int firstWValue = firstWSpinner.getValue();
        int widthValue = widthCurrentSignal.getValue();
        boolean boostToggleButtonSelected = enableBoostToggleButton.isSelected();

        firstIValue = (boostIValue - firstIValue >= 0.5) ? firstIValue : firstIValue - 0.5;
        secondIValue = (firstIValue - secondIValue >= 0.5) ? secondIValue : secondIValue - 0.5;
        firstWValue = (widthValue - firstWValue >= 30) ? firstWValue : firstWValue - 30;

        ultimaModbusWriter.add(ModbusMapUltima.Boost_U, boostUSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Battery_U, batteryUSpinner.getValue());
        ultimaModbusWriter.add(ModbusMapUltima.Negative_U, negativeValue);
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
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUOne, boostToggleButtonSelected);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUTwo, boostToggleButtonSelected);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUThree, boostToggleButtonSelected);
        ultimaModbusWriter.add(ModbusMapUltima.StartOnBatteryUFour, boostToggleButtonSelected);

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

}