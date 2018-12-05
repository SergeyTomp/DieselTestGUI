package fi.stardex.sisu.states;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PressureSensorModel {

    private final IntegerProperty pressureSensorProperty = new SimpleIntegerProperty();

    public IntegerProperty pressureSensorProperty() {
        return pressureSensorProperty;
    }
}
