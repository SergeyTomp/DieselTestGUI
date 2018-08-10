package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MeasurementResultsStorage {

    private Map<Injector, List<MeasurementResult>> measurementResultsMap = new HashMap<>();

    public List<MeasurementResult> getMeasurementResults(Injector injector) {
        return measurementResultsMap.get(injector);
    }

    public void putMeasurementResults(Injector injector, List<MeasurementResult> measurementResults) {
        measurementResultsMap.put(injector, measurementResults);
    }

    public List<MeasurementResult> getResultsList(Model model) {
        Injector injector = (Injector) model;
        return measurementResultsMap.get(injector);
    }

    public Boolean containsKey(Model model) {
        Injector injector = (Injector) model;
        return measurementResultsMap.containsKey(injector);
    }
}
