package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.model.InjectorTestModel;
import fi.stardex.sisu.model.PressureRegulatorOneModel;
import fi.stardex.sisu.model.PressureSensorModel;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.HighPressureSectionPwrState;
import fi.stardex.sisu.util.enums.RegActive;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.listeners.ThreeSpinnerStyleChangeListener;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.RegActive.*;

public class HighPressureSectionOneController {

    @FXML private StackPane rootStackPane;
    @FXML private Spinner<Integer> pressSpinner;
    @FXML private Spinner<Double> currentSpinner;
    @FXML private Spinner<Double> dutySpinner;
    @FXML private Label pressureLabel;
    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private Label regLabel;
    @FXML private ToggleButton regToggleButton;
    @FXML private StackPane pwbStackPane;
    private HighPressureSectionPwrState highPressureSectionPwrState;
    private PressureSensorModel pressureSensorModel;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private PressureRegulatorOneModel pressureRegulatorOneModel;
    private InjectorTestModel injectorTestModel;
    private final String GREEN_STYLE_CLASS = "regulator-spinner-selected";
    private I18N i18N;
    private RegulationModesModel regulationModesModel;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setHighPressureSectionPwrState(HighPressureSectionPwrState highPressureSectionPwrState) {
        this.highPressureSectionPwrState = highPressureSectionPwrState;
    }

    public void setPressureSensorModel(PressureSensorModel pressureSensorModel) {
        this.pressureSensorModel = pressureSensorModel;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }

    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    public void setPressureRegulatorOneModel(PressureRegulatorOneModel pressureRegulatorOneModel) {
        this.pressureRegulatorOneModel = pressureRegulatorOneModel;
    }

    public void setRegulationModesModel(RegulationModesModel regulationModesModel) {
        this.regulationModesModel = regulationModesModel;
    }

    @PostConstruct
    public void init(){

        bindingI18N();
        regulationModesModel.regulatorOneModeProperty().setValue(PRESSURE);
        setupSpinners();
        addListeners();
    }

    private void bindingI18N() {
        pressureLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.bar"));
        currentLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        dutyLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        regLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.reg1.name"));
    }

