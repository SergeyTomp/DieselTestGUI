package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PumpRepository extends CrudRepository<Pump, String> {

    @EntityGraph(value = "Pump.allLazy", type = EntityGraph.EntityGraphType.LOAD)
    List<Pump> findAllByManufacturerAndCustom(Producer producer, boolean custom);
    List<Pump> findByManufacturer(Producer producer);
    Pump findByPumpCode(String code);
    List<Pump> findByCustom (boolean isCustom);
    boolean existsByPumpCode (String modelCode);
}
