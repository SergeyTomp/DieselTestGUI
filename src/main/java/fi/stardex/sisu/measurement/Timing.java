package fi.stardex.sisu.measurement;

import javafx.beans.property.IntegerProperty;

public interface Timing {

    IntegerProperty getAdjustingTime();
    IntegerProperty getMeasuringTime();
    IntegerProperty getInitialAdjustingTime();
    IntegerProperty getInitialMeasuringTime();

}
