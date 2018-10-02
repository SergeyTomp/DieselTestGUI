package fi.stardex.sisu.persistence.orm.siemens_info;

import javax.persistence.*;

@Entity
public class Spares {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "engine")
    private Engines engine;

    @Column(name = "order_number")
    private String orderNumber;

    @Column
    String category;

    @Column
    private String description;

    public Integer getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Spares{" +
                "id=" + id +
                ", type='" + engine + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
