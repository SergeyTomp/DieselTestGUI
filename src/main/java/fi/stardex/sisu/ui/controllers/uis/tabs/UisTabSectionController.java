package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;

public class UisTabSectionController {

    @FXML private GridPane settingsGridPane;
    @FXML private Tab tabFlow;
    @FXML private Tab tabVoltage;
    @FXML private Tab tabDelay;
    @FXML private Tab tabRLC;
    @FXML private Tab tabReport;
    @FXML private Tab tabSettings;
    @FXML private Tab tabInfo;
    @FXML private Tab tabMechanical;

    private I18N i18N;

    public GridPane getSettingsGridPane() {
        return settingsGridPane;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {
        bindingI18N();
    }

    private void bindingI18N() {
        tabDelay.textProperty().bind(i18N.createStringBinding("additional.delay"));
        tabVoltage.textProperty().bind(i18N.createStringBinding("additional.voltage"));
        tabRLC.textProperty().bind(i18N.createStringBinding("h4.tab.RLC"));
        tabSettings.textProperty().bind(i18N.createStringBinding("additional.settings"));
        tabFlow.textProperty().bind(i18N.createStringBinding("additional.flow"));
        tabReport.textProperty().bind(i18N.createStringBinding("additional.report"));
        tabInfo.textProperty().bind(i18N.createStringBinding("additional.info"));
        tabMechanical.textProperty().bind(i18N.createStringBinding("additional.mechanical"));
    }
}
