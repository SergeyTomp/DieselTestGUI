package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.Versions;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.*;
import static fi.stardex.sisu.ui.updaters.FlowUpdater.Flow.BACK_FLOW;
import static fi.stardex.sisu.ui.updaters.FlowUpdater.Flow.DELIVERY;
import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER;

public abstract class FlowUpdater {

    protected FlowController flowController;

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    protected static final String DEGREES_CELSIUS = " \u2103";

    protected StringBuilder convertedValue = new StringBuilder();

    private List<Double> listOfConvertedValues = new ArrayList<>();

    protected Label deliveryRangeLabel;

    protected Label backFlowRangeLabel;

    protected Label temperature1Delivery1Label;

    protected Label temperature1Delivery2Label;

    protected Label temperature1Delivery3Label;

    protected Label temperature1Delivery4Label;

    protected Label temperature2Delivery1Label;

    protected Label temperature2Delivery2Label;

    protected Label temperature2Delivery3Label;

    protected Label temperature2Delivery4Label;

    protected Label temperature1BackFlow1Label;

    protected Label temperature1BackFlow2Label;

    protected Label temperature1BackFlow3Label;

    protected Label temperature1BackFlow4Label;

    protected Label temperature2BackFlow1Label;

    protected Label temperature2BackFlow2Label;

    protected Label temperature2BackFlow3Label;

    protected Label temperature2BackFlow4Label;

    protected List<Label> allFlowLabels = new ArrayList<>();

    protected TextField delivery1TextField;

    protected TextField delivery2TextField;

    protected TextField delivery3TextField;

    protected TextField delivery4TextField;

    protected TextField backFlow1TextField;

    protected TextField backFlow2TextField;

    protected TextField backFlow3TextField;

    protected TextField backFlow4TextField;

    protected List<TextField> allFlowTextFields = new ArrayList<>();

    protected ComboBox<String> deliveryFlowComboBox;

    protected ComboBox<String> backFlowComboBox;

    protected CheckBox checkBoxFlowVisible;

    protected ComboBox<InjectorChannel> comboInjectorConfig;

    protected ToggleButton ledBeaker1ToggleButton;

    protected ToggleButton ledBeaker2ToggleButton;

    protected ToggleButton ledBeaker3ToggleButton;

    protected ToggleButton ledBeaker4ToggleButton;

    protected ToggleButton injectorSectionPowerSwitch;

