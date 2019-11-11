package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class UisBipModel {

    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private BooleanProperty resultMapChanged = new SimpleBooleanProperty();
    private ObservableMap<Test, UisBipResult> resultObservableMap = FXCollections.observableMap(new LinkedHashMap<>());

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }

    public BooleanProperty resultMapChangedProperty() {
        return resultMapChanged;
    }
    public ObservableMap<Test, UisBipResult> getResultObservableMap() {
        return resultObservableMap;
    }

    @PostConstruct
    public void init() {
        uisInjectorSectionModel.getSaveBipButton().addEventHandler(ActionEvent.ACTION, event -> storeResult());
        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> clearResults());
    }

    public void storeResult(){

        Test test = mainSectionUisModel.injectorTestProperty().get();
        String nominalBip = uisInjectorSectionModel.bipRangeLabelProperty().get() + " " + "\u00B5s";

        if (!resultObservableMap.containsKey(test)) {

            resultObservableMap.put(test, new UisBipResult(test, nominalBip,"-", "-", "-","-","-", "-", "-","-"));
        }
        resultObservableMap.get(test).setParameters(String.valueOf((int)uisInjectorSectionModel.bipValueProperty().get()));
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

    public List<Result> getResultsList(){
        return new ArrayList<>(resultObservableMap.values());
    }

    public class UisBipResult implements Result {

        private final ObjectProperty<Test> injectorTest;
        private final StringProperty nominalBip;
        private final StringProperty bip1;
        private final StringProperty bip2;
        private final StringProperty bip3;
        private final StringProperty bip4;
        private final StringProperty bip5;
        private final StringProperty bip6;
        private final StringProperty bip7;
        private final StringProperty bip8;

        private double bip_1double;
        private double bip_2double;
        private double bip_3double;
        private double bip_4double;
        private double bip_5double;
        private double bip_6double;
        private double bip_7double;
        private double bip_8double;
        private double bipRangeLeft;
        private double bipRangeRight;
        private double acceptableBipRangeLeft;
        private double acceptableBipRangeRight;

        public ObjectProperty<Test> injectorTestProperty() {
            return injectorTest;
        }
        public StringProperty nominalBipProperty() {
            return nominalBip;
        }
        public StringProperty value1Property() {
            return bip1;
        }
        public StringProperty value2Property() {
            return bip2;
        }
        public StringProperty value3Property() {
            return bip3;
        }
        public StringProperty value4Property() {
            return bip4;
        }
        public StringProperty value5Property() {
            return bip5;
        }
        public StringProperty value6Property() {
            return bip6;
        }
        public StringProperty value7Property() {
            return bip7;
        }
        public StringProperty value8Property() {
            return bip8;
        }

        public double getDoubleValue_1() {
            return bip_1double;
        }
        public double getDoubleValue_2() {
            return bip_2double;
        }
        public double getDoubleValue_3() {
            return bip_3double;
        }
        public double getDoubleValue_4() {
            return bip_4double;
        }
        public double getDoubleValue_5() {
            return bip_5double;
        }
        public double getDoubleValue_6() {
            return bip_6double;
        }
        public double getDoubleValue_7() {
            return bip_7double;
        }
        public double getDoubleValue_8() {
            return bip_8double;
        }

        @Override
        public double getRangeLeft() {
            return bipRangeLeft;
        }
        @Override
        public double getRangeRight() {
            return bipRangeRight;
        }
        @Override
        public double getAcceptableRangeLeft() {
            return acceptableBipRangeLeft;
        }
        @Override
        public double getAcceptableRangeRight() {
            return acceptableBipRangeRight;
        }

        UisBipResult(Test test,
                     String nominalBip,
                     String bip1,
                     String bip2,
                     String bip3,
                     String bip4,
                     String bip5,
                     String bip6,
                     String bip7,
                     String bip8) {
            this.injectorTest = new SimpleObjectProperty<>(test);
            this.nominalBip = new SimpleStringProperty(nominalBip);
            this.bip1 = new SimpleStringProperty(bip1);
            this.bip2 = new SimpleStringProperty(bip2);
            this.bip3 = new SimpleStringProperty(bip3);
            this.bip4 = new SimpleStringProperty(bip4);
            this.bip5 = new SimpleStringProperty(bip5);
            this.bip6 = new SimpleStringProperty(bip6);
            this.bip7 = new SimpleStringProperty(bip7);
            this.bip8 = new SimpleStringProperty(bip8);
        }

        private void setParameters(String value) {

            List<String> actualBipList = getValueColumns();

            /**Universal approach of setting new values to existing actualFlowsList,could be used in both cases:
             * - single channel and multi channel modes if the last one will be activated in future*/
            uisInjectorSectionModel.activeLedToggleButtonsListProperty().get().stream()
                    .map(toggleButton -> (Integer.parseInt(toggleButton.getText()) - 1))
                    .forEach(i -> actualBipList.set(i, value));

            this.bip1.setValue(getBip(actualBipList.get(0)));
            this.bip2.setValue(getBip(actualBipList.get(1)));
            this.bip3.setValue(getBip(actualBipList.get(2)));
            this.bip4.setValue(getBip(actualBipList.get(3)));
            this.bip5.setValue(getBip(actualBipList.get(4)));
            this.bip6.setValue(getBip(actualBipList.get(5)));
            this.bip7.setValue(getBip(actualBipList.get(6)));
            this.bip8.setValue(getBip(actualBipList.get(7)));

            bip_1double = bip1.get().equals("-") ? -99d : Double.parseDouble(bip1.get());
            bip_2double = bip2.get().equals("-") ? -99d : Double.parseDouble(bip2.get());
            bip_3double = bip3.get().equals("-") ? -99d : Double.parseDouble(bip3.get());
            bip_4double = bip4.get().equals("-") ? -99d : Double.parseDouble(bip4.get());
            bip_5double = bip5.get().equals("-") ? -99d : Double.parseDouble(bip5.get());
            bip_6double = bip6.get().equals("-") ? -99d : Double.parseDouble(bip6.get());
            bip_7double = bip7.get().equals("-") ? -99d : Double.parseDouble(bip7.get());
            bip_8double = bip8.get().equals("-") ? -99d : Double.parseDouble(bip8.get());

            int bipNominal = ((InjectorUisTest)injectorTest.get()).getBip();
            int bipRange = ((InjectorUisTest)injectorTest.get()).getBipRange();
            bipRangeLeft = bipNominal - bipRange;
            bipRangeRight = bipNominal + bipRange;
            acceptableBipRangeLeft = bipRangeLeft - bipRangeLeft * 0.03;
            acceptableBipRangeRight = bipRangeRight + bipRangeRight * 0.03;
        }

        private String getBip(String bip) {
            return (bip == null || bip.isEmpty()) ? "-" : bip;
        }

        @Override
        public String getMainColumn() {
            return injectorTest.get().getTestName().getName();
        }

        @Override
        public String getSubColumn1() {
            return nominalBip.get();
        }

        @Override
        public String getSubColumn2() {
            return null;
        }

        @Override
        public List<String> getValueColumns() {
            return new ArrayList<>(Arrays.asList(bip1.get(), bip2.get(), bip3.get(), bip4.get(), bip5.get(), bip6.get(), bip7.get(), bip8.get()));
        }

        @Override
        public List<Double> getNumericDataColumns() {
            return new ArrayList<>(Arrays.asList(bip_1double, bip_2double, bip_3double, bip_4double, bip_5double, bip_6double, bip_7double, bip_8double));
        }
    }
}
