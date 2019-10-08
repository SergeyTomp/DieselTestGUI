package fi.stardex.sisu.model.uis;

import javafx.beans.property.*;
import javafx.scene.control.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class UisInjectorSectionModel {

    private ObjectProperty<List<ToggleButton>> activeLedToggleButtonsList = new SimpleObjectProperty<>();
    private List<Integer> arrayNumbersOfActiveLedToggleButtons = new ArrayList<>();

    private ToggleButton ledBeaker1ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker2ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker3ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker4ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker5ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker6ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker7ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker8ToggleButton = new ToggleButton();
    private IntegerProperty width_1 = new SimpleIntegerProperty();
    private IntegerProperty width_2 = new SimpleIntegerProperty();
    private IntegerProperty shift = new SimpleIntegerProperty();
    private IntegerProperty angle_1 = new SimpleIntegerProperty();
    private IntegerProperty angle_2 = new SimpleIntegerProperty();
    private BooleanProperty powerButton = new SimpleBooleanProperty();
    private IntegerProperty pressureSpinner = new SimpleIntegerProperty();

    public ToggleButton getLedBeaker1ToggleButton() {
        return ledBeaker1ToggleButton;
    }
    public ToggleButton getLedBeaker2ToggleButton() {
        return ledBeaker2ToggleButton;
    }
    public ToggleButton getLedBeaker3ToggleButton() {
        return ledBeaker3ToggleButton;
    }
    public ToggleButton getLedBeaker4ToggleButton() {
        return ledBeaker4ToggleButton;
    }
    public ToggleButton getLedBeaker5ToggleButton() {
        return ledBeaker5ToggleButton;
    }
    public ToggleButton getLedBeaker6ToggleButton() {
        return ledBeaker6ToggleButton;
    }
    public ToggleButton getLedBeaker7ToggleButton() {
        return ledBeaker7ToggleButton;
    }
    public ToggleButton getLedBeaker8ToggleButton() {
        return ledBeaker8ToggleButton;
    }
    public BooleanProperty powerButtonProperty() {
        return powerButton;
    }
    public IntegerProperty pressureSpinnerProperty() {
        return pressureSpinner;
    }

    public IntegerProperty width_1Property() {
        return width_1;
    }
    public IntegerProperty width_2Property() {
        return width_2;
    }
    public IntegerProperty shiftProperty() {
        return shift;
    }
    public IntegerProperty angle_1Property() {
        return angle_1;
    }
    public IntegerProperty angle_2Property() {
        return angle_2;
    }
    public List<Integer> getArrayNumbersOfActiveLedToggleButtons() {
        return arrayNumbersOfActiveLedToggleButtons;
    }
    public ObjectProperty<List<ToggleButton>> activeLedToggleButtonsListProperty() {
        return activeLedToggleButtonsList;
    }
}
