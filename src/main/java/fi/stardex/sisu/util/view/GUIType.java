package fi.stardex.sisu.util.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GUIType {
    CR_Inj, CR_Pump, UIS;

    private static Logger logger = LoggerFactory.getLogger(GUIType.class);

    public static GUIType getByString(String type) {
        for (GUIType guiType : GUIType.values()) {
            if (guiType.name().equalsIgnoreCase(type))
                return guiType;
        }
        logger.warn("Incorrect GUI type, return default type \"CR_Inj\"");
        return GUIType.CR_Inj;
    }
}
