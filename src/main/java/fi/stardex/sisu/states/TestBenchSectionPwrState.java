package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TestBenchSectionPwrState {

    private BooleanProperty powerButtonOn = new SimpleBooleanProperty();
    private BooleanProperty powerButtonDisabled = new SimpleBooleanProperty();

    public BooleanProperty isPowerButtonOnProperty() {
        return powerButtonOn;
    }
    public BooleanProperty isPowerButtonDisabledProperty() {
        return powerButtonDisabled;
    }
}
