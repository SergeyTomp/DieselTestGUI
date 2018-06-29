package fi.stardex.sisu.persistence.orm.cr.inj;

import javax.persistence.*;

@Entity
@Table(name = "injector_type")
public class InjectorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "injector_type", unique = true)
    private String injectorType;

    public String getInjectorType() {
        return injectorType;
    }

    @Override
    public String toString() {
        return injectorType;
    }
}
