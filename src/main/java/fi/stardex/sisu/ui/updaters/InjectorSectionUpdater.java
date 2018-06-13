package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;

@Module(value = Device.ULTIMA)
public class InjectorSectionUpdater implements Updater {

    private VoltageController voltageController;

    private FirmwareDataConverter firmwareDataConverter;

    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

    public InjectorSectionUpdater(VoltageController voltageController, FirmwareDataConverter firmwareDataConverter) {
        this.voltageController = voltageController;
        this.firmwareDataConverter = firmwareDataConverter;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {
        String value;
        if ((value = ModbusMapUltima.WidthBoardOne.getLastValue().toString()) != null)
            voltageController.getWidth().setText(value);
        if ((value = ModbusMapUltima.Boost_U.getLastValue().toString()) != null)
            voltageController.getVoltage().setText(value);
        if ((value = ModbusMapUltima.FirstWBoardOne.getLastValue().toString()) != null)
            voltageController.getFirstWidth().setText(value);
        if ((value = ModbusMapUltima.FirstIBoardOne.getLastValue().toString()) != null) {
            Float convertedValue = roundToOneDecimalPlace(Float.parseFloat(value) / ONE_AMPERE_MULTIPLY);
            voltageController.getFirstCurrent().setText(convertedValue.toString());
        }
        if ((value = ModbusMapUltima.SecondIBoardOne.getLastValue().toString()) != null) {
            Float convertedValue = roundToOneDecimalPlace(Float.parseFloat(value) / ONE_AMPERE_MULTIPLY);
            voltageController.getSecondCurrent().setText(convertedValue.toString());
        }
        if ((value = ModbusMapUltima.BoostIBoardOne.getLastValue().toString()) != null) {
            Float convertedValue = roundToOneDecimalPlace(Float.parseFloat(value) / ONE_AMPERE_MULTIPLY);
            voltageController.getBoostI().setText(convertedValue.toString());
        }
        if ((value = ModbusMapUltima.Battery_U.getLastValue().toString()) != null)
            voltageController.getBatteryU().setText(value);
        if ((value = ModbusMapUltima.Negative_U1.getLastValue().toString()) != null)
            voltageController.getNegativeU1().setText(value);
        if ((value = ModbusMapUltima.Negative_U2.getLastValue().toString()) != null)
            voltageController.getNegativeU2().setText(value);
    }

    private float roundToOneDecimalPlace(float value) {
        return (float) Math.round(value * 10) / 10;
    }
}
