package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class InstantFlowState {

    private final BooleanProperty isInstantFlowStateProperty = new SimpleBooleanProperty();

    public BooleanProperty isInstantFlowStateProperty() {
        return isInstantFlowStateProperty;
    }
}
