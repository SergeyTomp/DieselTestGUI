package fi.stardex.sisu.persistence.orm.siemens_info;

import javax.persistence.*;

@Entity
public class Cars {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "engine")
    private Engines engine;

    @Column
    private String car;

    public Integer getId() {
        return id;
    }

    public String getCar() {
        return car;
    }

    @Override
    public String toString() {
        return "Cars{" +
                "id=" + id +
                ", type='" + engine + '\'' +
                ", car='" + car + '\'' +
                '}';
    }
}
