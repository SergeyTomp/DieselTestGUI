package fi.stardex.sisu.persistence.orm;

import javax.persistence.*;

@Entity(name = "Manufacturer")
public class Manufacturer implements Comparable<Manufacturer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(unique = true)
    private String manufacturer;

    @Column(name = "display_order", unique = true, nullable = false)
    private Integer displayOrder;

    @Override
    public String toString() {
        return manufacturer;
    }

    @Override
    public int compareTo(Manufacturer o) {
        return displayOrder - o.displayOrder;
    }
}
