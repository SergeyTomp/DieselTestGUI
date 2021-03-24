package fi.stardex.sisu.model.cr;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;

//TODO This class should be used as CR/PUMP/HEUI settings model after all multiple separate setting controllers are combined in single CrSettingsController
public class CrSettingsModel {

    private Button differentialFmSettingsButton = new Button();
    private BooleanProperty firmwareUpdateProperty = new SimpleBooleanProperty();
    private IntegerProperty heuiMaxPressurePropery = new SimpleIntegerProperty();
    private IntegerProperty pumpMaxRpmProperty = new SimpleIntegerProperty();
    private IntegerProperty pressCorrectionProperty = new SimpleIntegerProperty();

    public Button getDifferentialFmSettingsButton() {
        return differentialFmSettingsButton;
    }
    public BooleanProperty firmwareUpdateProperty() {
        return firmwareUpdateProperty;
    }
    public IntegerProperty heuiMaxPressureProperyProperty() {
        return heuiMaxPressurePropery;
    }
    public IntegerProperty pumpMaxRpmPropertyProperty() {
        return pumpMaxRpmProperty;
    }
    public IntegerProperty pressCorrectionProperty() {
        return pressCorrectionProperty;
    }
}
