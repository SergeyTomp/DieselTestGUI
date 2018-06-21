package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.combobox_values.FlowUnits;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

@Module(value = Device.MODBUS_FLOW)
public class FlowStreamUpdater extends FlowUpdater implements Updater{

    public FlowStreamUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                             SettingsController settingsController, FirmwareDataConverter firmwareDataConverter) {

        super(flowController, injectorSectionController, settingsController, firmwareDataConverter);

    }

    private enum Flow {

        DELIVERY_1, DELIVERY_2, DELIVERY_3, DELIVERY_4, BACK_FLOW_1, BACK_FLOW_2, BACK_FLOW_3, BACK_FLOW_4

    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        System.err.println("Stream");

        if ((!checkBoxFlowVisible.isSelected()) && (!injectorSectionPowerSwitch.isSelected()))
            return;

        if (comboInjectorConfig.getSelectionModel().getSelectedItem() == InjectorChannel.SINGLE_CHANNEL)

            runOnSingleChannelMode(FlowFirmwareVersion.FLOW_STREAM);

        else {

            String value;

            if ((value = ModbusMapFlow.Channel1Level.getLastValue().toString()) != null)
                show(value, deliveryFlowComboBox.getSelectionModel().getSelectedItem(), Flow.DELIVERY_1);

            if ((value = ModbusMapFlow.Channel2Level.getLastValue().toString()) != null)
                show(value, deliveryFlowComboBox.getSelectionModel().getSelectedItem(), Flow.DELIVERY_2);

            if ((value = ModbusMapFlow.Channel3Level.getLastValue().toString()) != null)
                show(value, deliveryFlowComboBox.getSelectionModel().getSelectedItem(), Flow.DELIVERY_3);

            if ((value = ModbusMapFlow.Channel4Level.getLastValue().toString()) != null)
                show(value, deliveryFlowComboBox.getSelectionModel().getSelectedItem(), Flow.DELIVERY_4);

            if ((value = ModbusMapFlow.Channel5Level.getLastValue().toString()) != null)
                show(value, backFlowComboBox.getSelectionModel().getSelectedItem(), Flow.BACK_FLOW_1);

            if ((value = ModbusMapFlow.Channel6Level.getLastValue().toString()) != null)
                show(value, backFlowComboBox.getSelectionModel().getSelectedItem(), Flow.BACK_FLOW_2);

            if ((value = ModbusMapFlow.Channel7Level.getLastValue().toString()) != null)
                show(value, backFlowComboBox.getSelectionModel().getSelectedItem(), Flow.BACK_FLOW_3);

            if ((value = ModbusMapFlow.Channel8Level.getLastValue().toString()) != null)
                show(value, backFlowComboBox.getSelectionModel().getSelectedItem(), Flow.BACK_FLOW_4);

            if ((value = ModbusMapFlow.Channel1Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel1Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel2Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel2Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel3Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel3Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel4Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1Delivery4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel4Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2Delivery4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel5Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel5Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow1Label.setText(ledBeaker1ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel6Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel6Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow2Label.setText(ledBeaker2ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel7Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel7Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow3Label.setText(ledBeaker3ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel8Temperature1.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature1BackFlow4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

            if ((value = ModbusMapFlow.Channel8Temperature2.getLastValue().toString()) != null) {
                convertedValue.append(firmwareDataConverter.
                        roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
                temperature2BackFlow4Label.setText(ledBeaker4ToggleButton.isSelected() ? convertedValue.toString() : null);
                convertedValue.setLength(0);
            }

        }

    }

    private void show(String value, String selectedItem, Flow flow) {

        float convertedValueFloat = firmwareDataConverter.
                roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value));

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

        switch (selectedItem) {
            case FlowUnits.MILLILITRE_PER_MINUTE:
                field.setText(led.isSelected() ? String.valueOf(convertedValueFloat): null);
                break;
            case FlowUnits.LITRE_PER_HOUR:
                field.setText(led.isSelected() ? String.valueOf(firmwareDataConverter.
                        roundToOneDecimalPlace(convertedValueFloat * 0.06f)): null);
                break;
        }

    }

}
