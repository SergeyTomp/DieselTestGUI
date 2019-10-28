package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import javax.annotation.PostConstruct;

public class UisReportController {

    @FXML private Tab tabFlowReport;
    @FXML private Tab tabRLCReport;
    @FXML private Tab tabDelayReport;

    @FXML private UisRlcReportController uisRlcReportController;
    @FXML private UisDelayReportController uisDelayReportController;
    @FXML private UisFlowReportController uisFlowReportController;

    private I18N i18N;

    public UisRlcReportController getUisRlcReportController() {
        return uisRlcReportController;
    }
    public UisDelayReportController getUisDelayReportController() {
        return uisDelayReportController;
    }
    public UisFlowReportController getUisFlowReportController() {
        return uisFlowReportController;
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
