package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.util.enums.pump.PumpPressureControl;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig;
import fi.stardex.sisu.util.enums.pump.PumpRegulatortype;
import fi.stardex.sisu.util.enums.pump.PumpRotation;

import javax.persistence.*;

@Entity
@Table(name = "pump")
public class Pump {

    @Id
    @Column(name = "pump_code")
    private String pumpCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_name")
    private ManufacturerPump manufacturerPump;

    @Column
    private boolean custom;

    @Column(name = "feed_pressure", nullable = false)
    private double feedPressure;

    @Column(name = "rotation", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRotation pumpRotation;

    @Column(name = "regulator_config")
    @Enumerated(EnumType.STRING)
    private PumpRegulatorConfig pumpRegulatorConfig;

    @Column(name = "pressure_control")
    @Enumerated(EnumType.STRING)
    private PumpPressureControl pumpPressureControl;

    @Column(name = "regulator_type")
    @Enumerated(EnumType.STRING)
    private PumpRegulatortype pumpRegulatortype;

    @Column(name = "scv_current_inj")
    private Double scvCurrentInj;

    @Column(name = "scv_min_current")
    private Double scvMinCurrent;

    @Column(name = "scv_max_current")
    private Double scvMaxCurrent;

    public String getPumpCode() {
        return pumpCode;
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
