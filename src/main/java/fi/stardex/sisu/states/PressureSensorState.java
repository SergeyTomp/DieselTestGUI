package fi.stardex.sisu.states;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PressureSensorState {

    private final IntegerProperty pressureSensorState = new SimpleIntegerProperty();

    public IntegerProperty pressureSensorStateProperty() {
        return pressureSensorState;
    }
}