    public FlowUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                       SettingsController settingsController, FirmwareVersion<FlowVersions> flowFirmwareVersion) {

        this.flowController = flowController;
        this.flowFirmwareVersion = flowFirmwareVersion;

        deliveryRangeLabel = flowController.getDeliveryRangeLabel();
        backFlowRangeLabel = flowController.getBackFlowRangeLabel();
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
        deliveryFlowComboBox = flowController.getDeliveryFlowComboBox();
        backFlowComboBox = flowController.getBackFlowComboBox();
        checkBoxFlowVisible = settingsController.getFlowVisibleCheckBox();
        comboInjectorConfig = settingsController.getInjectorsConfigComboBox();
        ledBeaker1ToggleButton = injectorSectionController.getLedBeaker1Controller().getLedBeaker();
        ledBeaker2ToggleButton = injectorSectionController.getLedBeaker2Controller().getLedBeaker();
        ledBeaker3ToggleButton = injectorSectionController.getLedBeaker3Controller().getLedBeaker();
        ledBeaker4ToggleButton = injectorSectionController.getLedBeaker4Controller().getLedBeaker();
        injectorSectionPowerSwitch = injectorSectionController.getInjectorSectionStartToggleButton();

        allFlowLabels.add(temperature1Delivery1Label);
        allFlowLabels.add(temperature1Delivery2Label);
        allFlowLabels.add(temperature1Delivery3Label);
        allFlowLabels.add(temperature1Delivery4Label);
        allFlowLabels.add(temperature2Delivery1Label);
        allFlowLabels.add(temperature2Delivery2Label);
        allFlowLabels.add(temperature2Delivery3Label);
        allFlowLabels.add(temperature2Delivery4Label);
        allFlowLabels.add(temperature1BackFlow1Label);
        allFlowLabels.add(temperature1BackFlow2Label);
        allFlowLabels.add(temperature1BackFlow3Label);
        allFlowLabels.add(temperature1BackFlow4Label);
        allFlowLabels.add(temperature2BackFlow1Label);
        allFlowLabels.add(temperature2BackFlow2Label);
        allFlowLabels.add(temperature2BackFlow3Label);
        allFlowLabels.add(temperature2BackFlow4Label);

        allFlowTextFields.add(delivery1TextField);
        allFlowTextFields.add(delivery2TextField);
        allFlowTextFields.add(delivery3TextField);
        allFlowTextFields.add(delivery4TextField);
        allFlowTextFields.add(backFlow1TextField);
        allFlowTextFields.add(backFlow2TextField);
        allFlowTextFields.add(backFlow3TextField);
        allFlowTextFields.add(backFlow4TextField);

    }

    protected enum Flow {

        DELIVERY, BACK_FLOW

    }

    protected void initListeners() {

        initLedBeakerListener(ledBeaker1ToggleButton, temperature1Delivery1Label, temperature2Delivery1Label,
                temperature1BackFlow1Label, temperature2BackFlow1Label, delivery1TextField, backFlow1TextField);

        initLedBeakerListener(ledBeaker2ToggleButton, temperature1Delivery2Label, temperature2Delivery2Label,
                temperature1BackFlow2Label, temperature2BackFlow2Label, delivery2TextField, backFlow2TextField);

        initLedBeakerListener(ledBeaker3ToggleButton, temperature1Delivery3Label, temperature2Delivery3Label,
                temperature1BackFlow3Label, temperature2BackFlow3Label, delivery3TextField, backFlow3TextField);

        initLedBeakerListener(ledBeaker4ToggleButton, temperature1Delivery4Label, temperature2Delivery4Label,
                temperature1BackFlow4Label, temperature2BackFlow4Label, delivery4TextField, backFlow4TextField);

        checkBoxFlowVisible.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                setAllLabelsAndFieldsToNull();
        });

        injectorSectionPowerSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !checkBoxFlowVisible.isSelected())
                setAllLabelsAndFieldsToNull();
        });

        // FIXME: баг при выключенном instant flow: выбираю тип измерения - показывается поток
        deliveryFlowComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                refreshValues(DELIVERY));

        // FIXME: баг при выключенном instant flow: выбираю тип измерения - показывается поток
        backFlowComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                refreshValues(BACK_FLOW));

    }

    private void initLedBeakerListener(ToggleButton ledBeaker, Label temperature1Delivery, Label temperature2Delivery,
                                       Label temperature1BackFlow, Label temperature2BackFlow, TextField delivery, TextField backFlow) {
        ledBeaker.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery, temperature2Delivery, temperature1BackFlow, temperature2BackFlow);
                setDeliveryBackFlowFieldsToNull(delivery, backFlow);
            }
            delivery.setEditable(newValue);
            backFlow.setEditable(newValue);
        });
    }

    private void refreshValues(Flow flow) {

        FlowVersions version = flowFirmwareVersion.getVersions();

        switch (version) {
            case MASTER:
                switch (flow) {
                    case DELIVERY:
                        showOnChosenFlowUnit(Channel1Level.getLastValue().toString(), getDeliveryCoefficient(), flow);
                        break;
                    case BACK_FLOW:
                        showOnChosenFlowUnit(Channel2Level.getLastValue().toString(), getBackFlowCoefficient(), flow);
                        break;
                }
                break;
            case STREAM:
                switch (flow) {
                    case DELIVERY:
                        showOnChosenFlowUnit(Arrays.asList(Channel1Level.getLastValue().toString(),
                                Channel2Level.getLastValue().toString(),
                                Channel3Level.getLastValue().toString(),
                                Channel4Level.getLastValue().toString()), getDeliveryCoefficient(), flow);
                        break;
                    case BACK_FLOW:
                        showOnChosenFlowUnit(Arrays.asList(Channel5Level.getLastValue().toString(),
                                Channel6Level.getLastValue().toString(),
                                Channel7Level.getLastValue().toString(),
                                Channel8Level.getLastValue().toString()), getBackFlowCoefficient(), flow);
                        break;
                }
                break;
        }

    }

    private void showOnChosenFlowUnit(String value, double coefficient, Flow flow) {

        double convertedValue = round(convertDataToDouble(value) * coefficient);

        TextField field1 = (flow == DELIVERY) ? delivery1TextField : backFlow1TextField;
        TextField field2 = (flow == DELIVERY) ? delivery2TextField : backFlow2TextField;
        TextField field3 = (flow == DELIVERY) ? delivery3TextField : backFlow3TextField;
        TextField field4 = (flow == DELIVERY) ? delivery4TextField : backFlow4TextField;

        setDeliveryBackFlowFields(field1, field2, field3, field4, String.valueOf(convertedValue));

    }

    // TODO: реализовать 3 последние опции Combo box
    private void showOnChosenFlowUnit(List<String> listOfValues, double coefficient, Flow flow) {

        listOfConvertedValues.add(round(convertDataToDouble(listOfValues.get(0)) * coefficient));
        listOfConvertedValues.add(round(convertDataToDouble(listOfValues.get(1)) * coefficient));
        listOfConvertedValues.add(round(convertDataToDouble(listOfValues.get(2)) * coefficient));
        listOfConvertedValues.add(round(convertDataToDouble(listOfValues.get(3)) * coefficient));

        TextField field1 = (flow == DELIVERY) ? delivery1TextField : backFlow1TextField;
        TextField field2 = (flow == DELIVERY) ? delivery2TextField : backFlow2TextField;
        TextField field3 = (flow == DELIVERY) ? delivery3TextField : backFlow3TextField;
        TextField field4 = (flow == DELIVERY) ? delivery4TextField : backFlow4TextField;

        if (ledBeaker1ToggleButton.isSelected())
            flowController.changeFlow(field1, listOfConvertedValues.get(0).toString());
        else
            field1.setText(null);

        if (ledBeaker2ToggleButton.isSelected())
            flowController.changeFlow(field2, listOfConvertedValues.get(1).toString());
        else
            field2.setText(null);

        if (ledBeaker3ToggleButton.isSelected())
            flowController.changeFlow(field3, listOfConvertedValues.get(2).toString());
        else
            field3.setText(null);

        if (ledBeaker4ToggleButton.isSelected())
            flowController.changeFlow(field4, listOfConvertedValues.get(3).toString());
        else
            field4.setText(null);

        listOfConvertedValues.clear();

    }

    private void setDeliveryBackFlowFields(TextField field1, TextField field2, TextField field3, TextField field4, String value) {

        if (ledBeaker1ToggleButton.isSelected())
            flowController.changeFlow(field1, value);
        else
            field1.setText(null);

        if (ledBeaker2ToggleButton.isSelected())
            flowController.changeFlow(field2, value);
        else
            field2.setText(null);

        if (ledBeaker3ToggleButton.isSelected())
            flowController.changeFlow(field3, value);
        else
            field3.setText(null);

        if (ledBeaker4ToggleButton.isSelected())
            flowController.changeFlow(field4, value);
        else
            field4.setText(null);

    }


    private void setTempLabelsToNull(Label ... labels) {

        Stream.of(labels).forEach(label -> label.setText(null));

    }

    private void setDeliveryBackFlowFieldsToNull(TextField ... textFields) {

        Stream.of(textFields).forEach(textField -> textField.setText(null));

    }

    protected void setAllLabelsAndFieldsToNull() {

        allFlowLabels.forEach(label -> label.setText(null));
        allFlowTextFields.forEach(textField -> textField.setText(null));

    }

    private void setTempLabels(Label label1, Label label2, Label label3, Label label4, String value) {

        label1.setText(ledBeaker1ToggleButton.isSelected() ? value : null);
        label2.setText(ledBeaker2ToggleButton.isSelected() ? value : null);
        label3.setText(ledBeaker3ToggleButton.isSelected() ? value : null);
        label4.setText(ledBeaker4ToggleButton.isSelected() ? value : null);

    }

    protected void runOnSingleChannelMode(Versions version) {

        String value;

        if ((value = Channel1Level.getLastValue().toString()) != null)
            showOnChosenFlowUnit(value, getDeliveryCoefficient(), DELIVERY);

        if ((value = (version == MASTER) ? Channel2Level.getLastValue().toString()
                : Channel5Level.getLastValue().toString()) != null)
            showOnChosenFlowUnit(value, getBackFlowCoefficient(), BACK_FLOW);

        if ((value = Channel1Temperature1.getLastValue().toString()) != null) {
            convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature1Delivery1Label, temperature1Delivery2Label,
                    temperature1Delivery3Label, temperature1Delivery4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }
        if ((value = Channel1Temperature2.getLastValue().toString()) != null) {
            convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature2Delivery1Label, temperature2Delivery2Label,
                    temperature2Delivery3Label, temperature2Delivery4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }
        if ((value = (version == MASTER) ? Channel2Temperature1.getLastValue().toString()
                : Channel5Temperature1.getLastValue().toString()) != null) {
            convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature1BackFlow1Label, temperature1BackFlow2Label,
                    temperature1BackFlow3Label, temperature1BackFlow4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }
        if ((value = (version == MASTER) ? Channel2Temperature2.getLastValue().toString()
                : Channel5Temperature2.getLastValue().toString()) != null) {
            convertedValue.append(round(convertDataToFloat(value))).append(DEGREES_CELSIUS);
            setTempLabels(temperature2BackFlow1Label, temperature2BackFlow2Label,
                    temperature2BackFlow3Label, temperature2BackFlow4Label, convertedValue.toString());
            convertedValue.setLength(0);
        }

    }

    protected boolean isNotInstantFlow() {
        return !checkBoxFlowVisible.isSelected() && !injectorSectionPowerSwitch.isSelected();
    }

    protected boolean isNotMeasuring() {
        return deliveryRangeLabel.getText().isEmpty() && backFlowRangeLabel.getText().isEmpty();
    }

}
