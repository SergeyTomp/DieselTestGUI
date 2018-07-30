package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class TestBenchSectionController {

    private Logger logger = LoggerFactory.getLogger(TestBenchSectionController.class);

    @FXML
    private ToggleButton leftDirectionRotationToggleButton;

    @FXML
    private ToggleGroup rotationDirectionToggleGroup;

    @FXML
    private ToggleButton rightDirectionRotationToggleButton;

    @FXML
    private ToggleButton testBenchStartToggleButton;

    @FXML
    private Spinner<Integer> targetRPMSpinner;

    @FXML
    private ToggleButton pumpControlToggleButton;

    @FXML
    private ToggleButton buttonFanControl;

    @FXML
    private ProgressBar oilTank;

    private static final String PUMP_BUTTON_ON = "pump-button-on";

    private static final String PUMP_BUTTON_OFF = "pump-button-off";

    private StatePump pumpState;

    private ModbusRegisterProcessor standModbusWriter;

    public Spinner<Integer> getTargetRPMSpinner() {
        return targetRPMSpinner;
    }

    public ToggleButton getLeftDirectionRotationToggleButton() {
        return leftDirectionRotationToggleButton;
    }

    public ToggleButton getRightDirectionRotationToggleButton() {
        return rightDirectionRotationToggleButton;
    }

    public ToggleButton getTestBenchStartToggleButton() {
        return testBenchStartToggleButton;
    }

    public void setStandModbusWriter(ModbusRegisterProcessor standModbusWriter) {
        this.standModbusWriter = standModbusWriter;
    }

    public enum StatePump {

        ON("PUMP\n ON", "1", true, PUMP_BUTTON_ON),
        OFF("PUMP\n OFF ", "0", false, PUMP_BUTTON_OFF),
        AUTO_ON("PUMP\nAUTO", "1", true, PUMP_BUTTON_ON),
        AUTO_OFF("PUMP\nAUTO", "0", false, PUMP_BUTTON_OFF);

        private String text;
        private String code;
        private Boolean isActive;
        private String style;

        StatePump(String text, String code, Boolean isActive, String style) {
            this.text = text;
            this.code = code;
            this.isActive = isActive;
            this.style = style;
        }

        public String getText() {
            return text;
        }

        public String getCode() {
            return code;
        }

        public Boolean getActive() {
            return isActive;
        }

        public String getStyle() {
            return style;
        }

        public static boolean isAuto(StatePump statePump) {
            return statePump == AUTO_ON || statePump == AUTO_OFF;
        }

    }

    public boolean isOn() {
        return pumpState == StatePump.ON;
    }

    public boolean isOff() {
        return pumpState == StatePump.OFF;
    }

    public boolean isAuto() {
        return pumpState == StatePump.AUTO_ON || pumpState == StatePump.AUTO_OFF;
    }

    public StatePump getPumpState() {
        return pumpState;
    }

    public void setPumpState(StatePump pumpState) {
        this.pumpState = pumpState;
    }

    @PostConstruct
    private void init() {

        setupRotationDirectionToggleButton();

        setupTargetRPMSpinner();

        setupTestBenchStartToggleButton();

        setupPumpControlToggleButton();

    }

    private void setupRotationDirectionToggleButton() {

        leftDirectionRotationToggleButton.setUserData(false);
        rightDirectionRotationToggleButton.setUserData(true);

        rotationDirectionToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                standModbusWriter.add(ModbusMapStand.RotationDirection, newValue.getUserData());
        });

    }

    private void setupTargetRPMSpinner() {

        targetRPMSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3000, 0, 50));

        SpinnerManager.setupSpinner(targetRPMSpinner, 0, 0, 3000, new CustomTooltip(), new SpinnerValueObtainer(0));

        targetRPMSpinner.valueProperty().addListener((observable, oldValue, newValue) -> standModbusWriter.add(ModbusMapStand.TargetRPM, newValue));

    }

    private void setupTestBenchStartToggleButton() {

        testBenchStartToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            standModbusWriter.add(ModbusMapStand.Rotation, newValue);
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

    private void mouseHandler(MouseEvent mouseEvent) {

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                logger.warn("Double clicked");
                if (isAuto()) {
                    setPumpState(TestBenchSectionController.StatePump.OFF);
                    standModbusWriter.add(ModbusMapStand.PumpAutoMode, false);
                } else {
                    setPumpState(testBenchStartToggleButton.isSelected() ?
                            TestBenchSectionController.StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
                    standModbusWriter.add(ModbusMapStand.PumpAutoMode, true);
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
        standModbusWriter.add(ModbusMapStand.PumpTurnOn, pumpState.isActive);

    }

    private void setPumpAuto(boolean deviceStateOn) {

        setPumpState(deviceStateOn ? StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
        pumpStateChange();

    }

}
