package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InjectorsRepository extends CrudRepository<Injector, String> {
    List<Injector> findByManufacturerAndIsCustom(Manufacturer manufacturer, boolean isCustom);
    Injector findByInjectorCode(String injectorCode);
}
