package fi.stardex.sisu.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DimasState {

    private final BooleanProperty isDimasGuiProperty = new SimpleBooleanProperty();

    public BooleanProperty isDimasGuiProperty() {
        return isDimasGuiProperty;
    }
}
