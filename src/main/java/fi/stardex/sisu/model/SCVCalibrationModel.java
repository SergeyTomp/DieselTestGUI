package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SCVCalibrationModel {

    private BooleanProperty isFinished = new SimpleBooleanProperty();
    private DoubleProperty initialCurrent = new SimpleDoubleProperty();
    private BooleanProperty isSuccessful = new SimpleBooleanProperty();

    public BooleanProperty isFinishedProperty() {
        return isFinished;
    }
    public DoubleProperty initialCurrentProperty() {
        return initialCurrent;
    }
    public BooleanProperty isSuccessfulProperty() {
        return isSuccessful;
    }
}
