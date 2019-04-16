package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.InjectorSectionUpdateModel;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.VoltAmpereProfileDialogModel;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.converters.DataConverter.round;

public class VoltAmpereProfileController {

    @FXML private Label firstW2Label;
    @FXML private Spinner<Integer> firstW2Spinner;
    @FXML private Label boostI2Label;
    @FXML private Spinner<Double> boostI2Spinner;
    @FXML private Label firstI2Label;
    @FXML private Spinner<Double> firstI2Spinner;
    @FXML private Label secondI2Label;
    @FXML private Spinner<Double> secondI2Spinner;
    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
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

    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";

    private I18N i18N;
    private int firstWSavedValue;
    private double boostISavedValue;
    private double firstISavedValue;
    private double secondISavedValue;
    private int batteryUSavedValue;
    private int negativeUSavedValue;
    private int boostUSavedValue;
    private double firstI2SavedValue;
    private double secondI2SavedValue;
    private double boostI2SavedValue;
    private int firstW2SavedValue;
    private static final float ONE_AMPERE_MULTIPLY = 93.07f;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private Stage stage;
    private VoltAmpereProfileDialogModel voltAmpereProfileDialogModel;
    private List<Spinner> listOfVAPSpinners = new ArrayList<>();
    private CoilOnePulseParametersModel coilOnePulseParametersModel;
    private CoilTwoPulseParametersModel coilTwoPulseParametersModel;
    private InjectorTestModel injectorTestModel;
    private InjectorSectionUpdateModel injectorSectionUpdateModel;
    private InjectorModel injectorModel;
    private InjectorTypeModel injectorTypeModel;
    private CoilPulseCalculator coilPulseCalculator;
    private VoltAmpereProfile currentVAP;

    private Logger logger = LoggerFactory.getLogger(VoltAmpereProfileController.class);

