package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ManufacturerPumpRepository extends CrudRepository<ManufacturerPump, String> {

   List<ManufacturerPump> findAll ();
   List<ManufacturerPump> findByCustom(boolean isCustom);

}
