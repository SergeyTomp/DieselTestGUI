package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PumpTestWrapper {

    private PumpTest pumpTest;
    private BooleanProperty isIncluded = new SimpleBooleanProperty();

    public PumpTest getPumpTest() {
        return pumpTest;
    }

    public BooleanProperty isIncludedProperty() {
        return isIncluded;
    }

    public PumpTestWrapper(PumpTest pumpTest, boolean isIncluded) {
        this.pumpTest = pumpTest;
        this.isIncluded.setValue(isIncluded);
    }

    @Override
    public String toString() {
        return pumpTest.toString();
    }
}
