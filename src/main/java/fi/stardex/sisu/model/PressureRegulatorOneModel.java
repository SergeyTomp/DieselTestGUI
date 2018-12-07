package fi.stardex.sisu.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PressureRegulatorOneModel {

    private IntegerProperty pressureRegOne = new SimpleIntegerProperty();

    public IntegerProperty pressureRegOneProperty() {
        return pressureRegOne;
    }
}
