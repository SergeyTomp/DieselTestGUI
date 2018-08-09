package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.util.converters.DataConverter;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

@Module(value = Device.ULTIMA)
public class InjectorSectionUpdater implements Updater {

    private VoltageController voltageController;

    private DataConverter firmwareDataConverter;

    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

    public InjectorSectionUpdater(VoltageController voltageController, DataConverter dataConverter) {
        this.voltageController = voltageController;
        this.firmwareDataConverter = dataConverter;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        String value;
        float convertedValue;

        if ((value = WidthBoardOne.getLastValue().toString()) != null)
            voltageController.getWidth().setText(value);
        if ((value = Boost_U.getLastValue().toString()) != null)
            voltageController.getVoltage().setText(value);
        if ((value = FirstWBoardOne.getLastValue().toString()) != null)
            voltageController.getFirstWidth().setText(value);
        if ((value = FirstIBoardOne.getLastValue().toString()) != null) {
            convertedValue = firmwareDataConverter.round(firmwareDataConverter.convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
            voltageController.getFirstCurrent().setText(Float.toString(convertedValue));
        }
        if ((value = SecondIBoardOne.getLastValue().toString()) != null) {
            convertedValue = firmwareDataConverter.round(firmwareDataConverter.convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
            voltageController.getSecondCurrent().setText(Float.toString(convertedValue));
        }
        if ((value = BoostIBoardOne.getLastValue().toString()) != null) {
            convertedValue = firmwareDataConverter.round(firmwareDataConverter.convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
            voltageController.getBoostI().setText(Float.toString(convertedValue));
        }
        if ((value = Battery_U.getLastValue().toString()) != null)
            voltageController.getBatteryU().setText(value);
        if ((value = Negative_U.getLastValue().toString()) != null)
            voltageController.getNegativeU().setText(value);

    }

}
