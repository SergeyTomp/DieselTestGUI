package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.interfaces.TestService;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;

import java.util.List;

public class PumpTestService implements TestService {

    private PumpTestRepository pumpTestRepository;

    public PumpTestService(PumpTestRepository pumpTestRepository) {
        this.pumpTestRepository = pumpTestRepository;
    }

    @Override
    public List<PumpTest> findAllByModel(Model model) {
        return pumpTestRepository.findAllByPump(model);
    }

    @Override
    public List<? extends Test> findAllByIsCustom(boolean isCustom) {
        return pumpTestRepository.findAllByIsCustom(isCustom);
    }

    @Override
    public void save(Test test) {
        pumpTestRepository.save((PumpTest) test);
    }

    @Override
    public void delete(Test test) {
        pumpTestRepository.delete((PumpTest) test);
    }

    @Override
    public void update(Test test) {
        save(test);
    }
}
