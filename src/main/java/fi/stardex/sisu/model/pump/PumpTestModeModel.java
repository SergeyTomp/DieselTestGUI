package fi.stardex.sisu.model.pump;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import static fi.stardex.sisu.util.enums.Tests.TestType;
import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class PumpTestModeModel {

    private ObjectProperty<TestType> testMode = new SimpleObjectProperty<>(AUTO);

    public ObjectProperty<TestType> testModeProperty() {
        return testMode;
    }
}
