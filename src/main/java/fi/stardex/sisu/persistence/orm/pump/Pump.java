package fi.stardex.sisu.persistence.orm.pump;

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
