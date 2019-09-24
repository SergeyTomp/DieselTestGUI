package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.model.PiezoRepairModel;
import fi.stardex.sisu.model.Step3Model;
import fi.stardex.sisu.model.TabSectionModel;
import fi.stardex.sisu.model.TestBenchSectionModel;
import fi.stardex.sisu.model.cr.InjectorTypeModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.states.HighPressureSectionPwrState;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.ui.controllers.cr.tabs.*;
import fi.stardex.sisu.ui.controllers.cr.tabs.info.InfoController;
import fi.stardex.sisu.ui.controllers.cr.tabs.report.ReportController;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

public class TabSectionController {

    @FXML private Tab tabDiffFlow;
    @FXML private Tab tabStep3;
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
    @FXML private Step3Controller step3Controller;
    @FXML private DiffFlowController diffFlowController;

    private I18N i18N;
    private InjectorTypeModel injectorTypeModel;
    private MainSectionModel mainSectionModel;
    private TabSectionModel tabSectionModel;
    private Step3Model step3Model;
    private PiezoRepairModel piezoRepairModel;
    private ChangeListener<InjectorType> piezoTypeListener;
    private InjectorSectionPwrState injectorSectionPwrState;
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
    public Step3Controller getStep3Controller() {
        return step3Controller;
    }
    public DiffFlowController getDiffFlowController() {
        return diffFlowController;
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
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }
    public void setTabSectionModel(TabSectionModel tabSectionModel) {
        this.tabSectionModel = tabSectionModel;
    }
    public void setStep3Model(Step3Model step3Model) {
        this.step3Model = step3Model;
    }
    public void setPiezoRepairModel(PiezoRepairModel piezoRepairModel) {
        this.piezoRepairModel = piezoRepairModel;
    }
    public void setInjectorSectionPwrState(InjectorSectionPwrState injectorSectionPwrState) {
        this.injectorSectionPwrState = injectorSectionPwrState;
    }

    @PostConstruct
    private void init(){

        bindingI18N();
        tabPane.getTabs().remove(tabStep3);
        tabPane.getTabs().remove(tabDiffFlow);

        piezoTypeListener = (observableValue, oldValue, newValue) -> {

            if(newValue == InjectorType.PIEZO)
                tabPane.getTabs().add(tabPiezoRepair);
            else tabPane.getTabs().remove(tabPiezoRepair);
        };

        injectorTypeModel.injectorTypeProperty().addListener(piezoTypeListener);

        mainSectionModel.injectorProperty().addListener((observableValue, oldValue, newValue) -> {

            Object lastValue_1 = Version_controllable_1.getLastValue();
            Object lastValue_2 = Version_controllable_2.getLastValue();
            if ((lastValue_1 == null || lastValue_2 == null || (int) lastValue_1 != 0xF1 || (int) lastValue_2 != 0xAA)) return;
            if ((int)Main_version_0.getLastValue() < 3) return;
            if ((int)Main_version_1.getLastValue() < 19) return;

            if (newValue != null
                    && newValue.getManufacturer().getManufacturerName().equals("Bosch")
                    && injectorTypeModel.injectorTypeProperty().get() == InjectorType.COIL) {
                if (tabPane.getTabs().contains(tabStep3)) { return; }
                tabPane.getTabs().add(tabStep3);
            }
            else {
                tabPane.getTabs().remove(tabStep3);
            }
        });

        tabSectionModel.step3TabIsShowingProperty().bind(tabStep3.selectedProperty());
        tabSectionModel.piezoTabIsShowingProperty().bind(tabPiezoRepair.selectedProperty());

        tabPane.getTabs().stream().filter(t -> !t.textProperty().get().equals("Piezo") && !t.textProperty().get().equals("Step_3")).forEach(t -> {

            step3Model.step3PauseProperty().addListener((observableValue, oldValue, newValue) -> t.setDisable(newValue));
            piezoRepairModel.startMeasureProperty().addListener((observableValue, oldValue, newValue) -> t.setDisable(newValue));
        });

        mainSectionModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> disableTabs());
        injectorSectionPwrState.powerButtonProperty().addListener((observableValue, oldValue, newValue) -> disableTabs());
    }

    private void disableTabs() {
        tabStep3.setDisable(sectionsON());
        tabPiezoRepair.setDisable(sectionsON());
    }

    private boolean sectionsON() {
        return mainSectionModel.startButtonProperty().get() || injectorSectionPwrState.powerButtonProperty().get();
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
