package fi.stardex.sisu.states;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegulatorsQTYState {

    private final StringProperty regulatorsQTYState = new SimpleStringProperty();

    public StringProperty regulatorsQTYStateProperty() {
        return regulatorsQTYState;
    }
}
