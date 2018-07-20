package fi.stardex.sisu.persistence.repos;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.PostConstruct;
import java.util.List;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, String> {
    List<Manufacturer> findByIsCustom(boolean isCustom);
}
