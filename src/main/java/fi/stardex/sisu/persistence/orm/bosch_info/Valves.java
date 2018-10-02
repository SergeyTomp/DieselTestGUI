package fi.stardex.sisu.persistence.orm.bosch_info;

import javax.persistence.*;

@Entity
public class Valves {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "injector_code")
    private String injector;

    @Column(name = "valve_code")
    private String valve;

    public Integer getId() {
        return id;
    }

    public String getInjector() {
        return injector;
    }

    public String getValve() {
        return valve;
    }

    @Override
    public String toString() {
        return "Valves{" +
                "id=" + id +
                ", injector='" + injector + '\'' +
                ", valve='" + valve + '\'' +
                '}';
    }
}
