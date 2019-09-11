package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Step3Model {

    private BooleanProperty step3Pause = new SimpleBooleanProperty();
    private BooleanProperty pulseIsActive = new SimpleBooleanProperty();

    public BooleanProperty step3PauseProperty() {
        return step3Pause;
    }
    public BooleanProperty pulseIsActiveProperty() {
        return pulseIsActive;
    }
}
