package fi.stardex.sisu.persistence.orm.bosch_info;

import javax.persistence.*;

@Entity
public class Nozzles {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "injector_code")
    private String injector;

    @Column(name = "nozzle_code")
    private String nozzle;

    public Integer getId() {
        return id;
    }

    public String getInjector() {
        return injector;
    }

    public String getNozzle() {
        return nozzle;
    }

    @Override
    public String toString() {
        return "Nozzles{" +
                "id=" + id +
                ", injector='" + injector + '\'' +
                ", nozzle='" + nozzle + '\'' +
                '}';
    }
}
