package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.UisTabSectionModel;
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
    @FXML private UisVoltageController uisVoltageController;
    @FXML private UisDelayController uisDelayController;
    @FXML private UisRlcController uisRlcController;
    @FXML private UisReportController uisReportController;
    @FXML private MechanicalController mechanicalController;
    @FXML private UisFlowController uisFlowController;


    private I18N i18N;
    private UisTabSectionModel uisTabSectionModel;

    public UisVoltageController getUisVoltageController() {
        return uisVoltageController;
    }
    public UisDelayController getUisDelayController() {
        return uisDelayController;
    }
    public UisRlcController getUisRlcController() {
        return uisRlcController;
    }
    public UisReportController getUisReportController() {
        return uisReportController;
    }
    public MechanicalController getMechanicalController() {
        return mechanicalController;
    }
    public UisFlowController getUisFlowController() {
        return uisFlowController;
    }

    public GridPane getSettingsGridPane() {
        return settingsGridPane;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisTabSectionModel(UisTabSectionModel uisTabSectionModel) {
        this.uisTabSectionModel = uisTabSectionModel;
    }

    @PostConstruct
    public void init() {
        uisTabSectionModel.isTabVoltageShowingProperty().bind(tabVoltage.selectedProperty());
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
        tabMechanical.textProperty().bind(i18N.createStringBinding("additional.mechanical.openingPress"));
    }
}
