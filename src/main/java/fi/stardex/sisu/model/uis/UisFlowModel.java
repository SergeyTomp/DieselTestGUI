package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static fi.stardex.sisu.util.FlowUnitObtainer.getUisDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class UisFlowModel {

    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private I18N i18N;
    private BooleanProperty resultMapChanged = new SimpleBooleanProperty();
    private ObservableMap<Test, UisFlowResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());
    private StringProperty flowRangeLabelProperty = new SimpleStringProperty(""); //just the label text above beaker, no real flow range information for calculations.
    private StringProperty flowUnitsProperty = new SimpleStringProperty("");
    private StringProperty flowValueProperty = new SimpleStringProperty();

    private double scaledLeftLimit;
    private double scaledRightLimit;

    public void setScaledLeftLimit(double scaledLeftLimit) {
        this.scaledLeftLimit = scaledLeftLimit;
    }
    public void setScaledRightLimit(double scaledRightLimit) {
        this.scaledRightLimit = scaledRightLimit;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public BooleanProperty resultMapChangedProperty() {
        return resultMapChanged;
    }
    public ObservableMap<Test, UisFlowResult> getResultObservableMap() {
        return resultObservableMap;
    }
    public StringProperty getFlowRangeLabelProperty() {
        return flowRangeLabelProperty;
    }
    public StringProperty getFlowUnitsProperty() {
        return flowUnitsProperty;
    }
    public StringProperty getFlowValueProperty() {
        return flowValueProperty;
    }

    @PostConstruct
    public void init() {
        mainSectionUisModel.getStoreButton().setOnAction(event -> storeResult());
        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> clearResults());
    }

    public void storeResult(){

        if (flowRangeLabelProperty.get().isEmpty()) {return;}

        Test test = mainSectionUisModel.injectorTestProperty().get();

        String nominalFlow = flowRangeLabelProperty.get() + " " + flowUnitsProperty.get();

        if (!resultObservableMap.containsKey(test)) {

            resultObservableMap.put(test, new UisFlowResult(test, nominalFlow,"-", "-", "-","-","-", "-", "-","-"));
        }
        resultObservableMap.get(test).setParameters(flowValueProperty.get());
        resultMapChangeSignal();
    }

    public void clearResults(){
        resultObservableMap.clear();
        resultMapChangeSignal();
    }

    public void deleteResult(Test test){
        resultObservableMap.remove(test);
        resultMapChangeSignal();
    }

    private void resultMapChangeSignal() {
        resultMapChanged.setValue(true);
        resultMapChanged.setValue(false);
    }

    public List<UisFlowResult> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public class UisFlowResult implements Result{

        private final ObjectProperty<Test> injectorTest;
        private final StringProperty nominalFlow;
        private final StringProperty flow1;
        private final StringProperty flow2;
        private final StringProperty flow3;
        private final StringProperty flow4;
        private final StringProperty flow5;
        private final StringProperty flow6;
        private final StringProperty flow7;
        private final StringProperty flow8;

        private double flow_1double;
        private double flow_2double;
        private double flow_3double;
        private double flow_4double;
        private double flow_5double;
        private double flow_6double;
        private double flow_7double;
        private double flow_8double;
        private double flowRangeLeft;
        private double flowRangeRight;
        private double acceptableFlowRangeLeft;
        private double acceptableFlowRangeRight;

        public ObjectProperty<Test> injectorTestProperty() {
            return injectorTest;
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
        public StringProperty flow5Property() {
            return flow5;
        }
        public StringProperty flow6Property() {
            return flow6;
        }
        public StringProperty flow7Property() {
            return flow7;
        }
        public StringProperty flow8Property() {
            return flow8;
        }

        public double getFlow_1double() {
            return flow_1double;
        }
        public double getFlow_2double() {
            return flow_2double;
        }
        public double getFlow_3double() {
            return flow_3double;
        }
        public double getFlow_4double() {
            return flow_4double;
        }
        public double getFlow_5double() {
            return flow_5double;
        }
        public double getFlow_6double() {
            return flow_6double;
        }
        public double getFlow_7double() {
            return flow_7double;
        }
        public double getFlow_8double() {
            return flow_8double;
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

        UisFlowResult(Test test,
                             String nominalFlow,
                             String flow1,
                             String flow2,
                             String flow3,
                             String flow4,
                             String flow5,
                             String flow6,
                             String flow7,
                             String flow8) {
            this.injectorTest = new SimpleObjectProperty<>(test);
            this.nominalFlow = new SimpleStringProperty(nominalFlow);
            this.flow1 = new SimpleStringProperty(flow1);
            this.flow2 = new SimpleStringProperty(flow2);
            this.flow3 = new SimpleStringProperty(flow3);
            this.flow4 = new SimpleStringProperty(flow4);
            this.flow5 = new SimpleStringProperty(flow5);
            this.flow6 = new SimpleStringProperty(flow6);
            this.flow7 = new SimpleStringProperty(flow7);
            this.flow8 = new SimpleStringProperty(flow8);
        }

        private void setParameters(String value) {

            List<String> actualFlowsList = getValueColumns();
            double uisDeliveryCoefficient = getUisDeliveryCoefficient();

            /**Universal approach of setting new values to existing actualFlowsList,could be used in both cases:
             * - single channel and multi channel modes if the last one will be activated in future*/
            uisInjectorSectionModel.activeLedToggleButtonsListProperty().get().stream()
                    .map(toggleButton -> (Integer.parseInt(toggleButton.getText()) - 1))
                    .forEach(i -> actualFlowsList.set(i, value));

            this.flow1.setValue(getFlow(actualFlowsList.get(0)));
            this.flow2.setValue(getFlow(actualFlowsList.get(1)));
            this.flow3.setValue(getFlow(actualFlowsList.get(2)));
            this.flow4.setValue(getFlow(actualFlowsList.get(3)));
            this.flow5.setValue(getFlow(actualFlowsList.get(4)));
            this.flow6.setValue(getFlow(actualFlowsList.get(5)));
            this.flow7.setValue(getFlow(actualFlowsList.get(6)));
            this.flow8.setValue(getFlow(actualFlowsList.get(7)));

            flow_1double = flow1.get().equals("-") ? -99d : convertDataToDouble(flow1.get()) / uisDeliveryCoefficient;
            flow_2double = flow2.get().equals("-") ? -99d : convertDataToDouble(flow2.get()) / uisDeliveryCoefficient;
            flow_3double = flow3.get().equals("-") ? -99d : convertDataToDouble(flow3.get()) / uisDeliveryCoefficient;
            flow_4double = flow4.get().equals("-") ? -99d : convertDataToDouble(flow4.get()) / uisDeliveryCoefficient;
            flow_5double = flow5.get().equals("-") ? -99d : convertDataToDouble(flow5.get()) / uisDeliveryCoefficient;
            flow_6double = flow6.get().equals("-") ? -99d : convertDataToDouble(flow6.get()) / uisDeliveryCoefficient;
            flow_7double = flow7.get().equals("-") ? -99d : convertDataToDouble(flow7.get()) / uisDeliveryCoefficient;
            flow_8double = flow8.get().equals("-") ? -99d : convertDataToDouble(flow8.get()) / uisDeliveryCoefficient;

            System.err.println(flow_1double + " " + flow_2double);

            flowRangeLeft = scaledLeftLimit;
            flowRangeRight = scaledRightLimit;
            acceptableFlowRangeLeft = scaledLeftLimit - scaledLeftLimit * 0.03;
            acceptableFlowRangeRight = scaledRightLimit + scaledRightLimit * 0.03;
        }

        private String getFlow(String flow) {
            return (flow == null || flow.isEmpty()) ? "-" : flow;
        }

        @Override
        public String getMainColumn() {
            return injectorTest.get().getTestName().getName();
        }

        @Override
        public String getSubColumn1() {
            return nominalFlow.get();
        }

        @Override
        public String getSubColumn2() {
            return null;
        }

        @Override
        public List<String> getValueColumns() {
            return new ArrayList<>(Arrays.asList(flow1.get(), flow2.get(), flow3.get(), flow4.get(), flow5.get(), flow6.get(), flow7.get(), flow8.get()));
        }
    }
}
