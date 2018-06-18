package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import javax.annotation.PostConstruct;

@Module(value = Device.MODBUS_FLOW)
public class FlowUpdater implements Updater {

    private FirmwareDataConverter firmwareDataConverter;

    private static final String DEGREES_CELSIUS = " \u2103";

    private StringBuilder convertedValue = new StringBuilder();

    private Label temperature1Delivery1Label;

    private Label temperature1Delivery2Label;

    private Label temperature1Delivery3Label;

    private Label temperature1Delivery4Label;

    private Label temperature2Delivery1Label;

    private Label temperature2Delivery2Label;

    private Label temperature2Delivery3Label;

    private Label temperature2Delivery4Label;

    private Label temperature1BackFlow1Label;

    private Label temperature1BackFlow2Label;

    private Label temperature1BackFlow3Label;

    private Label temperature1BackFlow4Label;

    private Label temperature2BackFlow1Label;

    private Label temperature2BackFlow2Label;

    private Label temperature2BackFlow3Label;

    private Label temperature2BackFlow4Label;

    private TextField delivery1TextField;

    private TextField delivery2TextField;

    private TextField delivery3TextField;

    private TextField delivery4TextField;

    private TextField backFlow1TextField;

    private TextField backFlow2TextField;

    private TextField backFlow3TextField;

    private TextField backFlow4TextField;

    private ToggleButton ledBeaker1ToggleButton;

    private ToggleButton ledBeaker2ToggleButton;

    private ToggleButton ledBeaker3ToggleButton;

    private ToggleButton ledBeaker4ToggleButton;

    public FlowUpdater(FlowController flowController, InjectorSectionController injectorSectionController, FirmwareDataConverter firmwareDataConverter) {

        this.firmwareDataConverter = firmwareDataConverter;
        temperature1Delivery1Label = flowController.getTemperature1Delivery1();
        temperature1Delivery2Label = flowController.getTemperature1Delivery2();
        temperature1Delivery3Label = flowController.getTemperature1Delivery3();
        temperature1Delivery4Label = flowController.getTemperature1Delivery4();
        temperature2Delivery1Label = flowController.getTemperature2Delivery1();
        temperature2Delivery2Label = flowController.getTemperature2Delivery2();
        temperature2Delivery3Label = flowController.getTemperature2Delivery3();
        temperature2Delivery4Label = flowController.getTemperature2Delivery4();
        temperature1BackFlow1Label = flowController.getTemperature1BackFlow1();
        temperature1BackFlow2Label = flowController.getTemperature1BackFlow2();
        temperature1BackFlow3Label = flowController.getTemperature1BackFlow3();
        temperature1BackFlow4Label = flowController.getTemperature1BackFlow4();
        temperature2BackFlow1Label = flowController.getTemperature2BackFlow1();
        temperature2BackFlow2Label = flowController.getTemperature2BackFlow2();
        temperature2BackFlow3Label = flowController.getTemperature2BackFlow3();
        temperature2BackFlow4Label = flowController.getTemperature2BackFlow4();
        delivery1TextField = flowController.getDelivery1TextField();
        delivery2TextField = flowController.getDelivery2TextField();
        delivery3TextField = flowController.getDelivery3TextField();
        delivery4TextField = flowController.getDelivery4TextField();
        backFlow1TextField = flowController.getBackFlow1TextField();
        backFlow2TextField = flowController.getBackFlow2TextField();
        backFlow3TextField = flowController.getBackFlow3TextField();
        backFlow4TextField = flowController.getBackFlow4TextField();
        ledBeaker1ToggleButton = injectorSectionController.getLedBeaker1Controller().getLedBeaker();
        ledBeaker2ToggleButton = injectorSectionController.getLedBeaker2Controller().getLedBeaker();
        ledBeaker3ToggleButton = injectorSectionController.getLedBeaker3Controller().getLedBeaker();
        ledBeaker4ToggleButton = injectorSectionController.getLedBeaker4Controller().getLedBeaker();

    }

