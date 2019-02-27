package fi.stardex.sisu.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PumpPressureRegulatorModel {

    private IntegerProperty pressureRegValue = new SimpleIntegerProperty();

    public IntegerProperty pressureRegProperty() {
        return pressureRegValue;
    }
}
