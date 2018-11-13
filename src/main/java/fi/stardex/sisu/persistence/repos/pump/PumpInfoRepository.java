package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.pump.PumpInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PumpInfoRepository extends CrudRepository<PumpInfo, String> {

    @EntityGraph(value = "carModels", type = EntityGraph.EntityGraphType.LOAD)
    Optional<PumpInfo> findByPumpCode(String pumpCode);

}
