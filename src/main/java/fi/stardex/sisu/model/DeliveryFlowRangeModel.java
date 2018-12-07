package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//TODO this model is currently auxiliary, use it as separate during FlowController model-concept implementation
public class DeliveryFlowRangeModel {

    private StringProperty deliveryFlowRange = new SimpleStringProperty();

    public StringProperty deliveryFlowRangeProperty() {
        return deliveryFlowRange;
    }
}
