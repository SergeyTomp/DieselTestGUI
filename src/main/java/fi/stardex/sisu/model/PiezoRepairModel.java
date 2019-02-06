package fi.stardex.sisu.model;

import fi.stardex.sisu.util.enums.VoltageRange;
import javafx.beans.property.*;

public class PiezoRepairModel {

    private ObjectProperty<VoltageRange> voltageRangeObjectProperty = new SimpleObjectProperty<>();

    private DoubleProperty voltageValue = new SimpleDoubleProperty();

    private BooleanProperty startMeasure = new SimpleBooleanProperty();

    public ObjectProperty<VoltageRange> voltageRangeObjectProperty() {
        return voltageRangeObjectProperty;
    }

    public DoubleProperty voltageValueProperty() {
        return voltageValue;
    }

    public BooleanProperty startMeasureProperty() {
        return startMeasure;
    }
}
