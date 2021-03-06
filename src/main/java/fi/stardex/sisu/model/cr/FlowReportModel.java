package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.model.pump.FlowViewModel;
import fi.stardex.sisu.util.enums.Dimension;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.states.InjectorControllersState;
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
    private ObservableMap<InjectorTest, FlowResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());
    private ObservableMap<InjectorTest, FlowResult> densoResultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());
    private InjectorControllersState injectorControllersState;
    private InjectorTestModel injectorTestModel;
    private boolean densoResultRecovery;

    public FlowReportModel(FlowViewModel flowViewModel,
                           FlowValuesModel flowValuesModel,
                           DeliveryFlowRangeModel deliveryFlowRangeModel,
                           DeliveryFlowUnitsModel deliveryFlowUnitsModel,
                           BackFlowRangeModel backFlowRangeModel,
                           BackFlowUnitsModel backFlowUnitsModel,
                           InjectorControllersState injectorControllersState,
                           InjectorTestModel injectorTestModel) {

        this.flowViewModel = flowViewModel;
        this.flowValuesModel = flowValuesModel;
        this.deliveryFlowRangeModel = deliveryFlowRangeModel;
        this.deliveryFlowUnitsModel = deliveryFlowUnitsModel;
        this.backFlowRangeModel = backFlowRangeModel;
        this.backFlowUnitsModel = backFlowUnitsModel;
        this.injectorControllersState = injectorControllersState;
        this.injectorTestModel = injectorTestModel;
    }

    public List<Result> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }
    public ObservableMap<InjectorTest, FlowResult> getResultObservableMap() {
        return resultObservableMap;
    }
    public BooleanProperty resultMapChangedProperty() {
        return resultMapChanged;
    }
    public boolean isDensoResultRecovery() {
        return densoResultRecovery;
    }

    /** Defensive copy is made to prevent direct changes observation by test report controller TableView for Denso coding */
    public ObservableMap<InjectorTest, FlowResult> getDensoResultObservableMap() {
        ObservableMap<InjectorTest, FlowResult> densoResultMap = FXCollections.observableMap(new LinkedHashMap<>());
        densoResultObservableMap.forEach((k,v) -> densoResultMap.put(k, v.clone()));
        return densoResultMap;
    }

    public void clearResults(){
        resultObservableMap.clear();
        densoResultObservableMap.clear();
        resultMapChanged.setValue(true);
    }

    public void storeResult(){

        InjectorTest injectorTest = injectorTestModel.injectorTestProperty().get();
        Measurement measurement = injectorTest.getTestName().getMeasurement();
        String nominalFlow = null;

        switch (measurement) {

            case DELIVERY:
                nominalFlow = getNominalFlow(deliveryFlowRangeModel.deliveryFlowRangeProperty().get(), deliveryFlowUnitsModel.deliveryFlowUnitsProperty().get());
                break;
            case BACK_FLOW:
                nominalFlow = getNominalFlow(backFlowRangeModel.backFlowRangeProperty().get(), backFlowUnitsModel.backFlowUnitsProperty().get());
                break;
            case NO:
            case VISUAL:
                return;
        }

        Dimension dimension = flowViewModel.flowViewProperty().get();

        if (!resultObservableMap.containsKey(injectorTest)) {
            resultObservableMap.put(injectorTest, new FlowResult(injectorTest, nominalFlow,"-", "-", "-","-"));
        }
        resultObservableMap.get(injectorTest).setParameters(injectorTest, dimension, nominalFlow);
        resultMapChanged.setValue(true);
    }

    public void storeDensoCodingFlowResult(){
        InjectorTest injectorTest = injectorTestModel.injectorTestProperty().get();
        Measurement measurement = injectorTest.getTestName().getMeasurement();
        String nominalFlow = null;

        switch (measurement) {

            case DELIVERY:
                nominalFlow = getNominalFlow(deliveryFlowRangeModel.deliveryFlowRangeProperty().get(), deliveryFlowUnitsModel.deliveryFlowUnitsProperty().get());
                break;
            case BACK_FLOW:
                nominalFlow = getNominalFlow(backFlowRangeModel.backFlowRangeProperty().get(), backFlowUnitsModel.backFlowUnitsProperty().get());
                break;
            case NO:
            case VISUAL:
                return;
        }

        Dimension dimension = flowViewModel.flowViewProperty().get();

        if (!densoResultObservableMap.containsKey(injectorTest)) {
            densoResultObservableMap.put(injectorTest, new FlowResult(injectorTest, nominalFlow,"-", "-", "-","-"));
        }
        densoResultObservableMap.get(injectorTest).setParameters(injectorTest, dimension, nominalFlow);
    }

    public void setDensoCodingFlowReport() {
        densoResultRecovery = true;
        resultObservableMap.forEach((test, result) -> {

            FlowResult densoResult = densoResultObservableMap.get(test);
            result.flow1.setValue(densoResult.flow1.get());
            result.flow2.setValue(densoResult.flow2.get());
            result.flow3.setValue(densoResult.flow3.get());
            result.flow4.setValue(densoResult.flow4.get());
            result.setupDoubleFlowValues(injectorTestModel.injectorTestProperty().get().getTestName().getMeasurement());
        });

        resultMapChanged.setValue(true);
        densoResultRecovery = false;
    }

    public void deleteResult(InjectorTest injectorTest){
        resultObservableMap.remove(injectorTest);
        resultMapChanged.setValue(true);
    }

    private String getNominalFlow(String range, String flowUnit) {
        return range + " " + flowUnit;
    }

    public class FlowResult implements Result, Cloneable{

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
        String getFlow1() {
            return flow1.get();
        }
        String getFlow2() {
            return flow2.get();
        }
        String getFlow3() {
            return flow3.get();
        }
        String getFlow4() {
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
        public double getDoubleValue_1() {
            return flow1_double;
        }
        public double getDoubleValue_2() {
            return flow2_double;
        }
        public double getDoubleValue_3() {
            return flow3_double;
        }
        public double getDoubleValue_4() {
            return flow4_double;
        }

        @Override
        public double getRangeLeft() {
            return flowRangeLeft;
        }
        @Override
        public double getRangeRight() {
            return flowRangeRight;
        }
        @Override
        public double getAcceptableRangeLeft() {
            return acceptableFlowRangeLeft;
        }
        @Override
        public double getAcceptableRangeRight() {
            return acceptableFlowRangeRight;
        }

        FlowResult(InjectorTest injectorTest,
                   String nominalFlow,
                   String flow1,
                   String flow2,
                   String flow3,
                   String flow4) {

            this.injectorTest = new SimpleObjectProperty<>(injectorTest);
            this.flowType = new SimpleStringProperty(injectorTest.getTestName().getMeasurement().name());
            this.nominalFlow = new SimpleStringProperty(nominalFlow);
            this.flow1 = new SimpleStringProperty(flow1);
            this.flow2 = new SimpleStringProperty(flow2);
            this.flow3 = new SimpleStringProperty(flow3);
            this.flow4 = new SimpleStringProperty(flow4);
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
            acceptableFlowRangeLeft = flowRangeLeft - flowRangeLeft * 0.03;
            acceptableFlowRangeRight = flowRangeRight + flowRangeRight * 0.03;
        }

        private void setParameters(InjectorTest injectorTest, Dimension dimension, String nominalFlow) {

            Measurement measurement = injectorTest.getTestName().getMeasurement();
            List<StringProperty> newFlowsList = new ArrayList<>();
            switch (measurement) {
                case DELIVERY:
                    newFlowsList.addAll(flowValuesModel.getDeliveryList());
                    break;
                case BACK_FLOW:
                    newFlowsList.addAll(flowValuesModel.getBackFlowList());
            }
            List<String> actualFlowsList = getValueColumns();

            injectorControllersState.activeLedToggleButtonsListProperty().get().stream()
                    .map(toggleButton -> (Integer.parseInt(toggleButton.getText()) - 1))
                    .forEach(i -> actualFlowsList.set(i, newFlowsList.get(i).get()));

            this.flow1.setValue(getFlow(actualFlowsList.get(0)));
            this.flow2.setValue(getFlow(actualFlowsList.get(1)));
            this.flow3.setValue(getFlow(actualFlowsList.get(2)));
            this.flow4.setValue(getFlow(actualFlowsList.get(3)));

            setupDoubleFlowValues(injectorTest.getTestName().getMeasurement());
            extractFromNominalFlow(nominalFlow, dimension);
        }

        private String getFlow(String flow) {
            return (flow == null || flow.isEmpty()) ? "-" : flow;
        }

        @Override
        public String getMainColumn() {
            return getInjectorTest().getTestName().toString();
        }

        @Override
        public String getSubColumn1() {
            return flowType.get();
        }

        @Override
        public String getSubColumn2() {
            return nominalFlow.get();
        }

        @Override
        public List<String> getValueColumns() {
            return new ArrayList<>(Arrays.asList(getFlow1(), getFlow2(), getFlow3(), getFlow4()));
        }

        @Override
        public List<Double> getNumericDataColumns() {
            return new ArrayList<>(Arrays.asList(flow1_double, flow2_double, flow3_double, flow4_double));
        }

        @Override
        protected FlowResult clone() {
            FlowResult result = new FlowResult(this.getInjectorTest(),
                    this.nominalFlow.get(),
                    this.getFlow1(),
                    this.getFlow2(),
                    this.getFlow3(),
                    this.getFlow4());
            result.flowRangeLeft = this.flowRangeLeft;
            result.flowRangeRight = this.flowRangeRight;
            result.acceptableFlowRangeLeft = this.acceptableFlowRangeLeft;
            result.acceptableFlowRangeRight = this.acceptableFlowRangeRight;
            return result;
        }
    }
}
