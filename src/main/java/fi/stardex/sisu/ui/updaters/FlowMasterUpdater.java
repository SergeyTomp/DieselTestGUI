package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import javafx.scene.control.CheckBox;

import javax.annotation.PostConstruct;

@Module(value = Device.MODBUS_FLOW)
public class FlowMasterUpdater extends FlowUpdater implements Updater {

    public FlowMasterUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                             CheckBox checkBoxFlowVisible, FirmwareDataConverter firmwareDataConverter) {

        super(flowController, injectorSectionController, checkBoxFlowVisible, firmwareDataConverter);

    }

    // FIXME: метод вызывается 2 раза для каждого бина
//    @PostConstruct
//    private void init() {
//        initListeners();
//    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        System.err.println("Flow");

        if ((!checkBoxFlowVisible.isSelected()) && (!injectorSectionPowerSwitch.isSelected()))
            return;

        String value;

        if ((value = ModbusMapFlow.Channel1Level.getLastValue().toString()) != null)
            showOnChosenFlowUnit(value, deliveryFlowComboBox.getSelectionModel().getSelectedItem(), Flow.DELIVERY);

        if ((value = ModbusMapFlow.Channel2Level.getLastValue().toString()) != null)
            showOnChosenFlowUnit(value, backFlowComboBox.getSelectionModel().getSelectedItem(), Flow.BACK_FLOW);

        if ((value = ModbusMapFlow.Channel1Temperature1.getLastValue().toString()) != null) {
            convertedValue.append(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature1Delivery1Label, temperature1Delivery2Label,
                    temperature1Delivery3Label, temperature1Delivery4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }
        if ((value = ModbusMapFlow.Channel1Temperature2.getLastValue().toString()) != null) {
            convertedValue.append(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature2Delivery1Label, temperature2Delivery2Label,
                    temperature2Delivery3Label, temperature2Delivery4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }
        if ((value = ModbusMapFlow.Channel2Temperature1.getLastValue().toString()) != null) {
            convertedValue.append(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature1BackFlow1Label, temperature1BackFlow2Label,
                    temperature1BackFlow3Label, temperature1BackFlow4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }
        if ((value = ModbusMapFlow.Channel2Temperature2.getLastValue().toString()) != null) {
            convertedValue.append(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature2BackFlow1Label, temperature2BackFlow2Label,
                    temperature2BackFlow3Label, temperature2BackFlow4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }

    }

}
