package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//TODO this model is currently auxiliary, use it as separate during FlowController model-concept implementation
public class BackFlowRangeModel {

    private StringProperty backFlowRangeProperty = new SimpleStringProperty();

    public StringProperty backFlowRangeProperty() {
        return backFlowRangeProperty;
    }

}
