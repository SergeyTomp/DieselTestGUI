package fi.stardex.sisu.ui.controllers.cr;

import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;

public class TestBenchSectionController {

    private Logger logger = LoggerFactory.getLogger(TestBenchSectionController.class);

    @FXML private StackPane lcdStackPane;

    @FXML private Spinner<Integer> targetRPMSpinner;

    @FXML private ToggleButton leftDirectionRotationToggleButton;

    @FXML private ToggleGroup rotationDirectionToggleGroup;

    @FXML private ToggleButton rightDirectionRotationToggleButton;

    @FXML private ToggleButton testBenchStartToggleButton;

    @FXML private ToggleButton pumpControlToggleButton;

    @FXML private ToggleButton fanControlToggleButton;

    @FXML private ProgressBar tempProgressBar1;

    @FXML private ProgressBar tempProgressBar2;

    @FXML private ProgressBar pressProgressBar1;

    @FXML private ProgressBar tankOil;

    @FXML private Text tankOilText;

    @FXML private Text tempText1;

    @FXML private Text pressText1;

    @FXML private Text tempText2;

    private static final String PUMP_BUTTON_ON = "pump-button-on";

    private static final String PUMP_BUTTON_OFF = "pump-button-off";

    private StatePump pumpState;

    private ModbusRegisterProcessor standModbusWriter;

    private Lcd currentRPMLcd;

    public Spinner<Integer> getTargetRPMSpinner() {
        return targetRPMSpinner;
    }

    public ToggleButton getLeftDirectionRotationToggleButton() {
        return leftDirectionRotationToggleButton;
    }

    public ToggleButton getRightDirectionRotationToggleButton() {
        return rightDirectionRotationToggleButton;
    }

    public ToggleButton getPumpControlToggleButton() {
        return pumpControlToggleButton;
    }

    public ToggleButton getTestBenchStartToggleButton() {
        return testBenchStartToggleButton;
    }

    public ToggleButton getFanControlToggleButton() {
        return fanControlToggleButton;
    }

    public Lcd getCurrentRPMLcd() {
        return currentRPMLcd;
    }

    public ProgressBar getTempProgressBar1() {
        return tempProgressBar1;
    }

    public ProgressBar getTempProgressBar2() {
        return tempProgressBar2;
    }

    public ProgressBar getPressProgressBar1() {
        return pressProgressBar1;
    }

    public Text getTempText1() {
        return tempText1;
    }

    public Text getPressText1() {
        return pressText1;
    }

    public Text getTempText2() {
        return tempText2;
    }

    public ProgressBar getTankOil() {
        return tankOil;
    }

    public Text getTankOilText() {
        return tankOilText;
    }

    public void setStandModbusWriter(ModbusRegisterProcessor standModbusWriter) {
        this.standModbusWriter = standModbusWriter;
    }

    public boolean isOn() {
        return pumpState == StatePump.ON;
    }

    private boolean isAuto() {
        return pumpState == StatePump.AUTO_ON || pumpState == StatePump.AUTO_OFF;
    }

    public StatePump getPumpState() {
        return pumpState;
    }

    public void setPumpState(StatePump pumpState) {
        this.pumpState = pumpState;
    }

    public enum StatePump {

        ON("PUMP\n ON", true, PUMP_BUTTON_ON),
        OFF("PUMP\n OFF ", false, PUMP_BUTTON_OFF),
        AUTO_ON("PUMP\nAUTO", true, PUMP_BUTTON_ON),
        AUTO_OFF("PUMP\nAUTO", false, PUMP_BUTTON_OFF);

        private String text;
        private Boolean isActive;
        private String style;

        StatePump(String text, Boolean isActive, String style) {
            this.text = text;
            this.isActive = isActive;
            this.style = style;
        }

        public String getText() {
            return text;
        }

        public String getStyle() {
            return style;
        }

        public static boolean isAuto(StatePump statePump) {
            return statePump == AUTO_ON || statePump == AUTO_OFF;
        }

    }

