package fi.stardex.sisu.ui.controllers.uis.windows;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.*;
import fi.stardex.sisu.model.updateModels.UisHardwareUpdateModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.converters.DataConverter.round;
import static fi.stardex.sisu.util.enums.GUI_type.UIS;
import static fi.stardex.sisu.util.enums.InjectorSubType.*;

public class UisVapController {

    @FXML private Label firstWlabel;
    @FXML private Label batteryUlabel;
    @FXML private Label boostIlabel;
    @FXML private Label boostUlabel;
    @FXML private Label firstIlabel;
    @FXML private Label negativeUlabel;
    @FXML private Label secondIlabel;
    @FXML private Label firstW2Label;
    @FXML private Label boostI2Label;
    @FXML private Label firstI2Label;
    @FXML private Label secondI2Label;
    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
    @FXML private Label BIP_PWM;
    @FXML private Label BIP_Window;
    @FXML private Spinner<Integer> bipWindowSpinner;
    @FXML private Spinner<Integer> bipPwmSpinner;
    @FXML private Spinner<Integer> firstWSpinner;
    @FXML private Spinner<Integer> firstW2Spinner;
    @FXML private Spinner<Integer> batteryUSpinner;
    @FXML private Spinner<Double> boostISpinner;
    @FXML private Spinner<Integer> boostUSpinner;
    @FXML private Spinner<Double> firstISpinner;
    @FXML private Spinner<Integer> negativeUSpinner;
    @FXML private Spinner<Double> secondISpinner;
    @FXML private Spinner<Double> boostI2Spinner;
    @FXML private Spinner<Double> firstI2Spinner;
    @FXML private Spinner<Double> secondI2Spinner;
    @FXML private ToggleButton enableBoostToggleButton;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;

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
    private int bipWindowSavedValue;
    private int bipPwmSavedValue;
    private static final float ONE_AMPERE_MULTIPLY = 93.07f;
    private UisVoltageTabModel uisVoltageTabModel;
    private UisVapModel uisVapModel;
    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private UisHardwareUpdateModel uisHardwareUpdateModel;
    private GUI_TypeModel gui_typeModel;
    private UisSettingsModel uisSettingsModel;


    private ModbusRegisterProcessor ultimaModbusWriter;
    private Parent vapDialogView;
    private Stage stage;
    private I18N i18N;
    private List<Spinner> listOfVAPSpinners = new ArrayList<>();
    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";
    private InjectorUisVAP currentVAP;
    private InjectorUisTest currentTest;
    private boolean isBipTest;
    private Logger log = LoggerFactory.getLogger(UisVapController.class);

