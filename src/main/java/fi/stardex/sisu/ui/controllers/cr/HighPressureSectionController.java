package fi.stardex.sisu.ui.controllers.cr;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PressureSensorModel;
import fi.stardex.sisu.states.RegulatorsQTYState;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.RegActive;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.RegActive.*;

public class HighPressureSectionController {

    @FXML
    private GridPane gridPaneHBox2;

    @FXML
    private Spinner<Integer> pressReg1Spinner;

    @FXML
    private Spinner<Double> currentReg1Spinner;

    @FXML
    private Spinner<Double> dutyCycleReg1Spinner;

    @FXML
    private Label labelPressReg1;

    @FXML
    private Label labelAmpReg1;

    @FXML
    private Label labelCycleReg1;

    @FXML
    private Label labelAmpReg2;

    @FXML
    private Label labelCycleReg2;

    @FXML
    private Spinner<Double> currentReg2Spinner;

    @FXML
    private Spinner<Double> dutyCycleReg2Spinner;

    @FXML
    private Label labelReg1;

    @FXML
    private Label labelReg2;

    @FXML
    private Label labelAmpReg3;

    @FXML
    private Label labelCycleReg3;

    @FXML
    private Spinner<Double> currentReg3Spinner;

    @FXML
    private Spinner<Double> dutyCycleReg3Spinner;

    @FXML
    private Label labelReg3;

    @FXML
    private ToggleButton powerButton1;

    @FXML
    private ToggleButton powerButton2;

    @FXML
    private ToggleButton powerButton3;

    @FXML private StackPane stackPWB1;

    @FXML private StackPane stackPWB2;

    @FXML private StackPane stackPWB3;

    @FXML private StackPane stackPaneLCD;

    @FXML private ToggleButton highPressureStartToggleButton;

    private Lcd pressureLcd;
    private RegActive Reg1paramActive;
    private RegActive Reg2paramActive;
    private RegActive Reg3paramActive;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private I18N i18N;
    private PressureSensorModel pressureSensorModel;
    private RegulatorsQTYState regulatorsQTYState;

    public static final String GREEN_STYLE_CLASS = "regulator-spinner-selected";

    public Spinner<Integer> getPressReg1Spinner() {
        return pressReg1Spinner;
    }

    public Spinner<Double> getCurrentReg1Spinner() {
        return currentReg1Spinner;
    }

    public Spinner<Double> getDutyCycleReg1Spinner() {
        return dutyCycleReg1Spinner;
    }

    public Spinner<Double> getCurrentReg2Spinner() {
        return currentReg2Spinner;
    }

    public Spinner<Double> getDutyCycleReg2Spinner() {
        return dutyCycleReg2Spinner;
    }

    public Spinner<Double> getCurrentReg3Spinner() {
        return currentReg3Spinner;
    }

