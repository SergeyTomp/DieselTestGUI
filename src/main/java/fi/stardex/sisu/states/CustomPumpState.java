package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class CustomPumpState {

    private final BooleanProperty customPumpProperty = new SimpleBooleanProperty();

    public BooleanProperty customPumpProperty() {
        return customPumpProperty;
    }

}
