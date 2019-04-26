package fi.stardex.sisu.ui;

import fi.stardex.sisu.model.FlowReportModel;
import fi.stardex.sisu.model.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.FlowReportController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Tests.TestType;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.ui.controllers.GUI_TypeController.GUIType;
import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;
import static fi.stardex.sisu.util.enums.Tests.TestType.CODING;
import static fi.stardex.sisu.util.enums.Tests.getTestType;

public class Enabler {

    private ToggleButton mainSectionStartToggleButton;

    private ListView<InjectorTest> testListView;

    private ListView<Manufacturer> manufacturerListView;

    private ListView<Model> modelListView;

    private ToggleGroup testsToggleGroup;

    private ToggleGroup baseTypeToggleGroup;

    private VBox injectorsVBox;

    private ComboBox<GUIType> gui_typeComboBox;

    private ComboBox<String> speedComboBox;

    private Button moveUpButton;

    private Button moveDownButton;

    private VBox injectorTestsVBox;

    private GridPane timingGridPane;

    private StackPane startStackPane;

    private ToggleButton injectorSectionStartToggleButton;

    private ToggleGroup piezoCoilToggleGroup;

    private Button storeButton;

    private Button resetButton;

    private Button pulseSettingsButton;

    private Label ml_Min_DeliveryLabel;

    private Label ml_Min_BackFlowLabel;

    private ComboBox<String> deliveryFlowComboBox;

    private ComboBox<String> backFlowComboBox;

    private TableView<FlowResult> flowTableView;

    private Label flowReportAttentionLabel;

    private FlowReportModel flowReportModel;

    public Enabler(MainSectionController mainSectionController,
                   InjectorSectionController injectorSectionController,
                   VoltageController voltageController,
                   FlowController flowController,
                   FlowReportController flowReportController,
                   ComboBox<GUIType> gui_typeComboBox,
                   FlowReportModel flowReportModel) {

        mainSectionStartToggleButton = mainSectionController.getStartToggleButton();
        testListView = mainSectionController.getTestListView();
        manufacturerListView = mainSectionController.getManufacturerListView();
        modelListView = mainSectionController.getModelListView();
        injectorTestsVBox = mainSectionController.getInjectorTestsVBox();
        timingGridPane = mainSectionController.getTimingGridPane();
        startStackPane = mainSectionController.getStartStackPane();
        speedComboBox = mainSectionController.getSpeedComboBox();
        moveUpButton = mainSectionController.getMoveUpButton();
        moveDownButton = mainSectionController.getMoveDownButton();
        storeButton = mainSectionController.getStoreButton();
        resetButton = mainSectionController.getResetButton();
        testsToggleGroup = mainSectionController.getTestsToggleGroup();
        baseTypeToggleGroup = mainSectionController.getBaseTypeToggleGroup();
        injectorsVBox = mainSectionController.getInjectorsVBox();

        injectorSectionStartToggleButton = injectorSectionController.getInjectorSectionStartToggleButton();
        piezoCoilToggleGroup = injectorSectionController.getPiezoCoilToggleGroup();

        pulseSettingsButton = voltageController.getPulseSettingsButton();

        ml_Min_DeliveryLabel = flowController.getMl_Min_DeliveryLabel();
        ml_Min_BackFlowLabel = flowController.getMl_Min_BackFlowLabel();
        deliveryFlowComboBox = flowController.getDeliveryFlowComboBox();
        backFlowComboBox = flowController.getBackFlowComboBox();

        flowTableView = flowReportController.getFlowTableView();
        flowReportAttentionLabel = flowReportController.getFlowReportAttentionLabel();

        this.flowReportModel = flowReportModel;
        this.gui_typeComboBox = gui_typeComboBox;

    }

    @PostConstruct
    private void init() {

        initAutoTest();

    }

    private void initAutoTest() {

        testListView.setCellFactory(CheckBoxListCell.forListView(InjectorTest::includedProperty));
        showButtons(true, false);

    }

