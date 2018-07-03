package fi.stardex.sisu.combobox_values;

import java.util.ArrayList;
import java.util.List;

public class FlowUnits {

    public static final String MILLILITRE_PER_MINUTE = "ml/min";

    public static final String LITRE_PER_HOUR = "l/h";

    public static final String MILLILITRE_PER_100RPM = "ml/100str";

    public static final String MILLILITRE_PER_200RPM = "ml/200str";

    public static final String MILLILITRE_PER_1000RPM = "ml/1000str";

    private static List<String> arrayOfFlowUnits = new ArrayList<>();

    static {
        arrayOfFlowUnits.add(MILLILITRE_PER_MINUTE);
        arrayOfFlowUnits.add(LITRE_PER_HOUR);
        arrayOfFlowUnits.add(MILLILITRE_PER_100RPM);
        arrayOfFlowUnits.add(MILLILITRE_PER_200RPM);
        arrayOfFlowUnits.add(MILLILITRE_PER_1000RPM);
    }

    public static List<String> getArrayOfFlowUnits() {
        return arrayOfFlowUnits;
    }
}
