package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.leds.ActiveLeds;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;

import javax.annotation.PostConstruct;
import java.util.List;

@Module(value = Device.MODBUS_FLOW)
public class FlowUpdater implements Updater {

    private FlowController flowController;

    private FirmwareDataConverter firmwareDataConverter;

    private static final String DEGREES_CELSIUS = " \u2103";

    public FlowUpdater(FlowController flowController, FirmwareDataConverter firmwareDataConverter) {
        this.flowController = flowController;
        this.firmwareDataConverter = firmwareDataConverter;
    }

    @PostConstruct
    private void init() {
        List<LedController> l = ActiveLeds.activeControllers();
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        String value;
        Float convertedValue;

        System.err.println("value: " + ModbusMapFlow.Channel1Temperature1.getLastValue());

        if ((value = ModbusMapFlow.Channel1Temperature1.getLastValue().toString()) != null) {
            convertedValue = firmwareDataConverter.roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value));
            flowController.getTemperature1Delivery1().setText(convertedValue.toString() + DEGREES_CELSIUS);
            flowController.getTemperature1BackFlow1().setText(convertedValue.toString() + DEGREES_CELSIUS);
        }
        if ((value = ModbusMapFlow.Channel1Temperature2.getLastValue().toString()) != null) {
            convertedValue = firmwareDataConverter.roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value));
            flowController.getTemperature2Delivery1().setText(convertedValue.toString() + DEGREES_CELSIUS);
            flowController.getTemperature2BackFlow1().setText(convertedValue.toString() + DEGREES_CELSIUS);
        }

    }
}
