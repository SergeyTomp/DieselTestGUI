package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.uis.ManufacturerUIS;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ManufacturerUisRepository extends CrudRepository<ManufacturerUIS, String> {

    List<ManufacturerUIS> findAll ();
    List<ManufacturerUIS> findByIsCustom(boolean isCustom);

}
