package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InjectorsRepository extends CrudRepository<Injector, String> {
    List<Injector> findByManufacturerAndIsCustom(Manufacturer manufacturer, boolean isCustom);
}
