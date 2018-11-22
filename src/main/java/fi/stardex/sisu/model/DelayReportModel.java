package fi.stardex.sisu.model;

import fi.stardex.sisu.pdf.Result;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DelayReportModel {

    private ObservableMap<String, DelayResult> resultObservableMap = FXCollections.observableMap(new HashMap<>());

    private BooleanProperty newDelayAdded = new SimpleBooleanProperty();

    public List<Result> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public ObservableMap<String, DelayResult> getResultObservableMap() {
        return resultObservableMap;
    }

    public BooleanProperty newResultAddedProperty() {
        return newDelayAdded;
    }

    public void storeResult(int channelNumber, String injectorTestName, String averageDelay){

        if(!resultObservableMap.containsKey(injectorTestName)){
            resultObservableMap.put(injectorTestName, new DelayResult(injectorTestName));
        }
        resultObservableMap.get(injectorTestName).setParameterValue(channelNumber, averageDelay);
        newDelayAdded.setValue(true);
    }

    public void clearResults(){

        resultObservableMap.clear();
        newDelayAdded.setValue(true);
    }

    public class DelayResult implements Result {

        StringProperty test;
        StringProperty units;
        StringProperty channel_1;
        StringProperty channel_2;
        StringProperty channel_3;
        StringProperty channel_4;
        private List<String> parameterValues;

        public StringProperty testProperty() {
            return test;
        }

        public StringProperty unitsProperty() {
            return units;
        }

        public StringProperty channel_1Property() {
            return channel_1;
        }

        public StringProperty channel_2Property() {
            return channel_2;
        }

        public StringProperty channel_3Property() {
            return channel_3;
        }

        public StringProperty channel_4Property() {
            return channel_4;
        }

        public DelayResult(String test) {
            this.test = new SimpleStringProperty(test);
            this.units = new SimpleStringProperty("mkS");
            parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-"));
            setParameterValues();
        }

        public void setParameterValue(int channelNumber, String averageDelay) {
            if (averageDelay.equals("")) {
                averageDelay = "Incorrect result!";
            }
            parameterValues.set(channelNumber - 1, averageDelay);
            setParameterValues();
        }

        private void setParameterValues() {
            channel_1 = new SimpleStringProperty(parameterValues.get(0));
            channel_2 = new SimpleStringProperty(parameterValues.get(1));
            channel_3 = new SimpleStringProperty(parameterValues.get(2));
            channel_4 = new SimpleStringProperty(parameterValues.get(3));
        }

        @Override
        public String getMainColumn() {
            return test.getValue();
        }

        @Override
        public String getSubColumn1() {
            return units.getValue();
        }

        @Override
        public String getSubColumn2() {
            return null;
        }

        @Override
        public List<String> getValueColumns() {
            return parameterValues;
        }
    }
}
