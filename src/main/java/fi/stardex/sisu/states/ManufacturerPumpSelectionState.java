package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ManufacturerPumpSelectionState {

    private final BooleanProperty manufacturerPumpSelectionProperty = new SimpleBooleanProperty();

    public BooleanProperty manufacturerPumpSelectionProperty() {
        return manufacturerPumpSelectionProperty;
    }

}
