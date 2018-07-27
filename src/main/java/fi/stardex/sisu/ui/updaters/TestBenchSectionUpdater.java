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

    public TestBenchSectionUpdater(TestBenchSectionController testBenchSectionController) {
        targetRPMSpinner = testBenchSectionController.getTargetRPMSpinner();
        leftDirectionRotationToggleButton = testBenchSectionController.getLeftDirectionRotationToggleButton();
        rightDirectionRotationToggleButton = testBenchSectionController.getRightDirectionRotationToggleButton();
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if (ModbusMapStand.TargetRPM.isSyncWriteRead())
            ModbusMapStand.TargetRPM.setSyncWriteRead(false);
        else
            targetRPMSpinner.getValueFactory().setValue(Integer.valueOf(ModbusMapStand.TargetRPM.getLastValue().toString()));

        if (ModbusMapStand.RotationDirection.isSyncWriteRead())
            ModbusMapStand.RotationDirection.setSyncWriteRead(false);
        else {
            boolean lastValue = (Boolean) ModbusMapStand.RotationDirection.getLastValue();
            if (lastValue)
                rightDirectionRotationToggleButton.selectedProperty().setValue(true);
            else
                leftDirectionRotationToggleButton.selectedProperty().setValue(true);
        }

    }
}
