package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.ModelService;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.pump.Pump;

import java.util.List;

public class PumpModelService implements ModelService {

    private PumpRepository pumpRepository;

    public PumpModelService(PumpRepository pumpRepository) {
        this.pumpRepository = pumpRepository;
    }

    @Override
    public List<Pump> findByProducer(Producer producer) {
        return pumpRepository.findByManufacturer(producer);
    }

    @Override
    public List<Pump> findByProducerAndIsCustom(Producer producer, boolean isCustom) {
        return pumpRepository.findAllByManufacturerAndCustom(producer, isCustom);
    }

    @Override
    public Model findByModelCode(String modelCode) {
        return pumpRepository.findByPumpCode(modelCode);
    }

    @Override
    public List<? extends Model> findByIsCustom(boolean isCustom) {
        return pumpRepository.findByCustom(isCustom);
    }

    @Override
    public boolean existsByModelCode(String code) {
        return pumpRepository.existsByPumpCode(code);
    }

    @Override
    public void save(Model model) {
        pumpRepository.save((Pump) model);
    }

    @Override
    public void delete(Model model) { pumpRepository.delete((Pump) model); }
}
