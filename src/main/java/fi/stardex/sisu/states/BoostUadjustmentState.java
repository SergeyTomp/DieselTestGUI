package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class BoostUadjustmentState {

    private BooleanProperty boostUadjustmentState = new SimpleBooleanProperty();

    public BooleanProperty boostUadjustmentStateProperty() {
        return boostUadjustmentState;
    }
}
