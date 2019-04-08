package fi.stardex.sisu.states;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ToggleButton;

import java.util.List;

public class InjectorControllersState {

    private ObjectProperty<List<ToggleButton>> activeLedToggleButtonsList = new SimpleObjectProperty<>();

    public ObjectProperty<List<ToggleButton>> activeLedToggleButtonsListProperty() {
        return activeLedToggleButtonsList;
    }
}
