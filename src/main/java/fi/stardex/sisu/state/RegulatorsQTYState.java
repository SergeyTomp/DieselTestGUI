package fi.stardex.sisu.state;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RegulatorsQTYState {

    private final ObjectProperty<String> regulatorsQTYState = new SimpleObjectProperty<>();

    public ObjectProperty<String> regulatorsQTYStateProperty() {
        return regulatorsQTYState;
    }
}
