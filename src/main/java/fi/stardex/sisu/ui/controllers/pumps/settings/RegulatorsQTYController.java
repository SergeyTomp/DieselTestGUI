package fi.stardex.sisu.ui.controllers.pumps.settings;

import fi.stardex.sisu.state.RegulatorsQTYState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class RegulatorsQTYController {

    @FXML
    private ComboBox<String> regulatorsConfigComboBox;
    private RegulatorsQTYState regulatorsQTYState;
    private Preferences rootPrefs;
    private final String PREF_KEY = "regulatorsConfigSelected";

    public ComboBox<String> getRegulatorsConfigComboBox() {
        return regulatorsConfigComboBox;
    }

    public void setPressureSensorState(RegulatorsQTYState regulatorsQTYState) {
        this.regulatorsQTYState = regulatorsQTYState;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){
        regulatorsConfigComboBox.setItems(FXCollections.observableArrayList("3", "2", "1"));
        regulatorsQTYState.regulatorsQTYStateProperty().bind(regulatorsConfigComboBox.valueProperty());
        regulatorsConfigComboBox.getSelectionModel().select(rootPrefs.get(PREF_KEY, "3"));
        regulatorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue));
    }
}
