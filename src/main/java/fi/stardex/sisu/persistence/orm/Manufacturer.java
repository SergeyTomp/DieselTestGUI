package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Manufacturer {

    @Id
    @Column(unique = true)
    private String manufacturer;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manufacturer", cascade = CascadeType.ALL)
    private List<Injector> injectors = new LinkedList<>();

    public List<Injector> getInjectors() {
        return injectors;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return manufacturer;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
