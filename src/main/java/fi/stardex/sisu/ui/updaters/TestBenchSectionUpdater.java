package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import javafx.scene.control.Spinner;

@Module(value = Device.MODBUS_STAND)
public class TestBenchSectionUpdater implements Updater{

    private Spinner<Integer> targetRPMSpinner;

    public TestBenchSectionUpdater(Spinner<Integer> targetRPMSpinner) {
        this.targetRPMSpinner = targetRPMSpinner;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        targetRPMSpinner.getValueFactory().setValue(Integer.valueOf(ModbusMapStand.TargetRPM.getLastValue().toString()));

    }
}
