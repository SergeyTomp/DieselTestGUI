package fi.stardex.sisu.store;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<InjectorTest, FlowTestResult> mapOfAddedTests = new HashMap<>();

    public FlowReport(FlowReportController flowReportController, MainSectionController mainSectionController,
                      FlowController flowController, SettingsController settingsController) {

        flowTableViewItems = flowReportController.getFlowTableView().getItems();

        testsSelectionModel = mainSectionController.getTestsSelectionModel();
        deliveryFlowComboBoxSelectionModel = flowController.getDeliveryFlowComboBox().getSelectionModel();
        backFlowComboBoxSelectionModel = flowController.getBackFlowComboBox().getSelectionModel();
        flowOutputDimensionsSelectionModel = settingsController.getFlowOutputDimensionsComboBox().getSelectionModel();

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

    }

    public void save() {

        InjectorTest injectorTest = testsSelectionModel.getSelectedItem();

        Measurement measurement = injectorTest.getTestName().getMeasurement();

        if (alreadyContainsTest(injectorTest))
            setNewFlowTestResult(injectorTest, measurement);
        else
            addNewFlowTestResult(injectorTest, measurement);


    }

    public void delete(int rowIndex) {

        FlowTestResult removedFlowTestResult = flowTableViewItems.remove(rowIndex);

        mapOfAddedTests.remove(removedFlowTestResult.getInjectorTest());

    }

    private void setNewFlowTestResult(InjectorTest injectorTest, Measurement measurement) {

        switch (measurement) {

            case DELIVERY:
                flowTableViewItems.set(flowTableViewItems.indexOf(mapOfAddedTests.get(injectorTest)), new FlowTestResult(injectorTest, measurement.name(),
                        getNominalFlow(deliveryRangeLabel.getText(), deliveryFlowComboBoxSelectionModel.getSelectedItem()),
                        getFlow(delivery1TextField.getText()), getFlow(delivery2TextField.getText()),
                        getFlow(delivery3TextField.getText()), getFlow(delivery4TextField.getText()), flowOutputDimensionsSelectionModel.getSelectedItem()));
                break;
            case BACK_FLOW:
                flowTableViewItems.set(flowTableViewItems.indexOf(mapOfAddedTests.get(injectorTest)), new FlowTestResult(injectorTest, measurement.name(),
                        getNominalFlow(backFlowRangeLabel.getText(), backFlowComboBoxSelectionModel.getSelectedItem()),
                        getFlow(backFlow1TextField.getText()), getFlow(backFlow2TextField.getText()),
                        getFlow(backFlow3TextField.getText()), getFlow(backFlow4TextField.getText()), flowOutputDimensionsSelectionModel.getSelectedItem()));
                break;

        }

    }

    private void addNewFlowTestResult(InjectorTest injectorTest, Measurement measurement) {

        switch (measurement) {

            case DELIVERY:
                flowTableViewItems.add(new FlowTestResult(injectorTest, measurement.name(),
                        getNominalFlow(deliveryRangeLabel.getText(), deliveryFlowComboBoxSelectionModel.getSelectedItem()),
                        getFlow(delivery1TextField.getText()), getFlow(delivery2TextField.getText()),
                        getFlow(delivery3TextField.getText()), getFlow(delivery4TextField.getText()), flowOutputDimensionsSelectionModel.getSelectedItem()));
                break;
            case BACK_FLOW:
                flowTableViewItems.add(new FlowTestResult(injectorTest, measurement.name(),
                        getNominalFlow(backFlowRangeLabel.getText(), backFlowComboBoxSelectionModel.getSelectedItem()),
                        getFlow(backFlow1TextField.getText()), getFlow(backFlow2TextField.getText()),
                        getFlow(backFlow3TextField.getText()), getFlow(backFlow4TextField.getText()), flowOutputDimensionsSelectionModel.getSelectedItem()));
                break;

        }

    }

    private boolean alreadyContainsTest(InjectorTest injectorTest) {

        return mapOfAddedTests.containsKey(injectorTest);

    }

    private String getFlow(String flow) {
        return (flow == null || flow.isEmpty()) ? "-" : flow;
    }

//    private double getFlow(String flow) {
//
//        if (flow != null)
//            return !flow.isEmpty() ? convertDataToDouble(flow) : 0d;
//        else
//            return 0d;
//
//    }

    private String getNominalFlow(String range, String flowUnit) {

        return range + " " + flowUnit;

    }

    // Не удалять геттеры так как они нужны для работы метода setupTableColumns() в классе FlowReportController
    public static class FlowTestResult {

        private final ObjectProperty<InjectorTest> injectorTest;

        private final StringProperty flowType;

        private final StringProperty nominalFlow;

        private final StringProperty flow1;

        private final StringProperty flow2;

        private final StringProperty flow3;

        private final StringProperty flow4;

        private double flow1_double;

        private double flow2_double;

        private double flow3_double;

        private double flow4_double;

        private double flowRangeLeft;

        private double flowRangeRight;

        private double acceptableFlowRangeLeft;

        private double acceptableFlowRangeRight;

        public FlowTestResult(InjectorTest injectorTest, String flowType, String nominalFlow, String flow1, String flow2, String flow3, String flow4, Dimension dimension) {

            this.injectorTest = new SimpleObjectProperty<>(injectorTest);
            this.flowType = new SimpleStringProperty(flowType);
            this.nominalFlow = new SimpleStringProperty(nominalFlow);
            this.flow1 = new SimpleStringProperty(flow1);
            this.flow2 = new SimpleStringProperty(flow2);
            this.flow3 = new SimpleStringProperty(flow3);
            this.flow4 = new SimpleStringProperty(flow4);

            setupDoubleFlowValues();

            extractFromNominalFlow(nominalFlow, dimension);

            mapOfAddedTests.put(injectorTest, this);

        }

        public InjectorTest getInjectorTest() {
            return injectorTest.get();
        }

        public String getFlowType() {
            return flowType.get();
        }

        public String getNominalFlow() {
            return nominalFlow.get();
        }

        public String getFlow1() {
            return flow1.get();
        }

        public String getFlow2() {
            return flow2.get();
        }

        public String getFlow3() {
            return flow3.get();
        }

        public String getFlow4() {
            return flow4.get();
        }

        public double getFlow1_double() {
            return flow1_double;
        }

        public double getFlow2_double() {
            return flow2_double;
        }

        public double getFlow3_double() {
            return flow3_double;
        }

        public double getFlow4_double() {
            return flow4_double;
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

        private void setupDoubleFlowValues() {

            flow1_double = flow1.get().equals("-") ? -1d : convertDataToDouble(flow1.get());
            flow2_double = flow2.get().equals("-") ? -1d : convertDataToDouble(flow2.get());
            flow3_double = flow3.get().equals("-") ? -1d : convertDataToDouble(flow3.get());
            flow4_double = flow4.get().equals("-") ? -1d : convertDataToDouble(flow4.get());

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
