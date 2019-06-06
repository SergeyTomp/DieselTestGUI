package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.Producer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Manufacturer implements Producer {

    @Id
    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Injector> injectors = new LinkedList<>();

    @PostPersist
    @Override
    public void onPostPersist() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostRemove
    @Override
    public void onPostRemove() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @Override
    public List<Injector> getInjectors() {
        return injectors;
    }
    @Override
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
    @Override
    public String getManufacturerName() {
        return manufacturerName;
    }

    @Override
    public String toString() {
        return manufacturerName;
    }
    @Override
    public boolean isCustom() {
        return isCustom;
    }
    @Override
    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manufacturer)) return false;

        Manufacturer that = (Manufacturer) o;

        if (!getManufacturerName().equals(that.getManufacturerName())) return false;
        return isCustom.equals(that.isCustom);
    }

    @Override
    public int hashCode() {
        int result = getManufacturerName().hashCode();
        result = 31 * result + isCustom.hashCode();
        return result;
    }
}
