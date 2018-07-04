package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.orm.interfaces.VoltAmpereProfileProjection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InjectorsRepository extends CrudRepository<Injector, String> {

    List<Injector> findByManufacturerAndIsCustom(Manufacturer manufacturer, boolean isCustom);

    VoltAmpereProfileProjection findByInjectorCode(String injectorCode);
}
