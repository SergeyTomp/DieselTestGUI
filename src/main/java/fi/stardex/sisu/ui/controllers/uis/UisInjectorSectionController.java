package fi.stardex.sisu.ui.controllers.uis;

import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.model.ChartTaskDataModel;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.model.TestBenchSectionModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.uis.UisSettingsModel;
import fi.stardex.sisu.model.uis.UisVapModel;
import fi.stardex.sisu.model.updateModels.UisHardwareUpdateModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.common.GUI_TypeController;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.enums.RegActive;
import fi.stardex.sisu.util.listeners.ThreeSpinnerStyleChangeListener;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.ui.controllers.common.GUI_TypeController.GUIType.UIS;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.InjectorSubType.*;
import static fi.stardex.sisu.util.enums.InjectorType.COIL;
import static fi.stardex.sisu.util.enums.RegActive.CURRENT;
import static fi.stardex.sisu.util.enums.RegActive.DUTY;
import static fi.stardex.sisu.util.enums.RegActive.PRESSURE;

public class UisInjectorSectionController {

    @FXML private Label bipLabel;
    @FXML private Label bipTaskLabel;
    @FXML private ToggleButton led1ToggleButton;
    @FXML private ToggleButton led2ToggleButton;
    @FXML private ToggleButton led3ToggleButton;
    @FXML private ToggleButton led4ToggleButton;
    @FXML private ToggleButton led5ToggleButton;
    @FXML private ToggleButton led6ToggleButton;
    @FXML private ToggleButton led7ToggleButton;
    @FXML private ToggleButton led8ToggleButton;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> width2Spinner;
    @FXML private Spinner<Integer> offsetSpinner;
    @FXML private Spinner<Integer> angle1Spinner;
    @FXML private Spinner<Integer> angle2Spinner;
    @FXML private Spinner<Integer> pressureSpinner;
    @FXML private Spinner<Double> currentSpinner;
    @FXML private Spinner<Double> dutySpinner;
    @FXML private TextField typeTextField;
    @FXML private Label offsetLabel;
    @FXML private Label width2Label;
    @FXML private Label angle2Label;
    @FXML private Label widthLabel;
    @FXML private Label angle1Label;
    @FXML private Label pressureLabel;
    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private StackPane topLabelStackPane;
    @FXML private StackPane midLabelStackPane;
    @FXML private StackPane lowLabelStackPane;
    @FXML private StackPane topSpinnerStackPane;
    @FXML private StackPane midSpinnerStackPane;
    @FXML private StackPane lowSpinnerStackPane;
    @FXML private StackPane widthStackPane;
    @FXML private StackPane angle1StackPane;
    @FXML private StackPane led1StackPane;
    @FXML private StackPane led2StackPane;
    @FXML private StackPane led3StackPane;
    @FXML private StackPane led4StackPane;
    @FXML private StackPane led5StackPane;
    @FXML private StackPane led6StackPane;
    @FXML private StackPane led7StackPane;
    @FXML private StackPane led8StackPane;
    @FXML private StackPane bipStackPane;
    @FXML private StackPane delayStackPane;
    @FXML private Button saveBipButton;
    @FXML private Button saveDelayButton;
    @FXML private StackPane lcdStackPane;
    @FXML private ToggleButton regulatorToggleButton;
    @FXML private StackPane rootStackPane;

    private Lcd lcd;
    private GaugeCreator.BipGauge bipGauge;
    private Gauge delayGauge;
    private ObservableList<ToggleButton> ledToggleButtons;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private List<Timeline> timeLinesList;
    private List<KeyFrame> keyFramesList;
    private static final String LED_BLINK_ON = "ledBlink-on";
    private static final String LED_BLINK_OFF = "ledBlink-off";
    private final String GREEN_STYLE_CLASS = "regulator-spinner-selected";

    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private UisHardwareUpdateModel uisHardwareUpdateModel;
    private GUI_TypeModel gui_typeModel;
    private UisSettingsModel uisSettingsModel;
    private TestBenchSectionModel testBenchSectionModel;
    private RegulationModesModel regulationModesModel;
    private UisVapModel uisVapModel;
    private ChartTaskDataModel chartTaskDataModel;
    private TimerTasksManager timerTasksManager;

