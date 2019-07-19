package fi.stardex.sisu.ui.controllers.cr.tabs.report;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import javax.annotation.PostConstruct;

public class ReportController {

    @FXML
    private Tab tabFlowReport;

    @FXML
    private Tab tabRLCReport;

    @FXML
    private Tab tabDelayReport;

    @FXML private FlowReportController flowReportController;

    @FXML private RLC_ReportController rlcReportController;

    @FXML private DelayReportController delayReportController;

    private I18N i18N;

    public FlowReportController getFlowReportController() {
        return flowReportController;
    }

    public RLC_ReportController getRLCreportController() {
        return rlcReportController;
    }

    public DelayReportController getDelayReportController() {
        return delayReportController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init(){
        bindingI18N();
    }

    private void bindingI18N() {
        tabFlowReport.textProperty().bind(i18N.createStringBinding("additional.report.flow"));
        tabRLCReport.textProperty().bind(i18N.createStringBinding("additional.report.RLC"));
        tabDelayReport.textProperty().bind(i18N.createStringBinding("additional.report.delay"));
    }
}
