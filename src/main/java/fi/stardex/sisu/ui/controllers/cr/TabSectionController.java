package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.model.cr.InjectorTypeModel;
import fi.stardex.sisu.ui.controllers.cr.tabs.*;
import fi.stardex.sisu.ui.controllers.cr.tabs.info.InfoController;
import fi.stardex.sisu.ui.controllers.cr.tabs.report.ReportController;
import fi.stardex.sisu.util.enums.InjectorType;
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

    @FXML private Tab tabPiezoRepair;

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

    @FXML private PiezoRepairController piezoRepairController;

    private I18N i18N;

    public InjectorTypeModel injectorTypeModel;

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

    public PiezoRepairController getPiezoRepairController() {
        return piezoRepairController;
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

    public Tab getTabPiezoRepair() {
        return tabPiezoRepair;
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

    public void setInjectorTypeModel(InjectorTypeModel injectorTypeModel) {
        this.injectorTypeModel = injectorTypeModel;
    }

    @PostConstruct
    private void init(){
        bindingI18N();
//        tabPane.getTabs().remove(tabPiezoRepair);
        injectorTypeModel.injectorTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == InjectorType.PIEZO)
                tabPane.getTabs().add(tabPiezoRepair);
            else tabPane.getTabs().remove(tabPiezoRepair);
        });
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
