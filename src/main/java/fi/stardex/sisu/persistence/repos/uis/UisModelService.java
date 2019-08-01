package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.ModelService;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.uis.InjectorUIS;

import java.util.List;

public class UisModelService implements ModelService {

    private InjectorUisRepository injectorUisRepository;

    public UisModelService(InjectorUisRepository injectorUisRepository) {
        this.injectorUisRepository = injectorUisRepository;
    }

    @Override
    public List<InjectorUIS> findByProducer(Producer producer) {
        return injectorUisRepository.findByManufacturer(producer);
    }

    @Override
    public List<InjectorUIS> findByProducerAndIsCustom(Producer producer, boolean isCustom) {
        return injectorUisRepository.findByManufacturerAndIsCustom(producer, isCustom);
    }

    @Override
    public InjectorUIS findByModelCode(String modelCode) {
        return injectorUisRepository.findByInjectorCode(modelCode);
    }

    @Override
    public InjectorUIS findByIsCustom(boolean isCustom) {
        return injectorUisRepository.findByIsCustom(isCustom);
    }
}
