package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BackFlowRangeModel {

    private StringProperty backFlowRange = new SimpleStringProperty();

    public StringProperty backFlowRangeProperty() {
        return backFlowRange;
    }
}
