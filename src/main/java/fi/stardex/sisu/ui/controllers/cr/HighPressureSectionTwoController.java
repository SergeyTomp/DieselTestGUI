package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.HighPressureSectionPwrState;
import fi.stardex.sisu.util.enums.RegActive;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.listeners.TwoSpinnerStyleChangeListener;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.RegActive.CURRENT;
import static fi.stardex.sisu.util.enums.RegActive.DUTY;

public class HighPressureSectionTwoController {

    @FXML private Spinner<Double> currentSpinner;
    @FXML private Spinner<Double> dutySpinner;
    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private Label regLabel;
    @FXML private StackPane pwbStackPane;
    @FXML private ToggleButton regToggleButton;
    @FXML private StackPane rootStackPane;
    private HighPressureSectionPwrState highPressureSectionPwrState;
//    private ObjectProperty<RegActive> activeControlledParameter = new SimpleObjectProperty<>();
    private ModbusRegisterProcessor ultimaModbusWriter;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private final String GREEN_STYLE_CLASS = "regulator-spinner-selected";
    private I18N i18N;
    private RegulationModesModel regulationModesModel;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setHighPressureSectionPwrState(HighPressureSectionPwrState highPressureSectionPwrState) {
        this.highPressureSectionPwrState = highPressureSectionPwrState;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }

    public void setRegulationModesModel(RegulationModesModel regulationModesModel) {
        this.regulationModesModel = regulationModesModel;
    }

    @PostConstruct
    public void init(){

        bindingI18N();
        regulationModesModel.getRegulatorTwoMode().setValue(CURRENT);
        setupSpinners();
        addListeners();
    }

    private void setupSpinners(){
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_2_SPINNER_MIN, CURRENT_REG_2_SPINNER_MAX, CURRENT_REG_2_SPINNER_INIT, CURRENT_REG_2_SPINNER_STEP));
        dutySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_2_SPINNER_MIN, DUTY_CYCLE_REG_2_SPINNER_MAX, DUTY_CYCLE_REG_2_SPINNER_INIT, DUTY_CYCLE_REG_2_SPINNER_STEP));

        SpinnerManager.setupSpinner(currentSpinner, CURRENT_REG_2_SPINNER_INIT, CURRENT_REG_2_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(CURRENT_REG_2_SPINNER_INIT));
        SpinnerManager.setupSpinner(dutySpinner, DUTY_CYCLE_REG_2_SPINNER_INIT, DUTY_CYCLE_REG_2_SPINNER_FAKE, new Tooltip(), new SpinnerValueObtainer(DUTY_CYCLE_REG_2_SPINNER_INIT));

        currentSpinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        dutySpinner.getStyleClass().add(1, "");
    }

    private void addListeners(){

        pwbStackPane.widthProperty().addListener(new StackPanePowerButtonWidthListener(pwbStackPane, regToggleButton));

        /** слушаем активацию спиннеров регуляторов */

        /** смена режима по клику на стрелках*/
        currentSpinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(CURRENT, PressureReg2_I_Mode, true));
        dutySpinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(DUTY, PressureReg2_DutyMode, false));

        /** смена режима по клику в текстовом поле спиннера */
        currentSpinner.focusedProperty().addListener(new TwoSpinnerFocusListener(CURRENT, PressureReg2_I_Mode, true));
        dutySpinner.focusedProperty().addListener(new TwoSpinnerFocusListener(DUTY, PressureReg2_DutyMode, false));

        /**изменение цвета рамки спиннера на зелёный при переходе в спиннер другого рекгулятора*/
        currentSpinner.focusedProperty().addListener(new TwoSpinnerStyleChangeListener(currentSpinner, dutySpinner, CURRENT));
        dutySpinner.focusedProperty().addListener(new TwoSpinnerStyleChangeListener(currentSpinner, dutySpinner, DUTY));

        /** слушаем изменения значений в спиннерах и отправляем уставки спиннеров */
        currentSpinner.valueProperty().addListener(new ParameterChangeListener());
        dutySpinner.valueProperty().addListener(new ParameterChangeListener());

        /** слушаем кнопку включения секции регуляторов*/
        highPressureSectionPwrState.powerButtonProperty().addListener(new HighPressureSectionPwrListener());

        /** слушаем кнопку включения регулятора давления */
        regToggleButton.selectedProperty().addListener(new RegulatorButtonListener());

        /**слушаем изменения в модели данных, полученных из прошивки*/
        highPressureSectionUpdateModel.current_2Property().addListener((observableValue, oldValue, newValue) -> currentSpinner.getValueFactory().setValue((Double) newValue));
        highPressureSectionUpdateModel.duty_2Property().addListener((observableValue, oldValue, newValue) -> dutySpinner.getValueFactory().setValue((Double)newValue));
    }

    private void bindingI18N() {
        currentLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        dutyLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        regLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.reg2.name"));
    }

    private class TwoSpinnerArrowClickHandler implements EventHandler<MouseEvent> {
        RegActive activeParam;
        ModbusMapUltima mapParam;
        boolean mapParam_ON;

        public TwoSpinnerArrowClickHandler(RegActive activeParam,
                                           ModbusMapUltima mapParam,
                                           boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
        }

        @Override
        public void handle(MouseEvent event) {
            regulationModesModel.getRegulatorTwoMode().setValue(activeParam);
            if (highPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()) {
                ultimaModbusWriter.add(mapParam, mapParam_ON);
            }
        }
    }

    private class TwoSpinnerFocusListener implements ChangeListener<Boolean>{
        RegActive activeParam;
        ModbusMapUltima mapParam;
        boolean mapParam_ON;

        public TwoSpinnerFocusListener(RegActive activeParam,
                                       ModbusMapUltima mapParam,
                                       boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (newValue) {
                regulationModesModel.getRegulatorTwoMode().setValue(activeParam);
                if(highPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()){
                    ultimaModbusWriter.add(mapParam, mapParam_ON);
                }
            }
        }
    }

    private class ParameterChangeListener implements ChangeListener<Number>{

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if((highPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected())){
                switch (regulationModesModel.getRegulatorTwoMode().get()){
                    case CURRENT:
                        ultimaModbusWriter.add(PressureReg2_I_Task, newValue);
                        break;
                    case DUTY:
                        ultimaModbusWriter.add(PressureReg2_DutyTask, newValue);
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
                    ultimaModbusWriter.add(PressureReg2_ON, false);
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
                ultimaModbusWriter.add(PressureReg2_ON, false);
            }
        }
    }

    private void regulator_ON (){
        ultimaModbusWriter.add(PressureReg2_ON, true);
        switch (regulationModesModel.getRegulatorTwoMode().get()){
            case CURRENT:
                double current2 = currentSpinner.getValue();
                ultimaModbusWriter.add(PressureReg2_I_Task, current2);
                break;
            case DUTY:
                double duty2 = dutySpinner.getValue();
                ultimaModbusWriter.add(PressureReg2_DutyTask, duty2);
                break;
            default:ultimaModbusWriter.add(PressureReg2_ON, false);
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
}
