package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PumpTestModel {

    private final ObjectProperty<PumpTest> pumpTestProperty = new SimpleObjectProperty<>();

    public ObjectProperty<PumpTest> pumpTestProperty() {
        return pumpTestProperty;
    }
}