    private ModbusRegisterProcessor ultimaModbusWriter;
    private Logger logger = LoggerFactory.getLogger(UisInjectorSectionController.class);

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setUisHardwareUpdateModel(UisHardwareUpdateModel uisHardwareUpdateModel) {
        this.uisHardwareUpdateModel = uisHardwareUpdateModel;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setUisSettingsModel(UisSettingsModel uisSettingsModel) {
        this.uisSettingsModel = uisSettingsModel;
    }
    public void setTestBenchSectionModel(TestBenchSectionModel testBenchSectionModel) {
        this.testBenchSectionModel = testBenchSectionModel;
    }
    public void setRegulationModesModel(RegulationModesModel regulationModesModel) {
        this.regulationModesModel = regulationModesModel;
    }
    public void setUisVapModel(UisVapModel uisVapModel) {
        this.uisVapModel = uisVapModel;
    }
    public void setChartTaskDataModel(ChartTaskDataModel chartTaskDataModel) {
        this.chartTaskDataModel = chartTaskDataModel;
    }
    public void setTimerTasksManager(TimerTasksManager timerTasksManager) {
        this.timerTasksManager = timerTasksManager;
    }

    @PostConstruct
    public void init() {

        bipGauge = GaugeCreator.createBipGauge();
        delayGauge = GaugeCreator.createDelayGauge();
        lcd = GaugeCreator.createLcd("Bar");
        lcdStackPane.getChildren().add(0, lcd);
        bipStackPane.getChildren().add(0, bipGauge.getBipGauge());
        delayStackPane.getChildren().add(0, delayGauge);
        hideSlaveControls();
        rootStackPane.widthProperty().addListener(new HboxWidthListener(rootStackPane, lcdStackPane));
        setupSpinners();
        pressureSensorSelectionListener();
        setupLedControllers();
        setToggleGroupToLeds(toggleGroup);
        setupTimelines();
        setupModelToControlsBinding();
        setupBipSaveInvocation();
        setupInjectorErrorListener();
        setupWidthSpinnersStyleChange();
        setupModelListener();
        setupTestListener();
        setupTargetRPMListener();
        setupRPMSourceListener();
        setupVapListeners();
        setupGUI_TypeSwitchListener();
        setupCharTaskListener();
        setupF2eSpinnerListeners();
        setupStartButtonListener();
        lcd.valueProperty().bind(uisHardwareUpdateModel.lcdPressureProperty());
        bipLabel.setText("BIP(\u00B5s)");
        saveBipButton.setDisable(true);
    }

    private void configureModeControls(InjectorSubType injectorSubType) {

        hideSlaveControls();
        switch (injectorSubType) {
            case SINGLE_COIL:
            case SINGLE_PIEZO:
            case MECHANIC:
                break;
            case DOUBLE_COIL:
                offsetLabel.setVisible(true);
                offsetSpinner.setVisible(true);
                break;
            case DOUBLE_SIGNAL:
            case HPI:
                width2Label.setVisible(true);
                angle2Label.setVisible(true);
                width2Spinner.setVisible(true);
                angle2Spinner.setVisible(true);
                break;
            case F2E:
                pressureLabel.setVisible(true);
                dutyLabel.setVisible(true);
                currentLabel.setVisible(true);
                pressureSpinner.setVisible(true);
                currentSpinner.setVisible(true);
                dutySpinner.setVisible(true);
                regulatorToggleButton.setDisable(false);
                lcd.setOpacity(1);
                break;
        }
        injectorSubType.getModeSwitchRegisters().forEach((k, v) -> ultimaModbusWriter.add(k, v));
        injectorSubType.getSlotConfigureRegisters().forEach((k, v) -> ultimaModbusWriter.add(k, v));
    }

    private void hideSlaveControls() {

        pressureLabel.setVisible(false);
        dutyLabel.setVisible(false);
        currentLabel.setVisible(false);
        width2Label.setVisible(false);
        angle2Label.setVisible(false);
        offsetLabel.setVisible(false);
        width2Spinner.setVisible(false);
        angle2Spinner.setVisible(false);
        offsetSpinner.setVisible(false);
        pressureSpinner.setVisible(false);
        currentSpinner.setVisible(false);
        dutySpinner.setVisible(false);
        regulatorToggleButton.setSelected(false);
        regulatorToggleButton.setDisable(true);
        lcd.setOpacity(0.4);
    }

    private void setupSpinners() {

        int maxPressure = uisSettingsModel.pressureSensorProperty().get();
        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(WIDTH_CURRENT_SIGNAL_SPINNER_MIN,
                WIDTH_CURRENT_SIGNAL_SPINNER_MAX,
                WIDTH_CURRENT_SIGNAL_SPINNER_INIT,
                WIDTH_CURRENT_SIGNAL_SPINNER_STEP));
        width2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(WIDTH_CURRENT_SIGNAL_SPINNER_MIN,
                WIDTH_CURRENT_SIGNAL_SPINNER_MAX,
                WIDTH_CURRENT_SIGNAL_SPINNER_INIT,
                WIDTH_CURRENT_SIGNAL_SPINNER_STEP));
        offsetSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(OFFSET_SPINNER_MIN,
                OFFSET_SPINNER_MAX,
                OFFSET_SPINNER_INIT,
                OFFSET_SPINNER_STEP));
        angle1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                ANGLE_OFFSET_SPINNER_MIN,
                ANGLE_OFFSET_SPINNER_MAX,
                ANGLE_OFFSET_SPINNER_INIT,
                ANGLE_OFFSET_SPINNER_STEP));
        angle2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                ANGLE_OFFSET_SPINNER_MIN,
                ANGLE_OFFSET_SPINNER_MAX,
                ANGLE_OFFSET_SPINNER_INIT,
                ANGLE_OFFSET_SPINNER_STEP));
        pressureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PRESS_REG_1_SPINNER_MIN,
                maxPressure,
                PRESS_REG_1_SPINNER_INIT,
                PRESS_REG_1_SPINNER_STEP));
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_1_SPINNER_MIN,
                CURRENT_REG_1_SPINNER_MAX,
                CURRENT_REG_1_SPINNER_INIT,
                CURRENT_REG_1_SPINNER_STEP));
        dutySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_1_SPINNER_MIN,
                DUTY_CYCLE_REG_1_SPINNER_MAX,
                DUTY_CYCLE_REG_1_SPINNER_INIT,
                DUTY_CYCLE_REG_1_SPINNER_STEP));

        SpinnerManager.setupDoubleSpinner(currentSpinner);
        SpinnerManager.setupDoubleSpinner(dutySpinner);
        SpinnerManager.setupIntegerSpinner(width2Spinner);
        SpinnerManager.setupIntegerSpinner(offsetSpinner);
        SpinnerManager.setupIntegerSpinner(angle1Spinner);
        SpinnerManager.setupIntegerSpinner(angle2Spinner);
        SpinnerManager.setupIntegerSpinner(pressureSpinner);

        pressureSpinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        currentSpinner.getStyleClass().add(1, "");
        dutySpinner.getStyleClass().add(1, "");
    }

    private void setupLedControllers() {

        setNumber(1, led1ToggleButton);
        setNumber(2, led2ToggleButton);
        setNumber(3, led3ToggleButton);
        setNumber(4, led4ToggleButton);
        setNumber(5, led5ToggleButton);
        setNumber(6, led6ToggleButton);
        setNumber(7, led7ToggleButton);
        setNumber(8, led8ToggleButton);

        setNumber(1, uisInjectorSectionModel.getLedBeaker1ToggleButton());
        setNumber(2, uisInjectorSectionModel.getLedBeaker2ToggleButton());
        setNumber(3, uisInjectorSectionModel.getLedBeaker3ToggleButton());
        setNumber(4, uisInjectorSectionModel.getLedBeaker4ToggleButton());
        setNumber(5, uisInjectorSectionModel.getLedBeaker5ToggleButton());
        setNumber(6, uisInjectorSectionModel.getLedBeaker6ToggleButton());
        setNumber(7, uisInjectorSectionModel.getLedBeaker7ToggleButton());
        setNumber(8, uisInjectorSectionModel.getLedBeaker8ToggleButton());

        setBlinkingStatus(led1ToggleButton, false);
        setBlinkingStatus(led2ToggleButton, false);
        setBlinkingStatus(led3ToggleButton, false);
        setBlinkingStatus(led4ToggleButton, false);
        setBlinkingStatus(led5ToggleButton, false);
        setBlinkingStatus(led6ToggleButton, false);
        setBlinkingStatus(led7ToggleButton, false);
        setBlinkingStatus(led8ToggleButton, false);

        ledToggleButtons = FXCollections.observableArrayList(new LinkedList<>());

        ledToggleButtons.add(led1ToggleButton);
        ledToggleButtons.add(led2ToggleButton);
        ledToggleButtons.add(led3ToggleButton);
        ledToggleButtons.add(led4ToggleButton);
        ledToggleButtons.add(led5ToggleButton);
        ledToggleButtons.add(led6ToggleButton);
        ledToggleButtons.add(led7ToggleButton);
        ledToggleButtons.add(led8ToggleButton);
    }

    private void setupModelToControlsBinding() {

        uisInjectorSectionModel.getLedBeaker1ToggleButton().selectedProperty().bind(led1ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker2ToggleButton().selectedProperty().bind(led2ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker3ToggleButton().selectedProperty().bind(led3ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker4ToggleButton().selectedProperty().bind(led4ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker5ToggleButton().selectedProperty().bind(led5ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker6ToggleButton().selectedProperty().bind(led6ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker7ToggleButton().selectedProperty().bind(led7ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker8ToggleButton().selectedProperty().bind(led8ToggleButton.selectedProperty());

        uisInjectorSectionModel.powerButtonProperty().bind(regulatorToggleButton.selectedProperty());
        uisInjectorSectionModel.width_1Property().bind(widthSpinner.valueProperty());
        uisInjectorSectionModel.width_2Property().bind(width2Spinner.valueProperty());
        uisInjectorSectionModel.shiftProperty().bind(offsetSpinner.valueProperty());
        uisInjectorSectionModel.angle_1Property().bind(angle1Spinner.valueProperty());
        uisInjectorSectionModel.angle_2Property().bind(angle2Spinner.valueProperty());


    }

    private void setupBipSaveInvocation() {

        saveBipButton.setOnMouseClicked(mouseEvent -> uisInjectorSectionModel.getSaveBipButton().fire());
        mainSectionUisModel.getStoreButton().addEventHandler(ActionEvent.ACTION, event -> {
            if (!saveBipButton.isDisabled()) {
                uisInjectorSectionModel.getSaveBipButton().fire();
            }
        });
    }

    private void pressureSensorSelectionListener() {

        uisSettingsModel.pressureSensorProperty().addListener((observableValue, oldValue, newValue) ->
                pressureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        PRESS_REG_1_SPINNER_MIN,
                        newValue.intValue(),
                        PRESS_REG_1_SPINNER_INIT,
                        PRESS_REG_1_SPINNER_STEP)));
    }

    private void setupInjectorErrorListener() {
        uisHardwareUpdateModel.injectorErrorProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                ultimaModbusWriter.add(Inj_Process_Global_Error, false);
            }
        });
    }

    private void setupWidthSpinnersStyleChange() {

        widthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.toString().equals(uisHardwareUpdateModel.widthProperty().get())) {
                widthSpinner.getEditor().setStyle(null);
            }
        });
        width2Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.toString().equals(uisHardwareUpdateModel.width2Property().get())) {
                width2Spinner.getEditor().setStyle(null);
            }
        });
    }

    private void setupModelListener() {
        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {
                selectButton(true, led1ToggleButton);
                InjectorType injectorType = newValue.getVAP().getInjectorType();
                InjectorSubType injectorSubType = newValue.getVAP().getInjectorSubType();
                typeTextField.setText(injectorSubType.name());
                configureModeControls(injectorSubType);
                ultimaModbusWriter.add(Injector_type, injectorType.getValueToSend());
            }
            else {

                selectButton(false, (ledToggleButtons.toArray(new ToggleButton[8])));
                typeTextField.setText("");
                configureModeControls(SINGLE_COIL);
                ultimaModbusWriter.add(Injector_type, COIL.getValueToSend());
            }
        });
    }

    private void setupTestListener() {

        mainSectionUisModel.injectorTestProperty().addListener((observable, oldValue, newValue) -> {

            /** Additional check of GUI type is done to prevent ClassCactExeption when Test is casted to InjectorUisTest.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf InjectorUisTest} but it is slower */
            if (gui_typeModel.guiTypeProperty().get() != UIS) {return;}

            if (newValue == null) {

                widthSpinner.getValueFactory().setValue(WIDTH_CURRENT_SIGNAL_SPINNER_INIT);
                angle1Spinner.getValueFactory().setValue(ANGLE_OFFSET_SPINNER_INIT);
                bipGauge.setBipMode(false);
                Platform.runLater(bipGauge);
                bipTaskLabel.setText("");
                uisInjectorSectionModel.bipRangeLabelProperty().setValue("");
                saveBipButton.setDisable(true);
                return;
            }

            InjectorUisTest test = (InjectorUisTest) newValue;
            InjectorUisVAP vap = (InjectorUisVAP)newValue.getVoltAmpereProfile();
            Integer totalPulseTime1 = newValue.getTotalPulseTime1();
            Integer totalPulseTime2 = newValue.getTotalPulseTime2();
            Integer angle_1 = test.getAngle_1();
            Integer angle_2 = test.getAngle_2();
            Integer shift = newValue.getShift();
            Integer settedPressure = newValue.getSettedPressure();

            widthSpinner.getValueFactory().setValue(totalPulseTime1);
            angle1Spinner.getValueFactory().setValue(angle_1);

            InjectorSubType injectorSubType = newValue.getVoltAmpereProfile().getInjectorSubType();

            //TODO: черновой вариант, нужно вдумчиво привести в соответствие с требованиями железа т.к. в работающей старой версии всё неоптимально
            if (injectorSubType == DOUBLE_COIL || injectorSubType == HPI || injectorSubType == DOUBLE_SIGNAL) {
                width2Spinner.getValueFactory().setValue(injectorSubType == DOUBLE_COIL ? totalPulseTime1 : totalPulseTime2);
                if (injectorSubType != DOUBLE_COIL) {
                    angle2Spinner.getValueFactory().setValue(angle_2);
                } else {
                    offsetSpinner.getValueFactory().setValue(shift);
                }
            } else if (injectorSubType == F2E) {
                pressureSpinner.getValueFactory().setValue(settedPressure);
            }

            if (isBipTest(newValue)) {
                saveBipButton.setDisable(false);

                String bipRangeLabel = "\u0020" + test.getBip() + "\u00B1" + test.getBipRange();
                bipTaskLabel.setText(bipRangeLabel);
                uisInjectorSectionModel.bipRangeLabelProperty().setValue(bipRangeLabel);
                bipGauge.setParameters(test.getBip(), vap.getBipWindow(), vap.getFirstW(), test.getBipRange());
                ultimaModbusWriter.add(BipModeOn_1, true);
                ultimaModbusWriter.add(BipModeOn_2, true);
            } else {
                saveBipButton.setDisable(true);
                ultimaModbusWriter.add(BipModeOn_1, false);
                ultimaModbusWriter.add(BipModeOn_2, false);
                bipGauge.setBipMode(false);
                Platform.runLater(bipGauge);
                bipTaskLabel.setText("");
                uisInjectorSectionModel.bipRangeLabelProperty().setValue("");
            }
        });
    }

    private void setupVapListeners() {

        if (mainSectionUisModel.isModelIsChanging() || mainSectionUisModel.isTestIsChanging()) {
            return;
        }

        uisVapModel.bipWindowProperty().addListener((observable, oldValue, newValue) -> bipGauge.setBipGaugeWindow(newValue.intValue()));
        uisVapModel.firstWProperty().addListener((observable, oldValue, newValue) -> bipGauge.setBipGaugeFirstW(newValue.intValue()));
    }

    private void setupTargetRPMListener() {

        testBenchSectionModel.targetRPMProperty().addListener((observable, oldValue, newValue) -> {
            int targetRPM = newValue.intValue();
            int period = targetRPM != 0 ? (int) ((60d / targetRPM) * 1000) : 100;
            int pulseTime1 = (int) ((period / 360d) * angle1Spinner.getValue());
            int pulseTime2 = (int) ((period / 360d) * angle2Spinner.getValue());
//
            ultimaModbusWriter.add(Ftime1, pulseTime1);
            ultimaModbusWriter.add(Ftime2, pulseTime2);
            ultimaModbusWriter.add(GImpulsesPeriod, period);
        });
    }

    private void setupRPMSourceListener() {

        /** RPM source switch in fact is simply UIS <-> CR modes switch.*/
        uisSettingsModel.rpmSourceProperty().addListener((observableValue, oldValue, newValue) -> {
            if (gui_typeModel.guiTypeProperty().get() == UIS) {
                ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, newValue.getSourceId());
            }
        });
    }

    private void setupGUI_TypeSwitchListener() {

        /** При переходе в другой GUI нужно отключать регулятор давления и менять режим регулирования на давление.
         * Запрос фокуса на регулирующий спиннер работает только при открытии GUI - добавлен блок else if(){} для включения режима регулирования и визуализации его зелёной рамкой.*/
        gui_typeModel.guiTypeProperty().addListener((observable, oldValue, newValue) -> {

            if (oldValue == GUI_TypeController.GUIType.UIS) {

                regulatorToggleButton.setSelected(false);
                ultimaModbusWriter.add(BipModeOn_1, false);
                ultimaModbusWriter.add(BipModeOn_2, false);
                ultimaModbusWriter.add(DoubleSignalModeOn_1, false);
                ultimaModbusWriter.add(DoubleSignalModeOn_2, false);
                ultimaModbusWriter.add(HpiModeOn, false);
                ultimaModbusWriter.add(DoubleSignalModeOn_1, false);
                ultimaModbusWriter.add(DoubleSignalModeOn_2, false);
            }

            else if(newValue == GUI_TypeController.GUIType.UIS){

                regulationModesModel.regulatorOneModeProperty().setValue(PRESSURE);
                pressureSpinner.getValueFactory().setValue(0);
                /**UIS RPM_Source should be defined every time when UIS GUI selected.
                 * This is due to contradiction between default EXTERNAL RPM_Source UIS mode (UIS_to_CR_pulseControlSwitch == 1) and UIS INTERNAL RPM_Source mode (UIS_to_CR_pulseControlSwitch == 0).
                 * UIS INTERNAL RPM_Source mode is identical to CR mode in CR_Inj GUI - UIS_to_CR_pulseControlSwitch == 0 in both cases.
                 * If UIS GUI is selected it is necessary to set UIS pulse mode defined by user previously but not default one.*/
                ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, uisSettingsModel.rpmSourceProperty().get().getSourceId());
            }
        });
    }

    private void setupCharTaskListener() {

        chartTaskDataModel.bipSignalValueProperty().addListener((observableValue, oldValue, newValue) -> bipGauge.setValue(newValue.doubleValue()));
        uisInjectorSectionModel.bipValueProperty().bind(chartTaskDataModel.bipSignalValueProperty());
        chartTaskDataModel.delayValueProperty().addListener((observableValue, oldValue, newValue) -> delayGauge.setValue(newValue.doubleValue()));
    }

    private void setupStartButtonListener() {

        mainSectionUisModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {

            ultimaModbusWriter.add(Injectors_Running_En, newValue);
            if (newValue) {
                ledToggleButtons.forEach(l -> {if(l.isSelected()){ledBlinkStart(l);}});
                ledToggleButtons.forEach(l -> disableNode(true, l));
                timerTasksManager.start();
            } else {
                ledToggleButtons.forEach(l -> { if(l.isSelected()){ ledBlinkStop(l);}});
                ledToggleButtons.forEach(l -> disableNode(false, l));
                timerTasksManager.stop();
                Platform.runLater(bipGauge);
            }
        });
    }

    private void setupF2eSpinnerListeners() {

        /** смена режима по клику на стрелках*/
        //вкл.режим давления, откл.режим тока
        pressureSpinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(PRESSURE, PressureReg1_PressMode, true, PressureReg1_I_Mode, false));
        //откл.режим давления, вкл.режим тока
        currentSpinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(CURRENT, PressureReg1_PressMode, false, PressureReg1_I_Mode, true));
        //откл.режим давления, откл.режим тока, вкл.режим скважности
        dutySpinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(DUTY, PressureReg1_PressMode, false, PressureReg1_I_Mode, false));

        /** смена режима по клику в текстовом поле спиннера */
        //вкл.режим давления, откл.режим тока
        pressureSpinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(PRESSURE, PressureReg1_PressMode, true, PressureReg1_I_Mode, false));
        //откл.режим давления, вкл.режим тока
        currentSpinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(CURRENT, PressureReg1_PressMode, false, PressureReg1_I_Mode, true));
        //откл.режим давления, откл.режим тока, вкл.режим скважности
        dutySpinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(DUTY, PressureReg1_PressMode, false, PressureReg1_I_Mode, false));

        /** изменение цвета рамки спиннера на зелёный при переходе в спиннер другого рекулятора*/
        pressureSpinner.focusedProperty().addListener(new ThreeSpinnerStyleChangeListener(pressureSpinner, currentSpinner, dutySpinner, PRESSURE));
        currentSpinner.focusedProperty().addListener(new ThreeSpinnerStyleChangeListener(pressureSpinner, currentSpinner, dutySpinner, CURRENT));
        dutySpinner.focusedProperty().addListener(new ThreeSpinnerStyleChangeListener(pressureSpinner, currentSpinner, dutySpinner, DUTY));

        /** слушаем изменения значений в спиннерах и отправляем уставки спиннеров */
        pressureSpinner.valueProperty().addListener(new ParameterChangeListener(PRESSURE));
        uisInjectorSectionModel.pressureSpinnerProperty().bind(pressureSpinner.valueProperty());
        currentSpinner.valueProperty().addListener(new ParameterChangeListener(CURRENT));
        dutySpinner.valueProperty().addListener(new ParameterChangeListener(DUTY));

        /** слушаем кнопку включения секции регуляторов*/
        uisInjectorSectionModel.powerButtonProperty().addListener(new HighPressureSectionPwrListener());

        /** слушаем изменения в модели данных, полученных из прошивки (значения полей в модели данных изменяются только для "наблюдающих" спиннеров)*/
        uisHardwareUpdateModel.currentProperty().addListener((observableValue, oldValue, newValue) -> currentSpinner.getValueFactory().setValue((Double) newValue));
        uisHardwareUpdateModel.dutyProperty().addListener((observableValue, oldValue, newValue) -> dutySpinner.getValueFactory().setValue((Double)newValue));
    }

    private void setNumber(int number, ToggleButton ledToggleButton) {
        ledToggleButton.setText(String.valueOf(number));
    }

    private boolean isBipTest(Test test) {
        return ((InjectorUisTest)test).getVoltAmpereProfile().getBipPWM() != null
                && ((InjectorUisTest)test).getVoltAmpereProfile().getBipWindow() != null;
    }

    private int getNumber(ToggleButton ledToggleButton) {
        return Integer.parseInt(ledToggleButton.getText());
    }

    private void ledBlinkStart(ToggleButton ledToggleButton) {
        if (ledToggleButton.isSelected()) {
            setBlinkingStatus(ledToggleButton, true);
            timeLinePlay(ledToggleButton);
        }
        ledToggleButton.setDisable(true);
    }

    private void ledBlinkStop(ToggleButton ledToggleButton) {
        if (ledToggleButton.isSelected()) {
            setBlinkingStatus(ledToggleButton, false);
            ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
            ledToggleButton.setDisable(false);
            timeLineStop(ledToggleButton);
        } else {
            setBlinkingStatus(ledToggleButton, false);
            ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
            ledToggleButton.setDisable(false);
            timeLineStop(ledToggleButton);
        }
    }

    private void setBlinkingStatus(ToggleButton ledBeakerController, boolean blinking){
        ledBeakerController.setUserData(blinking);
    }

    private boolean getBlinkingStatus(ToggleButton ledBeakerController){
        return (boolean)ledBeakerController.getUserData();
    }

    private void setToggleGroupToLeds(ToggleGroup toggleGroup) {
        ledToggleButtons.forEach(s -> s.setToggleGroup(toggleGroup));
    }

    private void setupTimelines() {

        timeLinesList = new ArrayList<>();
        keyFramesList = new ArrayList<>();

        for(int i = 0; i < 8; i++){

            ToggleButton ledToggleButton = ledToggleButtons.get(i);

            timeLinesList.add(new Timeline());
            keyFramesList.add(makeKeyFrame(ledToggleButton));

            Timeline timeLine = timeLinesList.get(i);
            KeyFrame keyFrame = keyFramesList.get(i);

            timeLine.getKeyFrames().add(keyFrame);
            timeLine.setCycleCount(Animation.INDEFINITE);
            ledToggleButton.getStyleClass().add(2, LED_BLINK_OFF);
            ledToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                getLoggingInjectorSelection(newValue, ledToggleButton);
                if (newValue) {
                    ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
                } else {
                    ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
                }
            });
        }
    }

    private KeyFrame makeKeyFrame(ToggleButton ledToggleButton){
        return new KeyFrame(Duration.millis(500), event -> {
            if (UisInjectorSectionController.this.getBlinkingStatus(ledToggleButton)) {
                ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
                UisInjectorSectionController.this.setBlinkingStatus(ledToggleButton, false);
            } else {
                ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
                UisInjectorSectionController.this.setBlinkingStatus(ledToggleButton, true);
            }
        });
    }

    private void timeLinePlay(ToggleButton ledToggleButton){
        timeLinesList.get(getNumber(ledToggleButton) - 1).play();
    }

    private void timeLineStop(ToggleButton ledToggleButton){
        timeLinesList.get(getNumber(ledToggleButton) - 1).stop();
    }

    private class HboxWidthListener implements ChangeListener<Number> {

        private final StackPane rootStackPane;
        private final StackPane stackPaneLCD;

        HboxWidthListener(StackPane rootStackPane, StackPane stackPaneLCD) {
            this.rootStackPane = rootStackPane;
            this.stackPaneLCD = stackPaneLCD;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double tempWidth = rootStackPane.getWidth() / 7.416;
            if (tempWidth < 192) {
                if (stackPaneLCD.getWidth() > 150) {
                    stackPaneLCD.setPrefWidth(tempWidth);

                } else {
                    stackPaneLCD.setPrefWidth(140);
                }
            } else {
                stackPaneLCD.setPrefWidth(192);
            }
        }
    }

    private void getLoggingInjectorSelection(Boolean value, ToggleButton ledController) {
        String s = String.format("LedBeaker %s selected: %s", ledController.getText(), value);
        logger.info(s);
    }

    private double calcTargetPress(Integer target){
        return target.doubleValue() / uisSettingsModel.pressureSensorProperty().get();
    }

    private void regulator_ON(){

        switch(regulationModesModel.regulatorOneModeProperty().get()){
            case PRESSURE:
                double press1 = calcTargetPress(pressureSpinner.getValue());
                ultimaModbusWriter.add(PressureReg1_PressTask, press1);
                ultimaModbusWriter.add(PressureReg1_ON, true);
                break;
            case CURRENT:
                double current1 = currentSpinner.getValue();
                ultimaModbusWriter.add(PressureReg1_I_Task, current1);
                ultimaModbusWriter.add(PressureReg1_ON, true);
                break;
            case DUTY:
                double duty1 = dutySpinner.getValue();
                ultimaModbusWriter.add(PressureReg1_DutyTask, duty1);
                ultimaModbusWriter.add(PressureReg1_ON, true);
                break;
            default:ultimaModbusWriter.add(PressureReg1_ON, false);
        }
    }

    private void disableNode(boolean disable, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);

    }

    private void selectButton(boolean select, ToggleButton ... nodes) {
        for (ToggleButton node : nodes){
            node.setSelected(select);
        }
    }
    private class ThreeSpinnerArrowClickHandler implements EventHandler<MouseEvent> {

        RegActive activeRegulatingMode;
        ModbusMapUltima mapParam_1;
        boolean mapParam_1_ON;
        ModbusMapUltima mapParam_2;
        boolean mapParam_2_ON;

        ThreeSpinnerArrowClickHandler(RegActive activeRegulatingMode,
                                      ModbusMapUltima mapParam_1,
                                      boolean mapParam_1_ON,
                                      ModbusMapUltima mapParam_2,
                                      boolean mapParam_2_ON) {
            this.activeRegulatingMode = activeRegulatingMode;
            this.mapParam_1 = mapParam_1;
            this.mapParam_1_ON = mapParam_1_ON;
            this.mapParam_2 = mapParam_2;
            this.mapParam_2_ON = mapParam_2_ON;
        }

        @Override
        public void handle(MouseEvent event) {
            regulationModesModel.regulatorOneModeProperty().setValue(activeRegulatingMode);
            ultimaModbusWriter.add(mapParam_1, mapParam_1_ON);
            ultimaModbusWriter.add(mapParam_2, mapParam_2_ON);
        }
    }

    private class ThreeSpinnerFocusListener implements ChangeListener<Boolean>{
        RegActive activeParam;
        ModbusMapUltima mapParam_1;
        boolean mapParam_1_ON;
        ModbusMapUltima mapParam_2;
        boolean mapParam_2_ON;

        ThreeSpinnerFocusListener(RegActive activeParam,
                                  ModbusMapUltima mapParam_1,
                                  boolean mapParam_1_ON,
                                  ModbusMapUltima mapParam_2,
                                  boolean mapParam_2_ON) {
            this.activeParam = activeParam;
            this.mapParam_1 = mapParam_1;
            this.mapParam_1_ON = mapParam_1_ON;
            this.mapParam_2 = mapParam_2;
            this.mapParam_2_ON = mapParam_2_ON;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (newValue){
                regulationModesModel.regulatorOneModeProperty().setValue(activeParam);
                ultimaModbusWriter.add(mapParam_1, mapParam_1_ON);
                ultimaModbusWriter.add(mapParam_2, mapParam_2_ON);
                if(uisInjectorSectionModel.powerButtonProperty().get()){
                    switch (activeParam){
                        case PRESSURE:
                            ultimaModbusWriter.add(PressureReg1_PressTask, calcTargetPress(pressureSpinner.getValue()));
                            break;
                        case CURRENT:
                            ultimaModbusWriter.add(PressureReg1_I_Task, currentSpinner.getValue());
                            break;
                        case DUTY:
                            ultimaModbusWriter.add(PressureReg1_DutyTask, dutySpinner.getValue());
                            break;
                    }
                }
            }
        }
    }

    private class ParameterChangeListener implements ChangeListener<Number>{

        RegActive activeParam;

        ParameterChangeListener(RegActive activeParam) {
            this.activeParam = activeParam;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            if((uisInjectorSectionModel.powerButtonProperty().get() && activeParam == regulationModesModel.regulatorOneModeProperty().get())){

                switch (regulationModesModel.regulatorOneModeProperty().get()){
                    case PRESSURE:
                        ultimaModbusWriter.add(PressureReg1_PressTask, calcTargetPress(newValue.intValue()));
                        System.err.println("press1 " + newValue);
                        break;
                    case CURRENT:
                        ultimaModbusWriter.add(PressureReg1_I_Task, newValue);
                        System.err.println("current1 " + newValue);
                        break;
                    case DUTY:
                        ultimaModbusWriter.add(PressureReg1_DutyTask, newValue);
                        System.err.println("duty1 " + newValue);

                        break;
                }
            }
        }
    }

    private class HighPressureSectionPwrListener implements ChangeListener<Boolean>{
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                if (regulatorToggleButton.isSelected()) {
                    regulator_ON();
                }
            }
            else {
                ultimaModbusWriter.add(PressureReg1_ON, false);
            }
        }
    }
}
