package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.annotation.PostConstruct;

public class TestBenchSectionController {

    @FXML
    private ToggleButton leftDirectionRotationToggleButton;

    @FXML
    private ToggleGroup rotationDirectionToggleGroup;

    @FXML
    private ToggleButton rightDirectionRotationToggleButton;

    @FXML
    private ToggleButton testBenchStartBtn;

    @FXML
    private Spinner<Integer> targetRPMSpinner;

    @FXML
    private ToggleButton buttonPumpControl;

    @FXML
    private ToggleButton buttonFanControl;

    @FXML
    private ProgressBar oilTank;

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

    public void setStandModbusWriter(ModbusRegisterProcessor standModbusWriter) {
        this.standModbusWriter = standModbusWriter;
    }

    @PostConstruct
    private void init() {

        leftDirectionRotationToggleButton.setUserData(false);
        rightDirectionRotationToggleButton.setUserData(true);

        rotationDirectionToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                standModbusWriter.add(ModbusMapStand.RotationDirection, newValue.getUserData());
        });

        targetRPMSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3000, 0, 50));
        SpinnerManager.setupSpinner(targetRPMSpinner, 0, 0, 3000, new CustomTooltip(), new SpinnerValueObtainer(0));

        targetRPMSpinner.valueProperty().addListener((observable, oldValue, newValue) -> standModbusWriter.add(ModbusMapStand.TargetRPM, newValue));
    }

}
