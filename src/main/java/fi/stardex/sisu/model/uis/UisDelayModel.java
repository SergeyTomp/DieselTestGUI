package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.pdf.Result;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UisDelayModel {
    
    private IntegerProperty addingTime = new SimpleIntegerProperty();
    private BooleanProperty newDelayAddedProperty = new SimpleBooleanProperty();
    private ObservableMap<String, UisDelayResult> resultObservableMap = FXCollections.observableMap(new HashMap<>());
    private String averageDelay;

    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setAverageDelay(String averageDelay) {
        this.averageDelay = averageDelay;
    }

    public ObservableMap<String, UisDelayResult> getResultObservableMap() {
        return resultObservableMap;
    }
    public BooleanProperty newDelayAddedProperty() {
        return newDelayAddedProperty;
    }
    public List<Result> getResultsList() {
        return new ArrayList<>(resultObservableMap.values());
    }
    public IntegerProperty addingTimeProperty() {
        return addingTime;
    }

    public void storeResult() {

        String testName = mainSectionUisModel.injectorTestProperty().get().getTestName().getName();
        int ledNumber = Integer.parseInt(uisInjectorSectionModel.activeLedToggleButtonsListProperty().get().get(0).getText());

        if (!resultObservableMap.containsKey(testName)) {
            resultObservableMap.put(testName, new UisDelayResult(testName));
        }
        resultObservableMap.get(testName).setParameterValue(ledNumber, averageDelay);
        newDelayAddedProperty.setValue(true);
        newDelayAddedProperty.setValue(false);
    }

    public void clearResults() {

        resultObservableMap.clear();
        newResultSignal();
    }

    private void newResultSignal() {
        newDelayAddedProperty.setValue(true);
        newDelayAddedProperty.setValue(false);
    }

    public class UisDelayResult implements Result {

        private StringProperty test;
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


        UisDelayResult(String test) {
            this.test = new SimpleStringProperty(test);
            this.units = new SimpleStringProperty("mkS");
            parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-", "-", "-", "-", "-"));
            setParameterValues();
        }

        void setParameterValue(int channelNumber, String averageDelay) {
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
            channel_5 = new SimpleStringProperty(parameterValues.get(4));
            channel_6 = new SimpleStringProperty(parameterValues.get(5));
            channel_7 = new SimpleStringProperty(parameterValues.get(6));
            channel_8 = new SimpleStringProperty(parameterValues.get(7));
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
