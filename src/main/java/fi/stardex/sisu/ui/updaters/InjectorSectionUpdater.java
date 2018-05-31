package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;

@Module(value = Device.ULTIMA)
public class InjectorSectionUpdater implements Updater {

    private InjectorSectionController injectorSectionController;

    private VoltageController voltageController;

    public InjectorSectionUpdater(InjectorSectionController injectorSectionController, VoltageController voltageController) {
        this.injectorSectionController = injectorSectionController;
        this.voltageController = voltageController;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {
        if(ModbusMapUltima.WidthBoardOne.getLastValue() != null)
            voltageController.getWidth().setText(ModbusMapUltima.WidthBoardOne.getLastValue().toString());
    }
}
