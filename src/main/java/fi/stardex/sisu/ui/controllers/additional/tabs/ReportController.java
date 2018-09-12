package fi.stardex.sisu.ui.controllers.additional.tabs;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class ReportController {

    @FXML private Tab tabFlowReport;

    @FXML private FlowReportController flowReportController;

    public FlowReportController getFlowReportController() {
        return flowReportController;
    }
}
