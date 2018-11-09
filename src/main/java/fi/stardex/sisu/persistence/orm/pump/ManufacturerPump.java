package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "manufacturer_pump")
public class ManufacturerPump {

    @Id
    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column
    private boolean custom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manufacturerPump", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pump> pumps = new LinkedList<>();

    public ManufacturerPump() {
    }

    public ManufacturerPump(String manufacturerName, boolean custom) {
        this.manufacturerName = manufacturerName;
        this.custom = custom;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public boolean isCustom() {
        return custom;
    }

    @Override
    public String toString() {
        return manufacturerName;
    }

}
