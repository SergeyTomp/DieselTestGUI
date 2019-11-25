package fi.stardex.sisu.model.pump;

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
    private PumpTestModel pumpTestModel;
    private PumpModel pumpModel;
    private ObservableMap<PumpTest, PumpFlowResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());
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
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    /** Metod invoked from PumpBeakerController upon flowTextField changes.
    * It set values into this.deliveryValue and this.backFlowValue for reports generation.
    * This is necessary to have values in the report in case manual input into flowTextField of PumpBeakerController,
    * but not only in automatic crTestManager mode.
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
        String delivery = getFlow(deliveryValue.get());
        String backFlow = getFlow(backFlowValue.get());
        String deliveryRangeLabelText = deliveryFlowRangeModel.flowRangeLabelProperty().get() + " " + " " + deliveryFlowUnitsModel.flowUnitsProperty().get();
        String backFlowRangeLabelText = backFlowRangeModel.flowRangeLabelProperty().get() + " " + " " + backFlowUnitsModel.flowUnitsProperty().get();
        double deliveryLowLimit = deliveryFlowRangeModel.getScaledLowLimit();
        double deliveryTopLimit = deliveryFlowRangeModel.getScaledTopLimit();
        double backFlowLowLimit = backFlowRangeModel.getScaledLowLimit();
        double backFlowTopLimit = backFlowRangeModel.getScaledTopLimit();
        boolean noDeliveryTopLimit = deliveryFlowRangeModel.isNoTopLimit();
        boolean noBackFlowTopLimit = backFlowRangeModel.isNoTopLimit();

        resultObservableMap.put(pumpTest, new PumpFlowResult(
                pumpTest,
                motorSpeed,
                feedPressure,
                regulatorCurrent,
                pcvCurrent,
                delivery,
                backFlow,
                deliveryRangeLabelText,
                backFlowRangeLabelText,
                deliveryLowLimit,
                backFlowLowLimit,
                deliveryTopLimit,
                backFlowTopLimit,
                noDeliveryTopLimit,
                noBackFlowTopLimit));
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

    public class PumpFlowResult  implements Result {

        private final ObjectProperty<PumpTest> pumpTest;
        private final StringProperty rotatesPerMinute;
        private final StringProperty standPressure;
        private final StringProperty scvCurrent;
        private final StringProperty psvCurrent;
        private final StringProperty deliveryFlow;
        private final StringProperty backFlow;
        private ObjectProperty<TestPassed> testPassed;
        private final StringProperty deliveryFlowLabelText;
        private final StringProperty backFlowLabelText;

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

        public double getRangeLeft(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return deliveryFlowRangeLeft;
                case BACK_FLOW:
                    return backFlowRangeLeft;
                default:
                    return 0;
            }
        }

        public double getRangeRight(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return deliveryFlowRangeRight;
                case BACK_FLOW:
                    return backFlowRangeRight;
                default:
                    return 0;
            }
        }

        public double getAcceptableRangeLeft(Measurement flowType) {
            switch (flowType) {
                case DELIVERY:
                    return acceptableDeliveryFlowRangeLeft;
                case BACK_FLOW:
                    return acceptableBackFlowRangeLeft;
                default:
                    return 0;
            }
        }

        public double getAcceptableRangeRight(Measurement flowType) {
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
            return deliveryFlowLabelText;
        }
        public StringProperty nominalBackFlowProperty() {
            return backFlowLabelText;
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
                       String deliveryFlow,
                       String backFlow,
                       String deliveryFlowLabelText,
                       String backFlowLabelText,
                       double deliveryLowLimit,
                       double backFlowLowLimit,
                       double deliveryTopLimit,
                       double backFlowTopLimit,
                       boolean noDeliveryTopLimit,
                       boolean noBackFlowTopLimit) {

            this.pumpTest = new SimpleObjectProperty<>(pumpTest);
            this.rotatesPerMinute = new SimpleStringProperty(rotatesPerMinute);
            this.standPressure = new SimpleStringProperty(standPressure);
            this.scvCurrent = new SimpleStringProperty(scvCurrent);
            this.psvCurrent = new SimpleStringProperty(psvCurrent);
            this.deliveryFlow = new SimpleStringProperty(deliveryFlow);
            this.backFlow = new SimpleStringProperty(backFlow);
            this.testPassed = new SimpleObjectProperty<>(PASSED);
            this.deliveryFlowLabelText = new SimpleStringProperty(deliveryFlowLabelText);
            this.backFlowLabelText = new SimpleStringProperty(backFlowLabelText);

            deliveryFlow_double = deliveryFlow.equals("-") ? -99d : convertDataToDouble(deliveryFlow) / getDeliveryCoefficient();
            backFlow_double = backFlow.equals("-") ? -99d : convertDataToDouble(backFlow) / getBackFlowCoefficient();

            deliveryFlowRangeLeft = deliveryLowLimit;
            deliveryFlowRangeRight = !noDeliveryTopLimit ? deliveryTopLimit : deliveryTopLimit * 1000;
            backFlowRangeLeft = backFlowLowLimit;
            backFlowRangeRight = !noBackFlowTopLimit ? backFlowTopLimit : backFlowTopLimit * 1000;
            acceptableDeliveryFlowRangeLeft = deliveryFlowRangeLeft - deliveryFlowRangeLeft * 0.03;
            acceptableDeliveryFlowRangeRight = deliveryFlowRangeRight + deliveryFlowRangeRight * 0.03;
            acceptableBackFlowRangeLeft = backFlowRangeLeft - backFlowRangeLeft * 0.03;
            acceptableBackFlowRangeRight = backFlowRangeRight + backFlowRangeRight * 0.03;

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

        @Override
        public String getMainColumn() {
            return pumpTest.get().getPumpTestName().toString();
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

        @Override
        public double getRangeLeft() {
            return 0;
        }

        @Override
        public double getRangeRight() {
            return 0;
        }

        @Override
        public double getAcceptableRangeLeft() {
            return 0;
        }

        @Override
        public double getAcceptableRangeRight() {
            return 0;
        }

        @Override
        public List<Double> getNumericDataColumns() {
            return null;
        }
    }
}
