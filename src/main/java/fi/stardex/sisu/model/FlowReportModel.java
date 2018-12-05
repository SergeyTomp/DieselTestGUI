package fi.stardex.sisu.model;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.states.FlowViewModel;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;

import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class FlowReportModel {

    private FlowValuesModel flowValuesModel;
    private BackFlowRangeModel backFlowRangeModel;
    private BackFlowUnitsModel backFlowUnitsModel;
    private DeliveryFlowRangeModel deliveryFlowRangeModel;
    private DeliveryFlowUnitsModel deliveryFlowUnitsModel;
    private FlowViewModel flowViewModel;
    private BooleanProperty resultMapChanged = new SimpleBooleanProperty();
    private ObservableMap<InjectorTest, FlowResult> resultObservableMap = FXCollections.observableMap(new HashMap<>());
    private final double DEVIATION = 0.03;

    public List<FlowResult> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public ObservableMap<InjectorTest, FlowResult> getResultObservableMap() {
        return resultObservableMap;
    }

    public BooleanProperty resultMapChangedProperty() {
        return resultMapChanged;
    }

    public void setFlowValuesModel(FlowValuesModel flowValuesModel) {
        this.flowValuesModel = flowValuesModel;
    }

    public void setBackFlowRangeModel(BackFlowRangeModel backFlowRangeModel) {
        this.backFlowRangeModel = backFlowRangeModel;
    }

    public void setBackFlowUnitsModel(BackFlowUnitsModel backFlowUnitsModel) {
        this.backFlowUnitsModel = backFlowUnitsModel;
    }

    public void setDeliveryFlowRangeModel(DeliveryFlowRangeModel deliveryFlowRangeModel) {
        this.deliveryFlowRangeModel = deliveryFlowRangeModel;
    }

    public void setDeliveryFlowUnitsModel(DeliveryFlowUnitsModel deliveryFlowUnitsModel) {
        this.deliveryFlowUnitsModel = deliveryFlowUnitsModel;
    }

    public void setFlowViewModel(FlowViewModel flowViewModel) {
        this.flowViewModel = flowViewModel;
    }

    public void clearResults(){
        resultObservableMap.clear();
        resultMapChanged.setValue(true);
    }

    public void storeResult(InjectorTest injectorTest){

        Measurement measurement = injectorTest.getTestName().getMeasurement();
        switch (measurement){
            case DELIVERY:
                resultObservableMap.put(injectorTest, new FlowResult(
                    injectorTest,
                    getNominalFlow(deliveryFlowRangeModel.deliveryFlowRangeProperty().get(), deliveryFlowUnitsModel.deliveryFlowUnitsProperty().get()),
                    getFlow(flowValuesModel.delivery1Property().get()),
                    getFlow(flowValuesModel.delivery2Property().get()),
                    getFlow(flowValuesModel.delivery3Property().get()),
                    getFlow(flowValuesModel.delivery4Property().get()),
                    flowViewModel.flowViewProperty().get()));
                break;
            case BACK_FLOW:
                resultObservableMap.put(injectorTest, new FlowResult(
                    injectorTest,
                    getNominalFlow(backFlowRangeModel.backFlowRangeProperty().get(), backFlowUnitsModel.backFlowUnitsProperty().get()),
                    getFlow(flowValuesModel.backFlow1Property().get()),
                    getFlow(flowValuesModel.backFlow2Property().get()),
                    getFlow(flowValuesModel.backFlow3Property().get()),
                    getFlow(flowValuesModel.backFlow4Property().get()),
                    flowViewModel.flowViewProperty().get()));
                break;
        }
        resultMapChanged.setValue(true);
    }

    public void deleteResult(InjectorTest injectorTest){
        resultObservableMap.remove(injectorTest);
        resultMapChanged.setValue(true);
    }

    private String getNominalFlow(String range, String flowUnit) {
        return range + " " + flowUnit;
    }

    private String getFlow(String flow) {
        return (flow == null || flow.isEmpty()) ? "-" : flow;
    }

    public class FlowResult implements Result{

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

        public ObjectProperty<InjectorTest> injectorTestProperty() {
            return injectorTest;
        }

        public StringProperty flowTypeProperty() {
            return flowType;
        }

        public StringProperty nominalFlowProperty() {
            return nominalFlow;
        }

        public StringProperty flow1Property() {
            return flow1;
        }

        public StringProperty flow2Property() {
            return flow2;
        }

        public StringProperty flow3Property() {
            return flow3;
        }

        public StringProperty flow4Property() {
            return flow4;
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

        public FlowResult(InjectorTest injectorTest,
                          String nominalFlow,
                          String flow1,
                          String flow2,
                          String flow3,
                          String flow4,
                          Dimension dimension) {

            this.injectorTest = new SimpleObjectProperty<>(injectorTest);
            this.flowType = new SimpleStringProperty(injectorTest.getTestName().getMeasurement().name());
            this.nominalFlow = new SimpleStringProperty(nominalFlow);
            this.flow1 = new SimpleStringProperty(flow1);
            this.flow2 = new SimpleStringProperty(flow2);
            this.flow3 = new SimpleStringProperty(flow3);
            this.flow4 = new SimpleStringProperty(flow4);

            setupDoubleFlowValues(injectorTest.getTestName().getMeasurement());
            extractFromNominalFlow(nominalFlow, dimension);
        }

        private void setupDoubleFlowValues(Measurement measurement) {

            switch (measurement) {

                case DELIVERY:
                    flow1_double = flow1.get().equals("-") ? -99d : convertDataToDouble(flow1.get()) / getDeliveryCoefficient();
                    flow2_double = flow2.get().equals("-") ? -99d : convertDataToDouble(flow2.get()) / getDeliveryCoefficient();
                    flow3_double = flow3.get().equals("-") ? -99d : convertDataToDouble(flow3.get()) / getDeliveryCoefficient();
                    flow4_double = flow4.get().equals("-") ? -99d : convertDataToDouble(flow4.get()) / getDeliveryCoefficient();
                    break;
                case BACK_FLOW:
                    flow1_double = flow1.get().equals("-") ? -99d : convertDataToDouble(flow1.get()) / getBackFlowCoefficient();
                    flow2_double = flow2.get().equals("-") ? -99d : convertDataToDouble(flow2.get()) / getBackFlowCoefficient();
                    flow3_double = flow3.get().equals("-") ? -99d : convertDataToDouble(flow3.get()) / getBackFlowCoefficient();
                    flow4_double = flow4.get().equals("-") ? -99d : convertDataToDouble(flow4.get()) / getBackFlowCoefficient();
                    break;
            }
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
            acceptableFlowRangeLeft = flowRangeLeft - flowRangeLeft * DEVIATION;
            acceptableFlowRangeRight = flowRangeRight + flowRangeRight * DEVIATION;
        }

        @Override
        public String getMainColumn() {
            return getInjectorTest().getTestName().toString();
        }

        @Override
        public String getSubColumn1() {
            return getFlowType();
        }

        @Override
        public String getSubColumn2() {
            return getNominalFlow();
        }

        @Override
        public List<String> getValueColumns() {
            return new ArrayList<>(Arrays.asList(getFlow1(), getFlow2(), getFlow3(), getFlow4()));
        }
    }
}

