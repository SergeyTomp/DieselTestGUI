package fi.stardex.sisu.ui.data;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodingResultsStorage implements ResultsStorage {

    private Map<Injector, List<CodingResult>> codingResultMap = new HashMap<>();

    public Map<Injector, List<CodingResult>> getCodingResultMap() {
        return codingResultMap;
    }

    @Override
    public List<? extends Result> getResultsList(Model model) {
        Injector injector = (Injector) model;
        if (codingResultMap.containsKey(injector)) {
            return codingResultMap.get(injector);
        }
        return null;
    }

    @Override
    public Boolean containsKey(Model model) {
        Injector injector = (Injector) model;
        return codingResultMap.containsKey(injector);
    }
}
