package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SettingsModel {

    private BooleanProperty firmwareUpdateProperty = new SimpleBooleanProperty();

    public BooleanProperty firmwareUpdateProperty() {
        return firmwareUpdateProperty;
    }
}
