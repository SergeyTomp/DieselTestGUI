package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.ModelService;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.uis.InjectorUIS;
import fi.stardex.sisu.persistence.orm.uis.ReferenceUIS;

import java.util.List;
import java.util.Optional;

public class UisModelService implements ModelService {

    private InjectorUisRepository injectorUisRepository;
    private ReferenceUisRepository referenceUisRepository;

    public UisModelService(InjectorUisRepository injectorUisRepository,
                           ReferenceUisRepository referenceUisRepository) {
        this.injectorUisRepository = injectorUisRepository;
        this.referenceUisRepository = referenceUisRepository;
    }

    @Override
    public List<InjectorUIS> findByProducer(Producer producer) {
        return injectorUisRepository.findByManufacturer(producer);
    }

    @Override
    public List<InjectorUIS> findByProducerAndIsCustom(Producer producer, boolean isCustom) {
        return injectorUisRepository.findByManufacturerAndIsCustom(producer, isCustom);
    }

    //TODO Place here code for full search (InjectorUIS -> ReferenceUIS) when REFERENCE table becomes actual and is filled in.
    @Override
    public InjectorUIS findByModelCode(String modelCode) {
        // Simplified search is done so far REFERENCE table is not relevant.
        // In case Model found has empty fields then reference Model search in REFERENCE table should be done.
        // Just uncomment code below.
//        if (injectorUisRepository.findByInjectorCode(modelCode).getVAP() == null) {
//            ReferenceUIS referenceUIS = referenceUisRepository.findByKeyInjector(modelCode);
//            Model sourceModel = referenceUIS.getRefInjector();
//            modelCode = injectorUisRepository.findByInjectorCode(sourceModel.getModelCode()).getModelCode();
//        }
        return injectorUisRepository.findByInjectorCode(modelCode);
    }

    @Override
    public List<InjectorUIS> findByIsCustom(boolean isCustom) {
        return injectorUisRepository.findByIsCustom(isCustom);
    }

    @Override
    public boolean existsByModelCode(String code) {
        return injectorUisRepository.existsByInjectorCode(code);
    }

    @Override
    public void save(Model model) {
        injectorUisRepository.save((InjectorUIS)model);
    }

    @Override
    public void delete(Model model) {
        injectorUisRepository.delete((InjectorUIS)model);
    }
}
