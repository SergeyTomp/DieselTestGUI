package fi.stardex.sisu.util.rescalers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class BackFlowRescaler implements Rescaler {

    private Map<String, Float> mapOfLevels = new HashMap<>();

    private ObservableMap<String, Float> observableMapOfLevels = FXCollections.observableMap(mapOfLevels);

    @Override
    public Map<String, Float> getMapOfLevels() {
        return mapOfLevels;
    }

    @Override
    public ObservableMap<String, Float> getObservableMapOfLevels() {
        return observableMapOfLevels;
    }

}