    public Enabler startTest(boolean isStarted) {

        TestType testType = getTestType();

        switch (testType) {
            case AUTO:
                disableNode(!isStarted && testListView.getSelectionModel().getSelectedIndex() != 0, mainSectionStartToggleButton);
                disableNode(isStarted, speedComboBox, testListView);
                showNode(!isStarted, moveUpButton, moveDownButton);
                break;
        }

        disableNode(isStarted || injectorSectionStartToggleButton.isSelected(), gui_typeComboBox);

        disableRadioButtons(testsToggleGroup, isStarted);

        disableRadioButtons(baseTypeToggleGroup, isStarted);

        disableNode(isStarted, manufacturerListView, modelListView);

        return this;

    }

//    public Enabler selectInjectorType(String injectorType) {
//
//        if (injectorType == null) {
//            disableRadioButtons(piezoCoilToggleGroup, false);
//            return this;
//        }
//
//        switch (injectorType) {
//            case "coil":
//                selectInjectorTypeRadioButton(coilRadioButton);
//                break;
//            case "piezo":
//                selectInjectorTypeRadioButton(piezoRadioButton);
//                break;
//            case "piezoDelphi":
//                selectInjectorTypeRadioButton(piezoDelphiRadioButton);
//                break;
//        }
//
//        return this;
//
//    }
//
//    private void selectInjectorTypeRadioButton(RadioButton target) {
//
//        disableRadioButtons(piezoCoilToggleGroup, false);
//        target.setSelected(true);
//        disableRadioButtons(piezoCoilToggleGroup, true);
//    }

    public Enabler selectTestType() {

        TestType test = getTestType();

        switch (test) {

            case AUTO:
                disableNode(false, testListView);
                showButtons(true, false);
                showNode(true, timingGridPane);
                showDefaultFlowUnit(false);
                showFlowReport(true);
                break;
            case TESTPLAN:
                disableNode(false, testListView);
                showButtons(false, true);
                showNode(false, timingGridPane);
                showDefaultFlowUnit(false);
                showFlowReport(true);   // to switch off report table in TESTPLAN mode set false
                break;
            case CODING:
                disableNode(true, testListView);
                showButtons(false, true);
                showNode(true, timingGridPane);
                showDefaultFlowUnit(true);
                showFlowReport(true);   // to switch off report table in CODING mode set false
                break;

        }

        return this;

    }

    private void showFlowReport(boolean isTestAuto) {

        showNode(!isTestAuto, flowReportAttentionLabel);

        showNode(isTestAuto, flowTableView);

        flowReportModel.clearResults();

    }

    public Enabler disableVAP(boolean isStarted) {

        if (CurrentInjectorObtainer.getInjector() == null) {

            disableRadioButtons(piezoCoilToggleGroup, isStarted);

            disableNode(isStarted, injectorsVBox);

        }

        disableNode(isStarted || mainSectionStartToggleButton.isSelected(), gui_typeComboBox);
        disableNode(isStarted, pulseSettingsButton);

//        disableNode(isStarted, ledBeaker1, ledBeaker2, ledBeaker3, ledBeaker4, pulseSettingsButton, widthCurrentSignalSpinner, freqCurrentSignalSpinner);

        return this;

    }

    public Enabler enableUpDownButtons(int selectedIndex, int includedTestsSize) {

        if (selectedIndex == 0 && selectedIndex == includedTestsSize - 1)
            disableNode(true, moveUpButton, moveDownButton);
        else if (selectedIndex == 0) {
            disableNode(true, moveUpButton);
            disableNode(false, moveDownButton);
        } else if ((selectedIndex > 0) && (selectedIndex <= includedTestsSize - 2))
            disableNode(false, moveUpButton, moveDownButton);
        else {
            disableNode(false, moveUpButton);
            disableNode(true, moveDownButton);
        }

        return this;

    }

    public Enabler showButtons(boolean showUpDown, boolean showStoreReset) {

        showNode(showUpDown, moveUpButton, moveDownButton);

        showNode(showStoreReset, storeButton, resetButton);

        return this;

    }

    private void disableRadioButtons(ToggleGroup targetToggleGroup, boolean disable) {


        targetToggleGroup.getToggles().stream()
                .filter(radioButton -> !radioButton.isSelected())
                .forEach(radioButton -> ((Node) radioButton).setDisable(disable));
    }

    public Enabler showInjectorTests(boolean show) {

        showNode(show && (getTestType() == AUTO || getTestType() == CODING), timingGridPane);
        showNode(show, injectorTestsVBox, startStackPane);

        return this;

    }

    private void showDefaultFlowUnit(boolean isCoding) {

        deliveryFlowComboBox.getSelectionModel().selectFirst();
        showNode(!isCoding, deliveryFlowComboBox);
        showNode(isCoding, ml_Min_DeliveryLabel);

        backFlowComboBox.getSelectionModel().selectFirst();
        showNode(!isCoding, backFlowComboBox);
        showNode(isCoding, ml_Min_BackFlowLabel);

    }

    public void disableNode(boolean disable, Node ... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);

    }

    public void showNode(boolean show, Node ... nodes) {
        for (Node node : nodes)
            node.setVisible(show);

    }

    public static void setVisible(boolean visible, Node... nodes){
        for (Node node: nodes) {
            if (node instanceof Toggle && !visible) {
                ((Toggle)node).setSelected(false);
            }
            node.setVisible(visible);
        }
    }
}
