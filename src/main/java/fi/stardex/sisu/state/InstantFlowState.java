package fi.stardex.sisu.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class InstantFlowState {

    private final BooleanProperty isInstantFlowStateProperty = new SimpleBooleanProperty();

    public BooleanProperty isInstantFlowStateProperty() {
        return isInstantFlowStateProperty;
    }
}
