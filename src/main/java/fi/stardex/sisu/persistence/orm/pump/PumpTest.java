package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import javafx.beans.property.BooleanProperty;

import javax.persistence.*;

@Entity
@Table(name = "pump_test")
public class PumpTest implements Test {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pump_code", nullable = false)
    private Pump pump;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_name", nullable = false)
    private PumpTestName pumpTestName;

    @Column
    private boolean vacuum;

    @Column(name = "adjusting_time", nullable = false)
    private Integer adjustingTime;

    @Column(name = "measuring_time")
    private Integer measuringTime;

    @Column(name = "motor_speed", nullable = false)
    private int motorSpeed;

    @Column(name = "target_pressure", nullable = false)
    private int targetPressure;

    @Column(name = "min_direct_flow")
    private Double minDirectFlow;

    @Column(name = "max_direct_flow")
    private Double maxDirectFlow;

    @Column(name = "min_back_flow")
    private Double minBackFlow;

    @Column(name = "max_back_flow")
    private Double maxBackFlow;

    @Column(name = "regulator_current")
    private Double regulatorCurrent;

    @Column(name = "pcv_current")
    private Integer pcvCurrent;

    @Column(name = "calibration_min_i")
    private Double calibrationMinI;

    @Column(name = "calibration_max_i")
    private Double calibrationMaxI;

    @Column(name = "calibration_i1")
    private Double calibrationI1;

    @Column(name = "calibration_i2")
    private Double calibrationI2;

    @Column(name = "calibration_i_offset")
    private Double calibrationIoffset;

    @Column(name = "is_custom")
    private boolean isCustom;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Model getModel() {
        return pump;
    }

    @Override
    public Integer getMotorSpeed() {
        return motorSpeed;
    }

    @Override
    public Integer getTargetPressure() {
        return targetPressure;
    }

    @Override
    public Boolean isCustom() {
        return isCustom;
    }

    @Override
    public Integer getAdjustingTime() {
        return adjustingTime;
    }

    @Override
    public void setMotorSpeed(Integer motorSpeed) {
        this.motorSpeed = motorSpeed;
    }

    @Override
    public void setTargetPressure(Integer targetPressure) {
        this.targetPressure = targetPressure;
    }

    @Override
    public void setAdjustingTime(Integer adjustingTime) {
        this.adjustingTime = adjustingTime;
    }

    @Override
    public void setMeasuringTime(Integer measuringTime) {
        this.measuringTime = measuringTime;
    }

    @Override
    public PumpTestName getTestName() {
        return pumpTestName;
    }

    @Override
    public Integer getMeasuringTime() {
        return measuringTime;
    }

    public Double getRegulatorCurrent() {
        return regulatorCurrent;
    }

    public Integer getPcvCurrent() {
        return pcvCurrent;
    }

    public Double getMinDirectFlow() {
        return minDirectFlow;
    }

    public Double getMaxDirectFlow() {
        return maxDirectFlow;
    }

    public Double getMinBackFlow() {
        return minBackFlow;
    }

    public Double getMaxBackFlow() {
        return maxBackFlow;
    }

    public Double getCalibrationMinI() {
        return calibrationMinI;
    }

    public Double getCalibrationMaxI() {
        return calibrationMaxI;
    }

    public Double getCalibrationIoffset() {
        return calibrationIoffset;
    }

    @Override
    public String toString() {
        return pumpTestName.toString();
    }

    /**methods below are irrelevant for pumps but should be realized anyhow due to interface Test*/
    @Override
    public Integer getTotalPulseTime1() {
        return null;
    }
    @Override
    public Integer getTotalPulseTime2() {
        return null;
    }
    @Override
    public VAP getVoltAmpereProfile() {
        return null;
    }
    @Override
    public Integer getShift() {
        return null;
    }
    @Override
    public Double getNominalFlow() {
        return null;
    }
    @Override
    public Double getFlowRange() {
        return null;
    }
    @Override
    public BooleanProperty includedProperty() {
        return null;
    }
    @Override
    public void setShift(Integer shift) {

    }
    @Override
    public void setTotalPulseTime1(Integer totalPulseTime1) {

    }
    @Override
    public void setTotalPulseTime2(Integer totalPulseTime2) {

    }
    @Override
    public void setVAP(VAP vap) {

    }
    @Override
    public void setNominalFlow(Double nominalFlow) {

    }
    @Override
    public void setFlowRange(Double flowRange) {

    }
}
