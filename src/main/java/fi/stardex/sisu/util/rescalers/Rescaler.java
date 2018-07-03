package fi.stardex.sisu.util.rescalers;

import javafx.collections.ObservableMap;

import java.util.Map;

public interface Rescaler {

    Map<String, Float> getMapOfLevels();

    ObservableMap<String, Float> getObservableMapOfLevels();

}
