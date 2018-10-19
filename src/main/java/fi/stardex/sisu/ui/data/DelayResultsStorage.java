package fi.stardex.sisu.ui.data;


import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.interfaces.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayResultsStorage  implements ResultsStorage {

    private Map<Model, Map<Test, Result>> delayResultsMap = new HashMap<>();

    public Map<Model, Map<Test, Result>> getDelayResultsMap() {
        return delayResultsMap;
    }

    public void storeResults(Injector injector, InjectorTest test, Result result){
        delayResultsMap.computeIfAbsent(injector, k -> new HashMap<>());
        delayResultsMap.get(injector).put(test, result);
    }

    @Override
    public List<? extends Result> getResultsList(Model model) {
        Injector injector = (Injector) model;
        if (delayResultsMap.containsKey(injector)) {
            return new ArrayList<>(delayResultsMap.get(injector).values());
        }
        return null;
    }

    @Override
    public Boolean containsKey(Model model) {
        Injector injector = (Injector) model;
        return delayResultsMap.containsKey(injector);
    }
}
