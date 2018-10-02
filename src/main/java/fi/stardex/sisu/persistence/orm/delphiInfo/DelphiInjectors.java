package fi.stardex.sisu.persistence.orm.delphiInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DelphiInjectors {

    @Id
    @Column(name = "injector_code")
    private String injector;
}
