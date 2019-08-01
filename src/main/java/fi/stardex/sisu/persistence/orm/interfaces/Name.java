package fi.stardex.sisu.persistence.orm.interfaces;

import fi.stardex.sisu.util.enums.Measurement;

public interface Name {

    String getName();
    Integer getOrder();
    Measurement getMeasurement();
}
