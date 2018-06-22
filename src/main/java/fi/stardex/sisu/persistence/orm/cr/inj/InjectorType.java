package fi.stardex.sisu.persistence.orm.cr.inj;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class InjectorType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "injector_type", unique = true)
    private String injectorType;

    public String getInjectorType() {
        return injectorType;
    }

    public void setInjectorType(String injectorType) {
        this.injectorType = injectorType;
    }

    @Override
    public String toString() {
        return injectorType;
    }
}
