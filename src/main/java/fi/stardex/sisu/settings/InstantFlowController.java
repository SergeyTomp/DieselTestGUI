package fi.stardex.sisu.settings;

import fi.stardex.sisu.states.InstantFlowState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class InstantFlowController {

    @FXML private CheckBox flowVisibleCheckBox;
    private InstantFlowState instantFlowState;
    private I18N i18N;
    private Preferences rootPrefs;
    private final String PREF_KEY = "checkBoxFlowVisibleSelected";

    public CheckBox getFlowVisibleCheckBox() {
        return flowVisibleCheckBox;
    }

    public void setInstantFlowState(InstantFlowState instantFlowState) {
        this.instantFlowState = instantFlowState;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){
        instantFlowState.isInstantFlowStateProperty().bind(flowVisibleCheckBox.selectedProperty());
        flowVisibleCheckBox.textProperty().bind(i18N.createStringBinding("settings.flowVisible.CheckBox"));
        flowVisibleCheckBox.setSelected(rootPrefs.getBoolean(PREF_KEY, true));
        flowVisibleCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean(PREF_KEY, newValue));
    }
}
