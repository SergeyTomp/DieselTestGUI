package fi.stardex.sisu.ui.data;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.ui.controllers.additional.tabs.RLC_ReportController.RLCreportTableLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RLC_ResultsStorage implements ResultsStorage {

    private Map<Injector, List<RLCreportTableLine>> measurementResultsMap = new HashMap<>();

    public List<RLCreportTableLine> getMeasurementResults(Injector injector) {
        return measurementResultsMap.get(injector);
    }

    public void storeResults(Injector injector, List<RLCreportTableLine> RLCreportTableLines) {
        measurementResultsMap.put(injector, RLCreportTableLines);
    }

    @Override
    public List<? extends Result> getResultsList(Model model) {
        Injector injector = (Injector) model;
        return measurementResultsMap.get(injector);
    }

    @Override
    public Boolean containsKey(Model model) {
        Injector injector = (Injector) model;
        return measurementResultsMap.containsKey(injector);
    }
}
