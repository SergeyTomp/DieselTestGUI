package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.*;

@Entity
@Table(name = "pump_test")
public class PumpTest {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

}