    private void setupSpinners(){
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_1_SPINNER_MIN, CURRENT_REG_1_SPINNER_MAX, CURRENT_REG_1_SPINNER_INIT, CURRENT_REG_1_SPINNER_STEP));
        dutySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_1_SPINNER_MIN, DUTY_CYCLE_REG_1_SPINNER_MAX, DUTY_CYCLE_REG_1_SPINNER_INIT, DUTY_CYCLE_REG_1_SPINNER_STEP));

        int maxPressure = pressureSensorModel.pressureSensorProperty().intValue();
        pressSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, maxPressure, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));
        pressureSensorModel.pressureSensorProperty().addListener(((observable, oldValue, newValue) -> pressSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, newValue.intValue(), PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP))));

        SpinnerManager.setupIntegerSpinner(pressSpinner);
        SpinnerManager.setupDoubleSpinner(currentSpinner);
        SpinnerManager.setupDoubleSpinner(dutySpinner);

        pressSpinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        currentSpinner.getStyleClass().add(1, "");
        dutySpinner.getStyleClass().add(1, "");
    }

    private void addListeners(){

        pwbStackPane.widthProperty().addListener(new StackPanePowerButtonWidthListener(pwbStackPane, regToggleButton));

        /** слушаем активацию спиннеров регуляторов */

        /** смена режима по клику на стрелках*/
        //вкл.режим давления, откл.режим тока
        pressSpinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(PRESSURE, PressureReg1_PressMode, true, PressureReg1_I_Mode, false));

        //откл.режим давления, вкл.режим тока
        currentSpinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(CURRENT, PressureReg1_PressMode, false, PressureReg1_I_Mode, true));

        //откл.режим давления, откл.режим тока, вкл.режим скважности
        dutySpinner.setOnMouseClicked(new ThreeSpinnerArrowClickHandler(DUTY, PressureReg1_PressMode, false, PressureReg1_I_Mode, false));

        /** смена режима по клику в текстовом поле спиннера */
        //вкл.режим давления, откл.режим тока
        pressSpinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(PRESSURE, PressureReg1_PressMode, true, PressureReg1_I_Mode, false));
        //откл.режим давления, вкл.режим тока
        currentSpinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(CURRENT, PressureReg1_PressMode, false, PressureReg1_I_Mode, true));
        //откл.режим давления, откл.режим тока, вкл.режим скважности
        dutySpinner.focusedProperty().addListener(new ThreeSpinnerFocusListener(DUTY, PressureReg1_PressMode, false, PressureReg1_I_Mode, false));

        /**изменение цвета рамки спиннера на зелёный при переходе в спиннер другого рекгулятора*/
        pressSpinner.focusedProperty().addListener(new ThreeSpinnerStyleChangeListener(pressSpinner, currentSpinner, dutySpinner, PRESSURE));
        currentSpinner.focusedProperty().addListener(new ThreeSpinnerStyleChangeListener(pressSpinner, currentSpinner, dutySpinner, CURRENT));
        dutySpinner.focusedProperty().addListener(new ThreeSpinnerStyleChangeListener(pressSpinner, currentSpinner, dutySpinner, DUTY));

        /** слушаем изменения значений в спиннерах и отправляем уставки спиннеров */
        pressSpinner.valueProperty().addListener(new ParameterChangeListener(PRESSURE));
        pressureRegulatorOneModel.pressureRegOneProperty().bind(pressSpinner.valueProperty());
        currentSpinner.valueProperty().addListener(new ParameterChangeListener(CURRENT));
        dutySpinner.valueProperty().addListener(new ParameterChangeListener(DUTY));

        /** слушаем кнопку включения секции регуляторов*/
        highPressureSectionPwrState.powerButtonProperty().addListener(new HighPressureSectionPwrListener());

        /** слушаем кнопку включения регулятора давления */
        regToggleButton.selectedProperty().addListener(new RegulatorButtonListener());

        /**слушаем изменения в модели данных, полученных из прошивки (значения полей в модели данных изменяются только для "наблюдающих" спиннеров)*/
        highPressureSectionUpdateModel.current_1Property().addListener((observableValue, oldValue, newValue) -> currentSpinner.getValueFactory().setValue((Double) newValue));
        highPressureSectionUpdateModel.duty_1Property().addListener((observableValue, oldValue, newValue) -> dutySpinner.getValueFactory().setValue((Double)newValue));

        /**слушаем изменения в модели выбранного теста*/
        injectorTestModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                regulator1pressModeON(newValue.getSettedPressure());
            }
            else regulator1pressModeOFF();
        });
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

    private class ThreeSpinnerFocusListener implements ChangeListener<Boolean>{
        RegActive activeParam;
        ModbusMapUltima mapParam_1;
        boolean mapParam_1_ON;
        ModbusMapUltima mapParam_2;
        boolean mapParam_2_ON;

        public ThreeSpinnerFocusListener(RegActive activeParam,
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
                if(highPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()){
                    ultimaModbusWriter.add(mapParam_1, mapParam_1_ON);
                    ultimaModbusWriter.add(mapParam_2, mapParam_2_ON);
                }
            }
        }
    }

    private class ThreeSpinnerArrowClickHandler implements EventHandler<MouseEvent> {

        RegActive activeParam;
        ModbusMapUltima mapParam_1;
        boolean mapParam_1_ON;
        ModbusMapUltima mapParam_2;
        boolean mapParam_2_ON;

        public ThreeSpinnerArrowClickHandler(RegActive activeParam,
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
        public void handle(MouseEvent event) {
            regulationModesModel.regulatorOneModeProperty().setValue(activeParam);
            if (highPressureSectionPwrState.powerButtonProperty().get()  && regToggleButton.isSelected()) {
                ultimaModbusWriter.add(mapParam_1, mapParam_1_ON);
                ultimaModbusWriter.add(mapParam_2, mapParam_2_ON);

            }
        }
    }

    private class ParameterChangeListener implements ChangeListener<Number>{

        RegActive activeParam;

        public ParameterChangeListener(RegActive activeParam) {
            this.activeParam = activeParam;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            if((highPressureSectionPwrState.powerButtonProperty().get()
                    && regToggleButton.isSelected()
                    && activeParam == regulationModesModel.regulatorOneModeProperty().get())){

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

    private class RegulatorButtonListener implements ChangeListener<Boolean>{

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(highPressureSectionPwrState.powerButtonProperty().get()){
                if(newValue){
                    regulator_ON();

                }
                else{
                    ultimaModbusWriter.add(PressureReg1_ON, false);
                }
            }
        }
    }

    private class HighPressureSectionPwrListener implements ChangeListener<Boolean>{
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                if (regToggleButton.isSelected()) {
                    regulator_ON();
                }
            }
            else {
                ultimaModbusWriter.add(PressureReg1_ON, false);
            }
        }
    }

    private void regulator_ON(){
        ultimaModbusWriter.add(PressureReg1_ON, true);
        switch(regulationModesModel.regulatorOneModeProperty().get()){
            case PRESSURE:
                double press1 = calcTargetPress(pressSpinner.getValue());
                ultimaModbusWriter.add(PressureReg1_PressTask, press1);
                break;
            case CURRENT:
                double current1 = currentSpinner.getValue();
                ultimaModbusWriter.add(PressureReg1_I_Task, current1);
                break;
            case DUTY:
                double duty1 = dutySpinner.getValue();
                ultimaModbusWriter.add(PressureReg1_DutyTask, duty1);
                break;
            default:ultimaModbusWriter.add(PressureReg1_ON, false);
        }
    }

    /** метод запускается при выборе режимов тест и авто */
    private void regulator1pressModeON(Integer targetPress){
        if (regulationModesModel.regulatorOneModeProperty().get() != PRESSURE){
            regulationModesModel.regulatorOneModeProperty().setValue(PRESSURE);
            ultimaModbusWriter.add(PressureReg1_PressMode, true);   //вкл.режим давления
            ultimaModbusWriter.add(PressureReg1_I_Mode, false);     //откл.режим тока
        }
        pressSpinner.getValueFactory().setValue(targetPress);
        ultimaModbusWriter.add(PressureReg1_PressTask, calcTargetPress(targetPress));
        regToggleButton.setSelected(true);
    }

    /** метод запускается при выборе ручного режима тестирования*/
    private void regulator1pressModeOFF(){
        ultimaModbusWriter.add(PressureReg1_ON, false);
        regToggleButton.setSelected(false);
    }

    private double calcTargetPress(Integer target){
        return target.doubleValue() / pressureSensorModel.pressureSensorProperty().get();
    }


}
