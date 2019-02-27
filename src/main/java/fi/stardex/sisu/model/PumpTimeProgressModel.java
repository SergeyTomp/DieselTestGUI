package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PumpTimeProgressModel {

    private IntegerProperty adjustingTimeProperty = new SimpleIntegerProperty();

    private IntegerProperty measurementTimeProperty = new SimpleIntegerProperty();

    private BooleanProperty measurementTimeEnabled = new SimpleBooleanProperty();

    public IntegerProperty adjustingTimeProperty() {
        return adjustingTimeProperty;
    }

    public IntegerProperty measurementTimeProperty() {
        return measurementTimeProperty;
    }

    public BooleanProperty measurementTimeEnabledProperty() {
        return measurementTimeEnabled;
    }
}
