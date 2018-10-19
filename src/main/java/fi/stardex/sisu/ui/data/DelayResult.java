package fi.stardex.sisu.ui.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DelayResult implements Result {

    private String delayTestName;
    private String delayUnits = "\u03BCs";

    private String delayValue1 = "-";
    private String delayValue2 = "-";
    private String delayValue3 = "-";
    private String delayValue4 = "-";

    private List<String> delayValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-"));

    public DelayResult(String delayTestName) {
        this.delayTestName = delayTestName;
    }

    public void setDelayValue(int number, String value) {
        delayValues.set(number - 1, value);
        setDelayValues();
    }

    private void setDelayValues() {
        delayValue1 = delayValues.get(0);
        delayValue2 = delayValues.get(1);
        delayValue3 = delayValues.get(2);
        delayValue4 = delayValues.get(3);
    }

    public String getDelayTestName() {
        return delayTestName;
    }

    public String getDelayUnits() {
        return delayUnits;
    }

    public String getDelayValue1() {
        return delayValue1;
    }

    public String getDelayValue2() {
        return delayValue2;
    }

    public String getDelayValue3() {
        return delayValue3;
    }

    public String getDelayValue4() {
        return delayValue4;
    }

    @Override
    public String getMainColumn() {
        return delayTestName;
    }

    @Override
    public String getSubColumn1() {
        return delayUnits;
    }

    @Override
    public String getSubColumn2() {
        return null;
    }

    @Override
    public List<String> getValueColumns() {
        return delayValues;
    }
}
