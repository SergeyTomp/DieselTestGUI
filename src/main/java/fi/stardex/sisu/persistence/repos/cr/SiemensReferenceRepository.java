package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.siemens_info.Reference;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SiemensReferenceRepository extends CrudRepository<Reference, String> {
    Reference findByInjector(String injector);
    List<Reference> findByOrderNumber(String orderNumber);
}
