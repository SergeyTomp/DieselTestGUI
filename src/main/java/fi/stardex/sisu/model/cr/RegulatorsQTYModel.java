package fi.stardex.sisu.model.cr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegulatorsQTYModel {

    private final StringProperty regulatorsQTYProperty = new SimpleStringProperty();

    public StringProperty regulatorsQTYProperty() {
        return regulatorsQTYProperty;
    }

}
