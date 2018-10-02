package fi.stardex.sisu.persistence.orm.siemens_info;

import javax.persistence.*;

@Entity
public class Reference {

    @Id
    @Column
    private String injector;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="engine")
    private Engines engine;

    @Column(name = "order_number")
    private String orderNumber;

    @Column
    private String remarks;

    public String getInjector() {
        return injector;
    }

    public String getDescription() {
        return description;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public Engines getEngine() {
        return engine;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "injector='" + injector + '\'' +
                ", description='" + description + '\'' +
                ", type='" + engine + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
