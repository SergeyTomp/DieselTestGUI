package fi.stardex.sisu.store;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class FlowReport {

    private ObservableList<FlowTestResult> flowTableViewItems;

    private MultipleSelectionModel<InjectorTest> testsSelectionModel;

    private SingleSelectionModel<String> deliveryFlowComboBoxSelectionModel;

    private SingleSelectionModel<String> backFlowComboBoxSelectionModel;

    private SingleSelectionModel<Dimension> flowOutputDimensionsSelectionModel;

    private Label deliveryRangeLabel;

    private Label backFlowRangeLabel;

    private TextField delivery1TextField;

    private TextField delivery2TextField;

    private TextField delivery3TextField;

    private TextField delivery4TextField;

    private TextField backFlow1TextField;

    private TextField backFlow2TextField;

    private TextField backFlow3TextField;

    private TextField backFlow4TextField;

    public FlowReport(FlowReportController flowReportController, MainSectionController mainSectionController,
                      FlowController flowController, SettingsController settingsController) {

        flowTableViewItems = flowReportController.getFlowTableView().getItems();

        testsSelectionModel = mainSectionController.getTestsSelectionModel();

        deliveryFlowComboBoxSelectionModel = flowController.getDeliveryFlowComboBox().getSelectionModel();
        backFlowComboBoxSelectionModel = flowController.getBackFlowComboBox().getSelectionModel();
        deliveryRangeLabel = flowController.getDeliveryRangeLabel();
        backFlowRangeLabel = flowController.getBackFlowRangeLabel();
        delivery1TextField = flowController.getDelivery1TextField();
        delivery2TextField = flowController.getDelivery2TextField();
        delivery3TextField = flowController.getDelivery3TextField();
        delivery4TextField = flowController.getDelivery4TextField();
        backFlow1TextField = flowController.getBackFlow1TextField();
        backFlow2TextField = flowController.getBackFlow2TextField();
        backFlow3TextField = flowController.getBackFlow3TextField();
        backFlow4TextField = flowController.getBackFlow4TextField();

        flowOutputDimensionsSelectionModel = settingsController.getFlowOutputDimensionsComboBox().getSelectionModel();

    }

    public void save() {

        TestName testName = testsSelectionModel.getSelectedItem().getTestName();

        Measurement measurement = testName.getMeasurement();

        switch (measurement) {

            case DELIVERY:
                flowTableViewItems.add(new FlowTestResult(testName.toString(), measurement.name(),
                        getNominalFlow(deliveryRangeLabel.getText(), deliveryFlowComboBoxSelectionModel.getSelectedItem()),
                        getFlow(delivery1TextField.getText()), getFlow(delivery2TextField.getText()),
                        getFlow(delivery3TextField.getText()), getFlow(delivery4TextField.getText()), flowOutputDimensionsSelectionModel.getSelectedItem()));
                break;
            case BACK_FLOW:
                flowTableViewItems.add(new FlowTestResult(testName.toString(), measurement.name(),
                        getNominalFlow(backFlowRangeLabel.getText(), backFlowComboBoxSelectionModel.getSelectedItem()),
                        getFlow(backFlow1TextField.getText()), getFlow(backFlow2TextField.getText()),
                        getFlow(backFlow3TextField.getText()), getFlow(backFlow4TextField.getText()), flowOutputDimensionsSelectionModel.getSelectedItem()));
                break;

        }

    }

    private double getFlow(String flow) {

        return (flow != null) ? convertDataToDouble(flow) : 0d;

    }

    private String getNominalFlow(String range, String flowUnit) {

        return range + " " + flowUnit;

    }

    // Не удалять геттеры так как они нужны для работы метода setupTableColumns() в классе FlowReportController
    public static class FlowTestResult {

        private final StringProperty testName;

        private final StringProperty flowType;

        private final StringProperty nominalFlow;

        private final DoubleProperty flow1;

        private final DoubleProperty flow2;

        private final DoubleProperty flow3;

        private final DoubleProperty flow4;

        private double flowRangeLeft;

        private double flowRangeRight;

        private double acceptableFlowRangeLeft;

        private double acceptableFlowRangeRight;

        public FlowTestResult(String testName, String flowType, String nominalFlow, Double flow1, Double flow2, Double flow3, Double flow4, Dimension dimension) {

            this.testName = new SimpleStringProperty(testName);
            this.flowType = new SimpleStringProperty(flowType);
            this.nominalFlow = new SimpleStringProperty(nominalFlow);
            this.flow1 = new SimpleDoubleProperty(flow1);
            this.flow2 = new SimpleDoubleProperty(flow2);
            this.flow3 = new SimpleDoubleProperty(flow3);
            this.flow4 = new SimpleDoubleProperty(flow4);

            extractFromNominalFlow(nominalFlow, dimension);

        }

        public String getTestName() {
            return testName.get();
        }

        public String getFlowType() {
            return flowType.get();
        }

        public String getNominalFlow() {
            return nominalFlow.get();
        }

        public double getFlow1() {
            return flow1.get();
        }

        public double getFlow2() {
            return flow2.get();
        }

        public double getFlow3() {
            return flow3.get();
        }

        public double getFlow4() {
            return flow4.get();
        }

        public double getFlowRangeLeft() {
            return flowRangeLeft;
        }

        public double getFlowRangeRight() {
            return flowRangeRight;
        }

        public double getAcceptableFlowRangeLeft() {
            return acceptableFlowRangeLeft;
        }

        public double getAcceptableFlowRangeRight() {
            return acceptableFlowRangeRight;
        }

        private void extractFromNominalFlow(String nominalFlow, Dimension dimension) {

            String[] stringValues;

            switch (dimension) {

                case LIMIT:
                    stringValues = nominalFlow.split(" - ");
                    stringValues[1] = stringValues[1].substring(0, stringValues[1].indexOf(" "));
                    flowRangeLeft = convertDataToDouble(stringValues[0]);
                    flowRangeRight = convertDataToDouble(stringValues[1]);
                    break;
                case PLUS_OR_MINUS:
                    stringValues = nominalFlow.split(" \u00B1 ");
                    stringValues[1] = stringValues[1].substring(0, stringValues[1].indexOf(" "));
                    flowRangeLeft = convertDataToDouble(stringValues[0]) - convertDataToDouble(stringValues[1]);
                    flowRangeRight = convertDataToDouble(stringValues[0]) + convertDataToDouble(stringValues[1]);
                    break;

            }

            acceptableFlowRangeLeft -= flowRangeLeft * 0.03;
            acceptableFlowRangeRight += flowRangeRight * 0.03;

        }

    }

}
