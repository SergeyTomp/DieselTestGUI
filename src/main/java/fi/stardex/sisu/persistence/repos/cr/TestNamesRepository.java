package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import org.springframework.data.repository.CrudRepository;

public interface TestNamesRepository extends CrudRepository<TestName, Integer> {
}
