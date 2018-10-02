package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.caterpillar_info.CaterpillarInjectors;
import org.springframework.data.repository.CrudRepository;

public interface CaterpillarRepository extends CrudRepository<CaterpillarInjectors, String> {
}
