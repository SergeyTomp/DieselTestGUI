package fi.stardex.sisu.persistence.orm.denso_info;

import javax.persistence.*;

@Entity
public class DensoInjectors {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    String code1;

    @Column
    String code2;

    @Column
    String description;

    public Integer getId() {
        return id;
    }

    public String getCode1() {
        return code1;
    }

    public String getCode2() {
        return code2;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DensoInjectors{" +
                "id=" + id +
                ", code1='" + code1 + '\'' +
                ", code2='" + code2 + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
