package fi.stardex.sisu.settings;

import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class DimasGuiEditionController {

    @FXML private CheckBox isDIMASCheckBox;
    private DimasGUIEditionState dimasGUIEditionState;
    private I18N i18N;
    private Preferences rootPrefs;
    private final String PREF_KEY = "isDIMASCheckBoxSelected";

    public CheckBox getIsDIMASCheckBox() {
        return isDIMASCheckBox;
    }

    public void setDimasGUIEditionState(DimasGUIEditionState dimasGUIEditionState) {
        this.dimasGUIEditionState = dimasGUIEditionState;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){

        dimasGUIEditionState.isDimasGuiEditionProperty().bind(isDIMASCheckBox.selectedProperty());
        isDIMASCheckBox.textProperty().bind(i18N.createStringBinding("settings.isDIMAS.CheckBox"));
        isDIMASCheckBox.setSelected(rootPrefs.getBoolean(PREF_KEY, true));
        isDIMASCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean(PREF_KEY, newValue));
    }
}
