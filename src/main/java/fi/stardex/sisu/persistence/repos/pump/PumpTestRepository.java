package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PumpTestRepository extends CrudRepository<PumpTest, Integer> {

    @EntityGraph(value = "PumpTest.allLazy", type = EntityGraph.EntityGraphType.LOAD)
    List<PumpTest> findAllByPump(Model pumpCode);

    @EntityGraph(value = "PumpTest.allLazy", type = EntityGraph.EntityGraphType.LOAD)
    List<PumpTest> findAllByIsCustom(boolean isCustom);
}
