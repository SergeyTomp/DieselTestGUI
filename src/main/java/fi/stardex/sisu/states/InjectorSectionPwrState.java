package fi.stardex.sisu.states;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class InjectorSectionPwrState {

    private BooleanProperty powerButton = new SimpleBooleanProperty();

    public BooleanProperty powerButtonProperty() {
        return powerButton;
    }
}
