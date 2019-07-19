package fi.stardex.sisu.model.cr;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CoilOnePulseParametersModel {

    private IntegerProperty width = new SimpleIntegerProperty();
    private DoubleProperty period = new SimpleDoubleProperty();
    private boolean valueFactorySetting;

    public IntegerProperty widthProperty() {
        return width;
    }
    public DoubleProperty periodProperty() {
        return period;
    }

    public boolean isValueFactorySetting() {
        return valueFactorySetting;
    }

    public void setValueFactorySetting(boolean valueFactorySetting) {
        this.valueFactorySetting = valueFactorySetting;
    }
}
