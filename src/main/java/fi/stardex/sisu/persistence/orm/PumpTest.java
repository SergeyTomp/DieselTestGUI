package fi.stardex.sisu.persistence.orm;



import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "pump_test")
public class PumpTest implements Test, Comparable<PumpTest> {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pump_type")
    private PumpType pumpType;
    @Column(name = "efficiency_test")
    private String efficiencyTest;
    private BigDecimal delivery;
    @Column(name = "delta_plus")
    private Integer deltaPlus;
    @Column(name = "delta_minus")
    private Integer deltaMinus;
    @Column
    private BigDecimal recovery;
    @Column
    private Integer rpm;
    @Column
    private Integer pressure;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PumpType getPumpType() {
        return pumpType;
    }

    public void setPumpType(PumpType pumpType) {
        this.pumpType = pumpType;
    }

    public String getEfficiencyTest() {
        return efficiencyTest;
    }

    public void setEfficiencyTest(String efficiencyTest) {
        this.efficiencyTest = efficiencyTest;
    }

    public BigDecimal getDelivery() {
        return delivery;
    }

    public void setDelivery(BigDecimal delivery) {
        this.delivery = delivery;
    }

    public Integer getDeltaPlus() {
        return deltaPlus;
    }

    public void setDeltaPlus(Integer deltaPlus) {
        this.deltaPlus = deltaPlus;
    }

    public Integer getDeltaMinus() {
        return deltaMinus;
    }

    public void setDeltaMinus(Integer deltaMinus) {
        this.deltaMinus = deltaMinus;
    }

    public BigDecimal getRecovery() {
        return recovery;
    }

    public void setRecovery(BigDecimal recovery) {
        this.recovery = recovery;
    }

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    @Override
    public int compareTo(PumpTest o) {
        return efficiencyTest.compareTo(o.getEfficiencyTest());
    }
    @Override
    public String toString() {
        return efficiencyTest;
    }

}
