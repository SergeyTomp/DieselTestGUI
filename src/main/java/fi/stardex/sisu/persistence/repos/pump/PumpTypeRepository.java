package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.pump.PumpInfo;
import org.springframework.data.repository.CrudRepository;

public interface PumpTypeRepository extends CrudRepository<PumpInfo, String> {
}
