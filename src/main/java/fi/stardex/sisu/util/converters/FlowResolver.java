package fi.stardex.sisu.util.converters;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.combobox_values.FlowUnits;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;

public class FlowResolver {

    private MainSectionController mainSectionController;

    private SettingsController settingsController;

    private FlowController flowController;

    private DataConverter dataConverter;

    private static final double PERCENT = 0.01;

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
                setFlowLabels(mainSectionController.getTestListView().getSelectionModel().getSelectedItem(), newValue));

        flowController.getDeliveryFlowComboBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(mainSectionController.getTestListView().getSelectionModel().getSelectedItem(),
                        settingsController.getFlowOutputDimensionsComboBox().getSelectionModel().getSelectedItem()));

        flowController.getBackFlowComboBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(mainSectionController.getTestListView().getSelectionModel().getSelectedItem(),
                        settingsController.getFlowOutputDimensionsComboBox().getSelectionModel().getSelectedItem()));

    }

    private void setFlowLabels(InjectorTest injectorTest, Dimension dimension) {

        if (injectorTest == null) {
            setLevelsToNull();
            return;
        }


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
                setLevelsToNull();
                return;
        }

        double currentNominalFlow = injectorTest.getNominalFlow();
        double currentFlowRange = injectorTest.getFlowRange();

        double[] results = setupCurrentFlowLevels(currentNominalFlow, currentFlowRange, flowUnit, measurement);

        switch (dimension) {
            case LIMIT:
                flowLabel.setText(String.format("%.1f - %.1f", results[0], results[1]));
                break;
            case PLUS_OR_MINUS:
                double[] resultsPlusOrMinus = calculatePLUSMINUS(currentNominalFlow, currentFlowRange, flowUnit);
                flowLabel.setText(String.format("%.1f \u00B1 %.1f", resultsPlusOrMinus[0], resultsPlusOrMinus[1]));
        }

    }

    private void setLevelsToNull() {

        flowController.getDeliveryRangeLabel().setText("");
        flowController.getBackFlowRangeLabel().setText("");
        flowController.setCurrentDeliveryFlowLevels(null);
        flowController.setCurrentBackFlowLevels(null);

    }

    private double[] setupCurrentFlowLevels(double nominalFlow, double flowRange, String flowUnit, Measurement measurement) {

        double[] result = new double[2];

        float flowUnitsConvertValue = FlowUnits.getMapOfFlowUnits().get(flowUnit);

        result[0] = dataConverter.round((nominalFlow - nominalFlow * (flowRange * PERCENT)) * flowUnitsConvertValue);
        result[1] = dataConverter.round((nominalFlow + nominalFlow * (flowRange * PERCENT)) * flowUnitsConvertValue);

        switch (measurement) {
            case DIRECT:
                flowController.setCurrentDeliveryFlowLevels(result);
                break;
            case BACK_FLOW:
                flowController.setCurrentBackFlowLevels(result);
                break;
            default:
                return result;
        }

        return result;
    }

    private double[] calculatePLUSMINUS(double nominalFlow, double flowRange, String flowUnit) {

        double[] result = new double[2];

        float flowUnitsConvertValue = FlowUnits.getMapOfFlowUnits().get(flowUnit);

        result[0] = dataConverter.round(nominalFlow * flowUnitsConvertValue);

        result[1] = dataConverter.round((nominalFlow * (flowRange * PERCENT)) * flowUnitsConvertValue);

        return result;

    }

}
