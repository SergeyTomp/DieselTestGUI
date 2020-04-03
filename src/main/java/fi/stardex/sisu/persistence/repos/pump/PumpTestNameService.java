package fi.stardex.sisu.persistence.repos.pump;

import fi.stardex.sisu.persistence.orm.interfaces.NameService;
import fi.stardex.sisu.persistence.orm.pump.PumpTestName;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PumpTestNameService  implements NameService {

    private PumpTestNameRepository pumpTestNameRepository;

    public PumpTestNameService(PumpTestNameRepository pumpTestNameRepository) {
        this.pumpTestNameRepository = pumpTestNameRepository;
    }

    @Override
    public List<PumpTestName> findAll() {
        return StreamSupport.stream(pumpTestNameRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }
}
