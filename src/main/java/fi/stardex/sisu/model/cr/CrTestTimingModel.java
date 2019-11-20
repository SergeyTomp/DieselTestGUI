package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.measurement.Timing;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CrTestTimingModel implements Timing {

    private IntegerProperty adjustingTime = new SimpleIntegerProperty();
    private IntegerProperty measuringTime = new SimpleIntegerProperty();
    private IntegerProperty getInitialAdjustingTime = new SimpleIntegerProperty();
    private IntegerProperty getInitialMeasuringTime = new SimpleIntegerProperty();

    @Override
    public IntegerProperty getAdjustingTime() {
        return adjustingTime;
    }

    @Override
    public IntegerProperty getMeasuringTime() {
        return measuringTime;
    }

    @Override
    public IntegerProperty getInitialAdjustingTime() {
        return getInitialAdjustingTime;
    }

    @Override
    public IntegerProperty getInitialMeasuringTime() {
        return getInitialMeasuringTime;
    }
}
