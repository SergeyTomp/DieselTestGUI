package fi.stardex.sisu.util.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GUIType {
    CR_Inj, CR_Pump, UIS;

    private static Logger logger = LoggerFactory.getLogger(GUIType.class);
    private static GUIType currentType;

    public static GUIType getByString(String type) {
        for (GUIType guiType : GUIType.values()) {
            if (guiType.name().equalsIgnoreCase(type))
                return guiType;
        }
        logger.warn("Incorrect GUI currentType, return default currentType \"CR_Inj\"");
        return GUIType.CR_Inj;
    }

    public static GUIType getCurrentType() {
        return currentType;
    }

    public static void setCurrentType(GUIType newType) {
        currentType = newType;
    }
}
