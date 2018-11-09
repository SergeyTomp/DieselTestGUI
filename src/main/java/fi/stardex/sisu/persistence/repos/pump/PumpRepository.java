package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PumpRepository extends CrudRepository<Pump, String> {
    List<Pump> findAllByManufacturerPumpAndCustom(ManufacturerPump manufacturerPump, boolean custom);
}
