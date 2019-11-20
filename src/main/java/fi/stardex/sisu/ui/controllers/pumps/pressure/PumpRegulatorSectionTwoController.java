package fi.stardex.sisu.ui.controllers.pumps.pressure;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.model.pump.PumpPressureRegulatorOneModel;
import fi.stardex.sisu.model.pump.PumpTestModel;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.RegActive;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.listeners.TwoSpinnerStyleChangeListener;
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
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.GUI_type.CR_Pump;
import static fi.stardex.sisu.util.enums.RegActive.*;

public class PumpRegulatorSectionTwoController {

    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private Label regLabel;
    @FXML private ToggleButton regToggleButton;
    @FXML private StackPane rootStackPane;
    @FXML private Spinner<Double> currentSpinner;
    @FXML private Spinner<Double> dutySpinner;
    @FXML private VBox gaugeVBox;

    private Gauge gauge;
    private I18N i18N;
    private PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private RegulationModesModel regulationModesModel;
    private PumpModel pumpModel;
    private PumpTestModel pumpTestModel;
    private PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel;
    private GUI_TypeModel gui_typeModel;
    private final String GREEN_STYLE_CLASS = "regulator-spinner-selected";

    public Spinner<Double> getCurrentSpinner() {
        return currentSpinner;
    }

