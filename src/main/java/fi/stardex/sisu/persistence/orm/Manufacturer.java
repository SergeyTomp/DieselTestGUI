package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorCR;

import javax.persistence.*;
import java.util.List;

@Entity(name = "Manufacturer")
public class Manufacturer implements Comparable<Manufacturer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(unique = true)
    private String manufacturer;

    @Column(name = "display_order", unique = true, nullable = false)
    private Integer displayOrder;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "manufacturer")
    private List<InjectorCR> injectorsCR;

    public List<InjectorCR> getInjectorsCR() {
        return injectorsCR;
    }

    @Override
    public String toString() {
        return manufacturer;
    }

    @Override
    public int compareTo(Manufacturer o) {
        return displayOrder - o.displayOrder;
    }
}
