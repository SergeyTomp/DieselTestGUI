package fi.stardex.sisu.util.rescalers;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.FlowUnits;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;

public class FlowResolver {

    private MainSectionController mainSectionController;

    private SettingsController settingsController;

    private FlowController flowController;

    private DataConverter dataConverter;

    private static final double PERCENT = 0.01;

    private double currentNominalFlow;

    private double currentFlowRange;

    public FlowResolver(MainSectionController mainSectionController, SettingsController settingsController,
                        FlowController flowController, DataConverter dataConverter) {
        this.mainSectionController = mainSectionController;
        this.settingsController = settingsController;
        this.flowController = flowController;
        this.dataConverter = dataConverter;
    }

    @PostConstruct
    private void init() {
        mainSectionController.getTestListView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(newValue, settingsController.getFlowOutputDimensionsComboBox().getSelectionModel().getSelectedItem()));
        settingsController.getFlowOutputDimensionsComboBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                updateFlowLabels(newValue);
    }

    private void setFlowLabels(InjectorTest injectorTest, Dimension dimension) {
        if (injectorTest == null)
            return;

        Label flowLabel;
        String flowUnit;

        Measurement measurement = injectorTest.getTestName().getMeasurement();

        switch (measurement) {
            case DIRECT:
                flowLabel = flowController.getDeliveryRangeLabel();
                flowUnit = flowController.getDeliveryFlowComboBox().getSelectionModel().getSelectedItem();
                flowController.getBackFlowRangeLabel().setText("");
                break;
            case BACK_FLOW:
                flowLabel = flowController.getBackFlowRangeLabel();
                flowUnit = flowController.getBackFlowComboBox().getSelectionModel().getSelectedItem();
                flowController.getDeliveryRangeLabel().setText("");
                break;
            default:
                flowController.getDeliveryRangeLabel().setText("");
                flowController.getBackFlowRangeLabel().setText("");
                return;
        }

        currentNominalFlow = injectorTest.getNominalFlow();
        currentFlowRange = injectorTest.getFlowRange();

        switch (dimension) {
            case LIMIT:
                double[] results = calculateLIMIT(currentNominalFlow, currentFlowRange, flowUnit);
                flowLabel.setText(String.format("%.1f - %.1f", results[0], results[1]));
                break;
            case PLUS_OR_MINUS:
                double result = calculatePLUSMINUS(currentNominalFlow, currentFlowRange, flowUnit);
                flowLabel.setText(String.format("%.1f \\u00B1 %.1f", currentNominalFlow, result));
        }
    }

    private double[] calculateLIMIT(double nominalFlow, double flowRange, String flowUnit) {

        double[] result = new double[2];

        float flowUnitsConvertValue = FlowUnits.getMapOfFlowUnits().get(flowUnit);

        result[0] = dataConverter.roundToOneDecimalPlace((nominalFlow - nominalFlow * (flowRange * PERCENT)) * flowUnitsConvertValue);
        result[1] = dataConverter.roundToOneDecimalPlace((nominalFlow + nominalFlow * (flowRange * PERCENT)) * flowUnitsConvertValue);

        return result;

    }

    private double calculatePLUSMINUS(double nominalFlow, double flowRange, String flowUnit) {

        float flowUnitsConvertValue = FlowUnits.getMapOfFlowUnits().get(flowUnit);

        return dataConverter.roundToOneDecimalPlace((nominalFlow * (flowRange * PERCENT)) * flowUnitsConvertValue);
    }

    private void updateFlowLabels(Dimension dimension) {

        if (currentNominalFlow == 0d || currentFlowRange == 0d)
            return;



    }

}
