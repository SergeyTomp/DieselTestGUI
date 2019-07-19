package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.pdf.Result;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RLC_ReportModel {

    private ObservableMap<String, RlcResult> resultObservableMap = FXCollections.observableMap(new HashMap<>());
    private BooleanProperty newRlcAddedProperty = new SimpleBooleanProperty();

    public List<Result> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public BooleanProperty newRlcAddedProperty() {
        return newRlcAddedProperty;
    }

    public ObservableMap<String, RlcResult> getResultObservableMap() {
        return resultObservableMap;
    }

    public void storeResult(String unitsGauge1, String unitsGauge2, String titleGauge1, String titleGauge2, int ledNumber, Integer parameter1, Double parameter2){

        if(resultObservableMap.isEmpty()){

            resultObservableMap.put(titleGauge1, new RlcResult(titleGauge1, (unitsGauge1.equals("\u03BCH") ? "uH" : "uF")));
            resultObservableMap.put(titleGauge2, new RlcResult(titleGauge2, (unitsGauge2.equals("\u03A9") ? "Ohm" : "kOhm")));
        }
        resultObservableMap.get(titleGauge1).setParameterValue(ledNumber, parameter1.toString());
        resultObservableMap.get(titleGauge2).setParameterValue(ledNumber, parameter2.toString());
        newRlcAddedProperty.setValue(true);
    }

    public void clearResults(){
        resultObservableMap.clear();
        newRlcAddedProperty.setValue(true);
    }

    public static class RlcResult implements Result {

        private StringProperty parameter;
        private StringProperty units;
        private StringProperty channel_1;
        private StringProperty channel_2;
        private StringProperty channel_3;
        private StringProperty channel_4;
        private List<String> parameterValues;

        public StringProperty parameterProperty() {
            return parameter;
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

        RlcResult(String parameter, String units){

            this.parameter = new SimpleStringProperty(parameter);
            this.units = new SimpleStringProperty(units);
            parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-"));
            setParameterValues();

        }

        void setParameterValue(int number, String value) {
            parameterValues.set(number - 1, value);
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
            return parameter.getValue();
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
