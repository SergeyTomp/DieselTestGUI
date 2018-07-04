package fi.stardex.sisu.persistence.orm.cr.inj;

import javax.persistence.*;

@Entity
@Table(name = "injector_type")
public class InjectorType {

    @Id
    @Column(name = "type_name")
    private String typeName;

    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;*/

    @Column(name = "injector_type")
    private String injectorType;

    public String getInjectorType() {
        return injectorType;
    }

    @Override
    public String toString() {
        return injectorType;
    }
}
