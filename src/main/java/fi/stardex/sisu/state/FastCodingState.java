package fi.stardex.sisu.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FastCodingState {

    private final BooleanProperty fastCodingStateProperty = new SimpleBooleanProperty();

    public BooleanProperty isFastCodingStateProperty() {
        return fastCodingStateProperty;
    }
}
