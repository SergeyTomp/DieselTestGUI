package fi.stardex.sisu.util.converters;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.model.FlowViewModel;
import fi.stardex.sisu.model.MainSectionModel;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SingleSelectionModel;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.round;

public class FlowResolver {

    private SingleSelectionModel<String> deliveryFlowComboBoxSelectionModel;

    private SingleSelectionModel<String> backFlowComboBoxSelectionModel;

    private Label deliveryRangeLabel;

    private Label backFlowRangeLabel;

    private FlowController flowController;

    private FlowViewModel flowViewModel;

    private MainSectionModel mainSectionModel;

    private static final double PERCENT = 0.01;

    public FlowResolver(FlowController flowController,
                        FlowViewModel flowViewModel,
                        MainSectionModel mainSectionModel) {

        this.flowController = flowController;
        this.flowViewModel = flowViewModel;
        this.mainSectionModel = mainSectionModel;

        deliveryFlowComboBoxSelectionModel = flowController.getDeliveryFlowComboBox().getSelectionModel();
        backFlowComboBoxSelectionModel = flowController.getBackFlowComboBox().getSelectionModel();

        deliveryRangeLabel = flowController.getDeliveryRangeLabel();
        backFlowRangeLabel = flowController.getBackFlowRangeLabel();

    }

    @PostConstruct
    private void init() {

        mainSectionModel.injectorTestProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(newValue, flowViewModel.flowViewProperty().get()));

        flowViewModel.flowViewProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(mainSectionModel.injectorTestProperty().get(), newValue));

        deliveryFlowComboBoxSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(mainSectionModel.injectorTestProperty().get(), flowViewModel.flowViewProperty().get()));

        backFlowComboBoxSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(mainSectionModel.injectorTestProperty().get(), flowViewModel.flowViewProperty().get()));
    }

    private void setFlowLabels(InjectorTest injectorTest, Dimension dimension) {

        if (injectorTest == null) {
            setLevelsToNull();
            return;
        }


        Label flowLabel;
        Measurement measurement = injectorTest.getTestName().getMeasurement();

        switch (measurement) {
            case DELIVERY:
                flowLabel = deliveryRangeLabel;
                backFlowRangeLabel.setText("");
                break;
            case BACK_FLOW:
                flowLabel = backFlowRangeLabel;
                deliveryRangeLabel.setText("");
                break;
            default:
                setLevelsToNull();
                return;
        }

        double currentNominalFlow = injectorTest.getNominalFlow();
        double currentFlowRange = injectorTest.getFlowRange();

        switch (dimension) {

            case LIMIT:
                calculateLIMIT(currentNominalFlow, currentFlowRange, measurement, flowLabel);
                break;
            case PLUS_OR_MINUS:
                calculatePLUS_OR_MINUS(currentNominalFlow, currentFlowRange, measurement, flowLabel);
                break;

        }

    }

    private void setLevelsToNull() {

        deliveryRangeLabel.setText("");
        backFlowRangeLabel.setText("");
        flowController.setCurrentDeliveryFlowLevels(null);
        flowController.setCurrentBackFlowLevels(null);

    }

    private void calculateLIMIT(double nominalFlow, double flowRange, Measurement measurement, Label flowLabel) {

        double[] result;

        switch (measurement) {

            case DELIVERY:
                result = getRange(nominalFlow, flowRange, getDeliveryCoefficient());
                flowController.setCurrentDeliveryFlowLevels(result);
                flowLabel.setText(String.format("%.1f - %.1f", result[0], result[1]));
                break;
            case BACK_FLOW:
                result = getRange(nominalFlow, flowRange, getBackFlowCoefficient());
                flowController.setCurrentBackFlowLevels(result);
                flowLabel.setText(String.format("%.1f - %.1f", result[0], result[1]));
                break;

        }

    }

    private double[] getRange(double nominalFlow, double flowRange, double coefficient) {

        double[] result = new double[2];

        result[0] = round((nominalFlow - nominalFlow * (flowRange * PERCENT)) * coefficient);
        result[1] = round((nominalFlow + nominalFlow * (flowRange * PERCENT)) * coefficient);

        return result;

    }

    private void calculatePLUS_OR_MINUS(double nominalFlow, double flowRange, Measurement measurement, Label flowLabel) {

        double[] result = new double[2];

        double coefficient = (measurement == Measurement.DELIVERY) ? getDeliveryCoefficient() : getBackFlowCoefficient();

        result[0] = round((nominalFlow * (flowRange * PERCENT)) * coefficient);
        result[1] = round(nominalFlow * coefficient);

        switch (measurement) {

            case DELIVERY:
                flowController.setCurrentDeliveryFlowLevels(getRange(nominalFlow, flowRange, getDeliveryCoefficient()));
                break;
            case BACK_FLOW:
                flowController.setCurrentBackFlowLevels(getRange(nominalFlow, flowRange, getBackFlowCoefficient()));
                break;

        }

        flowLabel.setText(String.format("%.1f \u00B1 %.1f", result[1], result[0]));

    }

}
