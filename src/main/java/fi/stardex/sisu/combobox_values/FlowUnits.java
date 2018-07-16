package fi.stardex.sisu.combobox_values;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlowUnits {

    public static final String MILLILITRE_PER_MINUTE = "ml/min";

    public static final String LITRE_PER_HOUR = "l/h";

    public static final String MILLILITRE_PER_100RPM = "ml/100str";

    public static final String MILLILITRE_PER_200RPM = "ml/200str";

    public static final String MILLILITRE_PER_1000RPM = "ml/1000str";

    private static Map<String, Float> mapOfFlowUnits = new LinkedHashMap<>();

    // TODO: implement for RPM
    static {
        mapOfFlowUnits.put(MILLILITRE_PER_MINUTE, 1f);
        mapOfFlowUnits.put(LITRE_PER_HOUR, 0.06f);
        mapOfFlowUnits.put(MILLILITRE_PER_100RPM, 1f);
        mapOfFlowUnits.put(MILLILITRE_PER_200RPM, 1f);
        mapOfFlowUnits.put(MILLILITRE_PER_1000RPM, 1f);
    }

    public static Map<String, Float> getMapOfFlowUnits() {
        return mapOfFlowUnits;
    }
}
