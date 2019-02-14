package fi.stardex.sisu.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TargetRpmModel {

    private IntegerProperty targetRPM = new SimpleIntegerProperty();

    public IntegerProperty targetRPMProperty() {
        return targetRPM;
    }
}
