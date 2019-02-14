package fi.stardex.sisu.model;

import fi.stardex.sisu.util.enums.TestSpeed;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PumpTestSpeedModel {

    private ObjectProperty<TestSpeed> testSpeed = new SimpleObjectProperty<>();

    public ObjectProperty<TestSpeed> testSpeedProperty() {
        return testSpeed;
    }
}
