package fi.stardex.sisu.ui.controllers.cr;

import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.VisualUtils;
import fi.stardex.sisu.util.enums.RegActive;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
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

    @FXML
    private ToggleButton highPressureStartToggleButton;

    private Lcd pressureLcd;
    private RegActive Reg1paramActive;
    private RegActive Reg2paramActive;
    private RegActive Reg3paramActive;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private VisualUtils visualUtils;
    private I18N i18N;
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

    public void setReg1paramActive(RegActive reg1paramActive) {
        Reg1paramActive = reg1paramActive;
    }

    public void setReg2paramActive(RegActive reg2paramActive) {
        Reg2paramActive = reg2paramActive;
    }

    public void setReg3paramActive(RegActive reg3paramActive) {
        Reg3paramActive = reg3paramActive;
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

    public ToggleButton getPowerButton1() {
        return powerButton1;
    }

    public ToggleButton getPowerButton2() {
        return powerButton2;
    }

    public ToggleButton getPowerButton3() {
        return powerButton3;
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

    public void setVisualUtils(VisualUtils visualUtils) {
        this.visualUtils = visualUtils;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setVisibleRegulator2(boolean visible) {
        visualUtils.setVisible(visible,
                labelReg2, labelAmpReg2, labelCycleReg2, powerButton2, dutyCycleReg2Spinner, currentReg2Spinner);
    }

    public void setVisibleRegulator3(boolean visible) {
        visualUtils.setVisible(visible,
                labelReg3, labelAmpReg3, labelCycleReg3, powerButton3, dutyCycleReg3Spinner, currentReg3Spinner);
    }

    @PostConstruct
    public void init() {

        bindingI18N();

        /** установка активных параметров регулирования по умолчанию */
        Reg1paramActive = PRESSURE;
        Reg2paramActive = CURRENT;
        Reg3paramActive = CURRENT;

        /** активируем спиннеры регуляторов */
        pressReg1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0, 10));
        currentReg1Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 2.5, 0, 0.01));
        dutyCycleReg1Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 0, 0.01));
        currentReg2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 2.5, 0, 0.01));
        dutyCycleReg2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 0, 0.01));
        currentReg3Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 2.5, 0, 0.01));
        dutyCycleReg3Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 0, 0.01));

        /** слушаем активацию спиннеров регуляторов */
        // смена режима по клику на стрелках
        pressReg1Spinner.setOnMouseClicked(event -> {
            Reg1paramActive = PRESSURE;
            if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected()){
                ultimaModbusWriter.add(PressureReg1_PressMode, true);   //вкл.режим давления
                ultimaModbusWriter.add(PressureReg1_I_Mode, false);     //откл.режим тока
            }
        });
        currentReg1Spinner.setOnMouseClicked((MouseEvent event) -> {
            Reg1paramActive = CURRENT;
            if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected()) {
                ultimaModbusWriter.add(PressureReg1_PressMode, false);  //откл.режим давления
                ultimaModbusWriter.add(PressureReg1_I_Mode, true);      //вкл.режим тока
            }
        });
        dutyCycleReg1Spinner.setOnMouseClicked((event -> {
            Reg1paramActive = DUTY;
            if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected()) {
                ultimaModbusWriter.add(PressureReg1_PressMode, false);  //откл.режим давления
                ultimaModbusWriter.add(PressureReg1_I_Mode, false);     //откл.режим тока, вкл.режим скважности

            }
        }));
        currentReg2Spinner.setOnMouseClicked((event -> {
            Reg2paramActive = CURRENT;
            if(highPressureStartToggleButton.isSelected() && powerButton2.isSelected()) {
                ultimaModbusWriter.add(PressureReg2_I_Mode, true);
            }
        }));
        dutyCycleReg2Spinner.setOnMouseClicked((event -> {
            Reg2paramActive = DUTY;
            if(highPressureStartToggleButton.isSelected() && powerButton2.isSelected()) {
                ultimaModbusWriter.add(PressureReg2_DutyMode, false);
            }
        }));
        currentReg3Spinner.setOnMouseClicked((event -> {
            Reg3paramActive = CURRENT;
            if(highPressureStartToggleButton.isSelected() && powerButton3.isSelected()) {
                ultimaModbusWriter.add(PressureReg3_I_Mode, true);
            }
        }));
        dutyCycleReg3Spinner.setOnMouseClicked((event -> {
            Reg3paramActive = DUTY;
            if(highPressureStartToggleButton.isSelected() && powerButton3.isSelected()) {
                ultimaModbusWriter.add(PressureReg3_DutyMode, false);
            }
        }));

        /** смена режима по клику в текстовом поле спиннера */
        // спиннеры регулятора 1
        pressReg1Spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                Reg1paramActive = PRESSURE;
                if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected()){
                    ultimaModbusWriter.add(PressureReg1_PressMode, true);   //вкл.режим давления
                    ultimaModbusWriter.add(PressureReg1_I_Mode, false);     //откл.режим тока
                }
            }});
        currentReg1Spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){Reg1paramActive = CURRENT;
                if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected()) {
                    ultimaModbusWriter.add(PressureReg1_PressMode, false);  //откл.режим давления
                    ultimaModbusWriter.add(PressureReg1_I_Mode, true);      //вкл.режим тока

                }
            }});
        dutyCycleReg1Spinner.focusedProperty().addListener((observable, oldValue, newValue)-> {
            if (newValue){ Reg1paramActive = DUTY;
                if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected()) {
                    ultimaModbusWriter.add(PressureReg1_PressMode, false);  //откл.режим давления
                    ultimaModbusWriter.add(PressureReg1_I_Mode, false);     //откл.режим тока, вкл.режим скважности
                }
            }});

        // спиннеры регулятора 2
        currentReg2Spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){ Reg2paramActive = CURRENT;
                if(highPressureStartToggleButton.isSelected() && powerButton2.isSelected()) {
                    ultimaModbusWriter.add(PressureReg2_I_Mode, true);
                }
            }});
        dutyCycleReg2Spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){Reg2paramActive = DUTY;
                if(highPressureStartToggleButton.isSelected() && powerButton2.isSelected()) {
                    ultimaModbusWriter.add(PressureReg2_DutyMode, false);
                }
            }});

        // спиннеры регулятора 3
        currentReg3Spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){Reg3paramActive = CURRENT;
                if(highPressureStartToggleButton.isSelected() && powerButton3.isSelected()) {
                    ultimaModbusWriter.add(PressureReg3_I_Mode, true);
                }
            }});
        dutyCycleReg3Spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){Reg3paramActive = DUTY;
                if(highPressureStartToggleButton.isSelected() && powerButton3.isSelected()) {
                    ultimaModbusWriter.add(PressureReg3_DutyMode, false);
                }
            }});

        /** отправляем уставки спиннеров */
        // регулятор 1
        pressReg1Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected() && Reg1paramActive == PRESSURE){
                ultimaModbusWriter.add(PressureReg1_PressTask, newValue);
            }
        });
        currentReg1Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected() && Reg1paramActive == CURRENT) {
                ultimaModbusWriter.add(PressureReg1_I_Task, newValue);
            }
        });
        dutyCycleReg1Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton1.isSelected() && Reg1paramActive == DUTY) {
                ultimaModbusWriter.add(PressureReg1_DutyTask, newValue);
            }
        });

        // регулятор 2
        currentReg2Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton2.isSelected() && Reg2paramActive == CURRENT) {
                ultimaModbusWriter.add(PressureReg2_I_Task, newValue);
            }
        });
        dutyCycleReg2Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton2.isSelected() && Reg2paramActive == DUTY) {
                ultimaModbusWriter.add(PressureReg2_DutyTask, newValue);
            }
        });

        // регулятор 3
        currentReg3Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton3.isSelected() && Reg3paramActive == CURRENT) {
                ultimaModbusWriter.add(PressureReg3_I_Task, newValue);
            }
        });
        dutyCycleReg3Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(highPressureStartToggleButton.isSelected() && powerButton3.isSelected() && Reg3paramActive == DUTY) {
                ultimaModbusWriter.add(PressureReg3_DutyTask, newValue);
            }
        });

        /** слушаем кнопку включения секции */
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
        powerButton1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && highPressureStartToggleButton.selectedProperty().getValue()) {
                regulator1_ON();
            }else ultimaModbusWriter.add(PressureReg1_ON, false);

        });
        powerButton2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && highPressureStartToggleButton.selectedProperty().getValue()) {
                regulator2_ON();
            }else ultimaModbusWriter.add(PressureReg2_ON, false);
        });
        powerButton3.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && highPressureStartToggleButton.selectedProperty().getValue()) {
                regulator3_ON();
            }else ultimaModbusWriter.add(PressureReg3_ON, false);
        });

        /** рисуем LCD */
        pressureLcd = LcdBuilder.create()
                .prefWidth(130)
                .prefHeight(60)
                .styleClass(Lcd.STYLE_CLASS_BLACK_YELLOW)
                .backgroundVisible(true)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .title("")
                .titleVisible(false)
                .batteryVisible(false)
                .signalVisible(false)
                .alarmVisible(false)
                .unit(" bar")
                .unitVisible(true)
                .decimals(0)
                .minMeasuredValueDecimals(4)
                .minMeasuredValueVisible(false)
                .maxMeasuredValueDecimals(4)
                .maxMeasuredValueVisible(false)
                .formerValueVisible(false)
                .threshold(0)
                .thresholdVisible(false)
                .trendVisible(false)
                .trend(Lcd.Trend.RISING)
                .numberSystemVisible(false)
                .lowerRightTextVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
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

    /** вариант включения регуляторов через унифицированные методы*/
    private void regulator_ON(ModbusMapUltima regulNumReg,
                              RegActive mode,
                              Spinner<Integer> pressVal,
                              Spinner<Double> currentVal,
                              Spinner<Double> dutyVal,
                              ModbusMapUltima pressTaskReg,
                              ModbusMapUltima currTaskReg,
                              ModbusMapUltima dutyTaskReg){
        ultimaModbusWriter.add(regulNumReg, true);
        switch (mode){
            case PRESSURE:
                double press = pressVal.getValue();
                ultimaModbusWriter.add(pressTaskReg, press);
                break;
            case CURRENT:
                double current = currentVal.getValue();
                ultimaModbusWriter.add(currTaskReg, current);
                break;
            case DUTY:
                double duty = dutyVal.getValue();
                ultimaModbusWriter.add(dutyTaskReg, duty);
                break;
                default:ultimaModbusWriter.add(regulNumReg, false);
        }
    }

    /** вариант включения регуляторов через унифицированные методы*/
    private void regulator_ON(ModbusMapUltima regulNumReg,
                              RegActive mode,
                              Spinner<Double> currentVal,
                              Spinner<Double> dutyVal,
                              ModbusMapUltima currTaskReg,
                              ModbusMapUltima dutyTaskReg){
        ultimaModbusWriter.add(regulNumReg, true);
        switch (mode){
            case CURRENT:
                double current = currentVal.getValue();
                ultimaModbusWriter.add(currTaskReg, current);
                break;
            case DUTY:
                double duty = dutyVal.getValue();
                ultimaModbusWriter.add(dutyTaskReg, duty);
                break;
            default:ultimaModbusWriter.add(regulNumReg, false);
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

}
