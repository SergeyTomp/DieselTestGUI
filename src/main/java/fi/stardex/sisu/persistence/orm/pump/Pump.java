package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.util.enums.pump.PumpPressureControl;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorType;
import fi.stardex.sisu.util.enums.pump.PumpRotation;

import javax.persistence.*;
import java.util.ArrayList;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pump_code", referencedColumnName = "pump_code")
    private List<PumpCarModel> pumpCarModelList = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pump_code", referencedColumnName = "pump_code")
    private PumpInfo pumpInfo;

    public List<PumpCarModel> getPumpCarModelList() {
        return pumpCarModelList;
    }

    public PumpInfo getPumpInfo() {
        return pumpInfo;
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

    public PumpPressureControl getPumpPressureControl() {
        return pumpPressureControl;
    }

    public boolean isCustom() {
        return custom;
    }

    @Override
    public String toString() {
        return pumpCode;
    }

}
