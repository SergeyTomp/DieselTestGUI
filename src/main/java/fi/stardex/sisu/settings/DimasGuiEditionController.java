package fi.stardex.sisu.settings;

import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import fi.stardex.sisu.version.StandFirmwareVersion;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM_4_CH;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions.STAND_FORTE;

public class DimasGuiEditionController {

    @FXML private CheckBox isDIMASCheckBox;
    private DimasGUIEditionState dimasGUIEditionState;
    private I18N i18N;
    private Preferences rootPrefs;
    private final String PREF_KEY = "isDIMASCheckBoxSelected";
    private FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion;
    private FirmwareVersion<StandFirmwareVersion.StandVersions> standFirmwareVersion;

    public void setDimasGUIEditionState(DimasGUIEditionState dimasGUIEditionState) {
        this.dimasGUIEditionState = dimasGUIEditionState;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }
    public void setStandFirmwareVersion(FirmwareVersion<StandFirmwareVersion.StandVersions> standFirmwareVersion) {
        this.standFirmwareVersion = standFirmwareVersion;
    }

    @PostConstruct
    public void init(){

        dimasGUIEditionState.isDimasGuiEditionProperty().bind(isDIMASCheckBox.selectedProperty());
        isDIMASCheckBox.textProperty().bind(i18N.createStringBinding("settings.isDIMAS.CheckBox"));
        isDIMASCheckBox.setSelected(rootPrefs.getBoolean(PREF_KEY, true));
        isDIMASCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putBoolean(PREF_KEY, newValue));

        standFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == STAND_FORTE) {
                isDIMASCheckBox.setSelected(false);
                isDIMASCheckBox.setDisable(true);
            }
            else { isDIMASCheckBox.setDisable(false); }
        });

        flowFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == STAND_FM || newValue == STAND_FM_4_CH) { isDIMASCheckBox.setDisable(false); }
        });
    }
}
