package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TestBenchSectionModel {

    private IntegerProperty currentRPM = new SimpleIntegerProperty();
    private IntegerProperty targetRPM = new SimpleIntegerProperty();
    private BooleanProperty powerButtonOn = new SimpleBooleanProperty();
    private BooleanProperty powerButtonDisabled = new SimpleBooleanProperty();

    public IntegerProperty currentRPMProperty() {
        return currentRPM;
    }
    public IntegerProperty targetRPMProperty() {
        return targetRPM;
    }
    public BooleanProperty isPowerButtonOnProperty() {
        return powerButtonOn;
    }
    public BooleanProperty isPowerButtonDisabledProperty() {
        return powerButtonDisabled;
    }
}