    @PostConstruct
    private void init() {

        ledBeaker1ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery1Label, temperature2Delivery1Label, temperature1BackFlow1Label, temperature2BackFlow1Label);
                setDeliveryBackFlowFieldsToNull(delivery1TextField, backFlow1TextField);
            }
        });

        ledBeaker2ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery2Label, temperature2Delivery2Label, temperature1BackFlow2Label, temperature2BackFlow2Label);
                setDeliveryBackFlowFieldsToNull(delivery2TextField, backFlow2TextField);
            }
        });

        ledBeaker3ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery3Label, temperature2Delivery3Label, temperature1BackFlow3Label, temperature2BackFlow3Label);
                setDeliveryBackFlowFieldsToNull(delivery3TextField, backFlow3TextField);
            }
        });

        ledBeaker4ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery4Label, temperature2Delivery4Label, temperature1BackFlow4Label, temperature2BackFlow4Label);
                setDeliveryBackFlowFieldsToNull(delivery4TextField, backFlow4TextField);
            }
        });

    }


    private void setTempLabels(Label label1, Label label2, Label label3, Label label4, String value) {

        label1.setText(ledBeaker1ToggleButton.isSelected() ? value : null);
        label2.setText(ledBeaker2ToggleButton.isSelected() ? value : null);
        label3.setText(ledBeaker3ToggleButton.isSelected() ? value : null);
        label4.setText(ledBeaker4ToggleButton.isSelected() ? value : null);

    }

    private void setDeliveryBackFlowFields(TextField field1, TextField field2, TextField field3, TextField field4, String value) {

        field1.setText(ledBeaker1ToggleButton.isSelected() ? value : null);
        field2.setText(ledBeaker2ToggleButton.isSelected() ? value : null);
        field3.setText(ledBeaker3ToggleButton.isSelected() ? value : null);
        field4.setText(ledBeaker4ToggleButton.isSelected() ? value : null);

    }

    private void setTempLabelsToNull(Label temperature1DeliveryLabel, Label temperature2DeliveryLabel, Label temperature1BackFlowLabel,
                                     Label temperature2BackFlowLabel) {

        temperature1DeliveryLabel.setText(null);
        temperature2DeliveryLabel.setText(null);
        temperature1BackFlowLabel.setText(null);
        temperature2BackFlowLabel.setText(null);

    }

    private void setDeliveryBackFlowFieldsToNull(TextField deliveryTextField, TextField backFlowTextField) {
        deliveryTextField.setText(null);
        backFlowTextField.setText(null);
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        String value;

        if ((value = ModbusMapFlow.Channel1Level.getLastValue().toString()) != null) {
            convertedValue.append(String.valueOf(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))));
            if (!convertedValue.toString().equals(delivery1TextField.getText())) {
                setDeliveryBackFlowFields(delivery1TextField, delivery2TextField,
                        delivery3TextField, delivery4TextField, convertedValue.toString());
            }
            convertedValue.setLength(0);
        }

        if ((value = ModbusMapFlow.Channel2Level.getLastValue().toString()) != null) {
            convertedValue.append(String.valueOf(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value))));
            if (!convertedValue.toString().equals(delivery1TextField.getText())) {
                setDeliveryBackFlowFields(backFlow1TextField, backFlow2TextField,
                        backFlow3TextField, backFlow4TextField, convertedValue.toString());
            }
            convertedValue.setLength(0);
        }

        if ((value = ModbusMapFlow.Channel1Temperature1.getLastValue().toString()) != null) {
            convertedValue.append(String.valueOf(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value)))).append(DEGREES_CELSIUS);
            if (!convertedValue.toString().equals(temperature1Delivery1Label.getText())) {
                setTempLabels(temperature1Delivery1Label, temperature1Delivery2Label,
                        temperature1Delivery3Label, temperature1Delivery4Label, convertedValue.toString());
            }
            convertedValue.setLength(0);
        }
        if ((value = ModbusMapFlow.Channel1Temperature2.getLastValue().toString()) != null) {
            convertedValue.append(String.valueOf(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value)))).append(DEGREES_CELSIUS);
            if (!convertedValue.toString().equals(temperature1Delivery1Label.getText())) {
                setTempLabels(temperature2Delivery1Label, temperature2Delivery2Label,
                        temperature2Delivery3Label, temperature2Delivery4Label, convertedValue.toString());
            }
            convertedValue.setLength(0);
        }
        if ((value = ModbusMapFlow.Channel2Temperature1.getLastValue().toString()) != null) {
            convertedValue.append(String.valueOf(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value)))).append(DEGREES_CELSIUS);
            if (!convertedValue.toString().equals(temperature1Delivery1Label.getText())) {
                setTempLabels(temperature1BackFlow1Label, temperature1BackFlow2Label,
                        temperature1BackFlow3Label, temperature1BackFlow4Label, convertedValue.toString());
            }
            convertedValue.setLength(0);
        }
        if ((value = ModbusMapFlow.Channel2Temperature2.getLastValue().toString()) != null) {
            convertedValue.append(String.valueOf(firmwareDataConverter.
                    roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value)))).append(DEGREES_CELSIUS);
            if (!convertedValue.toString().equals(temperature1Delivery1Label.getText())) {
                setTempLabels(temperature2BackFlow1Label, temperature2BackFlow2Label,
                        temperature2BackFlow3Label, temperature2BackFlow4Label, convertedValue.toString());
            }
            convertedValue.setLength(0);
        }

    }

}
