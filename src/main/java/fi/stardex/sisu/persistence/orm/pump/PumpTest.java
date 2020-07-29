package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import javafx.beans.property.BooleanProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NamedEntityGraph(name = "PumpTest.allLazy", attributeNodes = {@NamedAttributeNode("pump"), @NamedAttributeNode("pumpTestName")})
@Table(name = "pump_test")
public class PumpTest implements Test {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pump_code", nullable = false)
    private Pump pump;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public PumpTest() { }

    public PumpTest(Pump pump,
                    PumpTestName pumpTestName,
                    int motorSpeed,
                    int targetPressure,
                    Double regulatorCurrent,
                    boolean vacuum,
                    Double maxDirectFlow,
                    Double minDirectFlow,
                    Double maxBackFlow,
                    Double minBackFlow,
                    Double calibrationMaxI,
                    Double calibrationMinI,
                    Double calibrationIoffset,
                    Integer adjustingTime,
                    Integer measuringTime) {
        this.pump = pump;
        this.pumpTestName = pumpTestName;
        this.vacuum = vacuum;
        this.adjustingTime = adjustingTime;
        this.measuringTime = measuringTime;
        this.motorSpeed = motorSpeed;
        this.targetPressure = targetPressure;
        this.minDirectFlow = minDirectFlow;
        this.maxDirectFlow = maxDirectFlow;
        this.minBackFlow = minBackFlow;
        this.maxBackFlow = maxBackFlow;
        this.regulatorCurrent = regulatorCurrent;
        this.pcvCurrent = null;
        this.calibrationMinI = calibrationMinI;
        this.calibrationMaxI = calibrationMaxI;
        this.calibrationIoffset = calibrationIoffset;
        this.isCustom = true;
        this.calibrationI1 = null;
        this.calibrationI2 = null;
    }

    //for test copy in pump copy procedure
    public PumpTest(PumpTest pumpTest, Pump pump) {

        this.pump = pump;
        this.pumpTestName = pumpTest.pumpTestName;
        this.vacuum = pumpTest.vacuum;
        this.adjustingTime = pumpTest.adjustingTime;
        this.measuringTime = pumpTest.measuringTime;
        this.motorSpeed = pumpTest.motorSpeed;
        this.targetPressure = pumpTest.targetPressure;
        this.minDirectFlow = pumpTest.minDirectFlow;
        this.maxDirectFlow = pumpTest.maxDirectFlow;
        this.minBackFlow = pumpTest.minBackFlow;
        this.maxBackFlow = pumpTest.maxBackFlow;
        this.regulatorCurrent = pumpTest.regulatorCurrent;
        this.pcvCurrent = pumpTest.pcvCurrent;
        this.calibrationMinI = pumpTest.calibrationMinI;
        this.calibrationMaxI = pumpTest.calibrationMaxI;
        this.calibrationIoffset = pumpTest.calibrationIoffset;
        this.isCustom = true;
        this.calibrationI1 = null;
        this.calibrationI2 = null;
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

    public void setVacuum(boolean vacuum) {
        this.vacuum = vacuum;
    }
    public void setMinDirectFlow(Double minDirectFlow) {
        this.minDirectFlow = minDirectFlow;
    }
    public void setMaxDirectFlow(Double maxDirectFlow) {
        this.maxDirectFlow = maxDirectFlow;
    }
    public void setMinBackFlow(Double minBackFlow) {
        this.minBackFlow = minBackFlow;
    }
    public void setMaxBackFlow(Double maxBackFlow) {
        this.maxBackFlow = maxBackFlow;
    }
    public void setRegulatorCurrent(Double regulatorCurrent) {
        this.regulatorCurrent = regulatorCurrent;
    }
    public void setCalibrationMinI(Double calibrationMinI) {
        this.calibrationMinI = calibrationMinI;
    }
    public void setCalibrationMaxI(Double calibrationMaxI) {
        this.calibrationMaxI = calibrationMaxI;
    }
    public void setCalibrationIoffset(Double calibrationIoffset) {
        this.calibrationIoffset = calibrationIoffset;
    }

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
    public boolean isVacuum() {
        return vacuum;
    }
    public Double getCalibrationI1() {
        return calibrationI1;
    }
    public Double getCalibrationI2() {
        return calibrationI2;
    }

    @Override
    public String toString() {
        return pumpTestName.toString();
    }

    @PostPersist
    private void onPostPersist() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostUpdate
    private void onPostUpdate() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostRemove
    private void onPostRemove() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PumpTest that = (PumpTest) o;
        return Objects.equals(pump, that.pump) &&
                Objects.equals(getTestName(), that.getTestName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(pump, getTestName());
    }
}
