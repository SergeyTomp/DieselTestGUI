package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class UisRlcModel {

    private int resultGauge1;
    private double resultGauge2;
    private int resultGauge3;
    private double resultGauge4;
    private ObservableMap<String, UisRlcResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());
    private BooleanProperty newRlcAddedProperty = new SimpleBooleanProperty();
    private BooleanProperty isMeasuring = new SimpleBooleanProperty();

    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;

    public void setResultGauge1(int resultGauge1) {
        this.resultGauge1 = resultGauge1;
    }
    public void setResultGauge2(double resultGauge2) {
        this.resultGauge2 = resultGauge2;
    }
    public void setResultGauge3(int resultGauge3) {
        this.resultGauge3 = resultGauge3;
    }
    public void setResultGauge4(double resultGauge4) {
        this.resultGauge4 = resultGauge4;
    }

    public List<Result> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }
    public ObservableMap<String, UisRlcResult> getResultObservableMap() {
        return resultObservableMap;
    }
    public BooleanProperty isMeasuringProperty() {
        return isMeasuring;
    }

    public BooleanProperty newRlcAddedProperty() {
        return newRlcAddedProperty;
    }

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }

    @PostConstruct
    public void init() {
        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> clearResults());
        mainSectionUisModel.manufacturerObjectProperty().addListener((observableValue, oldValue, newValue) -> clearResults());
    }

    public void storeResult() {

        InjectorType injectorType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorType();
        InjectorSubType injectorSubType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorSubType();
        boolean isDoubleCoil = injectorSubType == InjectorSubType.DOUBLE_COIL || injectorSubType == InjectorSubType.HPI;
        int ledNumber = Integer.parseInt(uisInjectorSectionModel.activeLedToggleButtonsListProperty().get().get(0).getText());

        String titleGauge1 = injectorType.getGauge1Title();
        String titleGauge2 = injectorType.getGauge2Title();
        String titleGauge3 = injectorType.getGauge1Title() + " 2";
        String titleGauge4 = injectorType.getGauge2Title() + " 2";
        String unitsGauge1 = injectorType.getGauge1Unit();
        String unitsGauge2 = injectorType.getGauge2Unit();
        String unitsGauge3 = injectorType.getGauge1Unit();
        String unitsGauge4 = injectorType.getGauge2Unit();

        if(resultObservableMap.isEmpty()){

            resultObservableMap.put(titleGauge1, new UisRlcResult(titleGauge1, (unitsGauge1.equals("\u03BCH") ? "uH" : "uF")));
            resultObservableMap.put(titleGauge2, new UisRlcResult(titleGauge2, (unitsGauge2.equals("\u03A9") ? "Ohm" : "kOhm")));

            if (isDoubleCoil) {
                resultObservableMap.put(titleGauge3, new UisRlcResult(titleGauge3, (unitsGauge3.equals("\u03BCH") ? "uH" : "uF")));
                resultObservableMap.put(titleGauge4, new UisRlcResult(titleGauge4, (unitsGauge4.equals("\u03A9") ? "Ohm" : "kOhm")));
            }
        }

        resultObservableMap.get(titleGauge1).setParameterValue(ledNumber, String.valueOf(resultGauge1));
        resultObservableMap.get(titleGauge2).setParameterValue(ledNumber, String.valueOf(resultGauge2));

        if (isDoubleCoil) {
            resultObservableMap.get(titleGauge3).setParameterValue(ledNumber, String.valueOf(resultGauge3));
            resultObservableMap.get(titleGauge4).setParameterValue(ledNumber, String.valueOf(resultGauge4));
        }
        newResultSignal();
    }

    public void clearResults(){
        resultObservableMap.clear();
        newResultSignal();
    }

    private void newResultSignal() {
        newRlcAddedProperty.setValue(true);
        newRlcAddedProperty.setValue(false);
    }

    public static class UisRlcResult implements Result {

        private StringProperty parameter;
        private StringProperty units;
        private StringProperty channel_1;
        private StringProperty channel_2;
        private StringProperty channel_3;
        private StringProperty channel_4;
        private StringProperty channel_5;
        private StringProperty channel_6;
        private StringProperty channel_7;
        private StringProperty channel_8;

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
        public StringProperty channel_5Property() {
            return channel_5;
        }
        public StringProperty channel_6Property() {
            return channel_6;
        }
        public StringProperty channel_7Property() {
            return channel_7;
        }
        public StringProperty channel_8Property() {
            return channel_8;
        }

        UisRlcResult(String parameter, String units){

            this.parameter = new SimpleStringProperty(parameter);
            this.units = new SimpleStringProperty(units);
            parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-","-", "-", "-", "-"));
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
            channel_5 = new SimpleStringProperty(parameterValues.get(4));
            channel_6 = new SimpleStringProperty(parameterValues.get(5));
            channel_7 = new SimpleStringProperty(parameterValues.get(6));
            channel_8 = new SimpleStringProperty(parameterValues.get(7));
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
