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
    void setMotorSpeed(Integer motorSpeed);
    void setSettedPressure(Integer settedPressure);
    void setAdjustingTime(Integer adjustingTime);
    void setMeasurementTime(Integer measurementTime);
    void setTotalPulseTime1(Integer totalPulseTime1);
    void setTotalPulseTime2(Integer totalPulseTime2);
    void setNominalFlow(Double nominalFlow);
    void setFlowRange(Double flowRange);
    void setShift(Integer shift);
}
