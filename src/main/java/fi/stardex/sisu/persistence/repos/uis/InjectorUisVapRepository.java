package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InjectorUisVapRepository extends CrudRepository<InjectorUisVAP, String> {

    List<InjectorUisVAP> findByIsCustomAndInjectorType(boolean isCustom, InjectorType injectorType);
    List<InjectorUisVAP> findByIsCustom(boolean isCustom);
    boolean existsById(String profileName);
    List<InjectorUisVAP> findByIsCustomAndInjectorTypeAndInjectorSubType(boolean isCustom, InjectorType injectorType, InjectorSubType injectorSubType);

}
