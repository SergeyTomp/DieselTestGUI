package fi.stardex.sisu.settings;

import fi.stardex.sisu.states.FastCodingState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class FastCodingController {

    @FXML private CheckBox fastCodingCheckBox;
    private FastCodingState fastCodingState;
    private I18N i18N;
    private Preferences rootPrefs;
    private final String PREF_KEY = "fastCodingCheckBoxSelected";

    public CheckBox getFastCodingCheckBox() {
        return fastCodingCheckBox;
    }

    public void setFastCodingState(FastCodingState fastCodingState) {
        this.fastCodingState = fastCodingState;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){
        fastCodingState.isFastCodingStateProperty().bind(fastCodingCheckBox.selectedProperty());
        fastCodingCheckBox.textProperty().bind(i18N.createStringBinding("settings.fastCoding.CheckBox"));
        fastCodingCheckBox.setSelected(rootPrefs.getBoolean(PREF_KEY, false));
        fastCodingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean(PREF_KEY, newValue));
    }
}
