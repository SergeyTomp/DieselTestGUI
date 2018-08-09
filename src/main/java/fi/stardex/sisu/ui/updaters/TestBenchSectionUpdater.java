package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;

@Module(value = Device.MODBUS_STAND)
public class TestBenchSectionUpdater implements Updater {

    private Spinner<Integer> targetRPMSpinner;

    private ToggleButton leftDirectionRotationToggleButton;

    private ToggleButton rightDirectionRotationToggleButton;

    private ToggleButton testBenchStartToggleButton;

    private ToggleButton pumpControlToggleButton;

    private TestBenchSectionController testBenchSectionController;

    public TestBenchSectionUpdater(TestBenchSectionController testBenchSectionController) {
        this.testBenchSectionController = testBenchSectionController;

        pumpControlToggleButton = testBenchSectionController.getPumpControlToggleButton();
        targetRPMSpinner = testBenchSectionController.getTargetRPMSpinner();
        leftDirectionRotationToggleButton = testBenchSectionController.getLeftDirectionRotationToggleButton();
        rightDirectionRotationToggleButton = testBenchSectionController.getRightDirectionRotationToggleButton();
        testBenchStartToggleButton = testBenchSectionController.getTestBenchStartToggleButton();
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        Object targetRPMLastValue = ModbusMapStand.TargetRPM.getLastValue();

        if (ModbusMapStand.TargetRPM.isSyncWriteRead())
            ModbusMapStand.TargetRPM.setSyncWriteRead(false);
        else if (targetRPMLastValue != null) {
            targetRPMSpinner.getValueFactory().setValue(Integer.valueOf(targetRPMLastValue.toString()));
        }

        Object rotationDirectionLastValue = ModbusMapStand.RotationDirection.getLastValue();

        if (ModbusMapStand.RotationDirection.isSyncWriteRead())
            ModbusMapStand.RotationDirection.setSyncWriteRead(false);
        else if (rotationDirectionLastValue != null) {
            boolean lastValue = (Boolean) rotationDirectionLastValue;
            if (lastValue)
                rightDirectionRotationToggleButton.selectedProperty().setValue(true);
            else
                leftDirectionRotationToggleButton.selectedProperty().setValue(true);
        }

        Object rotationLastValue = ModbusMapStand.Rotation.getLastValue();

        if (ModbusMapStand.Rotation.isSyncWriteRead())
            ModbusMapStand.Rotation.setSyncWriteRead(false);
        else if (rotationLastValue != null)
            testBenchStartToggleButton.selectedProperty().setValue((Boolean) rotationLastValue);


        Object pumpTurnOnLastValue = ModbusMapStand.PumpTurnOn.getLastValue();
        Object pumpAutoModeLastValue = ModbusMapStand.PumpAutoMode.getLastValue();

        if (pumpAutoModeLastValue != null) {
            if ((Boolean)pumpAutoModeLastValue) {
                testBenchSectionController.setPumpState(testBenchStartToggleButton.isSelected() ?
                        TestBenchSectionController.StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
            } else {
                if (pumpTurnOnLastValue != null) {
                    if ((Boolean) pumpTurnOnLastValue)
                        testBenchSectionController.setPumpState(TestBenchSectionController.StatePump.ON);
                    else
                        testBenchSectionController.setPumpState(TestBenchSectionController.StatePump.OFF);
                }
            }
            TestBenchSectionController.StatePump currentStatePump = testBenchSectionController.getPumpState();

            pumpControlToggleButton.getStyleClass().set(1, currentStatePump.getStyle());
            pumpControlToggleButton.setText(currentStatePump.getText());
        }

    }
}
