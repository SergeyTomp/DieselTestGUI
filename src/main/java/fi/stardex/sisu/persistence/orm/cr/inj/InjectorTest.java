package fi.stardex.sisu.persistence.orm.cr.inj;

import org.hibernate.annotations.NotFound;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NamedEntityGraph(name = "InjectorTest.testName", attributeNodes = @NamedAttributeNode("testName"))
@Table(name = "injector_test")
public class InjectorTest {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "injector_code")
    private Injector injector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_name")
    private TestName testName;

    @Column(name = "motor_speed")
    private Integer motorSpeed;

    @Column(name = "setted_pressure")
    private Integer settedPressure;

    @Column(name = "adjusting_time")
    private Integer adjustingTime;

    @Column(name = "measurement_time")
    private Integer measurementTime;

    @Column(name = "codefield", length = 45)
    private String codefield;

    @Column(name = "injection_rate")
    private Integer injectionRate;

    @Column(name = "total_pulse_time")
    private Integer totalPulseTime;

    @Column(name = "nominal_flow")
    private Double nominalFlow;

    @Column(name = "flow_range")
    private Double flowRange;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volt_ampere_profile")
    @NotFound
    private VoltAmpereProfile voltAmpereProfile;

    public InjectorTest() {
    }

    public InjectorTest(Injector injector, TestName testName, Integer motorSpeed, Integer settedPressure,
                        Integer adjustingTime, Integer measurementTime, Integer injectionRate,
                        Integer totalPulseTime, Double nominalFlow, Double flowRange) {
        this.injector = injector;
        this.testName = testName;
        this.motorSpeed = motorSpeed;
        this.settedPressure = settedPressure;
        this.adjustingTime = adjustingTime;
        this.measurementTime = measurementTime;
        this.injectionRate = injectionRate;
        this.totalPulseTime = totalPulseTime;
        this.nominalFlow = nominalFlow;
        this.flowRange = flowRange;
        this.isCustom = true;
    }

    public Integer getMotorSpeed() {
        return motorSpeed;
    }

    public Integer getSettedPressure() {
        return settedPressure;
    }

    public Integer getAdjustingTime() {
        return adjustingTime;
    }

    public Integer getMeasurementTime() {
        return measurementTime;
    }

    public String getCodefield() {
        return codefield;
    }

    public Integer getInjectionRate() {
        return injectionRate;
    }

    public Integer getTotalPulseTime() {
        return totalPulseTime;
    }

    public Double getNominalFlow() {
        return nominalFlow;
    }

    public Double getFlowRange() {
        return flowRange;
    }

    public Boolean getCustom() {
        return isCustom;
    }

    public VoltAmpereProfile getVoltAmpereProfile() {
        return voltAmpereProfile;
    }

    public TestName getTestName() {
        return testName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectorTest that = (InjectorTest) o;
        return Objects.equals(injector, that.injector) &&
                Objects.equals(getTestName(), that.getTestName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(injector, getTestName());
    }

    @Override
    public String toString() {
        return testName.toString();
    }
}
