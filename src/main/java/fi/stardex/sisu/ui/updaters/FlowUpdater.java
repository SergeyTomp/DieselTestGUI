package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;

@Module(value = Device.MODBUS_FLOW)
public class FlowUpdater implements Updater {

    @Override
    public void update() {

    }

    @Override
    public void run() {
        System.err.println("Updating Flow");
    }
}
