package fi.stardex.sisu.ui.controllers.cr.tabs.settings;

import fi.stardex.sisu.model.cr.CrSettingsModel;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.annotation.PostConstruct;

public class DifferentialFlowMeterButtonController {

    @FXML private Button diffFmSettingsButton;

    private CrSettingsModel crSettingsModel;
    private I18N i18N;

    public void setCrSettingsModel(CrSettingsModel crSettingsModel) {
        this.crSettingsModel = crSettingsModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {
        diffFmSettingsButton.setOnAction(actionEvent -> crSettingsModel.getDifferentialFmSettingsButton().fire());
        bindingI18N();
    }

    private void bindingI18N() {

        diffFmSettingsButton.textProperty().bind(i18N.createStringBinding("h4.tab.settings"));
    }
}
