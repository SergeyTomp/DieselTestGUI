package fi.stardex.sisu.persistence.orm.caterpillar_info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CaterpillarInjectors {

    @Id
    @Column(name = "injector_code")
    private String injector;
}
