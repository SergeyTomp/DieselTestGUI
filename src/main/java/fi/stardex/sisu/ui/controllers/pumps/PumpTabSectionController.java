package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.ui.controllers.pumps.flow.PumpFlowController;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;

public class PumpTabSectionController {

    @FXML private GridPane settingsGridPane;
    @FXML private Tab tabPumpSettings;
    @FXML private TabPane tabPane;
    @FXML private Tab tabPumpFlow;
    @FXML private Tab tabPumpInfo;
    @FXML private Tab tabPumpReport;
    @FXML private AnchorPane pumpFlowAnchorPane;
    @FXML private AnchorPane pumpInfoAnchorPane;
    @FXML private AnchorPane pumpReportAnchorPane;

    @FXML private PumpInfoController pumpInfoAnchorPaneController;
    @FXML private PumpFlowController pumpFlowController;
    @FXML private PumpReportController pumpReportAnchorPaneController;

    private I18N i18N;

    public Tab getTabPumpFlow() {
        return tabPumpFlow;
    }

    public Tab getTabPumpInfo() {
        return tabPumpInfo;
    }

    public Tab getTabPumpReport() {
        return tabPumpReport;
    }

    public GridPane getSettingsGridPane() {
        return settingsGridPane;
    }

    public AnchorPane getPumpFlowAnchorPane() {
        return pumpFlowAnchorPane;
    }

    public AnchorPane getPumpInfoAnchorPane() {
        return pumpInfoAnchorPane;
    }

    public AnchorPane getPumpReportAnchorPane() {
        return pumpReportAnchorPane;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public PumpInfoController getPumpInfoController() {
        return pumpInfoAnchorPaneController;
    }

    public PumpFlowController getPumpFlowController() {
        return pumpFlowController;
    }

    public PumpReportController getPumpReportController() {
        return pumpReportAnchorPaneController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){
        bindingI18N();
    }

    private void bindingI18N() {
        tabPumpSettings.textProperty().bind(i18N.createStringBinding("additional.settings"));
        tabPumpFlow.textProperty().bind(i18N.createStringBinding("additional.flow"));
        tabPumpReport.textProperty().bind(i18N.createStringBinding("additional.report"));
        tabPumpInfo.textProperty().bind(i18N.createStringBinding("additional.info"));
    }
}
