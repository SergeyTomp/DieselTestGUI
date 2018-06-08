package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.modbusmaps.ModbusMap;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;

@Module(value = Device.ULTIMA)
public class InjectorSectionUpdater implements Updater {

    private VoltageController voltageController;

    public InjectorSectionUpdater(VoltageController voltageController) {
        this.voltageController = voltageController;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {
        String value;
        if((value = ModbusMapUltima.WidthBoardOne.getLastValue().toString()) != null)
            voltageController.getWidth().setText(value);
        if ((value = ModbusMapUltima.Boost_U.getLastValue().toString()) != null)
            voltageController.getVoltage().setText(value);
        if ((ModbusMapUltima.FirstWBoardOne.getLastValue().toString()) != null)
            voltageController.getFirstWidth().setText(value);
        if((value = ModbusMapUltima.FirstIBoardOne.getLastValue().toString()) != null)
            voltageController.getFirstCurrent().setText(value);
        if ((value = ModbusMapUltima.SecondIBoardOne.getLastValue().toString()) != null)
            voltageController.getSecondCurrent().setText(value);
        if ((value = ModbusMapUltima.BoostIBoardOne.getLastValue().toString()) != null)
            voltageController.getBoostI().setText(value);
        if ((value = ModbusMapUltima.Battery_U.getLastValue().toString()) != null)
            voltageController.getBatteryU().setText(value);
        if ((value = ModbusMapUltima.Negative_U1.getLastValue().toString()) != null)
            voltageController.getNegativeU1().setText(value);
        if ((value = ModbusMapUltima.Negative_U2.getLastValue().toString()) != null)
            voltageController.getNegativeU2().setText(value);
    }
}
