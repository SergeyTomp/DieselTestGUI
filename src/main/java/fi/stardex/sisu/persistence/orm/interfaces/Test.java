package fi.stardex.sisu.persistence.orm.interfaces;

import javafx.beans.property.BooleanProperty;

public interface Test {

    Integer getId();
    Name getTestName();
    Model getInjector();
    VAP getVoltAmpereProfile();
    Integer getMotorSpeed();
    Integer getSettedPressure();
    Integer getAdjustingTime();
    Integer getMeasurementTime();
    Integer getTotalPulseTime1();
    Integer getTotalPulseTime2();
    Double getNominalFlow();
    Double getFlowRange();
    Boolean isCustom();
    Integer getShift();
    BooleanProperty includedProperty();
    void setVAP(VAP vap);

}
