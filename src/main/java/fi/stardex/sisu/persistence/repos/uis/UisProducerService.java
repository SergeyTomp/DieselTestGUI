package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.interfaces.ProducerService;
import fi.stardex.sisu.persistence.orm.uis.ManufacturerUIS;

import java.util.List;

public class UisProducerService implements ProducerService {

    private ManufacturerUisRepository manufacturerUisRepository;

    public UisProducerService(ManufacturerUisRepository manufacturerUisRepository) {
        this.manufacturerUisRepository = manufacturerUisRepository;
    }

    @Override
    public List<ManufacturerUIS> findAll() {
        return manufacturerUisRepository.findAll();
    }

    @Override
    public List<? extends Producer> findByIsCustom(boolean isCustom) {
        return manufacturerUisRepository.findByIsCustom(isCustom);
    }

    @Override
    public void save(Producer producer) {
        manufacturerUisRepository.save((ManufacturerUIS) producer);
    }

    @Override
    public void delete(Producer producer) {
        manufacturerUisRepository.delete((ManufacturerUIS) producer);
    }
}
