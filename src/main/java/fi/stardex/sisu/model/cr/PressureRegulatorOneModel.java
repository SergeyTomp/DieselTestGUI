package fi.stardex.sisu.model.cr;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PressureRegulatorOneModel {

    private IntegerProperty pressureRegOneProperty = new SimpleIntegerProperty();

    public IntegerProperty pressureRegOneProperty() {
        return pressureRegOneProperty;
    }
}