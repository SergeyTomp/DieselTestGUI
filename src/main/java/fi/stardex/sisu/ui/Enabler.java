package fi.stardex.sisu.ui;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.model.FlowReportModel;
import fi.stardex.sisu.model.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.RLCController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.FlowReportController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Tests.TestType;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.stream.Collectors;

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

    private Spinner<Integer> widthCurrentSignalSpinner;

    private Spinner<Double> freqCurrentSignalSpinner;

    private ToggleGroup piezoCoilToggleGroup;

    private RadioButton coilRadioButton;

    private RadioButton piezoRadioButton;

    private RadioButton piezoDelphiRadioButton;

    private ToggleButton ledBeaker1;

    private ToggleButton ledBeaker2;

    private ToggleButton ledBeaker3;

    private ToggleButton ledBeaker4;

    private Button storeRLCButton;

    private Button storeButton;

    private Button resetButton;

    private Button measureButton;

    private Gauge parameter1Gauge;

    private Gauge parameter2Gauge;

    private Tab tabCoilOne;

    private Tab tabCoilTwo;

    private TabPane measurementTabPane;

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
                   RLCController rlcController,
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
        widthCurrentSignalSpinner = injectorSectionController.getWidthCurrentSignalSpinner();
        freqCurrentSignalSpinner = injectorSectionController.getFreqCurrentSignalSpinner();
        coilRadioButton = injectorSectionController.getCoilRadioButton();
        piezoRadioButton = injectorSectionController.getPiezoRadioButton();
        piezoDelphiRadioButton = injectorSectionController.getPiezoDelphiRadioButton();
        piezoCoilToggleGroup = injectorSectionController.getPiezoCoilToggleGroup();
        ledBeaker1 = injectorSectionController.getLed1ToggleButton();
        ledBeaker2 = injectorSectionController.getLed2ToggleButton();
        ledBeaker3 = injectorSectionController.getLed3ToggleButton();
        ledBeaker4 = injectorSectionController.getLed4ToggleButton();

        storeRLCButton = rlcController.getStoreButton();
        measureButton = rlcController.getMeasureButton();
        parameter1Gauge = rlcController.getParameter1Gauge();
        parameter2Gauge = rlcController.getParameter2Gauge();
        tabCoilOne = rlcController.getTabCoilOne();
        tabCoilTwo = rlcController.getTabCoilTwo();
        measurementTabPane = rlcController.getMeasurementTabPane();

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

    public Enabler selectInjectorType(String injectorType) {

        if (injectorType == null) {
            disableRadioButtons(piezoCoilToggleGroup, false);
//            setGaugesToNull();
            return this;
        }

        switch (injectorType) {
            case "coil":
                selectInjectorTypeRadioButton(coilRadioButton);
//                setupGauges(0, 2, "Inductance", "\u03BCH", "\u03A9", 500d, 3d, "COIL");
                break;
            case "piezo":
                selectInjectorTypeRadioButton(piezoRadioButton);
//                setupGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 10d, 2000d, "PIEZO");
                break;
            case "piezoDelphi":
                selectInjectorTypeRadioButton(piezoDelphiRadioButton);
//                setupGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 20d, 2000d, "PIEZO");
                break;
        }

        return this;

    }

    private void selectInjectorTypeRadioButton(RadioButton target) {

        disableRadioButtons(piezoCoilToggleGroup, false);
        target.setSelected(true);
        disableRadioButtons(piezoCoilToggleGroup, true);
    }

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

    private void setGaugesToNull() {

        disableNode(true, storeRLCButton, measureButton);

        parameter1Gauge.setDecimals(1);
        parameter1Gauge.setTitle("");
        parameter1Gauge.setUnit("");
        parameter1Gauge.setValue(0d);
        parameter1Gauge.setMaxValue(500d);

        parameter2Gauge.setDecimals(1);
        parameter2Gauge.setTitle("");
        parameter2Gauge.setUnit("");
        parameter2Gauge.setValue(0d);
        parameter2Gauge.setMaxValue(3d);

    }

    private void setupGauges(int gauge1Decimals, int gauge2Decimals, String gauge1Title, String gauge1Unit, String gauge2Unit,
                             double gauge1MaxValue, double gauge2MaxValue, String tabCoilOneText) {

        disableNode(false, storeRLCButton, measureButton);

        parameter1Gauge.setDecimals(gauge1Decimals);
        parameter1Gauge.setTitle(gauge1Title);
        parameter1Gauge.setUnit(gauge1Unit);
        parameter1Gauge.setMaxValue(gauge1MaxValue);

        parameter2Gauge.setDecimals(gauge2Decimals);
        parameter2Gauge.setTitle("Resistance");
        parameter2Gauge.setUnit(gauge2Unit);
        parameter2Gauge.setMaxValue(gauge2MaxValue);

        tabCoilOne.setText(tabCoilOneText);
        measurementTabPane.getTabs().remove(tabCoilTwo);

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
