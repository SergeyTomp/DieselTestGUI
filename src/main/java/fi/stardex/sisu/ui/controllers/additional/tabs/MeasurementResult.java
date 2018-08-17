package fi.stardex.sisu.ui.controllers.additional.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeasurementResult {

    private String parameterName = null;
    private String units = null;

    private String parameterValue1 = "-";
    private String parameterValue2 = "-";
    private String parameterValue3 = "-";
    private String parameterValue4 = "-";

    private List<String> parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-"));


    public MeasurementResult(String parameterName, String units) {
        this.parameterName = parameterName;
        this.units = units;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getUnits() {
        return units;
    }

    public void setParameterValue(int number, String value) {
        parameterValues.set(number - 1, value);
        setParameterValues();
    }

    private void setParameterValues() {
        parameterValue1 = parameterValues.get(0);
        parameterValue2 = parameterValues.get(1);
        parameterValue3 = parameterValues.get(2);
        parameterValue4 = parameterValues.get(3);
    }

    public List<String> getParameterValues() {
        return parameterValues;
    }

    public String getParameterValue1() {
        return parameterValue1;
    }

    public String getParameterValue2() {
        return parameterValue2;
    }

    public String getParameterValue3() {
        return parameterValue3;
    }

    public String getParameterValue4() {
        return parameterValue4;
    }

    public String getMainColumn() {
        return parameterName;
    }

    public String getSubColumn1() {
        return units;
    }

    public String getSubColumn2() {
        return null;
    }

    public List<String> getValueColumns() {
        return parameterValues;
    }
}
