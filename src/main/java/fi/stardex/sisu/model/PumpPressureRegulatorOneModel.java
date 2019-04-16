package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PumpPressureRegulatorOneModel {

    private IntegerProperty pressureRegValue = new SimpleIntegerProperty();
    private BooleanProperty switchButton = new SimpleBooleanProperty();

    public IntegerProperty pressureRegProperty() {
        return pressureRegValue;
    }

    public BooleanProperty switchButtonProperty() {
        return switchButton;
    }
}
