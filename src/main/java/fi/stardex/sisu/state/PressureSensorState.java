package fi.stardex.sisu.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PressureSensorState {

    private final ObjectProperty<Integer> pressureSensorState = new SimpleObjectProperty<>();

    public ObjectProperty<Integer> pressureSensorStateProperty() {
        return pressureSensorState;
    }
}
