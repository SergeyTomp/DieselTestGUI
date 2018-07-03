package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VoltAmpereProfileRepository extends CrudRepository<VoltAmpereProfile, String> {
    List<VoltAmpereProfile> findByIsCustomAndInjectorType(Boolean isCustom, InjectorType injectorType);
}
