package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BackFlowUnitsModel {

    private StringProperty backFlowUnits = new SimpleStringProperty();

    public StringProperty backFlowUnitsProperty() {
        return backFlowUnits;
    }
}
