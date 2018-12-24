package fi.stardex.sisu.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PumpTimeProgressModel {

    private IntegerProperty adjustingTimeProperty = new SimpleIntegerProperty();

    private IntegerProperty measurementTimeProperty = new SimpleIntegerProperty();

    public IntegerProperty adjustingTimeProperty() {
        return adjustingTimeProperty;
    }

    public IntegerProperty measurementTimeProperty() {
        return measurementTimeProperty;
    }

}
