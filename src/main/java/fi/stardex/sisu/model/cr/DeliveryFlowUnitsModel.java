package fi.stardex.sisu.model.cr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//TODO this model is currently auxiliary, use it as separate during FlowController model-concept implementation
public class DeliveryFlowUnitsModel {

    private StringProperty deliveryFlowUnitsProperty = new SimpleStringProperty();

    public StringProperty deliveryFlowUnitsProperty() {
        return deliveryFlowUnitsProperty;
    }

}
