package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.azpi_info.AZPIInjectors;
import org.springframework.data.repository.CrudRepository;

public interface AZPIRepository extends CrudRepository<AZPIInjectors, String> {
}