    public void setUisVoltageTabModel(UisVoltageTabModel uisVoltageTabModel) {
        this.uisVoltageTabModel = uisVoltageTabModel;
    }
    public void setVapDialogView(Parent vapDialogView) {
        this.vapDialogView = vapDialogView;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisVapModel(UisVapModel uisVapModel) {
        this.uisVapModel = uisVapModel;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setUisHardwareUpdateModel(UisHardwareUpdateModel uisHardwareUpdateModel) {
        this.uisHardwareUpdateModel = uisHardwareUpdateModel;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setUisSettingsModel(UisSettingsModel uisSettingsModel) {
        this.uisSettingsModel = uisSettingsModel;
    }

    @PostConstruct
    public void init() {

        setupDisableBoostToggleButton();
        setupVAPSpinners();
        setupApplyButton();
        setupCancelButton();
        setupSpinnerStyleWhenValueChangedListener();
        setupVapListener();
        setupVoltageTabListener();
        bindingI18N();
        setupVoltageTabListener();
        setupPulseSequenceChangeListeners();
    }

    private enum Invocator {
        TEST, SPINNER, DIALOG
    }

    private void setupVapListener() {

        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

            /** Additional check of GUI type is done to prevent ClassCactExeption in sendVAPRegisters where Test is casted to InjectorUisTest.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf InjectorUisTest} but it is slower */
            if (gui_typeModel.guiTypeProperty().get() != UIS) { return; }

            if (newValue != null) {

                currentVAP = (InjectorUisVAP)newValue.getVoltAmpereProfile();
                currentTest = (InjectorUisTest)newValue;
                isBipTest = isBipTest(newValue);

                int firstW = currentVAP.getFirstW();
                Integer width = newValue.getTotalPulseTime1();
                firstW = (width - firstW >= MIN_DELTA_WIDTH_TO_FIRST_WIDTH) ? firstW : width - MIN_DELTA_WIDTH_TO_FIRST_WIDTH;
                firstWSpinner.getValueFactory().setValue(firstW);

                if (currentVAP.getInjectorSubType() == DOUBLE_COIL) {

                    firstW = currentVAP.getFirstW2();
                    width = width - newValue.getShift();
                    firstW = (width - firstW >= MIN_DELTA_WIDTH_TO_FIRST_WIDTH) ? firstW : width - MIN_DELTA_WIDTH_TO_FIRST_WIDTH;
                    firstW2Spinner.getValueFactory().setValue(firstW);
                }

                setValuesToVapSpinners();
                sendVAPRegisters(Invocator.TEST);
            }
            else{

                currentVAP = null;
                currentTest = null;
                isBipTest = false;
                setInitialsToVapSpinners();
            }
        });
    }

    //TODO: черновой вариант, нужно вдумчиво привести в соответствие с требованиями железа
    // в работающей старой версии все параметры отправляются по факту изменения хотя бы одного, что неочевидно для углов, например
    private void setupPulseSequenceChangeListeners() {

        uisInjectorSectionModel.width_1Property().addListener((observableValue, oldValue, newValue) -> {
            if (mainSectionUisModel.isTestIsChanging() || mainSectionUisModel.isModelIsChanging()) { return; }
            if ((newValue.intValue() >= WIDTH_CURRENT_SIGNAL_SPINNER_MIN)
                    && (newValue.intValue() <= WIDTH_CURRENT_SIGNAL_SPINNER_MAX)) {
                sendVAPRegisters(Invocator.SPINNER);
            }
        });

        uisInjectorSectionModel.width_2Property().addListener((observableValue, oldValue, newValue) -> {

            if (mainSectionUisModel.isTestIsChanging() || mainSectionUisModel.isModelIsChanging()) { return; }
            if ((newValue.intValue() >= WIDTH_CURRENT_SIGNAL_SPINNER_MIN)
                    && (newValue.intValue() <= WIDTH_CURRENT_SIGNAL_SPINNER_MAX)) {
                sendVAPRegisters(Invocator.SPINNER);
            }
        });

        uisInjectorSectionModel.angle_1Property().addListener((observableValue, oldValue, newValue) -> {

            if (mainSectionUisModel.isTestIsChanging() || mainSectionUisModel.isModelIsChanging()) { return; }
            sendVAPRegisters(Invocator.SPINNER);
        });

        uisInjectorSectionModel.angle_2Property().addListener((observableValue, oldValue, newValue) -> {

            if (mainSectionUisModel.isTestIsChanging() || mainSectionUisModel.isModelIsChanging()) { return; }
            sendVAPRegisters(Invocator.SPINNER);
        });

        uisInjectorSectionModel.shiftProperty().addListener((observableValue, oldValue, newValue) -> {

            if (mainSectionUisModel.isTestIsChanging() || mainSectionUisModel.isModelIsChanging()) { return; }
            ultimaModbusWriter.add(SecondCoilShiftTime, newValue);
        });

        uisSettingsModel.angleOffsetProperty().addListener((observableValue, oldValue, newValue) -> sendVAPRegisters(Invocator.SPINNER));
    }

    private void setupVoltageTabListener() {

        uisVoltageTabModel.getPulseSettingsButton().setOnAction(e -> {
            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Settings");
                stage.setScene(new Scene(vapDialogView));
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UTILITY);
                stage.setOnCloseRequest(ev -> cancelButton.fire());
            }
            saveValues();
            stage.show();
        });
    }

    private void setValuesToVapSpinners() {

        setBoostUSpinnerValueFactory();

        double firstI = currentVAP.getFirstI();
        double secondI = currentVAP.getSecondI();
        double boostI = currentVAP.getBoostI();

        firstI = calculateFirstI(boostI, firstI);
        secondI = calculateSecondI(firstI, secondI);

        boostUSpinner.getValueFactory().setValue(currentVAP.getBoostU());
        batteryUSpinner.getValueFactory().setValue(currentVAP.getBatteryU());

        boostISpinner.getValueFactory().setValue((boostI * 100 % 10 != 0) ? round(boostI) : boostI);
        firstISpinner.getValueFactory().setValue((firstI * 100 % 10 != 0) ? round(firstI) : firstI);
        secondISpinner.getValueFactory().setValue((secondI * 100 % 10 != 0) ? round(secondI) : secondI);
        firstWSpinner.getValueFactory().setValue(currentVAP.getFirstW());
        negativeUSpinner.getValueFactory().setValue(currentVAP.getNegativeU());
        uisVapModel.boostDisableProperty().setValue(currentVAP.getBoostDisable());
        enableBoostToggleButton.setSelected(currentVAP.getBoostDisable());

        boolean isDoubleChannel = currentVAP.getInjectorSubType() == DOUBLE_COIL || currentVAP.getInjectorSubType() == HPI;

        activateCoil2Spinners(isDoubleChannel);
        activateBipSpinners(isBipTest);

        if (isDoubleChannel) {

            firstI = currentVAP.getFirstI2();
            secondI = currentVAP.getSecondI2();
            boostI = currentVAP.getBoostI2();

            firstI = calculateFirstI(boostI, firstI);
            secondI = calculateSecondI(firstI, secondI);
            firstW2Spinner.getValueFactory().setValue(currentVAP.getFirstW2());
            firstI2Spinner.getValueFactory().setValue((firstI * 100 % 10 != 0) ? round(firstI) : firstI);
            secondI2Spinner.getValueFactory().setValue((secondI * 100 % 10 != 0) ? round(secondI) : secondI);
            boostI2Spinner.getValueFactory().setValue((boostI * 100 % 10 != 0) ? round(boostI) : boostI);
        }

        if (isBipTest) {
            bipWindowSpinner.getValueFactory().setValue(currentVAP.getBipWindow());
            bipPwmSpinner.getValueFactory().setValue(currentVAP.getBipPWM());
        }
        saveDataToVapModel(Invocator.TEST);
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
        activateBipSpinners(false);
        uisVapModel.boostDisableProperty().setValue(false);

        saveDataToVapModel(Invocator.TEST);
    }

    private void setupDisableBoostToggleButton() {

        enableBoostToggleButton.setSelected(true);
        enableBoostToggleButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUenable"));
        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                enableBoostToggleButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUenable"));
            }
            else {
                enableBoostToggleButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUdisable"));
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
        bipPwmSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BIP_PWM_SPINNER_MIN,
                BIP_PWM_SPINNER_MAX,
                BIP_PWM_SPINNER_INIT,
                BIP_PWM_SPINNER_STEP));
        bipWindowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BIP_WINDOW_SPINNER_MIN,
                BIP_WINDOW_SPINNER_MAX,
                BIP_WINDOW_SPINNER_INIT,
                BIP_WINDOW_SPINNER_STEP));

        activateCoil2Spinners(false);

