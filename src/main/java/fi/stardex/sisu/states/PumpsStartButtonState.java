package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PumpsStartButtonState {

    private BooleanProperty startButton = new SimpleBooleanProperty();

    public BooleanProperty startButtonProperty() {
        return startButton;
    }
}
