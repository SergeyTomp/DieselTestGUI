package fi.stardex.sisu.persistence.orm.bosch_info;

import javax.persistence.*;

@Entity
public class Applications {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "injector_code")
    private String injector;

    @Column(name = "application")
    private String application;

    public Integer getId() {
        return id;
    }

    public String getInjector() {
        return injector;
    }

    public String getApplication() {
        return application;
    }

    @Override
    public String toString() {
        return "Applications{" +
                "id=" + id +
                ", injector='" + injector + '\'' +
                ", application='" + application + '\'' +
                '}';
    }
}
