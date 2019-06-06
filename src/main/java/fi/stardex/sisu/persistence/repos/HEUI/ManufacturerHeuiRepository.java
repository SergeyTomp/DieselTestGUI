package fi.stardex.sisu.persistence.repos.HEUI;

import fi.stardex.sisu.persistence.Producer;
import fi.stardex.sisu.persistence.orm.ManufacturerHEUI;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ManufacturerHeuiRepository  extends CrudRepository<ManufacturerHEUI, String> {
    List<Producer> findByIsCustom(boolean isCustom);
}
