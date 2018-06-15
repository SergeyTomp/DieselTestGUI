package fi.stardex.sisu.combobox_values;

import java.util.ArrayList;
import java.util.List;

public enum FlowUnits {

    MILLILITRE_PER_MINUTE("ml/min"),
    LITRE_PER_HOUR("l/h"),
    MILLILITRE_PER_100RPM("ml/100str"),
    MILLILITRE_PER_200RPM("ml/200str"),
    MILLILITRE_PER_1000RPM("ml/1000str");

    private String label;

    private static List<String> stringValues = new ArrayList<>();

    static {
        stringValues.add(MILLILITRE_PER_MINUTE.getLabel());
        stringValues.add(LITRE_PER_HOUR.getLabel());
        stringValues.add(MILLILITRE_PER_100RPM.getLabel());
        stringValues.add(MILLILITRE_PER_200RPM.getLabel());
        stringValues.add(MILLILITRE_PER_1000RPM.getLabel());
    }

    public String getLabel() {
        return label;
    }

    public static List<String> getStringValues() {
        return stringValues;
    }

    FlowUnits(String label) {
        this.label = label;
    }

}
