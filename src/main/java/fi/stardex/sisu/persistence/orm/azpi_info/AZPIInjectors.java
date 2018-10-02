package fi.stardex.sisu.persistence.orm.azpi_info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AZPIInjectors {

    @Id
    @Column(name = "injector_code")
    private String injector;
}
