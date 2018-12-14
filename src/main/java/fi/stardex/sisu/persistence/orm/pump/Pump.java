package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.util.enums.pump.PumpPressureControl;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorType;
import fi.stardex.sisu.util.enums.pump.PumpRotation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "pump")
public class Pump {

    @Id
    @Column(name = "pump_code")
    private String pumpCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manufacturer")
    private ManufacturerPump manufacturerPump;

    @Column
    private boolean custom;

    @Column(name = "feed_pressure", nullable = false)
    private double feedPressure;

    @Column(name = "rotation", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRotation pumpRotation;

    @Column(name = "regulator_config", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRegulatorConfig pumpRegulatorConfig;

    @Column(name = "pressure_control", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpPressureControl pumpPressureControl;

    @Column(name = "regulator_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRegulatorType pumpRegulatorType;

    @Column(name = "scv_curr_inj")
    private Double scvCurrInj;

    @Column(name = "scv_min_curr")
    private Double scvMinCurr;

    @Column(name = "scv_max_curr")
    private Double scvMaxCurr;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pump", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PumpTest> pumpTests = new LinkedList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pumpCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PumpCarModel> pumpCarModelList = new ArrayList<>();

    public List<PumpCarModel> getPumpCarModelList() {
        return pumpCarModelList;
    }

    public String getPumpCode() {
        return pumpCode;
    }

    public double getFeedPressure() {
        return feedPressure;
    }

    public PumpRotation getPumpRotation() {
        return pumpRotation;
    }

    public PumpRegulatorConfig getPumpRegulatorConfig() {
        return pumpRegulatorConfig;
    }

    public ManufacturerPump getManufacturerPump() {
        return manufacturerPump;
    }

    public boolean isCustom() {
        return custom;
    }

    @Override
    public String toString() {
        return pumpCode;
    }

}
