package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "manufacturer_pump")
public class ManufacturerPump implements Producer {

    @Id
    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column
    private boolean custom;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pump> pumps = new LinkedList<>();

    public ManufacturerPump() {
    }

    public ManufacturerPump(String manufacturerName, boolean custom) {
        this.manufacturerName = manufacturerName;
        this.custom = custom;
    }

    @Override
    public String getManufacturerName() {
        return manufacturerName;
    }

    @Override
    public boolean isCustom() {
        return custom;
    }

    @Override
    public int getDisplayOrder() {
        return 0;
    }

    @Override
    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    @Override
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    @Override
    public String toString() {
        return manufacturerName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManufacturerPump)) return false;

        ManufacturerPump that = (ManufacturerPump) o;

        if (isCustom() != that.isCustom()) return false;
        return getManufacturerName().equals(that.getManufacturerName());
    }

    @Override
    public int hashCode() {
        int result = getManufacturerName().hashCode();
        result = 31 * result + (isCustom() ? 1 : 0);
        return result;
    }
}
