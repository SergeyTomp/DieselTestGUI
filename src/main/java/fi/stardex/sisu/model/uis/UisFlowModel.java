package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class UisFlowModel {

    private BooleanProperty resultMapChanged = new SimpleBooleanProperty();
    private ObservableMap<Test, UisFlowResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());

    public BooleanProperty resultMapChangedProperty() {
        return resultMapChanged;
    }
    public ObservableMap<Test, UisFlowResult> getResultObservableMap() {
        return resultObservableMap;
    }

    public void storeResult(){



        resultMapChanged.setValue(true);
        resultMapChanged.setValue(false);
    }

    public void clearResults(){
        resultObservableMap.clear();
        resultMapChanged.setValue(true);
        resultMapChanged.setValue(false);
    }

    public void deleteResult(Test test){
        resultObservableMap.remove(test);
        resultMapChanged.setValue(true);
        resultMapChanged.setValue(false);
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

        public UisFlowResult(Test test,
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
