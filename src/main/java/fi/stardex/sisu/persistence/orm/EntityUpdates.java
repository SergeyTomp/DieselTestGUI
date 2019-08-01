package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.orm.cr.inj.*;
import fi.stardex.sisu.persistence.orm.uis.ManufacturerUIS;

import java.util.HashMap;
import java.util.Map;

public class EntityUpdates {

    private static final Map<String,Boolean> mapOfEntityUpdates = new HashMap<>();

    static {

        mapOfEntityUpdates.put(Manufacturer.class.getSimpleName(), false);
        mapOfEntityUpdates.put(VoltAmpereProfile.class.getSimpleName(), false);
        mapOfEntityUpdates.put(Injector.class.getSimpleName(), false);
        mapOfEntityUpdates.put(InjectorTest.class.getSimpleName(), false);
        mapOfEntityUpdates.put(ManufacturerUIS.class.getSimpleName(), false);

    }

    public static Map<String, Boolean> getMapOfEntityUpdates() {
        return mapOfEntityUpdates;
    }

}
