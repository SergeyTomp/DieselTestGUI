package fi.stardex.sisu.persistence.orm.cr.inj;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "injector_type")
public class InjectorType {

    @Id
    @Column(name = "type_name")
    private String typeName;

    @Column(name = "injector_type")
    private String injectorType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "injectorType", cascade = CascadeType.ALL)
    private List<VoltAmpereProfile> voltAmpereProfiles = new LinkedList<>();

    public String getInjectorType() {
        return injectorType;
    }

    @Override
    public String toString() {
        return injectorType;
    }
}