    public Spinner<Integer> getBoostUSpinner() {
        return boostUSpinner;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getApplyButton() {
        return applyButton;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setVoltAmpereProfileDialogModel(VoltAmpereProfileDialogModel voltAmpereProfileDialogModel) {
        this.voltAmpereProfileDialogModel = voltAmpereProfileDialogModel;
    }

    public void setCoilOnePulseParametersModel(CoilOnePulseParametersModel coilOnePulseParametersModel) {
        this.coilOnePulseParametersModel = coilOnePulseParametersModel;
    }

    public void setCoilTwoPulseParametersModel(CoilTwoPulseParametersModel coilTwoPulseParametersModel) {
        this.coilTwoPulseParametersModel = coilTwoPulseParametersModel;
    }

    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    public void setInjectorSectionUpdateModel(InjectorSectionUpdateModel injectorSectionUpdateModel) {
        this.injectorSectionUpdateModel = injectorSectionUpdateModel;
    }

    public void setInjectorModel(InjectorModel injectorModel) {
        this.injectorModel = injectorModel;
    }

    public void setInjectorTypeModel(InjectorTypeModel injectorTypeModel) {
        this.injectorTypeModel = injectorTypeModel;
    }

    private enum Invocator {
        TEST,
        SPINNER,
        DIALOG
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

        setupTestModelListener();

        bindingI18N();

    }

    private void setupTestModelListener() {

        injectorTestModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null && newValue.getTestName().getMeasurement() != Measurement.NO) {

                currentVAP = newValue.getVoltAmpereProfile();

                int firstW = currentVAP.getFirstW();
                Integer width = newValue.getTotalPulseTime();
                firstW = (width - firstW >= MAX_DELTA_WIDTH_TO_FIRST_WIDTH) ? firstW : width - MAX_DELTA_WIDTH_TO_FIRST_WIDTH;
                firstWSpinner.getValueFactory().setValue(firstW);

                if (currentVAP.isDoubleCoil()) {

                    firstW = currentVAP.getFirstW2();
                    width = newValue.getTotalPulseTime2();
                    firstW = (width - firstW >= MAX_DELTA_WIDTH_TO_FIRST_WIDTH) ? firstW : width - MAX_DELTA_WIDTH_TO_FIRST_WIDTH;
                    firstW2Spinner.getValueFactory().setValue(firstW);
                }

                setValuesToVapSpinners();
                sendVAPRegisters(Invocator.TEST);
            }
            else if(newValue == null){

                currentVAP = null;
                setInitialsToVapSpinners();
            }
        });
    }

    private void setValuesToVapSpinners() {

        setBoostUSpinnerValueFactory();

        double firstI = currentVAP.getFirstI();
        double secondI = currentVAP.getSecondI();
        double boostI = currentVAP.getBoostI();

        firstI = boostI - firstI >= 0.5 ? firstI : boostI - 0.5;
        secondI = firstI - secondI  >= 0.5 ? secondI : firstI - 0.5;

        boostUSpinner.getValueFactory().setValue(currentVAP.getBoostU());
        batteryUSpinner.getValueFactory().setValue(currentVAP.getBatteryU());

        boostISpinner.getValueFactory().setValue((boostI * 100 % 10 != 0) ? round(boostI) : boostI);
        firstISpinner.getValueFactory().setValue((firstI * 100 % 10 != 0) ? round(firstI) : firstI);
        secondISpinner.getValueFactory().setValue((secondI * 100 % 10 != 0) ? round(secondI) : secondI);
        firstWSpinner.getValueFactory().setValue(currentVAP.getFirstW());
        negativeUSpinner.getValueFactory().setValue(currentVAP.getNegativeU());
        voltAmpereProfileDialogModel.isDoubleCoilProperty().setValue(currentVAP.isDoubleCoil());
        voltAmpereProfileDialogModel.boostDisableProperty().setValue(currentVAP.getBoostDisable());
        enableBoostToggleButton.setSelected(currentVAP.getBoostDisable());

        Boolean isDoubleCoil = currentVAP.isDoubleCoil();

        activateCoil2Spinners(isDoubleCoil);

        if (isDoubleCoil) {

            firstI = currentVAP.getFirstI2();
            secondI = currentVAP.getSecondI2();
            boostI = currentVAP.getBoostI2();

            firstI = boostI - firstI >= 0.5 ? firstI : boostI - 0.5;
            secondI = firstI - secondI  >= 0.5 ? secondI : firstI - 0.5;
            firstW2Spinner.getValueFactory().setValue(currentVAP.getFirstW2());
            firstI2Spinner.getValueFactory().setValue((firstI * 100 % 10 != 0) ? round(firstI) : firstI);
            secondI2Spinner.getValueFactory().setValue((secondI * 100 % 10 != 0) ? round(secondI) : secondI);
            boostI2Spinner.getValueFactory().setValue((boostI * 100 % 10 != 0) ? round(boostI) : boostI);
        }
        saveDataToVapDialogModel(Invocator.TEST);
    }

    private void setInitialsToVapSpinners() {

        boostUSpinner.getValueFactory().setValue(BOOST_U_SPINNER_INIT);
        batteryUSpinner.getValueFactory().setValue(BATTERY_U_SPINNER_INIT);
        boostISpinner.getValueFactory().setValue(BOOST_I_SPINNER_INIT);
        firstISpinner.getValueFactory().setValue(FIRST_I_SPINNER_INIT);
        secondISpinner.getValueFactory().setValue(SECOND_I_SPINNER_INIT);
        firstWSpinner.getValueFactory().setValue(FIRST_W_SPINNER_INIT);
        negativeUSpinner.getValueFactory().setValue(NEGATIVE_U_SPINNER_INIT);

        enableBoostToggleButton.setSelected(false);
        activateCoil2Spinners(false);
        firstW2Spinner.getValueFactory().setValue(FIRST_W_SPINNER_INIT);
        firstI2Spinner.getValueFactory().setValue(FIRST_I_SPINNER_INIT);
        secondI2Spinner.getValueFactory().setValue(SECOND_I_SPINNER_INIT);
        boostI2Spinner.getValueFactory().setValue(BOOST_I_SPINNER_INIT);
        voltAmpereProfileDialogModel.isDoubleCoilProperty().setValue(false);
        voltAmpereProfileDialogModel.boostDisableProperty().setValue(false);

        saveDataToVapDialogModel(Invocator.TEST);
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

        activateCoil2Spinners(false);

//        SpinnerManager.setupIntegerSpinner(widthCurrentSignal);
        SpinnerManager.setupIntegerSpinner(firstWSpinner);
        SpinnerManager.setupDoubleSpinner(boostISpinner);
        SpinnerManager.setupDoubleSpinner(firstISpinner);
        SpinnerManager.setupDoubleSpinner(secondISpinner);
        SpinnerManager.setupIntegerSpinner(batteryUSpinner);
        SpinnerManager.setupIntegerSpinner(negativeUSpinner);
        SpinnerManager.setupIntegerSpinner(boostUSpinner);

        SpinnerManager.setupIntegerSpinner(firstW2Spinner);
        SpinnerManager.setupDoubleSpinner(boostI2Spinner);
        SpinnerManager.setupDoubleSpinner(firstI2Spinner);
        SpinnerManager.setupDoubleSpinner(secondI2Spinner);

//        listOfVAPSpinners.add(widthCurrentSignal);
        listOfVAPSpinners.add(firstWSpinner);
        listOfVAPSpinners.add(boostISpinner);
        listOfVAPSpinners.add(firstISpinner);
        listOfVAPSpinners.add(secondISpinner);
        listOfVAPSpinners.add(batteryUSpinner);
        listOfVAPSpinners.add(negativeUSpinner);
        listOfVAPSpinners.add(boostUSpinner);
        listOfVAPSpinners.add(firstW2Spinner);
        listOfVAPSpinners.add(boostI2Spinner);
        listOfVAPSpinners.add(firstI2Spinner);
        listOfVAPSpinners.add(secondI2Spinner);

        listOfVAPSpinners.forEach(e -> e.setEditable(true));

        firstW2Spinner.setDisable(true);
        boostI2Spinner.setDisable(true);
        firstI2Spinner.setDisable(true);
        secondI2Spinner.setDisable(true);
}

    private void setupPiezoCoilToggleGroupListener() {

        injectorTypeModel.injectorTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (currentVAP == null) {

                if (newValue == InjectorType.PIEZO || newValue == InjectorType.PIEZO_DELPHI) {
                    boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                            BOOST_U_SPINNER_MAX_PIEZO,
                            BOOST_U_SPINNER_INIT,
                            BATTERY_U_SPINNER_STEP));
                }else{

                    if (convertDataToInt(injectorSectionUpdateModel.boost_UProperty().get()) > BOOST_U_SPINNER_MAX)
                        ultimaModbusWriter.add(ModbusMapUltima.Boost_U, BOOST_U_SPINNER_INIT);
                    boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                            BOOST_U_SPINNER_MAX,
                            BOOST_U_SPINNER_INIT,
                            BOOST_U_SPINNER_STEP));
                }
            }
        });
    }

    private void setBoostUSpinnerValueFactory() {

        String injectorType = currentVAP.getInjectorType().getInjectorType();

        if (injectorType.equals("piezo") || injectorType.equals("piezoDelphi")) {
            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX_PIEZO,
                    BOOST_U_SPINNER_INIT,
                    BATTERY_U_SPINNER_STEP));
        }else{

            if (convertDataToInt(injectorSectionUpdateModel.boost_UProperty().get()) > BOOST_U_SPINNER_MAX){
                ultimaModbusWriter.add(ModbusMapUltima.Boost_U, BOOST_U_SPINNER_INIT);
            }
            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX,
                    BOOST_U_SPINNER_INIT,
                    BOOST_U_SPINNER_STEP));
        }
    }

    private void setupApplyButton() {

        applyButton.setOnAction(event -> {
            listOfVAPSpinners.forEach(e -> e.increment(0));
            saveDataToVapDialogModel(Invocator.DIALOG);
            sendVAPRegisters(Invocator.DIALOG);
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
            firstW2Spinner.getValueFactory().setValue(firstW2SavedValue);
            boostI2Spinner.getValueFactory().setValue(boostI2SavedValue);
            firstI2Spinner.getValueFactory().setValue(firstI2SavedValue);
            secondI2Spinner.getValueFactory().setValue(secondI2SavedValue);
            stage.close();
        });

    }

    private void setupWidthCurrentSignalListener() {

        coilOnePulseParametersModel.widthProperty().addListener((observable, oldValue, newValue) -> {

            if (coilOnePulseParametersModel.isValueFactorySetting() ||
                    injectorTestModel.isTestIsChanging()||
                    injectorModel.isInjectorIsChanging()) {
                return;
            }
            if ((newValue.intValue() >= WIDTH_CURRENT_SIGNAL_SPINNER_MIN) &&
                    (newValue.intValue() <= WIDTH_CURRENT_SIGNAL_SPINNER_MAX)) {
                sendVAPRegisters(Invocator.SPINNER);
            }
        });

        coilTwoPulseParametersModel.width_2Property().addListener((observable, oldValue, newValue) -> {

            if (coilOnePulseParametersModel.isValueFactorySetting() ||
                    injectorTestModel.isTestIsChanging()||
                    injectorModel.isInjectorIsChanging()) {
                return;
            }
            if (currentVAP.isDoubleCoil()) {
                if ((newValue.intValue() >= WIDTH_CURRENT_SIGNAL_SPINNER_MIN) &&
                        (newValue.intValue() <= WIDTH_CURRENT_SIGNAL_SPINNER_MAX)) {
                    sendVAPRegisters(Invocator.SPINNER);
                }
            }
        });
    }

    private void activateCoil2Spinners(boolean activate) {

        firstI2Spinner.setDisable(!activate);
        secondI2Spinner.setDisable(!activate);
        firstW2Spinner.setDisable(!activate);
        boostI2Spinner.setDisable(!activate);

        if (activate) {

            firstW2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(FIRST_W_SPINNER_MIN,
                    FIRST_W_SPINNER_MAX,
                    FIRST_W_SPINNER_INIT,
                    FIRST_W_SPINNER_STEP));
            boostI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(BOOST_I_SPINNER_MIN,
                    BOOST_I_SPINNER_MAX,
                    BOOST_I_SPINNER_INIT,
                    BOOST_I_SPINNER_STEP));
            firstI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(FIRST_I_SPINNER_MIN,
                    FIRST_I_SPINNER_MAX,
                    FIRST_I_SPINNER_INIT,
                    FIRST_I_SPINNER_STEP));
            secondI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(SECOND_I_SPINNER_MIN,
                    SECOND_I_SPINNER_MAX,
                    SECOND_I_SPINNER_INIT,
                    SECOND_I_SPINNER_STEP));
        }
        else{

            firstW2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0));
            boostI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0d));
            firstI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0d));
            secondI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0d));
        }
    }

    private void saveDataToVapDialogModel(Invocator who) {

        voltAmpereProfileDialogModel.boostUProperty().setValue(boostUSpinner.getValue());
        voltAmpereProfileDialogModel.batteryUProperty().setValue(batteryUSpinner.getValue());
        voltAmpereProfileDialogModel.negativeUProperty().setValue(negativeUSpinner.getValue());
        voltAmpereProfileDialogModel.firstWProperty().setValue(firstWSpinner.getValue());
        voltAmpereProfileDialogModel.firstIProperty().setValue(firstISpinner.getValue());
        voltAmpereProfileDialogModel.secondIProperty().setValue(secondISpinner.getValue());
        voltAmpereProfileDialogModel.boostIProperty().setValue(boostISpinner.getValue());
        voltAmpereProfileDialogModel.firstW2Property().setValue(firstW2Spinner.getValue());
        voltAmpereProfileDialogModel.firstI2Property().setValue(firstI2Spinner.getValue());
        voltAmpereProfileDialogModel.secondI2Property().setValue(secondI2Spinner.getValue());
        voltAmpereProfileDialogModel.boostI2Property().setValue(boostI2Spinner.getValue());
    }

    private void setupSpinnerStyleWhenValueChangedListener() {

        boostUSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(boostUSpinner, injectorSectionUpdateModel.boost_UProperty().get()));
        negativeUSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(negativeUSpinner, injectorSectionUpdateModel.negative_UProperty().get()));
        batteryUSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(batteryUSpinner, injectorSectionUpdateModel.battery_UProperty().get()));
        firstWSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstWSpinner, injectorSectionUpdateModel.first_WProperty().get()));
        boostISpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(boostISpinner, injectorSectionUpdateModel.boost_IProperty().get()));
        firstISpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstISpinner, injectorSectionUpdateModel.first_IProperty().get()));
        secondISpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(secondISpinner, injectorSectionUpdateModel.second_IProperty().get()));
        firstW2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstW2Spinner, injectorSectionUpdateModel.first_W2Property().get()));
        firstI2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstI2Spinner, injectorSectionUpdateModel.first_I2Property().get()));
        secondI2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(secondI2Spinner, injectorSectionUpdateModel.second_I2Property().get()));
        boostI2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(boostI2Spinner, injectorSectionUpdateModel.boost_I2Property().get()));
    }

    private void setColor(Spinner<? extends Number> spinner, String updaterValue) {

        if (spinner.getValue().toString().equals(updaterValue)) {

            spinner.getEditor().setStyle(null);
        }else{
            spinner.getEditor().setStyle(RED_COLOR_STYLE);
        }
    }

    private void sendVAPRegisters(Invocator who) {

//        System.err.println(who);
//        System.err.println("sendVAPRegisters");

        int negativeValue = negativeUSpinner.getValue();
        double boostIValue = boostISpinner.getValue();
        double firstIValue = firstISpinner.getValue();
        double secondIValue = secondISpinner.getValue();
        int firstWValue = firstWSpinner.getValue();
        int widthValue;
        switch (who) {
            case TEST:
                widthValue = injectorTestModel.injectorTestProperty().get().getTotalPulseTime();
                break;
            case SPINNER:
                widthValue = coilOnePulseParametersModel.widthProperty().get();
                break;
            default:
                widthValue = coilOnePulseParametersModel.widthProperty().get();
        }
        double boostI2Value = boostI2Spinner.getValue();
        double firstI2Value = firstI2Spinner.getValue();
        double secondI2Value = secondI2Spinner.getValue();
        int firstW2Value = firstW2Spinner.getValue();
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
        ultimaModbusWriter.add(StartOnBatteryUOne, boostToggleButtonSelected);

//        System.err.println("Boost_U " + boostUSpinner.getValue());
//        System.err.println("Battery_U " + batteryUSpinner.getValue());
//        System.err.println("Negative_U " + negativeValue);
//        System.err.println("BoostIBoardOne " + boostIValue);
//        System.err.println("FirstIBoardOne " + firstIValue);
//        System.err.println("SecondIBoardOne " + secondIValue);
//        System.err.println("FirstWBoardOne " + firstWValue);
//        System.err.println("WidthBoardOne " + widthValue);
//        System.err.println("BoostUOne " + boostToggleButtonSelected);
//        System.err.println();


        if (currentVAP != null && currentVAP.isDoubleCoil()) {

            int width2Value;
            switch (who) {
                case TEST:
                    width2Value = injectorTestModel.injectorTestProperty().get().getTotalPulseTime2();
                    break;
                case SPINNER:
                    width2Value = coilTwoPulseParametersModel.width_2Property().get();
                    break;
                default:
                    width2Value = coilTwoPulseParametersModel.width_2Property().get();
            }

            firstI2Value = (boostI2Value - firstI2Value >= 0.5) ? firstI2Value : boostI2Value - 0.5;
            secondI2Value = (firstI2Value - secondI2Value >= 0.5) ? secondI2Value : firstI2Value - 0.5;
            if ((width2Value - firstW2Value <= MAX_DELTA_WIDTH_TO_FIRST_WIDTH)) {
                firstW2Value = width2Value - MAX_DELTA_WIDTH_TO_FIRST_WIDTH;
                secondI2Value = firstI2Value - 0.6d;
            }

            ultimaModbusWriter.add(BoostIBoardTwo, (int) (boostI2Value * ONE_AMPERE_MULTIPLY));
            ultimaModbusWriter.add(FirstIBoardTwo, (int) (firstI2Value * ONE_AMPERE_MULTIPLY));
            ultimaModbusWriter.add(SecondIBoardTwo, (int) (secondI2Value * ONE_AMPERE_MULTIPLY));
            ultimaModbusWriter.add(FirstWBoardTwo, firstW2Value);
            ultimaModbusWriter.add(WidthBoardTwo, width2Value);
            ultimaModbusWriter.add(StartOnBatteryUTwo, boostToggleButtonSelected);
//            System.err.println("BoostIBoardTwo " + boostI2Value);
//            System.err.println("FirstIBoardTwo " + firstI2Value);
//            System.err.println("SecondIBoardTwo " + secondI2Value);
//            System.err.println("FirstWBoardTwo " + firstW2Value);
//            System.err.println("WidthBoardTwo " + width2Value);
//            System.err.println("BoostUTwo " + boostToggleButtonSelected);

        }
        else{

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
            ultimaModbusWriter.add(StartOnBatteryUTwo, boostToggleButtonSelected);
            ultimaModbusWriter.add(StartOnBatteryUThree, boostToggleButtonSelected);
            ultimaModbusWriter.add(StartOnBatteryUFour, boostToggleButtonSelected);
        }
    }

    public void saveValues() {

        firstWSavedValue = firstWSpinner.getValue();
        boostISavedValue = boostISpinner.getValue();
        firstISavedValue = firstISpinner.getValue();
        secondISavedValue = secondISpinner.getValue();
        batteryUSavedValue = batteryUSpinner.getValue();
        negativeUSavedValue = negativeUSpinner.getValue();
        boostUSavedValue = boostUSpinner.getValue();
        firstW2SavedValue = firstW2Spinner.getValue();
        boostI2SavedValue = boostI2Spinner.getValue();
        firstI2SavedValue = firstI2Spinner.getValue();
        secondI2SavedValue = secondI2Spinner.getValue();

        setColor(boostUSpinner, injectorSectionUpdateModel.boost_UProperty().get());
        setColor(negativeUSpinner, injectorSectionUpdateModel.negative_UProperty().get());
        setColor(batteryUSpinner, injectorSectionUpdateModel.battery_UProperty().get());
        setColor(firstWSpinner, injectorSectionUpdateModel.first_WProperty().get());
        setColor(boostISpinner, injectorSectionUpdateModel.boost_IProperty().get());
        setColor(firstISpinner, injectorSectionUpdateModel.first_IProperty().get());
        setColor(secondISpinner, injectorSectionUpdateModel.second_IProperty().get());
        setColor(firstW2Spinner, injectorSectionUpdateModel.first_W2Property().get());
        setColor(firstI2Spinner, injectorSectionUpdateModel.first_I2Property().get());
        setColor(secondI2Spinner, injectorSectionUpdateModel.second_I2Property().get());
        setColor(boostI2Spinner, injectorSectionUpdateModel.boost_I2Property().get());
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
        firstW2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.firstW"));
        boostI2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostI"));
        firstI2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.firstI"));
        secondI2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.secondI"));
        coil1Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil1"));
        coil2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil2"));
    }

    private class CoilPulseCalculator{

        private DoubleProperty i1_coil1_Property;
        private DoubleProperty i2_coil1_Property;
        private DoubleProperty i1_coil2_Property;
        private DoubleProperty i2_coil2_Property;
        private DoubleProperty boostI_coil1_Property;
        private DoubleProperty boostI_coil2_Property;
        private IntegerProperty pulseWidthCoil1_Property;
        private IntegerProperty pulseWidthCoil2_Property;
        private IntegerProperty widthCurrent1_coil1_Property;
        private IntegerProperty widthCurrent1_coil2_Property;

        public CoilPulseCalculator(CoilOnePulseParametersModel coilOnePulseParametersModel,
                                   CoilTwoPulseParametersModel coilTwoPulseParametersModel,
                                   VoltAmpereProfileDialogModel voltAmpereProfileDialogModel) {
            this.i1_coil1_Property = voltAmpereProfileDialogModel.firstIProperty();
            this.i2_coil1_Property = voltAmpereProfileDialogModel.secondIProperty();
            this.i1_coil2_Property = voltAmpereProfileDialogModel.firstI2Property();
            this.i2_coil2_Property = voltAmpereProfileDialogModel.secondI2Property();
            this.boostI_coil1_Property = voltAmpereProfileDialogModel.boostIProperty();
            this.boostI_coil2_Property = voltAmpereProfileDialogModel.boostI2Property();
            this.pulseWidthCoil1_Property = coilOnePulseParametersModel.widthProperty();
            this.pulseWidthCoil2_Property = coilTwoPulseParametersModel.width_2Property();
            this.widthCurrent1_coil1_Property = voltAmpereProfileDialogModel.firstWProperty();
            this.widthCurrent1_coil2_Property = voltAmpereProfileDialogModel.firstW2Property();
        }

        double getI1_coil1() {

            double boostI = boostI_coil1_Property.get();
            double i_1 = i1_coil1_Property.get();

            return getI(boostI, i_1);
        }
        double getI2_coli1() {

            double i_1 = getI1_coil1();
            double i_2 = i2_coil1_Property.get();

            return getI(i_1, i_2);
        }

        double getI1_coil2() {

            double boostI = boostI_coil2_Property.get();
            double i_1 = i1_coil2_Property.get();

            return getI(boostI, i_1);
        }

        double getI2_coli2() {

            double i_1 = getI1_coil2();
            double i_2 = i2_coil2_Property.get();

            return getI(i_1, i_2);
        }

        int getWidth1_coil1() {

            int width = pulseWidthCoil1_Property.get();
            int width_1 = widthCurrent1_coil1_Property.get();

            return getWidth(width, width_1);
        }

        int getWidth1_coil2() {

            int width = pulseWidthCoil2_Property.get();
            int width_1 = widthCurrent1_coil2_Property.get();
            return getWidth(width, width_1);
        }

        double getCorrectedI2_coil1() {

            int width = pulseWidthCoil1_Property.get();
            int width_1 = widthCurrent1_coil1_Property.get();

            return getCurrent2(width, width_1, getI1_coil1(), getI2_coli1());
        }

        double getCorrectedI2_coil2() {

            int width = pulseWidthCoil2_Property.get();
            int width_1 = widthCurrent1_coil2_Property.get();

            return getCurrent2(width, width_1, getI1_coil2(), getI2_coli2());

        }

        private double getI(double i1, double i2) {
            return (i1 - i2 >= 0.5) ? i2 : i1 - 0.5;
        }

        private int getWidth(int width, int width_1) { return width - width_1 >= MAX_DELTA_WIDTH_TO_FIRST_WIDTH ? width_1 : width; }

        private double getCurrent2(int width, int width_1, double i1, double i2) { return width - width_1 <= MAX_DELTA_WIDTH_TO_FIRST_WIDTH ? i2 : i1 - 0.6; }
    }
}