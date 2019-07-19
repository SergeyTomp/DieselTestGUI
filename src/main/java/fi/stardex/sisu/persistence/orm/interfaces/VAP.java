package fi.stardex.sisu.persistence.orm.interfaces;

import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;

public interface VAP {

    String getProfileName();
    InjectorType getInjectorType();
    InjectorSubType getInjectorSubType();
    Boolean isCustom();
    Integer getBoostU();
    Integer getBatteryU();
    Double getBoostI();
    Double getFirstI();
    Integer getFirstW();
    Double getSecondI();
    Integer getNegativeU();
    Boolean getBoostDisable();
    Double getBoostI2();
    Double getFirstI2();
    Integer getFirstW2();
    Double getSecondI2();
}
