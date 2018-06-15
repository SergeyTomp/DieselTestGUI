package fi.stardex.sisu.persistence.repos;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import org.springframework.data.repository.CrudRepository;

interface ManufacturerRepository extends CrudRepository<Manufacturer, Integer> {
}
