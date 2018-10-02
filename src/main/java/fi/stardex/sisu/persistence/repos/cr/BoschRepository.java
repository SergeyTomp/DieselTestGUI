package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.bosch_info.Parameters;
import org.springframework.data.repository.CrudRepository;

public interface BoschRepository extends CrudRepository<Parameters, String> {
}
