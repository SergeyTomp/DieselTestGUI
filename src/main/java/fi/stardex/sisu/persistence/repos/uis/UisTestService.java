package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.interfaces.TestService;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;

import java.util.List;

public class UisTestService implements TestService {

    private InjectorUisTestRepository injectorUisTestRepository;

    public UisTestService(InjectorUisTestRepository injectorUisTestRepository) {
        this.injectorUisTestRepository = injectorUisTestRepository;
    }

    @Override
    public List<InjectorUisTest> findAllByInjector(Model model) {
        return injectorUisTestRepository.findAllByInjector(model);
    }

    @Override
    public List<? extends Test> findAllByIsCustom(boolean isCustom) {
        return injectorUisTestRepository.findAllByIsCustom(isCustom);
    }
}
