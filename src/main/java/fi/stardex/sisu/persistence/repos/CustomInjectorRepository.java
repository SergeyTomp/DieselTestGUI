package fi.stardex.sisu.persistence.repos;

import fi.stardex.sisu.persistence.orm.cr.inj.CustomInjectorCR;
import org.springframework.data.repository.CrudRepository;

public interface CustomInjectorRepository extends CrudRepository<CustomInjectorCR, String> {
}
