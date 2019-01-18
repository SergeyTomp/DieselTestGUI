package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlowUnitsModel {

    private StringProperty flowUnitsProperty = new SimpleStringProperty();

    public StringProperty flowUnitsProperty() {
        return flowUnitsProperty;
    }
}
