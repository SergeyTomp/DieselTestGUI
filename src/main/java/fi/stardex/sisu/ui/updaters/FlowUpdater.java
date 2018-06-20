package fi.stardex.sisu.ui.updaters;


import fi.stardex.sisu.combobox_values.FlowUnits;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import javafx.scene.control.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class FlowUpdater {

    protected FirmwareDataConverter firmwareDataConverter;

    protected static final String DEGREES_CELSIUS = " \u2103";

    protected StringBuilder convertedValue = new StringBuilder();

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

    protected ToggleButton ledBeaker1ToggleButton;

    protected ToggleButton ledBeaker2ToggleButton;

    protected ToggleButton ledBeaker3ToggleButton;

    protected ToggleButton ledBeaker4ToggleButton;

    protected ToggleButton injectorSectionPowerSwitch;

    public FlowUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                             CheckBox checkBoxFlowVisible, FirmwareDataConverter firmwareDataConverter) {

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
        deliveryFlowComboBox = flowController.getDeliveryFlowComboBox();
        backFlowComboBox = flowController.getBackFlowComboBox();
        this.checkBoxFlowVisible = checkBoxFlowVisible;
        ledBeaker1ToggleButton = injectorSectionController.getLedBeaker1Controller().getLedBeaker();
        ledBeaker2ToggleButton = injectorSectionController.getLedBeaker2Controller().getLedBeaker();
        ledBeaker3ToggleButton = injectorSectionController.getLedBeaker3Controller().getLedBeaker();
        ledBeaker4ToggleButton = injectorSectionController.getLedBeaker4Controller().getLedBeaker();
        injectorSectionPowerSwitch = injectorSectionController.getPowerSwitch();
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

        ledBeaker1ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery1Label, temperature2Delivery1Label,
                        temperature1BackFlow1Label, temperature2BackFlow1Label);
                setDeliveryBackFlowFieldsToNull(delivery1TextField, backFlow1TextField);
            }
        });

        ledBeaker2ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery2Label, temperature2Delivery2Label,
                        temperature1BackFlow2Label, temperature2BackFlow2Label);
                setDeliveryBackFlowFieldsToNull(delivery2TextField, backFlow2TextField);
            }
        });

        ledBeaker3ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery3Label, temperature2Delivery3Label,
                        temperature1BackFlow3Label, temperature2BackFlow3Label);
                setDeliveryBackFlowFieldsToNull(delivery3TextField, backFlow3TextField);
            }
        });

        ledBeaker4ToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue) {
                setTempLabelsToNull(temperature1Delivery4Label, temperature2Delivery4Label,
                        temperature1BackFlow4Label, temperature2BackFlow4Label);
                setDeliveryBackFlowFieldsToNull(delivery4TextField, backFlow4TextField);
            }
        });

        deliveryFlowComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                showOnChosenFlowUnit(ModbusMapFlow.Channel1Level.getLastValue().toString(), newValue, FlowMasterUpdater.Flow.DELIVERY));

        backFlowComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                showOnChosenFlowUnit(ModbusMapFlow.Channel2Level.getLastValue().toString(), newValue, FlowMasterUpdater.Flow.BACK_FLOW));

        checkBoxFlowVisible.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                setAllLabelsAndFieldsToNull();
        });

        injectorSectionPowerSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !checkBoxFlowVisible.isSelected())
                setAllLabelsAndFieldsToNull();
        });

    }

    protected void setTempLabelsToNull(Label temperature1DeliveryLabel, Label temperature2DeliveryLabel,
                                       Label temperature1BackFlowLabel, Label temperature2BackFlowLabel) {

        temperature1DeliveryLabel.setText(null);
        temperature2DeliveryLabel.setText(null);
        temperature1BackFlowLabel.setText(null);
        temperature2BackFlowLabel.setText(null);

    }

    protected void setDeliveryBackFlowFieldsToNull(TextField deliveryTextField, TextField backFlowTextField) {

        deliveryTextField.setText(null);
        backFlowTextField.setText(null);

    }

    protected void setAllLabelsAndFieldsToNull() {

        allFlowLabels.forEach(e -> e.setText(null));
        allFlowTextFields.forEach(e -> e.setText(null));

    }

    // TODO: реализовать 3 последних опции Combo box
    protected void showOnChosenFlowUnit(String value, String selectedItem, FlowMasterUpdater.Flow flow) {

        float convertedValueFloat = firmwareDataConverter.
                roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(value));

        TextField field1 = (flow == FlowMasterUpdater.Flow.DELIVERY) ? delivery1TextField : backFlow1TextField;
        TextField field2 = (flow == FlowMasterUpdater.Flow.DELIVERY) ? delivery2TextField : backFlow2TextField;
        TextField field3 = (flow == FlowMasterUpdater.Flow.DELIVERY) ? delivery3TextField : backFlow3TextField;
        TextField field4 = (flow == FlowMasterUpdater.Flow.DELIVERY) ? delivery4TextField : backFlow4TextField;

        switch (selectedItem) {
            case FlowUnits.MILLILITRE_PER_MINUTE:
                setDeliveryBackFlowFields(field1, field2, field3, field4, String.valueOf(convertedValueFloat));
                break;
            case FlowUnits.LITRE_PER_HOUR:
                setDeliveryBackFlowFields(field1, field2, field3, field4, String.valueOf(firmwareDataConverter.
                        roundToOneDecimalPlace(convertedValueFloat * 0.06f)));
                break;
            case FlowUnits.MILLILITRE_PER_100RPM:
                break;
            case FlowUnits.MILLILITRE_PER_200RPM:
                break;
            case FlowUnits.MILLILITRE_PER_1000RPM:
                break;
            default:
                break;
        }

    }

    protected void setTempLabels(Label label1, Label label2, Label label3, Label label4, String value) {

        label1.setText(ledBeaker1ToggleButton.isSelected() ? value : null);
        label2.setText(ledBeaker2ToggleButton.isSelected() ? value : null);
        label3.setText(ledBeaker3ToggleButton.isSelected() ? value : null);
        label4.setText(ledBeaker4ToggleButton.isSelected() ? value : null);

    }

    protected void setDeliveryBackFlowFields(TextField field1, TextField field2, TextField field3, TextField field4, String value) {

        field1.setText(ledBeaker1ToggleButton.isSelected() ? value : null);
        field2.setText(ledBeaker2ToggleButton.isSelected() ? value : null);
        field3.setText(ledBeaker3ToggleButton.isSelected() ? value : null);
        field4.setText(ledBeaker4ToggleButton.isSelected() ? value : null);

    }

}
