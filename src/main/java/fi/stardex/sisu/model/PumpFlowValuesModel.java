package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PumpFlowValuesModel {

    private StringProperty flowProperty = new SimpleStringProperty();

    public StringProperty flowProperty() {
        return flowProperty;
    }
}
