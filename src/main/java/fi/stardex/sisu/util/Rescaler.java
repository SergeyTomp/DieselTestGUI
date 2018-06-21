package fi.stardex.sisu.util;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import java.util.TreeMap;

public class Rescaler {

    private TreeMap<String, Float> mapOfLevels = new TreeMap<>();

    private FloatProperty mediumLevel = new SimpleFloatProperty(0);

    private FloatProperty fullLevel = new SimpleFloatProperty(0);

    public float getMediumLevel() {
        return mediumLevel.get();
    }

    public FloatProperty mediumLevelProperty() {
        return mediumLevel;
    }

    public void setMediumLevel(float mediumLevel) {
        this.mediumLevel.set(mediumLevel);
    }

    public float getFullLevel() {
        return fullLevel.get();
    }

    public FloatProperty fullLevelProperty() {
        return fullLevel;
    }

    public void setFullLevel(float fullLevel) {
        this.fullLevel.set(fullLevel);
    }

    public TreeMap<String, Float> getMapOfLevels() {
        return mapOfLevels;
    }
}
