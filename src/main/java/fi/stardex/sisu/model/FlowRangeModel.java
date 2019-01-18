package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlowRangeModel {

    private StringProperty flowRangeProperty = new SimpleStringProperty();

    public StringProperty flowRangeProperty() {
        return flowRangeProperty;
    }
}
