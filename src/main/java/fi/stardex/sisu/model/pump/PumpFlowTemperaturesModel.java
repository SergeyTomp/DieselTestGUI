package fi.stardex.sisu.model.pump;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PumpFlowTemperaturesModel {

    StringProperty temperature1Flow = new SimpleStringProperty();
    StringProperty temperature2Flow =  new SimpleStringProperty();

    public StringProperty temperature1FlowProperty() {
        return temperature1Flow;
    }

    public StringProperty temperature2FlowProperty() {
        return temperature2Flow;
    }
}
