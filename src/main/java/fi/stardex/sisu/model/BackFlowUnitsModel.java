package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//TODO this model is currently auxiliary, use it as separate during FlowController model-concept implementation
public class BackFlowUnitsModel {

    private StringProperty backFlowUnitsProperty = new SimpleStringProperty();

    public StringProperty backFlowUnitsProperty() {
        return backFlowUnitsProperty;
    }

}
