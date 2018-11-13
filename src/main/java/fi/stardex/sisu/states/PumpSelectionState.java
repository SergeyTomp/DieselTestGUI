package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PumpSelectionState {

    private final BooleanProperty pumpSelectionProperty = new SimpleBooleanProperty();

    public BooleanProperty pumpSelectionProperty() {
        return pumpSelectionProperty;
    }

}