//        SpinnerManager.setupIntegerSpinner(widthCurrentSignal);
        SpinnerManager.setupIntegerSpinner(firstWSpinner);
        SpinnerManager.setupDoubleSpinner(boostISpinner);
        SpinnerManager.setupDoubleSpinner(firstISpinner);
        SpinnerManager.setupDoubleSpinner(secondISpinner);
        SpinnerManager.setupIntegerSpinner(batteryUSpinner);
        SpinnerManager.setupIntegerSpinner(negativeUSpinner);
        SpinnerManager.setupIntegerSpinner(boostUSpinner);
        SpinnerManager.setupIntegerSpinner(bipPwmSpinner);
        SpinnerManager.setupIntegerSpinner(bipWindowSpinner);

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
        listOfVAPSpinners.add(bipPwmSpinner);
        listOfVAPSpinners.add(bipWindowSpinner);

        listOfVAPSpinners.forEach(e -> e.setEditable(true));

        firstW2Spinner.setDisable(true);
        boostI2Spinner.setDisable(true);
        firstI2Spinner.setDisable(true);
        secondI2Spinner.setDisable(true);
        bipPwmSpinner.setDisable(true);
        bipWindowSpinner.setDisable(true);
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

    private void activateBipSpinners(boolean activate) {

        bipPwmSpinner.setDisable(!activate);
        bipWindowSpinner.setDisable(!activate);

        if (activate) {
            bipPwmSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BIP_PWM_SPINNER_MIN,
                    BIP_PWM_SPINNER_MAX,
                    BIP_PWM_SPINNER_INIT,
                    BIP_PWM_SPINNER_STEP));
            bipWindowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BIP_WINDOW_SPINNER_MIN,
                    BIP_WINDOW_SPINNER_MAX,
                    BIP_WINDOW_SPINNER_INIT,
                    BIP_WINDOW_SPINNER_STEP));
        }
        else {
            bipPwmSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0));
            bipWindowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0));
        }
    }

    private void setBoostUSpinnerValueFactory() {

        if (currentVAP.getInjectorType() == InjectorType.PIEZO) {
            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX_PIEZO,
                    BOOST_U_SPINNER_INIT,
                    BATTERY_U_SPINNER_STEP));
        }else{

            if (convertDataToInt(uisHardwareUpdateModel.boost_UProperty().get()) > BOOST_U_SPINNER_MAX){
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
            saveDataToVapModel(Invocator.DIALOG);
            sendVAPRegisters(Invocator.DIALOG);
            if (stage != null)
                stage.close();
        });

    }

    private void sendVAPRegisters(Invocator who) {

        log.info("-----------------------------------");
        log.info((char)27 + "[31m" + who + " sendVAPRegisters");
        log.info("-----------------------------------");

        int negativeValue = negativeUSpinner.getValue();
        double boostIValue = boostISpinner.getValue();
        double firstIValue = firstISpinner.getValue();
        double secondIValue = secondISpinner.getValue();
        int firstWValue = firstWSpinner.getValue();
        int angleOffset = uisSettingsModel.angleOffsetProperty().get();
        int widthValue;
        int angle_1;
        int bipWindow = 0;
        int bipPWM = 0;
        switch (who) {
            case TEST:
                widthValue = currentTest.getTotalPulseTime1();
                angle_1 = calculateAngle(angleOffset, currentTest.getAngle_1());
                if (isBipTest) {
                    bipWindow = currentVAP.getBipWindow();
                    bipPWM = currentVAP.getBipPWM();
                }
                break;
            case SPINNER:
                widthValue = uisInjectorSectionModel.width_1Property().get();
                angle_1 = calculateAngle(angleOffset, uisInjectorSectionModel.angle_1Property().get());
                if (isBipTest) {
                    bipWindow = uisVapModel.bipWindowProperty().get();
                    bipPWM = uisVapModel.bipPWMProperty().get();
                }
                break;
            default:
                widthValue = uisInjectorSectionModel.width_1Property().get();
                angle_1 = calculateAngle(angleOffset, uisInjectorSectionModel.angle_1Property().get());
                if (isBipTest) {
                    bipWindow = uisVapModel.bipWindowProperty().get();
                    bipPWM = uisVapModel.bipPWMProperty().get();
                }
        }

        boolean boostToggleButtonSelected = enableBoostToggleButton.isSelected();

        firstIValue = calculateFirstI(boostIValue, firstIValue);
        secondIValue = calculateSecondI(firstIValue, secondIValue);
        if ((widthValue - firstWValue <= MIN_DELTA_WIDTH_TO_FIRST_WIDTH)) {
            firstWValue = getCorrectedFirstW(widthValue, MIN_DELTA_WIDTH_TO_FIRST_WIDTH);
            secondIValue = getCorrectedSecondI(firstIValue);
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
        ultimaModbusWriter.add(Angle_1, angle_1);
        ultimaModbusWriter.add(StartOnBatteryUOne, boostToggleButtonSelected);

        log.info((char)27 + "[31mBoost_U " + boostUSpinner.getValue());
        log.info((char)27 + "[31mBattery_U  " + batteryUSpinner.getValue());
        log.info((char)27 + "[31mNegative_U " + negativeValue);
        log.info((char)27 + "[31mBoostIBoardOne " + boostIValue);
        log.info((char)27 + "[31mFirstIBoardOne " + firstIValue);
        log.info((char)27 + "[31mSecondIBoardOne " + secondIValue);
        log.info((char)27 + "[31mFirstWBoardOne " + firstWValue);
        log.info((char)27 + "[31mWidthBoardOne " + widthValue);
        log.info((char)27 + "[31mBoostUOneDisabled " + boostToggleButtonSelected);
        log.info((char)27 + "[31mAngle_1 " + angle_1);

        if (isBipTest) {
            ultimaModbusWriter.add(BipModeInterval_1, bipWindow + firstWValue);
            ultimaModbusWriter.add(BipModeInterval_2, bipWindow + firstWValue);
            ultimaModbusWriter.add(BipModeDuty_1, bipPWM);
            ultimaModbusWriter.add(BipModeDuty_2, bipPWM);

            log.info((char)27 + "[31mBipModeInterval_1 " + bipWindow);
            log.info((char)27 + "[31mBipModeDuty_1 " + bipPWM);

        }

        if (currentVAP != null) {

            InjectorSubType injectorSubType = currentVAP.getInjectorSubType();

            if (injectorSubType == DOUBLE_COIL || injectorSubType == HPI) {

                double boostI2Value = boostI2Spinner.getValue();
                double firstI2Value = firstI2Spinner.getValue();
                double secondI2Value = secondI2Spinner.getValue();
                int firstW2Value = firstW2Spinner.getValue();
                int width2Value;
                switch (who) {
                    case TEST:
                        width2Value = injectorSubType == DOUBLE_COIL ? currentTest.getTotalPulseTime1() : currentTest.getTotalPulseTime2();
                        break;
                    case SPINNER:
                        width2Value = injectorSubType == DOUBLE_COIL ? uisInjectorSectionModel.width_1Property().get() : uisInjectorSectionModel.width_2Property().get();
                        break;
                    default:
                        width2Value = uisInjectorSectionModel.width_1Property().get();
                }

                firstI2Value = calculateFirstI(boostI2Value, firstI2Value);
                secondI2Value = calculateSecondI(firstI2Value, secondI2Value);
                if ((width2Value - firstW2Value <= MIN_DELTA_WIDTH_TO_FIRST_WIDTH)) {
                    firstW2Value = getCorrectedFirstW(width2Value, MIN_DELTA_WIDTH_TO_FIRST_WIDTH);
                    secondI2Value = getCorrectedSecondI(firstI2Value);
                }

                ultimaModbusWriter.add(BoostIBoardTwo, (int) (boostI2Value * ONE_AMPERE_MULTIPLY));
                ultimaModbusWriter.add(FirstIBoardTwo, (int) (firstI2Value * ONE_AMPERE_MULTIPLY));
                ultimaModbusWriter.add(SecondIBoardTwo, (int) (secondI2Value * ONE_AMPERE_MULTIPLY));
                ultimaModbusWriter.add(FirstWBoardTwo, firstW2Value);
                ultimaModbusWriter.add(WidthBoardTwo, width2Value);
                ultimaModbusWriter.add(StartOnBatteryUTwo, boostToggleButtonSelected);

                log.info((char)27 + "[31mBoostIBoardTwo " + boostIValue);
                log.info((char)27 + "[31mFirstIBoardTwo " + firstIValue);
                log.info((char)27 + "[31mSecondIBoardTwo " + secondIValue);
                log.info((char)27 + "[31mFirstWBoardTwo " + firstWValue);
                log.info((char)27 + "[31mWidthBoardTwo " + widthValue);
                log.info((char)27 + "[31mBoostUTwoDisabled " + boostToggleButtonSelected);
            }
            else {

                ultimaModbusWriter.add(BoostIBoardTwo, (int) (boostIValue * ONE_AMPERE_MULTIPLY));
                ultimaModbusWriter.add(FirstIBoardTwo, (int) (firstIValue * ONE_AMPERE_MULTIPLY));
                ultimaModbusWriter.add(SecondIBoardTwo, (int) (secondIValue * ONE_AMPERE_MULTIPLY));
                ultimaModbusWriter.add(FirstWBoardTwo, firstWValue);
                ultimaModbusWriter.add(WidthBoardTwo, widthValue);
                ultimaModbusWriter.add(StartOnBatteryUTwo, boostToggleButtonSelected);
            }

            if (currentVAP.getInjectorSubType() == HPI || currentVAP.getInjectorSubType() == DOUBLE_SIGNAL) {

                int angle_2;
                switch (who) {
                    case TEST:
                        angle_2 = calculateAngle(angleOffset, currentTest.getAngle_2());
                        break;
                    case SPINNER:
                        angle_2 = calculateAngle(angleOffset, uisInjectorSectionModel.angle_2Property().get());
                        break;
                    default:
                        angle_2 = calculateAngle(angleOffset, uisInjectorSectionModel.angle_2Property().get());
                }
                ultimaModbusWriter.add(Angle_2, angle_2);

                log.info((char)27 + "[31mAngle_2 " + angle_2);
            }
            if (currentVAP.getInjectorSubType() == DOUBLE_SIGNAL) {

                int width_2;
                switch (who) {
                    case TEST:
                        width_2 = currentTest.getTotalPulseTime2();
                        break;
                    case SPINNER:
                        width_2 = uisInjectorSectionModel.width_2Property().get();
                        break;
                    default:
                        width_2 = uisInjectorSectionModel.width_1Property().get();
                        break;
                }
                ultimaModbusWriter.add(SecondSignalInterval, width_2);

                log.info((char)27 + "[31mSecondSignalInterval " + width_2);
            }
        }
        ultimaModbusWriter.add(FirstPulseFlag, true);
        log.info("-----------------------------------");
    }

    private int calculateAngle(int offset, int angle) { return (offset - angle + 360) % 360; }

    private double calculateFirstI(double boostI, double firstI) { return boostI - firstI >= 0.5 ? firstI : boostI - 0.5; }

    private double calculateSecondI(double firstI, double secondI) { return firstI - secondI >= 0.5 ? secondI : firstI - 0.5; }

    private int getCorrectedFirstW(int width, int delta) { return width - delta; }

    private double getCorrectedSecondI(double firstI) { return firstI - 0.6; }

    private void setupSpinnerStyleWhenValueChangedListener() {

        boostUSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(boostUSpinner, uisHardwareUpdateModel.boost_UProperty().get()));
        negativeUSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(negativeUSpinner, uisHardwareUpdateModel.negative_UProperty().get()));
        batteryUSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(batteryUSpinner, uisHardwareUpdateModel.battery_UProperty().get()));
        firstWSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstWSpinner, uisHardwareUpdateModel.first_WProperty().get()));
        boostISpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(boostISpinner, uisHardwareUpdateModel.boost_IProperty().get()));
        firstISpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstISpinner, uisHardwareUpdateModel.first_IProperty().get()));
        secondISpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(secondISpinner, uisHardwareUpdateModel.second_IProperty().get()));
        firstW2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstW2Spinner, uisHardwareUpdateModel.first_W2Property().get()));
        firstI2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(firstI2Spinner, uisHardwareUpdateModel.first_I2Property().get()));
        secondI2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(secondI2Spinner, uisHardwareUpdateModel.second_I2Property().get()));
        boostI2Spinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(boostI2Spinner, uisHardwareUpdateModel.boost_I2Property().get()));
        bipPwmSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> setColor(bipPwmSpinner, uisHardwareUpdateModel.bipPWMProperty().get()));
        bipWindowSpinner.valueProperty().addListener((observableValue, oldValue, newValue)
                -> setColor(bipWindowSpinner, String.valueOf(Integer.parseInt(uisHardwareUpdateModel.bipWindowProperty().get()) - Integer.parseInt(uisHardwareUpdateModel.first_WProperty().get()))));
    }

    private void setColor(Spinner<? extends Number> spinner, String updaterValue) {

        if (spinner.getValue().toString().equals(updaterValue)) {

            spinner.getEditor().setStyle(null);
        }else{
            spinner.getEditor().setStyle(RED_COLOR_STYLE);
        }
    }

    private void saveDataToVapModel(Invocator who) {

        uisVapModel.boostUProperty().setValue(boostUSpinner.getValue());
        uisVapModel.batteryUProperty().setValue(batteryUSpinner.getValue());
        uisVapModel.negativeUProperty().setValue(negativeUSpinner.getValue());
        uisVapModel.firstWProperty().setValue(firstWSpinner.getValue());
        uisVapModel.firstIProperty().setValue(firstISpinner.getValue());
        uisVapModel.secondIProperty().setValue(secondISpinner.getValue());
        uisVapModel.boostIProperty().setValue(boostISpinner.getValue());
        uisVapModel.firstW2Property().setValue(firstW2Spinner.getValue());
        uisVapModel.firstI2Property().setValue(firstI2Spinner.getValue());
        uisVapModel.secondI2Property().setValue(secondI2Spinner.getValue());
        uisVapModel.boostI2Property().setValue(boostI2Spinner.getValue());
        uisVapModel.bipWindowProperty().setValue(bipWindowSpinner.getValue());
        uisVapModel.bipPWMProperty().setValue(bipPwmSpinner.getValue());
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
            bipPwmSpinner.getValueFactory().setValue(bipPwmSavedValue);
            bipWindowSpinner.getValueFactory().setValue(bipWindowSavedValue);
            stage.close();
        });

    }

    private void saveValues() {

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
        bipPwmSavedValue = bipPwmSpinner.getValue();
        bipWindowSavedValue = bipWindowSpinner.getValue();

        setColor(boostUSpinner, uisHardwareUpdateModel.boost_UProperty().get());
        setColor(negativeUSpinner, uisHardwareUpdateModel.negative_UProperty().get());
        setColor(batteryUSpinner, uisHardwareUpdateModel.battery_UProperty().get());
        setColor(firstWSpinner, uisHardwareUpdateModel.first_WProperty().get());
        setColor(boostISpinner, uisHardwareUpdateModel.boost_IProperty().get());
        setColor(firstISpinner, uisHardwareUpdateModel.first_IProperty().get());
        setColor(secondISpinner, uisHardwareUpdateModel.second_IProperty().get());
        setColor(firstW2Spinner, uisHardwareUpdateModel.first_W2Property().get());
        setColor(firstI2Spinner, uisHardwareUpdateModel.first_I2Property().get());
        setColor(secondI2Spinner, uisHardwareUpdateModel.second_I2Property().get());
        setColor(boostI2Spinner, uisHardwareUpdateModel.boost_I2Property().get());
        setColor(bipPwmSpinner, uisHardwareUpdateModel.bipPWMProperty().get());
        setColor(bipWindowSpinner, String.valueOf(Integer.parseInt(uisHardwareUpdateModel.bipWindowProperty().get()) - Integer.parseInt(uisHardwareUpdateModel.first_WProperty().get())));
    }

    private boolean isBipTest(Test test) {
        return ((InjectorUisTest)test).getVoltAmpereProfile().getBipPWM() != null
                && ((InjectorUisTest)test).getVoltAmpereProfile().getBipWindow() != null;
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
        BIP_Window.textProperty().bind(i18N.createStringBinding("voapProfile.label.bipWindow"));
        BIP_PWM.textProperty().bind(i18N.createStringBinding("voapProfile.label.bipPWM"));
    }
}
