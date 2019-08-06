package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.NameService;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTestName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UisTestNameService implements NameService {

    InjectorUisTestNameRepository injectorUisTestNameRepository;

    public UisTestNameService(InjectorUisTestNameRepository injectorUisTestNameRepository) {
        this.injectorUisTestNameRepository = injectorUisTestNameRepository;
    }

    @Override
    public List<InjectorUisTestName> findAll() {
        return StreamSupport.stream(injectorUisTestNameRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
