package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.interfaces.ProducerService;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;

import java.util.List;

public class PumpProducerService implements ProducerService {

    private ManufacturerPumpRepository manufacturerPumpRepository;

    public PumpProducerService(ManufacturerPumpRepository manufacturerPumpRepository) {
        this.manufacturerPumpRepository = manufacturerPumpRepository;
    }

    @Override
    public List<ManufacturerPump> findAll() {
        return manufacturerPumpRepository.findAll();
    }

    @Override
    public List<? extends Producer> findByIsCustom(boolean isCustom) {
        return manufacturerPumpRepository.findByCustom(isCustom);
    }

    @Override
    public void save(Producer producer) {
        manufacturerPumpRepository.save((ManufacturerPump) producer);
    }

    @Override
    public void delete(Producer producer) {
        manufacturerPumpRepository.delete((ManufacturerPump) producer);
    }
}
