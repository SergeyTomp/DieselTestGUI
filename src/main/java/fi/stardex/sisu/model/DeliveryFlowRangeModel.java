package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DeliveryFlowRangeModel {

    private StringProperty deliveryFlowRange = new SimpleStringProperty();

    public StringProperty deliveryFlowRangeProperty() {
        return deliveryFlowRange;
    }
}
