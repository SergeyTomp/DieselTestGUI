package fi.stardex.sisu.persistence.orm.uis;

import javax.persistence.*;

@Entity
@Table(name = "reference_uis")
public class ReferenceUIS {

    @Id
    @Column(name = "generated_injector_code")
    private String keyInjector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "injector_code")
    private InjectorUIS refInjector;

    public String getKeyInjector() {
        return keyInjector;
    }
    public InjectorUIS getRefInjector() {
        return refInjector;
    }
}
