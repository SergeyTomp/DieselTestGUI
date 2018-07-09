package fi.stardex.sisu.persistence.orm.cr.inj;

import org.hibernate.annotations.NotFound;

import javax.persistence.*;

@Entity
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
    private Double totalPulseTime;

    @Column(name = "nominal_flow")
    private Double nominalFlow;

    @Column(name = "flow_range")
    private Double flowRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volt_ampere_profile")
    @NotFound
    private VoltAmpereProfile voltAmpereProfile;

    public TestName getTestName() {
        return testName;
    }

    public Double getNominalFlow() {
        return nominalFlow;
    }

    public Double getFlowRange() {
        return flowRange;
    }

    @Override
    public String toString() {
        return testName.toString();
    }
}
