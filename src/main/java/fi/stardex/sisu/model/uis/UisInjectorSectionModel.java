package fi.stardex.sisu.model.uis;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class UisInjectorSectionModel {

    private ObjectProperty<List<ToggleButton>> activeLedToggleButtonsList = new SimpleObjectProperty<>();

    public ObjectProperty<List<ToggleButton>> activeLedToggleButtonsListProperty() {
        return activeLedToggleButtonsList;
    }

    private List<Integer> arrayNumbersOfActiveLedToggleButtons = new ArrayList<>();

    private ToggleButton ledBeaker1ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker2ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker3ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker4ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker5ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker6ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker7ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker8ToggleButton = new ToggleButton();

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

    public List<Integer> getArrayNumbersOfActiveLedToggleButtons() {
        return arrayNumbersOfActiveLedToggleButtons;
    }
}
