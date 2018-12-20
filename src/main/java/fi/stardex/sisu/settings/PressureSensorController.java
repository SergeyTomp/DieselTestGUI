package fi.stardex.sisu.settings;

import fi.stardex.sisu.model.PressureSensorModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class PressureSensorController {

    @FXML
    private ComboBox <Integer> pressureSensorComboBox;

    private PressureSensorModel pressureSensorModel;

    private Preferences rootPrefs;

    private static final String PREF_KEY = "pressureSensorSelected";

    public void setPressureSensorModel(PressureSensorModel pressureSensorModel) {
        this.pressureSensorModel = pressureSensorModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){

        pressureSensorComboBox.setItems(FXCollections.observableArrayList(1500, 1800, 2000, 2200, 2400));

        pressureSensorModel.pressureSensorProperty().bind(pressureSensorComboBox.valueProperty());

        pressureSensorComboBox.getSelectionModel().select(Integer.valueOf(rootPrefs.getInt(PREF_KEY, 1500)));

        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putInt(PREF_KEY, newValue));

    }

}
