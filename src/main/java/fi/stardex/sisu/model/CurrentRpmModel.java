package fi.stardex.sisu.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CurrentRpmModel {

    private IntegerProperty currentRPM = new SimpleIntegerProperty();

    public IntegerProperty currentRPMProperty() {
        return currentRPM;
    }
}
