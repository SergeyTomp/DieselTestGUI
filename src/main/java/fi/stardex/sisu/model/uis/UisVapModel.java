package fi.stardex.sisu.model.uis;

import javafx.beans.property.*;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class UisVapModel {

    private IntegerProperty boostUProperty = new SimpleIntegerProperty(BOOST_U_SPINNER_INIT);
    private IntegerProperty batteryU = new SimpleIntegerProperty(BATTERY_U_SPINNER_INIT);
    private DoubleProperty boostI = new SimpleDoubleProperty(BOOST_I_SPINNER_INIT);
    private DoubleProperty firstI = new SimpleDoubleProperty(FIRST_I_SPINNER_INIT);
    private IntegerProperty firstW = new SimpleIntegerProperty(FIRST_W_SPINNER_INIT);
    private DoubleProperty secondI = new SimpleDoubleProperty(SECOND_I_SPINNER_INIT);
    private IntegerProperty negativeU = new SimpleIntegerProperty(NEGATIVE_U_SPINNER_INIT);
    private BooleanProperty boostDisable = new SimpleBooleanProperty();
    private DoubleProperty boostI2 = new SimpleDoubleProperty(0);
    private DoubleProperty firstI2 = new SimpleDoubleProperty(0);
    private IntegerProperty firstW2 = new SimpleIntegerProperty(0);
    private DoubleProperty secondI2 = new SimpleDoubleProperty(0);
    private IntegerProperty bipWindow = new SimpleIntegerProperty();
    private IntegerProperty bipPWM = new SimpleIntegerProperty();

    public IntegerProperty boostUProperty() {
        return boostUProperty;
    }
    public IntegerProperty batteryUProperty() {
        return batteryU;
    }
    public DoubleProperty boostIProperty() {
        return boostI;
    }
    public DoubleProperty firstIProperty() {
        return firstI;
    }
    public IntegerProperty firstWProperty() {
        return firstW;
    }
    public DoubleProperty secondIProperty() {
        return secondI;
    }
    public IntegerProperty negativeUProperty() {
        return negativeU;
    }
    public BooleanProperty boostDisableProperty() {
        return boostDisable;
    }
    public DoubleProperty boostI2Property() {
        return boostI2;
    }
    public DoubleProperty firstI2Property() {
        return firstI2;
    }
    public IntegerProperty firstW2Property() {
        return firstW2;
    }
    public DoubleProperty secondI2Property() {
        return secondI2;
    }
    public IntegerProperty bipWindowProperty() {
        return bipWindow;
    }
    public IntegerProperty bipPWMProperty() {
        return bipPWM;
    }
}
