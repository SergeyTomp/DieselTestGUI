package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;

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

    @Column(name = "commonRail")
    private Boolean commonRail;

    @Column(name = "heui")
    private Boolean heui;

    @PostPersist
    public void onPostPersist() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostRemove
    public void onPostRemove() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

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
    public int getDisplayOrder() {
        return 0;
    }

    @Override
    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public void setCommonRail(Boolean commonRail) {
        this.commonRail = commonRail;
    }
    public void setHeui(Boolean heui) {
        this.heui = heui;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manufacturer)) return false;

        Manufacturer that = (Manufacturer) o;

        if (!getManufacturerName().equals(that.getManufacturerName())) return false;
        if (!isCustom.equals(that.isCustom)) return false;
        if (!commonRail.equals(that.commonRail)) return false;
        return heui.equals(that.heui);
    }

    @Override
    public int hashCode() {
        int result = getManufacturerName().hashCode();
        result = 31 * result + isCustom.hashCode();
        result = 31 * result + commonRail.hashCode();
        result = 31 * result + heui.hashCode();
        return result;
    }
}
