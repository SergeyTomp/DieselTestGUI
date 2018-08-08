package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;

@Module(value = Device.MODBUS_STAND)
public class TestBenchSectionUpdater implements Updater {

    private Spinner<Integer> targetRPMSpinner;

    private ToggleButton leftDirectionRotationToggleButton;

    private ToggleButton rightDirectionRotationToggleButton;

    private ToggleButton testBenchStartToggleButton;

    private ToggleButton pumpControlToggleButton;

    private ToggleButton fanControlToggleButton;

    private TestBenchSectionController testBenchSectionController;

    public TestBenchSectionUpdater(TestBenchSectionController testBenchSectionController) {
        this.testBenchSectionController = testBenchSectionController;

        pumpControlToggleButton = testBenchSectionController.getPumpControlToggleButton();
        targetRPMSpinner = testBenchSectionController.getTargetRPMSpinner();
        leftDirectionRotationToggleButton = testBenchSectionController.getLeftDirectionRotationToggleButton();
        rightDirectionRotationToggleButton = testBenchSectionController.getRightDirectionRotationToggleButton();
        testBenchStartToggleButton = testBenchSectionController.getTestBenchStartToggleButton();
        fanControlToggleButton = testBenchSectionController.getFanControlToggleButton();
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        Object targetRPMLastValue = TargetRPM.getLastValue();

        if (TargetRPM.isSyncWriteRead())
            TargetRPM.setSyncWriteRead(false);
        else if (targetRPMLastValue != null) {
            targetRPMSpinner.getValueFactory().setValue(Integer.valueOf(targetRPMLastValue.toString()));
        }

        Object rotationDirectionLastValue = RotationDirection.getLastValue();

        if (RotationDirection.isSyncWriteRead())
            RotationDirection.setSyncWriteRead(false);
        else if (rotationDirectionLastValue != null) {
            boolean lastValue = (Boolean) rotationDirectionLastValue;
            if (lastValue)
                rightDirectionRotationToggleButton.selectedProperty().setValue(true);
            else
                leftDirectionRotationToggleButton.selectedProperty().setValue(true);
        }

        Object rotationLastValue = Rotation.getLastValue();

        if (Rotation.isSyncWriteRead())
            Rotation.setSyncWriteRead(false);
        else if (rotationLastValue != null)
            testBenchStartToggleButton.selectedProperty().setValue((Boolean) rotationLastValue);


        Object pumpTurnOnLastValue = PumpTurnOn.getLastValue();
        Object pumpAutoModeLastValue = PumpAutoMode.getLastValue();

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

        Object fanTurnOnLastValue = FanTurnOn.getLastValue();

        if (FanTurnOn.isSyncWriteRead())
            FanTurnOn.setSyncWriteRead(false);
        else if (fanTurnOnLastValue != null)
            fanControlToggleButton.selectedProperty().setValue((Boolean) fanTurnOnLastValue);
    }
}
