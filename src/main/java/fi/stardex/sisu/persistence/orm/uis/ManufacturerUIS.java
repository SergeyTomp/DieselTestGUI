package fi.stardex.sisu.persistence.orm.uis;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "manufacturers_uis")
public class ManufacturerUIS implements Producer {

    @Id
    @Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InjectorUIS> injectors = new LinkedList<>();

    @PostPersist public void onPostPersist() { EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true); }
    @PostRemove public void onPostRemove() { EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true); }

    @Override public String getManufacturerName() {
        return manufacturerName;
    }
    @Override public boolean isCustom() {
        return isCustom;
    }

    @Override public void setCustom(boolean custom) {
        isCustom = custom;
    }

    @Override public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    @Override public int getDisplayOrder() {
        return displayOrder;
    }

    @Override public String toString() {
        return manufacturerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManufacturerUIS)) return false;

        ManufacturerUIS that = (ManufacturerUIS) o;

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