    @PostConstruct
    private void init() {

        setupLCD();

        setupRotationDirectionToggleButton();

        setupTargetRPMSpinner();

        setupTestBenchStartToggleButton();

        setupPumpControlToggleButton();

        setupFanControlToggleButton();

    }

    private void setupLCD() {

        currentRPMLcd = LcdBuilder.create().prefWidth(130).prefHeight(60).styleClass(Lcd.STYLE_CLASS_BLACK_YELLOW)
                .backgroundVisible(true).foregroundShadowVisible(true).crystalOverlayVisible(true)
                .title("").titleVisible(false).batteryVisible(false).signalVisible(false).alarmVisible(false)
                .unit("rpm").unitVisible(true).decimals(0).minMeasuredValueDecimals(4).minMeasuredValueVisible(false)
                .maxMeasuredValueDecimals(4).maxMeasuredValueVisible(false).formerValueVisible(false).threshold(26)
                .thresholdVisible(false).trendVisible(false).trend(Lcd.Trend.RISING).numberSystemVisible(false)
                .lowerRightTextVisible(true).valueFont(Lcd.LcdFont.DIGITAL_BOLD).animated(false).build();

        currentRPMLcd.setMaxValue(5000.0);

        currentRPMLcd.setValue(0);

        lcdStackPane.getChildren().add(currentRPMLcd);

    }

    private void setupRotationDirectionToggleButton() {

        leftDirectionRotationToggleButton.setUserData(false);

        rightDirectionRotationToggleButton.setUserData(true);

        rotationDirectionToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                standModbusWriter.add(RotationDirection, newValue.getUserData());
        });

    }

    private void setupTargetRPMSpinner() {

        targetRPMSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3000, 0, 50));

        SpinnerManager.setupSpinner(targetRPMSpinner, 0, 0, 3000, new CustomTooltip(), new SpinnerValueObtainer(0));

        targetRPMSpinner.valueProperty().addListener((observable, oldValue, newValue) -> standModbusWriter.add(TargetRPM, newValue));

    }

    private void setupTestBenchStartToggleButton() {

        testBenchStartToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            standModbusWriter.add(Rotation, newValue);
            if (StatePump.isAuto(pumpState))
                setPumpAuto(newValue);
        });

    }

    private void setupPumpControlToggleButton() {

        pumpState = StatePump.OFF;

        pumpControlToggleButton.getStyleClass().add(1, "pump-button");

        pumpControlToggleButton.getStyleClass().add(1, PUMP_BUTTON_ON);

        pumpControlToggleButton.getStyleClass().add(1, PUMP_BUTTON_OFF);

        pumpControlToggleButton.getStyleClass().set(1, PUMP_BUTTON_OFF);

        pumpControlToggleButton.setText(pumpState.getText());

        pumpControlToggleButton.setOnMouseClicked(this::mouseHandler);

    }

    private void setupFanControlToggleButton() {

        fanControlToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> standModbusWriter.add(FanTurnOn, newValue));

    }

    private void mouseHandler(MouseEvent mouseEvent) {

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                logger.warn("Double clicked");
                if (isAuto()) {
                    setPumpState(TestBenchSectionController.StatePump.OFF);
                    standModbusWriter.add(PumpAutoMode, false);
                } else {
                    setPumpState(testBenchStartToggleButton.isSelected() ?
                            TestBenchSectionController.StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
                    standModbusWriter.add(PumpAutoMode, true);
                }
            } else if (mouseEvent.getClickCount() == 1) {
                if (pumpState == TestBenchSectionController.StatePump.OFF)
                    setPumpState(TestBenchSectionController.StatePump.ON);
                else if (pumpState == TestBenchSectionController.StatePump.ON)
                    setPumpState(TestBenchSectionController.StatePump.OFF);
            }
            pumpStateChange();
        }

    }

    private void pumpStateChange() {

        pumpControlToggleButton.getStyleClass().set(1, pumpState.getStyle());

        pumpControlToggleButton.setText(pumpState.getText());

        standModbusWriter.add(PumpTurnOn, pumpState.isActive);

    }

    private void setPumpAuto(boolean deviceStateOn) {

        setPumpState(deviceStateOn ? StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);

        pumpStateChange();

    }

}
