package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.states.InjConfigurationState;
import fi.stardex.sisu.states.InstantFlowState;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.*;
import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STREAM;

@Module(value = Device.MODBUS_FLOW)
public class FlowStreamUpdater extends FlowUpdater implements Updater {

    public FlowStreamUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                             FirmwareVersion<FlowVersions> flowFirmwareVersion,
                             InjConfigurationState injConfigurationState, InstantFlowState instantFlowState) {

        super(flowController, injectorSectionController, flowFirmwareVersion, injConfigurationState, instantFlowState);

    }

    private enum Flow {

        DELIVERY_1, DELIVERY_2, DELIVERY_3, DELIVERY_4, BACK_FLOW_1, BACK_FLOW_2, BACK_FLOW_3, BACK_FLOW_4

    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if (isNotInstantFlow() || isNotMeasuring()) {
            setAllLabelsAndFieldsToNull();
            return;
        }

        if(injConfigurationState.get() == InjectorChannel.SINGLE_CHANNEL){
            runOnSingleChannelMode(STREAM);
        }

        else {

            String value;
            double deliveryCoefficient = getDeliveryCoefficient();
            double backFlowCoefficient = getBackFlowCoefficient();

            if ((value = Channel1Level.getLastValue().toString()) != null)
                show(value, deliveryCoefficient, Flow.DELIVERY_1);

            if ((value = Channel2Level.getLastValue().toString()) != null)
                show(value, deliveryCoefficient, Flow.DELIVERY_2);

            if ((value = Channel3Level.getLastValue().toString()) != null)
                show(value, deliveryCoefficient, Flow.DELIVERY_3);

            if ((value = Channel4Level.getLastValue().toString()) != null)
                show(value, deliveryCoefficient, Flow.DELIVERY_4);

            if ((value = Channel5Level.getLastValue().toString()) != null)
                show(value, backFlowCoefficient, Flow.BACK_FLOW_1);

            if ((value = Channel6Level.getLastValue().toString()) != null)
                show(value, backFlowCoefficient, Flow.BACK_FLOW_2);

            if ((value = Channel7Level.getLastValue().toString()) != null)
                show(value, backFlowCoefficient, Flow.BACK_FLOW_3);

            if ((value = Channel8Level.getLastValue().toString()) != null)
                show(value, backFlowCoefficient, Flow.BACK_FLOW_4);

            if ((value = Channel1Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel1Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel2Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel2Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel3Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel3Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel4Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel4Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel5Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel5Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel6Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel6Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel7Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel7Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel8Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = Channel8Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

        }

    }

    private void show(String value, double coefficient, Flow flow) {

        double convertedValue = round(convertDataToDouble(value) * coefficient);

        TextField field = null;
        ToggleButton led = null;

        switch (flow) {
            case DELIVERY_1:
                field = delivery1TextField;
                led = ledBeaker1ToggleButton;
                break;
            case DELIVERY_2:
                field = delivery2TextField;
                led = ledBeaker2ToggleButton;
                break;
            case DELIVERY_3:
                field = delivery3TextField;
                led = ledBeaker3ToggleButton;
                break;
            case DELIVERY_4:
                field = delivery4TextField;
                led = ledBeaker4ToggleButton;
                break;
            case BACK_FLOW_1:
                field = backFlow1TextField;
                led = ledBeaker1ToggleButton;
                break;
            case BACK_FLOW_2:
                field = backFlow2TextField;
                led = ledBeaker2ToggleButton;
                break;
            case BACK_FLOW_3:
                field = backFlow3TextField;
                led = ledBeaker3ToggleButton;
                break;
            case BACK_FLOW_4:
                field = backFlow4TextField;
                led = ledBeaker4ToggleButton;
                break;
        }

        if (led.isSelected())
            flowController.changeFlow(field, String.valueOf(convertedValue));
        else
            field.setText(null);

    }

}
