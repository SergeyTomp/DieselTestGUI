package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.VoltAmpereProfileProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InjectorsRepository extends CrudRepository<Injector, String> {

    List<Injector> findByManufacturerAndIsCustom(Manufacturer manufacturer, boolean isCustom);

    List<Injector> findByManufacturerAndIsCustomAndIsHeui(Manufacturer manufacturer, boolean isCustom, boolean isHeui);

    VoltAmpereProfileProjection findByInjectorCode(String injectorCode);

    @EntityGraph(value = "Injector.allLazy", type = EntityGraph.EntityGraphType.LOAD)
    List<Injector> findByIsCustom(boolean isCustom);
}
