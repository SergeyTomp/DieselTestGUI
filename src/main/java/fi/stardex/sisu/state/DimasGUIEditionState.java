package fi.stardex.sisu.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DimasGUIEditionState {

    private final BooleanProperty isDimasGuiEditionProperty = new SimpleBooleanProperty();

    public BooleanProperty isDimasGuiEditionProperty() {
        return isDimasGuiEditionProperty;
    }
}
