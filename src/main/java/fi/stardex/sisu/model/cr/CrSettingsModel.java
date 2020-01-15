package fi.stardex.sisu.model.cr;

import javafx.scene.control.Button;

//TODO This class should be used as CR/PUMP/HEUI settings model after all multiple separate setting controllers are combined in single CrSettingsController
public class CrSettingsModel {

    private Button differentialFmSettingsButton = new Button();

    public Button getDifferentialFmSettingsButton() {
        return differentialFmSettingsButton;
    }
}
