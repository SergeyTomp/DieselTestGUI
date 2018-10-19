package fi.stardex.sisu.ui.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InjectorTestResult {

    private final static Logger logger = LoggerFactory.getLogger(InjectorTestResult.class);

    private String testName = "";
    private Double nominalValue = null;
    private Double rangePercent = null;

    private List<Double> beakerValues;
    private List<String> stringBeakerValues = Arrays.asList("-", "-", "-", "-");

    private String stringBeakerValue1 = "-";
    private String stringBeakerValue2 = "-";
    private String stringBeakerValue3 = "-";
    private String stringBeakerValue4 = "-";

    public InjectorTestResult(String testName, Double nominalValue, Double rangePercent) {
        this.testName = testName;
        this.nominalValue = nominalValue;
        this.rangePercent = rangePercent;

        beakerValues = new ArrayList<>(Arrays.asList(-1d, -1d, -1d, -1d));
        setStringValues();
    }

    private void setStringValues() {
        stringBeakerValue1 = String.valueOf(beakerValues.get(0));
        stringBeakerValue2 = String.valueOf(beakerValues.get(1));
        stringBeakerValue3 = String.valueOf(beakerValues.get(2));
        stringBeakerValue4 = String.valueOf(beakerValues.get(3));

        for (int i = 0; i < beakerValues.size(); i ++) {
            stringBeakerValues.set(i, String.valueOf(beakerValues.get(i)));
        }
    }
}
