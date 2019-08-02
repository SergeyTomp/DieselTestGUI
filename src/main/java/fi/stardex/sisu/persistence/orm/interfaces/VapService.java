package fi.stardex.sisu.persistence.orm.interfaces;

import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;

import java.util.List;

public interface VapService {

    void save(VAP vap);
    void delete(VAP vap);
    boolean existsById(String id);
    List<? extends VAP> findByIsCustom(boolean isCustom);
    List<? extends VAP> findByIsCustomAndInjectorType(boolean isCustom, InjectorType injectorType);
    List<? extends VAP>findByIsCustomAndInjectorTypeAndInjectorSubType(boolean isCustom, InjectorType injectorType, InjectorSubType injectorSubType);
}
