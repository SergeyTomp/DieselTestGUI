package fi.stardex.sisu.model;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;

import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class FlowResultModel{

    private ObservableMap<InjectorTest, FlowResult> resultObservableMap = FXCollections.observableMap(new HashMap<>());

    public List<FlowResult> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public void clearResults(){
        resultObservableMap.clear();
    }

    public void storeResult(InjectorTest injectorTest,
                            String flowType,
                            String nominalFlow,
                            String flow1,
                            String flow2,
                            String flow3,
                            String flow4,
                            Dimension dimension,
                            Measurement measurement){

        resultObservableMap.put(injectorTest, new FlowResult(injectorTest, flowType, nominalFlow, flow1, flow2, flow3, flow4, dimension, measurement));
    }

    public void deleteResult(InjectorTest injectorTest){
        resultObservableMap.remove(injectorTest);
    }

    private class FlowResult implements Result{

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
                          String flowType,
                          String nominalFlow,
                          String flow1,
                          String flow2,
                          String flow3,
                          String flow4,
                          Dimension dimension,
                          Measurement measurement) {

            this.injectorTest = new SimpleObjectProperty<>(injectorTest);
            this.flowType = new SimpleStringProperty(flowType);
            this.nominalFlow = new SimpleStringProperty(nominalFlow);
            this.flow1 = new SimpleStringProperty(flow1);
            this.flow2 = new SimpleStringProperty(flow2);
            this.flow3 = new SimpleStringProperty(flow3);
            this.flow4 = new SimpleStringProperty(flow4);

            setupDoubleFlowValues(measurement);

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

            acceptableFlowRangeLeft -= flowRangeLeft * 0.03;
            acceptableFlowRangeRight += flowRangeRight * 0.03;

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

