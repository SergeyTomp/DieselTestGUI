package fi.stardex.sisu.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CoilOnePulseParametersModel {

    private IntegerProperty width = new SimpleIntegerProperty();
    private DoubleProperty period = new SimpleDoubleProperty();

    public IntegerProperty widthProperty() {
        return width;
    }
    public DoubleProperty periodProperty() {
        return period;
    }
}
