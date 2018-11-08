package fi.stardex.sisu.ui.controllers.pumps.settings;

import fi.stardex.sisu.state.PressureSensorState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class PressureSensorController {

    @FXML
    private ComboBox <Integer> pressureSensorComboBox;
    private PressureSensorState pressureSensorState;
    private Preferences rootPrefs;
    private final String PREF_KEY = "pressureSensorSelected";

    public ComboBox <Integer> getPressureSensorComboBox() {
        return pressureSensorComboBox;
    }

    public void setPressureSensorState(PressureSensorState pressureSensorState) {
        this.pressureSensorState = pressureSensorState;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){
        pressureSensorComboBox.setItems(FXCollections.observableArrayList(1500, 1800, 2000, 2200, 2400));
        pressureSensorState.pressureSensorStateProperty().bind(pressureSensorComboBox.valueProperty());
        pressureSensorComboBox.getSelectionModel().select(new Integer(rootPrefs.getInt(PREF_KEY, 1500)));
        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putInt(PREF_KEY, newValue));
    }
}