    public void setHighPressureSectionPwrState(PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState) {
        this.pumpHighPressureSectionPwrState = pumpHighPressureSectionPwrState;
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
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpPressureRegulatorOneModel(PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel) {
        this.pumpPressureRegulatorOneModel = pumpPressureRegulatorOneModel;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){
        gauge = GaugeCreator.createPumpGauge(CURRENT_REG_2_SPINNER_MAX);
        gauge.setMaxValue(CURRENT_REG_2_SPINNER_MAX);
        gaugeVBox.getChildren().add(1, gauge);
        bindingI18N();
        setupSpinners();
        addListeners();
        addRegulatorDependentListeners();
    }

    private void bindingI18N() {

        currentLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        dutyLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        regLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.reg2.name"));

    }

    private void setupSpinners(){
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                CURRENT_REG_2_SPINNER_MIN,
                CURRENT_REG_2_SPINNER_MAX,
                CURRENT_REG_2_SPINNER_INIT,
                CURRENT_REG_2_SPINNER_STEP));
        dutySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                DUTY_CYCLE_REG_2_SPINNER_MIN,
                DUTY_CYCLE_REG_2_SPINNER_MAX,
                DUTY_CYCLE_REG_2_SPINNER_INIT,
                DUTY_CYCLE_REG_2_SPINNER_STEP));

        SpinnerManager.setupDoubleSpinner(currentSpinner);
        SpinnerManager.setupDoubleSpinner(dutySpinner);

        currentSpinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        dutySpinner.getStyleClass().add(1, "");
    }

    private void addListeners(){


        /** слушаем активацию спиннеров регуляторов */

        /** смена режима по клику на стрелках*/
        currentSpinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(CURRENT, PressureReg2_I_Mode, true));
        dutySpinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(DUTY, PressureReg2_DutyMode, false));

        /** смена режима по клику в текстовом поле спиннера */
        currentSpinner.focusedProperty().addListener(new TwoSpinnerFocusListener(CURRENT, PressureReg2_I_Mode, true));
        dutySpinner.focusedProperty().addListener(new TwoSpinnerFocusListener(DUTY, PressureReg2_DutyMode, false));

        /**изменение цвета рамки спиннера на зелёный при переходе в спиннер другого регулятора*/
        currentSpinner.focusedProperty().addListener(new TwoSpinnerStyleChangeListener(currentSpinner, dutySpinner, CURRENT));
        dutySpinner.focusedProperty().addListener(new TwoSpinnerStyleChangeListener(currentSpinner, dutySpinner, DUTY));

        /** слушаем изменения значений в спиннерах и отправляем уставки спиннеров */
        currentSpinner.valueProperty().addListener(new ParameterChangeListener(CURRENT));
        dutySpinner.valueProperty().addListener(new ParameterChangeListener(DUTY));

        /** слушаем кнопку включения секции регуляторов*/
        pumpHighPressureSectionPwrState.powerButtonProperty().addListener(new HighPressureSectionPwrListener());

        /** слушаем кнопку включения регулятора давления */
        regToggleButton.selectedProperty().addListener(new RegulatorButtonListener());

        /**слушаем изменения в модели данных, полученных из прошивки*/
        highPressureSectionUpdateModel.current_2Property().addListener((observableValue, oldValue, newValue) -> currentSpinner.getValueFactory().setValue((Double) newValue));
        highPressureSectionUpdateModel.duty_2Property().addListener((observableValue, oldValue, newValue) -> dutySpinner.getValueFactory().setValue((Double)newValue));

        /** При переходе в другой GUI нужно отключать регулятор давления и менять режим регулирования на ток.
         * Запрос фокуса на регулирующий спиннер работает только при открытии GUI - добавлен блок else if(){} для включения режима регулирования и визуализации его зелёной рамкой.*/
        gui_typeModel.guiTypeProperty().addListener((observable, oldValue, newValue) -> {

            if (oldValue == CR_Pump) {

                regToggleButton.setSelected(false);
            }

            else if(newValue == CR_Pump){

                currentSpinner.requestFocus();
                rootStackPane.requestFocus();
                currentSpinner.getValueFactory().setValue(0d);
            }
        });

        gauge.valueProperty().bind(highPressureSectionUpdateModel.gauge_2PropertyProperty());
    }

    private void addRegulatorDependentListeners() {

        pumpModel.pumpProperty().addListener((observableValue, oldValue, newValue) -> Optional.ofNullable(newValue).ifPresentOrElse(pump -> {

            boolean isRailAndPump = pump.getPumpPressureControl().isRail_and_Pump();

            bridgeModeSwitch(isRailAndPump);

            if (isRailAndPump) {
                regulationModesModel.regulatorTwoModeProperty().setValue(NO_REGULATION);
                currentSpinner.getStyleClass().set(1, "");
                dutySpinner.getStyleClass().set(1, "");
            }
            else {
                regulationModesModel.regulatorTwoModeProperty().setValue(CURRENT);
                currentSpinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
            }
        }, () -> {
            bridgeModeSwitch(false);
            currentSpinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
            dutySpinner.getStyleClass().set(1, "");
            regulationModesModel.regulatorTwoModeProperty().setValue(CURRENT);
        }));

        pumpTestModel.pumpTestProperty().addListener((observableValue, oldTest, newTest) -> {

            currentSpinner.getValueFactory().setValue(0d);
            Optional.ofNullable(newTest).ifPresentOrElse(test ->

                Optional.ofNullable(test.getRegulatorCurrent()).ifPresent(current -> {

                    boolean isRailAndPump = pumpModel.pumpProperty().get().getPumpPressureControl().isRail_and_Pump();

                    if (!isRailAndPump) {
                        currentSpinner.getValueFactory().setValue(current);
                        regToggleButton.setSelected(true);
                    }
                }), () -> {
                currentSpinner.getValueFactory().setValue(0d);
                regToggleButton.setSelected(false);
            });
        });
    }

    private class TwoSpinnerArrowClickHandler implements EventHandler<MouseEvent> {
        RegActive activeParam;
        ModbusMapUltima mapParam;
        boolean mapParam_ON;

        TwoSpinnerArrowClickHandler(RegActive activeParam,
                                           ModbusMapUltima mapParam,
                                           boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
        }

        @Override
        public void handle(MouseEvent event) {
            regulationModesModel.regulatorTwoModeProperty().setValue(activeParam);
//            if (pumpHighPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()) {
                ultimaModbusWriter.add(mapParam, mapParam_ON);
//            }
        }
    }

    private class TwoSpinnerFocusListener implements ChangeListener<Boolean> {
        RegActive activeParam;
        ModbusMapUltima mapParam;
        boolean mapParam_ON;

        TwoSpinnerFocusListener(RegActive activeParam,
                                       ModbusMapUltima mapParam,
                                       boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (newValue) {
                regulationModesModel.regulatorTwoModeProperty().setValue(activeParam);
//                if(pumpHighPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()){
                    ultimaModbusWriter.add(mapParam, mapParam_ON);
//                }
                if(pumpHighPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()){
                    switch (activeParam){
                        case CURRENT:
                            ultimaModbusWriter.add(PressureReg2_I_Task, currentSpinner.getValue());
                            break;
                        case DUTY:
                            ultimaModbusWriter.add(PressureReg2_DutyTask, dutySpinner.getValue());
                            break;
                    }
                }
            }
        }
    }

    private class ParameterChangeListener implements ChangeListener<Number>{

        RegActive regActive;

        ParameterChangeListener(RegActive regActive) {
            this.regActive = regActive;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            if((pumpHighPressureSectionPwrState.powerButtonProperty().get()
                    && regToggleButton.isSelected()
                    && regActive == regulationModesModel.regulatorTwoModeProperty().get())){

                switch (regulationModesModel.regulatorTwoModeProperty().get()){
                    case CURRENT:
                        ultimaModbusWriter.add(PressureReg2_I_Task, newValue);
                        System.err.println("current2 " + newValue);
                        break;
                    case DUTY:
                        ultimaModbusWriter.add(PressureReg2_DutyTask, newValue);
                        System.err.println("duty2 " + newValue);
                        break;

                }
            }
        }
    }

    private class RegulatorButtonListener implements ChangeListener<Boolean>{

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(pumpHighPressureSectionPwrState.powerButtonProperty().get()){
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
        switch (regulationModesModel.regulatorTwoModeProperty().get()){
            case CURRENT:
                double current = currentSpinner.getValue();
                ultimaModbusWriter.add(PressureReg2_I_Task, current);
                ultimaModbusWriter.add(PressureReg2_ON, true);
                break;
            case DUTY:
                double duty = dutySpinner.getValue();
                ultimaModbusWriter.add(PressureReg2_DutyTask, duty);
                ultimaModbusWriter.add(PressureReg2_ON, true);
                break;
            case NO_REGULATION:
                ultimaModbusWriter.add(PressureReg2_DutyMode, false); //duty mode is active when channel duty mode register is set false
                ultimaModbusWriter.add(Reg1_To_Reg2_Mirror, true);
                ultimaModbusWriter.add(PressureReg2_ON, true);
                break;
            default: ultimaModbusWriter.add(PressureReg2_ON, false);
        }
    }

    private void bridgeModeSwitch(boolean active) {

        ultimaModbusWriter.add(PressureReg2_DutyMode, !active); //duty mode is active when channel duty mode register is set false
        ultimaModbusWriter.add(Reg1_To_Reg2_Mirror, active);
        regToggleButton.setSelected(active);
        rootStackPane.setDisable(active);

        if (active) {
            pumpPressureRegulatorOneModel.switchButtonProperty().bindBidirectional(regToggleButton.selectedProperty());
        }else {
            pumpPressureRegulatorOneModel.switchButtonProperty().unbindBidirectional(regToggleButton.selectedProperty());
        }
    }
}
