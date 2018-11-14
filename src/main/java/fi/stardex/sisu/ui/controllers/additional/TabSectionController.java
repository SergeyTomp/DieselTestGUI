package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import fi.stardex.sisu.ui.controllers.additional.tabs.info.InfoController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.ReportController;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class TabSectionController {

    @FXML private GridPane settingsGridPane;

    @FXML private Tab tabDelay;

    @FXML private Tab tabVoltage;

    @FXML private Tab tabRLC;

    @FXML private Tab tabSettings;

    @FXML private Tab tabFlow;

    @FXML private Tab tabReport;

    @FXML private Tab tabCoding;

    @FXML private Tab tabInfo;

    @FXML private TabPane tabPane;

    @FXML private StackPane voltage;

    @FXML private GridPane delay;

    @FXML private AnchorPane flow;

    @FXML private GridPane coding;

    @FXML private GridPane rlc;

    @FXML private GridPane info;

    @FXML private VoltageController voltageController;

    @FXML private DelayController delayController;

    @FXML private FlowController flowController;

    @FXML private ReportController reportController;

    @FXML private RLCController rlcController;

    @FXML private Spinner<Double> sensitivitySpinner;

    @FXML private CodingController codingController;

    @FXML private InfoController infoController;

    private I18N i18N;

    public RLCController getRlCController() {
        return rlcController;
    }

    public FlowController getFlowController() {
        return flowController;
    }

    public ReportController getReportController() {
        return reportController;
    }

    public VoltageController getVoltageController() {
        return voltageController;
    }

    public DelayController getDelayController() {
        return delayController;
    }

    public CodingController getCodingController() {
        return codingController;
    }

    public InfoController getInfoController() {
        return infoController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public Tab getTabVoltage() {
        return tabVoltage;
    }

    public Tab getTabDelay() {
        return tabDelay;
    }

    public Tab getTabInfo() {
        return tabInfo;
    }

    public Tab getTabSettings() {
        return tabSettings;
    }

    public GridPane getSettingsGridPane() {
        return settingsGridPane;
    }

    @PostConstruct
    private void init(){
        bindingI18N();
    }

    private void bindingI18N() {
        tabDelay.textProperty().bind(i18N.createStringBinding("additional.delay"));
        tabVoltage.textProperty().bind(i18N.createStringBinding("additional.voltage"));
        tabRLC.textProperty().bind(i18N.createStringBinding("h4.tab.RLC"));
        tabSettings.textProperty().bind(i18N.createStringBinding("additional.settings"));
        tabFlow.textProperty().bind(i18N.createStringBinding("additional.flow"));
        tabReport.textProperty().bind(i18N.createStringBinding("additional.report"));
        tabCoding.textProperty().bind(i18N.createStringBinding("additional.coding"));
        tabInfo.textProperty().bind(i18N.createStringBinding("additional.info"));
    }
}
