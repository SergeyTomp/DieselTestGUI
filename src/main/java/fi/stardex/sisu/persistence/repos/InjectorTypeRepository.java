package fi.stardex.sisu.persistence.repos;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import org.springframework.data.repository.CrudRepository;

public interface InjectorTypeRepository extends CrudRepository<InjectorType, Integer> {
}
