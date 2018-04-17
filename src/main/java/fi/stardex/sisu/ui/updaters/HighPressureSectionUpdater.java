package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;

@Module(Device.ULTIMA)
public class HighPressureSectionUpdater implements Updater {


    private HighPressureSectionController highPressureSectionController;

    public HighPressureSectionUpdater(HighPressureSectionController highPressureSectionController) {
        this.highPressureSectionController = highPressureSectionController;
    }

    @Override
    public void update() {
        highPressureSectionController.getCurrentRPMLabel().setText(ModbusMapUltima.PositionRheostatOne.getLastValue().toString());
    }
}
