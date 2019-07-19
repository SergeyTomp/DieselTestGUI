package fi.stardex.sisu.model.cr;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CoilTwoPulseParametersModel {

    private IntegerProperty width_2 = new SimpleIntegerProperty();
    private DoubleProperty shift = new SimpleDoubleProperty();
    private boolean valueFactorySetting;

    public IntegerProperty width_2Property() {
        return width_2;
    }
    public DoubleProperty shiftProperty() {
        return shift;
    }

    public boolean isValueFactorySetting() {
        return valueFactorySetting;
    }

    public void setValueFactorySetting(boolean valueFactorySetting) {
        this.valueFactorySetting = valueFactorySetting;
    }
}
