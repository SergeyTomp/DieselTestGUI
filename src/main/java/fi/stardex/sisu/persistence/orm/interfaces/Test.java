package fi.stardex.sisu.persistence.orm.interfaces;

public interface Test {

    Integer getId();
    Name getTestName();
    Model getInjector();
    VAP getVoltAmpereProfile();
    boolean isIncluded();
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

}
