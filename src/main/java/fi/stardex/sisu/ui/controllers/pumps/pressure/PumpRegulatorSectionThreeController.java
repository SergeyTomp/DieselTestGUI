package fi.stardex.sisu.ui.controllers.pumps.pressure;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.model.RegulationModesModel;
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
import static fi.stardex.sisu.util.enums.RegActive.*;

public class PumpRegulatorSectionThreeController {

    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private Label labelRegNumber;
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
    private final String GREEN_STYLE_CLASS = "regulator-spinner-selected";

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
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){
        gauge = GaugeCreator.createPumpGauge(CURRENT_REG_3_SPINNER_MAX);
        gauge.setMaxValue(CURRENT_REG_3_SPINNER_MAX);
        gaugeVBox.getChildren().add(1, gauge);
        bindingI18N();
        setupSpinners();
        addListeners();
        addRegulatorDependentListeners();
    }

    private void bindingI18N() {

        currentLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        dutyLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        labelRegNumber.textProperty().bind(i18N.createStringBinding("highPressure.label.reg3.name"));

    }

    private void setupSpinners(){
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                CURRENT_REG_3_SPINNER_MIN,
                CURRENT_REG_3_SPINNER_MAX,
                CURRENT_REG_3_SPINNER_INIT,
                CURRENT_REG_3_SPINNER_STEP));
        dutySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                DUTY_CYCLE_REG_3_SPINNER_MIN,
                DUTY_CYCLE_REG_3_SPINNER_MAX,
                DUTY_CYCLE_REG_3_SPINNER_INIT,
                DUTY_CYCLE_REG_3_SPINNER_STEP));

        SpinnerManager.setupDoubleSpinner(currentSpinner);
        SpinnerManager.setupDoubleSpinner(dutySpinner);

        currentSpinner.getStyleClass().add(1, GREEN_STYLE_CLASS);
        dutySpinner.getStyleClass().add(1, "");
    }

    private void addListeners(){


        /** слушаем активацию спиннеров регуляторов */

        /** смена режима по клику на стрелках*/
        currentSpinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(CURRENT, PressureReg3_I_Mode, true));
        dutySpinner.setOnMouseClicked(new TwoSpinnerArrowClickHandler(DUTY, PressureReg3_DutyMode, false));

        /** смена режима по клику в текстовом поле спиннера */
        currentSpinner.focusedProperty().addListener(new TwoSpinnerFocusListener(CURRENT, PressureReg3_I_Mode, true));
        dutySpinner.focusedProperty().addListener(new TwoSpinnerFocusListener(DUTY, PressureReg3_DutyMode, false));

        /**изменение цвета рамки спиннера на зелёный при переходе в спиннер другого рекгулятора*/
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
        highPressureSectionUpdateModel.current_3Property().addListener((observableValue, oldValue, newValue) -> currentSpinner.getValueFactory().setValue((Double) newValue));
        highPressureSectionUpdateModel.duty_3Property().addListener((observableValue, oldValue, newValue) -> dutySpinner.getValueFactory().setValue((Double)newValue));

        gauge.valueProperty().bind(highPressureSectionUpdateModel.gauge_3PropertyProperty());
    }

    private void addRegulatorDependentListeners(){

        pumpModel.pumpProperty().addListener((observableValue, oldValue, newValue) -> Optional.ofNullable(newValue).ifPresentOrElse(pump -> {

            boolean isRailAndPump = pump.getPumpPressureControl().isRail_and_Pump();

            currentSpinner.getStyleClass().set(1, "");
            dutySpinner.getStyleClass().set(1, "");
            regulationModesModel.regulatorThreeModeProperty().setValue(NO_REGULATION);

            if(isRailAndPump) {

                regulationModesModel.regulatorThreeModeProperty().setValue(CURRENT);
                currentSpinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
            }
        }, () -> {
            regToggleButton.setSelected(false);
            currentSpinner.getStyleClass().set(1, GREEN_STYLE_CLASS);
            dutySpinner.getStyleClass().set(1, "");
            regulationModesModel.regulatorTwoModeProperty().setValue(CURRENT);
        }));

        pumpTestModel.pumpTestProperty().addListener((observableValue, oldTest, newTest) -> {

            currentSpinner.getValueFactory().setValue(0d);
            regToggleButton.setSelected(false);

            Optional.ofNullable(newTest).ifPresentOrElse(test ->

                Optional.ofNullable(test.getRegulatorCurrent()).ifPresent(current -> {

                    boolean isRailAndPump = pumpModel.pumpProperty().get().getPumpPressureControl().isRail_and_Pump();

                    if (isRailAndPump) {
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

        public TwoSpinnerArrowClickHandler(RegActive activeParam,
                                           ModbusMapUltima mapParam,
                                           boolean mapParam_ON) {
            this.activeParam = activeParam;
            this.mapParam = mapParam;
            this.mapParam_ON = mapParam_ON;
        }

        @Override
        public void handle(MouseEvent event) {
            regulationModesModel.regulatorThreeModeProperty().setValue(activeParam);
            if (pumpHighPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()) {
                ultimaModbusWriter.add(mapParam, mapParam_ON);
            }
        }
    }

    private class TwoSpinnerFocusListener implements ChangeListener<Boolean> {
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
                regulationModesModel.regulatorThreeModeProperty().setValue(activeParam);
                if(pumpHighPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()){
                    ultimaModbusWriter.add(mapParam, mapParam_ON);
                }
                if(pumpHighPressureSectionPwrState.powerButtonProperty().get() && regToggleButton.isSelected()){
                    switch (activeParam){
                        case CURRENT:
                            ultimaModbusWriter.add(PressureReg3_I_Task, currentSpinner.getValue());
                            break;
                        case DUTY:
                            ultimaModbusWriter.add(PressureReg3_DutyTask, dutySpinner.getValue());
                            break;
                    }
                }
            }
        }
    }

    private class ParameterChangeListener implements ChangeListener<Number>{

        RegActive regActive;

        public ParameterChangeListener(RegActive regActive) {
            this.regActive = regActive;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            if((pumpHighPressureSectionPwrState.powerButtonProperty().get()
                    && regToggleButton.isSelected()
                    && regActive == regulationModesModel.regulatorThreeModeProperty().get())){

                switch (regulationModesModel.regulatorThreeModeProperty().get()){
                    case CURRENT:
                        ultimaModbusWriter.add(PressureReg3_I_Task, newValue);
                        System.err.println("current2 " + newValue);
                        break;
                    case DUTY:
                        ultimaModbusWriter.add(PressureReg3_DutyTask, newValue);
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
                    ultimaModbusWriter.add(PressureReg3_ON, false);
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
                ultimaModbusWriter.add(PressureReg3_ON, false);
            }
        }
    }

    private void regulator_ON (){
        ultimaModbusWriter.add(PressureReg3_ON, true);
        switch (regulationModesModel.regulatorThreeModeProperty().get()){
            case CURRENT:
                double current = currentSpinner.getValue();
                ultimaModbusWriter.add(PressureReg3_I_Task, current);
                break;
            case DUTY:
                double duty = dutySpinner.getValue();
                ultimaModbusWriter.add(PressureReg3_DutyTask, duty);
                break;
            default:ultimaModbusWriter.add(PressureReg3_ON, false);
        }
    }
}
