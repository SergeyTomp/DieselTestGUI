package fi.stardex.sisu.persistence.orm.interfaces;

import javafx.beans.property.BooleanProperty;

public interface Test {

    Integer getId();
    Name getTestName();
    Model getModel();
    VAP getVoltAmpereProfile();
    Integer getMotorSpeed();
    Integer getTargetPressure();
    Integer getAdjustingTime();
    Integer getMeasuringTime();
    Integer getTotalPulseTime1();
    Integer getTotalPulseTime2();
    Double getNominalFlow();
    Double getFlowRange();
    Boolean isCustom();
    Integer getShift();
    BooleanProperty includedProperty();
    void setVAP(VAP vap);
    void setMotorSpeed(Integer motorSpeed);
    void setTargetPressure(Integer settedPressure);
    void setAdjustingTime(Integer adjustingTime);
    void setMeasuringTime(Integer measurementTime);
    void setTotalPulseTime1(Integer totalPulseTime1);
    void setTotalPulseTime2(Integer totalPulseTime2);
    void setNominalFlow(Double nominalFlow);
    void setFlowRange(Double flowRange);
    void setShift(Integer shift);
}