    public Spinner<Double> getDutyCycleReg3Spinner() {
        return dutyCycleReg3Spinner;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public RegActive getReg1paramActive() {
        return Reg1paramActive;
    }

    public RegActive getReg2paramActive() {
        return Reg2paramActive;
    }

    public RegActive getReg3paramActive() {
        return Reg3paramActive;
    }

    public StackPane getStackPaneLCD() {
        return stackPaneLCD;
    }

    public ToggleButton getHighPressureStartToggleButton() {
        return highPressureStartToggleButton;
    }

    public Lcd getPressureLcd() {
        return pressureLcd;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setPressureSensorModel(PressureSensorModel pressureSensorModel) {
        this.pressureSensorModel = pressureSensorModel;
    }

    public void setRegulatorsQTYState(RegulatorsQTYState regulatorsQTYState) {
        this.regulatorsQTYState = regulatorsQTYState;
    }

    @PostConstruct
    public void init() {

        bindingI18N();

        /** установка активных параметров регулирования по умолчанию */
        Reg1paramActive = PRESSURE;
        Reg2paramActive = CURRENT;
        Reg3paramActive = CURRENT;

        /** создаём спиннеры регуляторов */
        currentReg1Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_1_SPINNER_MIN, CURRENT_REG_1_SPINNER_MAX, CURRENT_REG_1_SPINNER_INIT, CURRENT_REG_1_SPINNER_STEP));
        dutyCycleReg1Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_1_SPINNER_MIN, DUTY_CYCLE_REG_1_SPINNER_MAX, DUTY_CYCLE_REG_1_SPINNER_INIT, DUTY_CYCLE_REG_1_SPINNER_STEP));
        currentReg2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_2_SPINNER_MIN, CURRENT_REG_2_SPINNER_MAX, CURRENT_REG_2_SPINNER_INIT, CURRENT_REG_2_SPINNER_STEP));
        dutyCycleReg2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_2_SPINNER_MIN, DUTY_CYCLE_REG_2_SPINNER_MAX, DUTY_CYCLE_REG_2_SPINNER_INIT, DUTY_CYCLE_REG_2_SPINNER_STEP));
        currentReg3Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_3_SPINNER_MIN, CURRENT_REG_3_SPINNER_MAX, CURRENT_REG_3_SPINNER_INIT, CURRENT_REG_3_SPINNER_STEP));
        dutyCycleReg3Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_3_SPINNER_MIN, DUTY_CYCLE_REG_3_SPINNER_MAX, DUTY_CYCLE_REG_3_SPINNER_INIT, DUTY_CYCLE_REG_3_SPINNER_STEP));

        pressReg1Spinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        currentReg1Spinner.getStyleClass().add(1, "");
        dutyCycleReg1Spinner.getStyleClass().add(1, "");
        currentReg2Spinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        dutyCycleReg2Spinner.getStyleClass().add(1, "");
        currentReg3Spinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        dutyCycleReg3Spinner.getStyleClass().add(1, "");

        int maxPressure = pressureSensorModel.pressureSensorProperty().intValue();
        pressReg1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, maxPressure, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));
        pressureSensorModel.pressureSensorProperty().addListener(((observable, oldValue, newValue) -> pressReg1Spinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, newValue.intValue(), PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP))));

        configRegulatorsInvolved(Integer.parseInt(regulatorsQTYState.regulatorsQTYStateProperty().get()));
        regulatorsQTYState.regulatorsQTYStateProperty().addListener(new RegulatorsConfigListener());

        SpinnerManager.setupSpinner(pressReg1Spinner, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_MIN, PRESS_REG_1_SPINNER_MAX, new Tooltip(), new SpinnerValueObtainer(PRESS_REG_1_SPINNER_INIT));
        SpinnerManager.setupSpinner(currentReg1Spinner, CURRENT_REG_1_SPINNER_INIT, CURRENT_REG_1_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(CURRENT_REG_1_SPINNER_INIT));
        SpinnerManager.setupSpinner(dutyCycleReg1Spinner, DUTY_CYCLE_REG_1_SPINNER_INIT, DUTY_CYCLE_REG_1_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(DUTY_CYCLE_REG_1_SPINNER_INIT));
        SpinnerManager.setupSpinner(currentReg2Spinner, CURRENT_REG_2_SPINNER_INIT, CURRENT_REG_2_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(CURRENT_REG_2_SPINNER_INIT));
        SpinnerManager.setupSpinner(dutyCycleReg2Spinner, DUTY_CYCLE_REG_2_SPINNER_INIT, DUTY_CYCLE_REG_2_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(DUTY_CYCLE_REG_2_SPINNER_INIT));
        SpinnerManager.setupSpinner(currentReg3Spinner, CURRENT_REG_3_SPINNER_INIT, CURRENT_REG_3_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(CURRENT_REG_3_SPINNER_INIT));
        SpinnerManager.setupSpinner(dutyCycleReg3Spinner, DUTY_CYCLE_REG_3_SPINNER_INIT, DUTY_CYCLE_REG_3_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(DUTY_CYCLE_REG_3_SPINNER_INIT));

        /** слушаем активацию спиннеров регуляторов */

        /** смена режима по клику на стрелках*/

        //вкл.режим давления, откл.режим тока
        pressReg1Spinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(PRESSURE, powerButton1, PressureReg1_PressMode, true, PressureReg1_I_Mode, false));
        //откл.режим давления, вкл.режим тока
        currentReg1Spinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(CURRENT, powerButton1, PressureReg1_PressMode, false, PressureReg1_I_Mode, true));
        //откл.режим давления, откл.режим тока, вкл.режим скважности
        dutyCycleReg1Spinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(DUTY, powerButton1, PressureReg1_PressMode, false, PressureReg1_I_Mode, false));

        currentReg2Spinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(CURRENT, powerButton2, PressureReg2_I_Mode, true));
        dutyCycleReg2Spinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(DUTY, powerButton2, PressureReg2_DutyMode, false));

        currentReg3Spinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(CURRENT, powerButton3, PressureReg3_I_Mode, true));
        dutyCycleReg3Spinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(DUTY, powerButton3, PressureReg3_DutyMode, false));

        /** смена режима по клику в текстовом поле спиннера */

        // спиннеры регулятора 1
        //вкл.режим давления, откл.режим тока
        pressReg1Spinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(PRESSURE, powerButton1, PressureReg1_PressMode, true, PressureReg1_I_Mode, false));
        //откл.режим давления, вкл.режим тока
        currentReg1Spinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(CURRENT, powerButton1, PressureReg1_PressMode, false, PressureReg1_I_Mode, true));
        //откл.режим давления, откл.режим тока, вкл.режим скважности
        dutyCycleReg1Spinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(DUTY, powerButton1, PressureReg1_PressMode, false, PressureReg1_I_Mode, false));

        // спиннеры регулятора 2
        currentReg2Spinner.focusedProperty().addListener(new TwoSpinnerFocusListener(CURRENT, powerButton2, PressureReg2_I_Mode, true));
        dutyCycleReg2Spinner.focusedProperty().addListener(new TwoSpinnerFocusListener(DUTY, powerButton2, PressureReg2_DutyMode, false));

        // спиннеры регулятора 3
        currentReg3Spinner.focusedProperty().addListener(new TwoSpinnerFocusListener(CURRENT, powerButton3, PressureReg3_I_Mode, true));
        dutyCycleReg3Spinner.focusedProperty().addListener(new TwoSpinnerFocusListener(DUTY, powerButton3, PressureReg3_DutyMode, false));

        /** слушаем изменения значений в спиннерах и отправляем уставки спиннеров */

        // регулятор 1
        pressReg1Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton1));
        currentReg1Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton1));
        dutyCycleReg1Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton1));

        // регулятор 2
        currentReg2Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton2));
        dutyCycleReg2Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton2));

        // регулятор 3
        currentReg3Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton3));
        dutyCycleReg3Spinner.valueProperty().addListener(new ParameterChangeListener(powerButton3));

        /** слушаем кнопку включения секции регуляторов*/
        highPressureStartToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                if (powerButton1.isSelected()) {
                    regulator1_ON();
                }
                if (powerButton2.isSelected()) {
                    regulator2_ON ();
                }
                if (powerButton3.isSelected()) {
                    regulator3_ON();
                }
            }
            else {
                ultimaModbusWriter.add(PressureReg1_ON, false);
                ultimaModbusWriter.add(PressureReg2_ON, false);
                ultimaModbusWriter.add(PressureReg3_ON, false);
            }
        });

        /** слушаем кнопки включения регуляторов давления */

        powerButton1.selectedProperty().addListener(new SectionButtonListener(powerButton1));
        powerButton2.selectedProperty().addListener(new SectionButtonListener(powerButton2));
        powerButton3.selectedProperty().addListener(new SectionButtonListener(powerButton3));


        /** рисуем LCD */
        pressureLcd = GaugeCreator.createLcd();
        pressureLcd.setMaxValue(5000.0);
        pressureLcd.setValue(0);

        stackPaneLCD.getChildren().add(pressureLcd);
        Tooltip lcdTooltip = new Tooltip("Real time pressure sensor readings.");
        lcdTooltip.getStyleClass().add("ttip");
        Tooltip.install(pressureLcd, lcdTooltip);

        stackPWB1.widthProperty().addListener(new StackPanePowerButtonWidthListener(stackPWB1, powerButton1));
        stackPWB2.widthProperty().addListener(new StackPanePowerButtonWidthListener(stackPWB2, powerButton2));
        stackPWB3.widthProperty().addListener(new StackPanePowerButtonWidthListener(stackPWB3, powerButton3));
        gridPaneHBox2.widthProperty().addListener(new GridPaneWidthListener(gridPaneHBox2, stackPaneLCD));
    }

    public void setVisibleRegulator2(boolean visible) {
        Enabler.setVisible(visible,
                labelReg2, labelAmpReg2, labelCycleReg2, powerButton2, dutyCycleReg2Spinner, currentReg2Spinner);
    }

    public void setVisibleRegulator3(boolean visible) {
        Enabler.setVisible(visible,
                labelReg3, labelAmpReg3, labelCycleReg3, powerButton3, dutyCycleReg3Spinner, currentReg3Spinner);
    }

    private void regulator1_ON(){
        ultimaModbusWriter.add(PressureReg1_ON, true);
        switch(Reg1paramActive){
            case PRESSURE:
                double press1 = pressReg1Spinner.getValue();
                ultimaModbusWriter.add(PressureReg1_PressTask, press1);
                break;
            case CURRENT:
                double current1 = currentReg1Spinner.getValue();
                ultimaModbusWriter.add(PressureReg1_I_Task, current1);
                break;
            case DUTY:
                double duty1 = dutyCycleReg1Spinner.getValue();
                ultimaModbusWriter.add(PressureReg1_DutyTask, duty1);
                break;
            default:ultimaModbusWriter.add(PressureReg1_ON, false);
        }
    }

    private void regulator2_ON (){
        ultimaModbusWriter.add(PressureReg2_ON, true);
        switch (Reg2paramActive){
            case CURRENT:
                double current2 = currentReg2Spinner.getValue();
                ultimaModbusWriter.add(PressureReg2_I_Task, current2);
                break;
            case DUTY:
                double duty2 = dutyCycleReg2Spinner.getValue();
                ultimaModbusWriter.add(PressureReg2_DutyTask, duty2);
                break;
                default:ultimaModbusWriter.add(PressureReg2_ON, false);
        }
    }

    private void regulator3_ON(){
        ultimaModbusWriter.add(PressureReg3_ON, true);
        switch (Reg3paramActive){
            case CURRENT:
                double current3 = currentReg3Spinner.getValue();
                ultimaModbusWriter.add(PressureReg3_I_Task, current3);
                break;
            case DUTY:
                double duty3 = dutyCycleReg3Spinner.getValue();
                ultimaModbusWriter.add(PressureReg3_DutyTask, duty3);
                break;
                default:ultimaModbusWriter.add(PressureReg3_ON, false);
        }
    }

    /** метод запускается из MainSectionController при выборе режимов тест и авто */
    public void regulator1pressModeON(Integer targetPress){
        if (Reg1paramActive != PRESSURE){
            Reg1paramActive = PRESSURE;
            ultimaModbusWriter.add(PressureReg1_PressMode, true);   //вкл.режим давления
            ultimaModbusWriter.add(PressureReg1_I_Mode, false);     //откл.режим тока
        }
        pressReg1Spinner.getValueFactory().setValue(targetPress);
        ultimaModbusWriter.add(PressureReg1_PressTask, targetPress);
        powerButton1.setSelected(true);
    }

    /** метод запускается из MainSectionController при выборе ручного режима */
    public void regulator1pressModeOFF(){
        ultimaModbusWriter.add(PressureReg1_ON, false);
        powerButton1.setSelected(false);
    }

    private void bindingI18N() {
        labelPressReg1.textProperty().bind(i18N.createStringBinding("highPressure.label.bar"));
        labelAmpReg1.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        labelCycleReg1.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        labelAmpReg2.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        labelCycleReg2.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        labelAmpReg3.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        labelCycleReg3.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        labelReg1.textProperty().bind(i18N.createStringBinding("highPressure.label.reg1.name"));
        labelReg2.textProperty().bind(i18N.createStringBinding("highPressure.label.reg2.name"));
        labelReg3.textProperty().bind(i18N.createStringBinding("highPressure.label.reg3.name"));
    }

    private void configRegulatorsInvolved(int number) {
        switch (number){
            case 1:
                setVisibleRegulator2(false);
                setVisibleRegulator3(false);
                break;
            case 2:
                setVisibleRegulator2(true);
                setVisibleRegulator3(false);
                break;
            case 3:
                setVisibleRegulator2(true);
                setVisibleRegulator3(true);
                break;
            default:
                setVisibleRegulator2(true);
                setVisibleRegulator3(true);
                break;
        }
    }

    private class RegulatorsConfigListener implements ChangeListener<String> {
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

    private class StackPanePowerButtonWidthListener implements ChangeListener<Number> {

        private final StackPane stackPWB;
        private final ToggleButton powerButton;

        public StackPanePowerButtonWidthListener(StackPane stackPWB, ToggleButton powerButton) {
            this.stackPWB = stackPWB;
            this.powerButton = powerButton;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if(stackPWB.getWidth()> 105 ) {
                if(stackPWB.getWidth()<155){
                    powerButton.setPrefWidth(stackPWB.widthProperty().get() * 0.57);
                    powerButton.setPrefHeight(stackPWB.widthProperty().get() * 0.57 + 5);
                } else {
                    powerButton.setPrefWidth(90.0);
                    powerButton.setPrefHeight(99.0);
                }
            } else {
                powerButton.setPrefWidth(60.0);
                powerButton.setPrefHeight(65.0);
            }
        }
    }

    private class GridPaneWidthListener implements ChangeListener<Number> {

        private final GridPane rootGridPane;
        private final StackPane stackPaneLCD;

        public GridPaneWidthListener(GridPane rootGridPane, StackPane stackPaneLCD) {
            this.rootGridPane = rootGridPane;
            this.stackPaneLCD = stackPaneLCD;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double tempWidth = rootGridPane.getWidth() / 7.416;
            if (tempWidth < 192) {
                if (stackPaneLCD.getWidth() > 150) {
                    stackPaneLCD.setPrefWidth(tempWidth);

                } else {
                    stackPaneLCD.setPrefWidth(135);
                }
            } else {
                stackPaneLCD.setPrefWidth(192);
            }
        }
    }

    private class ThreeSpinnerArrowClickHandler implements EventHandler<MouseEvent> {

        RegActive activeParam;
        ModbusMapUltima mapParam_1;
        boolean mapParam_1_ON;
        ModbusMapUltima mapParam_2;
        boolean mapParam_2_ON;
        ToggleButton powerButton;

        public ThreeSpinnerArrowClickHandler(RegActive activeParam,
                                             ToggleButton powerButton,
                                             ModbusMapUltima mapParam_1,
                                             boolean mapParam_1_ON,
                                             ModbusMapUltima mapParam_2,
                                             boolean mapParam_2_ON) {
            this.activeParam = activeParam;
            this.mapParam_1 = mapParam_1;
            this.mapParam_1_ON = mapParam_1_ON;
            this.mapParam_2 = mapParam_2;
            this.mapParam_2_ON = mapParam_2_ON;
            this.powerButton = powerButton;
        }

        @Override
        public void handle(MouseEvent event) {
            Reg1paramActive = activeParam;
            if (highPressureStartToggleButton.isSelected() && powerButton.isSelected()) {
                ultimaModbusWriter.add(mapParam_1, mapParam_1_ON);
                ultimaModbusWriter.add(mapParam_2, mapParam_2_ON);
            }
        }
    }

    private class TwoSpinnerArrowClickHandler implements EventHandler<MouseEvent> {
        RegActive activeParam;
        ModbusMapUltima mapParam;
        boolean mapParam_ON;
        ToggleButton powerButton;

        public TwoSpinnerArrowClickHandler(RegActive activeParam,
                                           ToggleButton powerButton,
                                           ModbusMapUltima mapParam,
                                           boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
            this.powerButton = powerButton;
        }

        @Override
        public void handle(MouseEvent event) {
            if (powerButton == powerButton2){
                Reg2paramActive = activeParam;
            }
            else if(powerButton == powerButton3){
                Reg3paramActive = activeParam;
            }
            if (highPressureStartToggleButton.isSelected() && powerButton.isSelected()) {
                ultimaModbusWriter.add(mapParam, mapParam_ON);
            }
        }
    }



    private class ThreeSpinnerFocusListener implements ChangeListener<Boolean>{
        RegActive activeParam;
        ModbusMapUltima mapParam_1;
        boolean mapParam_1_ON;
        ModbusMapUltima mapParam_2;
        boolean mapParam_2_ON;
        ToggleButton powerButton;

        public ThreeSpinnerFocusListener(RegActive activeParam,
                                             ToggleButton powerButton,
                                             ModbusMapUltima mapParam_1,
                                             boolean mapParam_1_ON,
                                             ModbusMapUltima mapParam_2,
                                             boolean mapParam_2_ON) {
            this.activeParam = activeParam;
            this.mapParam_1 = mapParam_1;
            this.mapParam_1_ON = mapParam_1_ON;
            this.mapParam_2 = mapParam_2;
            this.mapParam_2_ON = mapParam_2_ON;
            this.powerButton = powerButton;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (newValue){
                Reg1paramActive = activeParam;
                switch (Reg1paramActive){
                    case PRESSURE:
                        pressReg1Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                        currentReg1Spinner.getStyleClass().set(1, "");
                        dutyCycleReg1Spinner.getStyleClass().set(1, "");
                        break;
                    case CURRENT:
                        pressReg1Spinner.getStyleClass().set(1, "");
                        currentReg1Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                        dutyCycleReg1Spinner.getStyleClass().set(1, "");
                        break;
                    case DUTY:
                        pressReg1Spinner.getStyleClass().set(1, "");
                        currentReg1Spinner.getStyleClass().set(1, "");
                        dutyCycleReg1Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                        break;
                }
                if(highPressureStartToggleButton.isSelected() && powerButton.isSelected()){
                    ultimaModbusWriter.add(mapParam_1, mapParam_1_ON);
                    ultimaModbusWriter.add(mapParam_2, mapParam_2_ON);
                }
            }
        }
    }

    private class TwoSpinnerFocusListener implements ChangeListener<Boolean>{
        RegActive activeParam;
        ModbusMapUltima mapParam;
        boolean mapParam_ON;
        ToggleButton powerButton;

        public TwoSpinnerFocusListener(RegActive activeParam,
                                           ToggleButton powerButton,
                                           ModbusMapUltima mapParam,
                                           boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
            this.powerButton = powerButton;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (newValue) {
                if (powerButton == powerButton2){
                    Reg2paramActive = activeParam;
                    switch (Reg2paramActive){
                        case CURRENT:
                            currentReg2Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                            dutyCycleReg2Spinner.getStyleClass().set(1, "");
                            break;
                        case DUTY:
                            currentReg2Spinner.getStyleClass().set(1, "");
                            dutyCycleReg2Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                            break;
                    }
                }
                else if(powerButton == powerButton3){
                    Reg3paramActive = activeParam;
                    switch (Reg3paramActive){
                        case CURRENT:
                            currentReg3Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                            dutyCycleReg3Spinner.getStyleClass().set(1, "");
                            break;
                        case DUTY:
                            currentReg3Spinner.getStyleClass().set(1, "");
                            dutyCycleReg3Spinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
                            break;
                    }
                }
                if(highPressureStartToggleButton.isSelected() && powerButton.isSelected()){
                    ultimaModbusWriter.add(mapParam, mapParam_ON);
                }
            }
        }
    }

    private class ParameterChangeListener implements ChangeListener<Number>{

        ToggleButton powerButton;

        public ParameterChangeListener(ToggleButton powerButton) {
            this.powerButton = powerButton;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            if((highPressureStartToggleButton.isSelected() && powerButton.isSelected())){
                if (powerButton == powerButton1){
                    switch (Reg1paramActive){
                        case PRESSURE:
                            ultimaModbusWriter.add(PressureReg1_PressTask, newValue);
                            break;
                        case CURRENT:
                            ultimaModbusWriter.add(PressureReg1_I_Task, newValue);
                            break;
                        case DUTY:
                            ultimaModbusWriter.add(PressureReg1_DutyTask, newValue);
                            break;
                    }
                }
                else if(powerButton == powerButton2){
                    switch (Reg2paramActive){
                        case CURRENT:
                            ultimaModbusWriter.add(PressureReg2_I_Task, newValue);
                            break;
                        case DUTY:
                            ultimaModbusWriter.add(PressureReg2_DutyTask, newValue);
                            break;
                    }
                }
                else if(powerButton == powerButton3){
                    switch (Reg3paramActive){
                        case CURRENT:
                            ultimaModbusWriter.add(PressureReg3_I_Task, newValue);
                            break;
                        case DUTY:
                            ultimaModbusWriter.add(PressureReg3_DutyTask, newValue);
                            break;
                    }
                }
            }
        }
    }

    private class SectionButtonListener implements ChangeListener<Boolean>{

        ToggleButton powerButton;

        public SectionButtonListener(ToggleButton powerButton) {
            this.powerButton = powerButton;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(highPressureStartToggleButton.selectedProperty().getValue()){
                if(newValue){
                    if(powerButton == powerButton1){
                        regulator1_ON();
                    }
                    else if(powerButton == powerButton2){
                        regulator2_ON();
                    }
                    else if(powerButton == powerButton3){
                        regulator3_ON();
                    }
                }
                else{
                    if(powerButton == powerButton1){
                        ultimaModbusWriter.add(PressureReg1_ON, false);
                    }
                    else if(powerButton == powerButton2){
                        ultimaModbusWriter.add(PressureReg2_ON, false);
                    }
                    else if(powerButton == powerButton3){
                        ultimaModbusWriter.add(PressureReg3_ON, false);
                    }
                }
            }
        }
    }
}
