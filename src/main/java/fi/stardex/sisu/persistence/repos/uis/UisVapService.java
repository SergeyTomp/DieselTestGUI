package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import fi.stardex.sisu.persistence.orm.interfaces.VapService;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;

import java.util.List;

public class UisVapService implements VapService {

    private InjectorUisVapRepository injectorUisVapRepository;

    public UisVapService(InjectorUisVapRepository injectorUisVapRepository) {
        this.injectorUisVapRepository = injectorUisVapRepository;
    }

    @Override
    public List<InjectorUisVAP> findByIsCustomAndInjectorType(boolean isCustom, InjectorType injectorType) {
        return injectorUisVapRepository.findByIsCustomAndInjectorType(isCustom, injectorType);
    }

    @Override
    public List<InjectorUisVAP> findByIsCustom(boolean isCustom) {
        return injectorUisVapRepository.findByIsCustom(isCustom);
    }
    @Override
    public List<InjectorUisVAP> findByIsCustomAndInjectorTypeAndInjectorSubType(boolean isCustom, InjectorType injectorType, InjectorSubType injectorSubType) {
        return injectorUisVapRepository.findByIsCustomAndInjectorTypeAndInjectorSubType(isCustom, injectorType, injectorSubType);
    }

    @Override
    public boolean existsById(String id) {
        return injectorUisVapRepository.existsById(id);
    }

    @Override
    public void save(VAP vap) { injectorUisVapRepository.save((InjectorUisVAP) vap); }

    @Override
    public void delete(VAP vap) {
        injectorUisVapRepository.delete((InjectorUisVAP) vap);
    }
}
