package fi.stardex.sisu.settings;

import fi.stardex.sisu.model.RegulatorsQTYModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class RegulatorsQTYController {

    @FXML
    private ComboBox<String> regulatorsConfigComboBox;

    private RegulatorsQTYModel regulatorsQTYModel;

    private Preferences rootPreferences;

    private final String PREF_KEY = "regulatorsConfigSelected";

    public void setPressureSensorState(RegulatorsQTYModel regulatorsQTYModel) {
        this.regulatorsQTYModel = regulatorsQTYModel;
    }

    public void setRootPreferences(Preferences rootPreferences) {
        this.rootPreferences = rootPreferences;
    }

    @PostConstruct
    public void init(){

        regulatorsConfigComboBox.setItems(FXCollections.observableArrayList("3", "2", "1"));
        regulatorsQTYModel.regulatorsQTYProperty().bind(regulatorsConfigComboBox.valueProperty());
        regulatorsConfigComboBox.getSelectionModel().select(rootPreferences.get(PREF_KEY, "3"));
        regulatorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPreferences.put(PREF_KEY, newValue));

    }

}