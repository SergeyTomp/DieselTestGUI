package fi.stardex.sisu.model.cr;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PressureRegulatorOneModel {

    private IntegerProperty pressureRegOneProperty = new SimpleIntegerProperty();
    private BooleanProperty overPressure = new SimpleBooleanProperty();

    public IntegerProperty pressureRegOneProperty() {
        return pressureRegOneProperty;
    }
    public BooleanProperty overPressureProperty() {
        return overPressure;
    }
}
