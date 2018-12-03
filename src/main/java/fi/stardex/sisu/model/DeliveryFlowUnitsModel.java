package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DeliveryFlowUnitsModel {

    private StringProperty deliveryFlowUnits = new SimpleStringProperty();

    public StringProperty deliveryFlowUnitsProperty() {
        return deliveryFlowUnits;
    }
}
