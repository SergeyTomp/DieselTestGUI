package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.delphiInfo.DelphiInjectors;
import org.springframework.data.repository.CrudRepository;

public interface DelphiRepository extends CrudRepository<DelphiInjectors, String> {
}
