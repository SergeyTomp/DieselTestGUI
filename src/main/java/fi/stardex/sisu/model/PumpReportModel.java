package fi.stardex.sisu.model;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.enums.TestPassed;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.enums.TestPassed.FAIL;
import static fi.stardex.sisu.util.enums.TestPassed.PASSED;

public class PumpReportModel {

    private FlowRangeModel deliveryFlowRangeModel;
    private FlowRangeModel backFlowRangeModel;
    private FlowUnitsModel deliveryFlowUnitsModel;
    private FlowUnitsModel backFlowUnitsModel;
    private FlowViewModel flowViewModel;
    private PumpTestModel pumpTestModel;
    private PumpModel pumpModel;
    private ObservableMap<PumpTest, PumpFlowResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());
    private BooleanProperty resultMapChanged = new SimpleBooleanProperty();
    private StringProperty deliveryValue = new SimpleStringProperty();
    private StringProperty backFlowValue = new SimpleStringProperty();

    public void setDeliveryFlowRangeModel(FlowRangeModel deliveryFlowRangeModel) {
        this.deliveryFlowRangeModel = deliveryFlowRangeModel;
    }
    public void setBackFlowRangeModel(FlowRangeModel backFlowRangeModel) {
        this.backFlowRangeModel = backFlowRangeModel;
    }
    public void setDeliveryFlowUnitsModel(FlowUnitsModel deliveryFlowUnitsModel) {
        this.deliveryFlowUnitsModel = deliveryFlowUnitsModel;
    }
    public void setBackFlowUnitsModel(FlowUnitsModel backFlowUnitsModel) {
        this.backFlowUnitsModel = backFlowUnitsModel;
    }
    public void setFlowViewModel(FlowViewModel flowViewModel) {
        this.flowViewModel = flowViewModel;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }



    /** Metod invoked from PumpBeakerController upon flowTextField changes.
    * It set values into this.deliveryValue and this.backFlowValue for reports generation.
    * This is necessary to have values in the report in case manual input into flowTextField of PumpBeakerController,
    * but not only in automatic measurements mode.
    * Otherwise it would have been necessary to have PumpFlowValuesModel to be controlled by two different controllers.*/
    public void setFlowValues(String value, BeakerType flowType) {

        switch (flowType) {
            case DELIVERY:
                deliveryValue.setValue(value);
                break;
            case BACKFLOW:
                backFlowValue.setValue(value);
                break;
        }
    }

    public void storeResult(){

        PumpTest pumpTest = pumpTestModel.pumpTestProperty().get();
        String motorSpeed = String.valueOf(pumpTestModel.pumpTestProperty().get().getMotorSpeed());
        String feedPressure = String.valueOf(pumpModel.pumpProperty().get().getFeedPressure());
        String regulatorCurrent = String.valueOf(Optional.ofNullable(pumpTestModel.pumpTestProperty().get().getRegulatorCurrent()).orElse(0d));
        String pcvCurrent = String.valueOf(Optional.ofNullable(pumpTestModel.pumpTestProperty().get().getPcvCurrent()).orElse(0));
        String deliveryRange = getRange(deliveryFlowRangeModel.flowRangeProperty().get());
        String delivery = getFlow(deliveryValue.get());
        String backFlowRange = getRange(backFlowRangeModel.flowRangeProperty().get());
        String backFlow = getFlow(backFlowValue.get());
        Dimension dimension = flowViewModel.flowViewProperty().get();

        resultObservableMap.put(pumpTest, new PumpFlowResult(
                pumpTest,
                motorSpeed,
                feedPressure,
                regulatorCurrent,
                pcvCurrent,
                deliveryRange,
                delivery,
                backFlowRange,
                backFlow,
                dimension));
    }

    public void deleteResult(PumpTest pumpTest){

        resultObservableMap.remove(pumpTest);
    }

    public void clearResults(){

        resultObservableMap.clear();
    }

    public List<PumpFlowResult> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public ObservableMap<PumpTest, PumpFlowResult> getResultObservableMap() {
        return resultObservableMap;
    }

    private String getFlow(String flow) {
        return (flow == null || flow.isEmpty()) || convertDataToDouble(flow) == 0d ? "-" : flow;
    }

    private String getRange(String range) {
        return range == null || range.isEmpty() ? "-" : range;
    }

    public class PumpFlowResult  implements Result {

        private final ObjectProperty<PumpTest> pumpTest;
        private final StringProperty rotatesPerMinute;
        private final StringProperty standPressure;
        private final StringProperty scvCurrent;
        private final StringProperty psvCurrent;
        private final StringProperty nominalDeliveryFlow;
        private final StringProperty nominalBackFlow;
        private final StringProperty deliveryFlow;
        private final StringProperty backFlow;
        private ObjectProperty<TestPassed> testPassed;

        private int rotatesPerMinute_int;
        private int standPressure_int;
        private double scvCurrent_double;
        private double psvCurrent_double;
        private double deliveryFlow_double;
        private double backFlow_double;
        private double deliveryFlowRangeLeft;
        private double deliveryFlowRangeRight;
        private double backFlowRangeLeft;
        private double backFlowRangeRight;
        private double acceptableDeliveryFlowRangeLeft;
        private double acceptableDeliveryFlowRangeRight;
        private double acceptableBackFlowRangeLeft;
        private double acceptableBackFlowRangeRight;

        public PumpTest getPumpTest() {
            return pumpTest.get();
        }

        public double getFlowRangeLeft(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return deliveryFlowRangeLeft;
                case BACK_FLOW:
                    return backFlowRangeLeft;
                default:
                    return 0;
            }
        }

        public double getFlowRangeRight(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return deliveryFlowRangeRight;
                case BACK_FLOW:
                    return backFlowRangeRight;
                default:
                    return 0;
            }
        }

        public double getAcceptableFlowRangeLeft(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return acceptableDeliveryFlowRangeLeft;
                case BACK_FLOW:
                    return acceptableBackFlowRangeLeft;
                default:
                    return 0;
            }
        }

        public double getAcceptableFlowRangeRight(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return acceptableDeliveryFlowRangeRight;
                case BACK_FLOW:
                    return acceptableBackFlowRangeRight;
                default:
                    return 0;
            }
        }

        public double getDelivery_double() {
            return deliveryFlow.get().equals("-") ? 0d : convertDataToDouble(deliveryFlow.get());
        }

        public double getBackFlow_double() {
            return backFlow.get().equals("-") ? 0d : convertDataToDouble(backFlow.get());
        }

        public ObjectProperty<PumpTest> pumpTestProperty() {
            return pumpTest;
        }
        public StringProperty rotatesPerMinuteProperty() {
            return rotatesPerMinute;
        }
        public StringProperty standPressureProperty() {
            return standPressure;
        }
        public StringProperty scvCurrentProperty() {
            return scvCurrent;
        }
        public StringProperty psvCurrentProperty() {
            return psvCurrent;
        }
        public StringProperty nominalDeliveryFlowProperty() {
            return nominalDeliveryFlow;
        }
        public StringProperty nominalBackFlowProperty() {
            return nominalBackFlow;
        }
        public StringProperty deliveryFlowProperty() {
            return deliveryFlow;
        }
        public StringProperty backFlowProperty() {
            return backFlow;
        }
        public ObjectProperty<TestPassed> testPassedProperty() {
            return testPassed;
        }

        PumpFlowResult(PumpTest pumpTest,
                       String rotatesPerMinute,
                       String standPressure,
                       String scvCurrent,
                       String psvCurrent,
                       String nominalDeliveryFlow,
                       String deliveryFlow,
                       String nominalBackFlow,
                       String backFlow,
                       Dimension dimension) {

            this.pumpTest = new SimpleObjectProperty<>(pumpTest);
            this.rotatesPerMinute = new SimpleStringProperty(rotatesPerMinute);
            this.standPressure = new SimpleStringProperty(standPressure);
            this.scvCurrent = new SimpleStringProperty(scvCurrent);
            this.psvCurrent = new SimpleStringProperty(psvCurrent);
            this.nominalDeliveryFlow = new SimpleStringProperty(getNominalFlow(nominalDeliveryFlow, deliveryFlowUnitsModel.flowUnitsProperty().get()));
            this.nominalBackFlow = new SimpleStringProperty(getNominalFlow(nominalBackFlow, backFlowUnitsModel.flowUnitsProperty().get()));
            this.deliveryFlow = new SimpleStringProperty(deliveryFlow);
            this.backFlow = new SimpleStringProperty(backFlow);
            this.testPassed = new SimpleObjectProperty<>(PASSED);

            deliveryFlow_double = deliveryFlow.equals("-") ? -99d : convertDataToDouble(deliveryFlow) / getDeliveryCoefficient();
            backFlow_double = backFlow.equals("-") ? -99d : convertDataToDouble(backFlow) / getBackFlowCoefficient();

            double[] limits = extractFromNominalFlow((this.nominalDeliveryFlow).get(), dimension);
            deliveryFlowRangeLeft = limits[0];
            deliveryFlowRangeRight = limits[1];
            acceptableDeliveryFlowRangeLeft = limits[0] - limits[0] * 0.03;
            acceptableDeliveryFlowRangeRight = limits[1] + limits[1] * 0.03;

            limits = extractFromNominalFlow((this.nominalBackFlow).get(), dimension);
            backFlowRangeLeft = limits[0];
            backFlowRangeRight = limits[1];
            acceptableBackFlowRangeLeft = limits[0] - limits[0] * 0.03;
            acceptableBackFlowRangeRight = limits[1] + limits[1] * 0.03;

            if (!checkTestPassed()) {
                testPassed.setValue(FAIL);
            }
        }

        private boolean checkTestPassed() {

            boolean deliveryPassed, backFlowPassed;

            deliveryPassed = deliveryFlow_double < 0
                    || deliveryFlow_double >= acceptableDeliveryFlowRangeLeft && deliveryFlow_double <= acceptableDeliveryFlowRangeRight
                    || acceptableDeliveryFlowRangeLeft == 0 && acceptableDeliveryFlowRangeRight == 0;
            backFlowPassed = backFlow_double < 0
                    || backFlow_double >= acceptableBackFlowRangeLeft && backFlow_double <= acceptableBackFlowRangeRight
                    || acceptableBackFlowRangeLeft == 0 && acceptableBackFlowRangeRight == 0;
            return (deliveryPassed && backFlowPassed);
        }

        private String getNominalFlow(String range, String flowUnit) {
            return range + " " + flowUnit;
        }

            private double[] extractFromNominalFlow(String nominalFlow, Dimension dimension) {

            String[] stringValues;
            double[] limits = new double[2];
            double leftLimit = 0;
            double rightLimit = 0;

            if(nominalFlow.isEmpty() || nominalFlow.startsWith("-")){
                return limits;
            }
            else{
                switch (dimension) {

                    case LIMIT:
                        stringValues = nominalFlow.split(" - ");
                        stringValues[1] = stringValues[1].substring(0, stringValues[1].indexOf(" "));
                        leftLimit = convertDataToDouble(stringValues[0]);
                        rightLimit = convertDataToDouble(stringValues[1]);
                        break;
                    case PLUS_OR_MINUS:
                        stringValues = nominalFlow.split(" \u00B1 ");
                        stringValues[1] = stringValues[1].substring(0, stringValues[1].indexOf(" "));
                        leftLimit = convertDataToDouble(stringValues[0]) - convertDataToDouble(stringValues[1]);
                        rightLimit = convertDataToDouble(stringValues[0]) + convertDataToDouble(stringValues[1]);
                        break;
                }
                limits[0] = leftLimit;
                limits[1] = rightLimit;
                return limits;
            }
        }

        @Override
        public String getMainColumn() {
            return pumpTest.getName();
        }

        @Override
        public String getSubColumn1() {
            return rotatesPerMinute.get();
        }

        @Override
        public String getSubColumn2() {
            return standPressure.get();
        }

        public String getSubColumn3() {
            return scvCurrent.get();
        }

        public String getSubColumn4() {
            return psvCurrent.get();
        }

        @Override
        public List<String> getValueColumns() {
            return null;
        }
    }
}
