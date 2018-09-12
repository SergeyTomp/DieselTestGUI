package fi.stardex.sisu.ui;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.ui.controllers.additional.tabs.RLCController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController.GUIType;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.enums.Tests.TestType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;

public class Enabler {

    private MainSectionController mainSectionController;

    private ToggleButton mainSectionStartToggleButton;

    private ListView<InjectorTest> testListView;

    private ListView<Manufacturer> manufacturerListView;

    private ListView<Model> modelListView;

    private ToggleGroup testsToggleGroup;

    private Tests tests;

    private ComboBox<GUIType> versionComboBox;

    private ComboBox<String> speedComboBox;

    private Button moveUpButton;

    private Button moveDownButton;

    private VBox injectorTestsVBox;

    private GridPane timingGridPane;

    private HBox startHBox;

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

    public Enabler(MainSectionController mainSectionController, InjectorSectionController injectorSectionController,
                   RLCController rlcController, VoltageController voltageController) {

        this.mainSectionController = mainSectionController;
        mainSectionStartToggleButton = mainSectionController.getStartToggleButton();
        testListView = mainSectionController.getTestListView();
        manufacturerListView = mainSectionController.getManufacturerListView();
        modelListView = mainSectionController.getModelListView();
        tests = mainSectionController.getTests();
        injectorTestsVBox = mainSectionController.getInjectorTestsVBox();
        timingGridPane = mainSectionController.getTimingGridPane();
        startHBox = mainSectionController.getStartHBox();
        versionComboBox = mainSectionController.getVersionComboBox();
        speedComboBox = mainSectionController.getSpeedComboBox();
        moveUpButton = mainSectionController.getMoveUpButton();
        moveDownButton = mainSectionController.getMoveDownButton();
        storeButton = mainSectionController.getStoreButton();
        resetButton = mainSectionController.getResetButton();
        testsToggleGroup = mainSectionController.getTestsToggleGroup();

        injectorSectionStartToggleButton = injectorSectionController.getInjectorSectionStartToggleButton();
        widthCurrentSignalSpinner = injectorSectionController.getWidthCurrentSignalSpinner();
        freqCurrentSignalSpinner = injectorSectionController.getFreqCurrentSignalSpinner();
        coilRadioButton = injectorSectionController.getCoilRadioButton();
        piezoRadioButton = injectorSectionController.getPiezoRadioButton();
        piezoDelphiRadioButton = injectorSectionController.getPiezoDelphiRadioButton();
        piezoCoilToggleGroup = injectorSectionController.getPiezoCoilToggleGroup();
        ledBeaker1 = injectorSectionController.getLedBeaker1Controller().getLedBeaker();
        ledBeaker2 = injectorSectionController.getLedBeaker2Controller().getLedBeaker();
        ledBeaker3 = injectorSectionController.getLedBeaker3Controller().getLedBeaker();
        ledBeaker4 = injectorSectionController.getLedBeaker4Controller().getLedBeaker();

        storeRLCButton = rlcController.getStoreButton();
        measureButton = rlcController.getMeasureButton();
        parameter1Gauge = rlcController.getParameter1Gauge();
        parameter2Gauge = rlcController.getParameter2Gauge();
        tabCoilOne = rlcController.getTabCoilOne();
        tabCoilTwo = rlcController.getTabCoilTwo();
        measurementTabPane = rlcController.getMeasurementTabPane();

        pulseSettingsButton = voltageController.getPulseSettingsButton();

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

        enableMainSectionStartToggleButton(isStarted);

        speedComboBox.setDisable(isStarted);

        versionComboBox.setDisable(isStarted);

        disableAllCoilPiezoRadioButtons(isStarted);

        disableAllTestsRadioButtons(isStarted);

        hideUpDownButtons(isStarted);

        manufacturerListView.setDisable(isStarted);
        modelListView.setDisable(isStarted);

        testListView.setDisable(isStarted);

        return this;

    }

    public Enabler selectInjector(boolean selected) {

        if (selected) {
            showInjectorTests(true);
            mainSectionController.fillTestListView();
        } else {
            showInjectorTests(false);
            testListView.getItems().clear();
        }

        return this;

    }

    public Enabler selectInjectorType(String injectorType) {

        if (injectorType == null) {
            disableAllCoilPiezoRadioButtons(false);
            setGaugesToNull();
            return this;
        }

        switch (injectorType) {
            case "coil":
                enableCoilPiezoRadioButton(coilRadioButton);
                setupGauges(0, 2, "Inductance", "\u03BCH", "\u03A9", 500d, 3d, "COIL");
                break;
            case "piezo":
                enableCoilPiezoRadioButton(piezoRadioButton);
                setupGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 10d, 2000d, "PIEZO");
                break;
            case "piezoDelphi":
                enableCoilPiezoRadioButton(piezoDelphiRadioButton);
                setupGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 20d, 2000d, "PIEZO");
                break;
        }

        return this;

    }

    public Enabler selectTestType() {

        TestType test = tests.getTestType();

        switch (test) {

            case AUTO:
                testListView.setDisable(false);
                testListView.setCellFactory(CheckBoxListCell.forListView(InjectorTest::includedProperty));
                mainSectionController.refreshTestListView();
                showButtons(true, false);
                showTiming(true);
                break;
            case TESTPLAN:
                testListView.setDisable(false);
                testListView.setCellFactory(null);
                mainSectionController.refreshTestListView();
                showButtons(false, true);
                showTiming(false);
                break;
            case CODING:
                testListView.setDisable(true);
                testListView.setCellFactory(null);
                mainSectionController.refreshTestListView();
                showButtons(false, true);
                showTiming(false);
                break;

        }

        return this;

    }

    public Enabler disableVAP(boolean isStarted) {

        if (isStarted)
            disableAllCoilPiezoRadioButtons(true);
        else
            enableCoilPiezoRadioButton((RadioButton) piezoCoilToggleGroup.getSelectedToggle());

        disableAllLeds(isStarted);

        pulseSettingsButton.setDisable(isStarted);

        disableWidthFreqSpinners(isStarted);

        return this;

    }

    public Enabler enableUpDownButtons(int selectedIndex, int includedTestsSize) {

        if (selectedIndex == 0 && selectedIndex == includedTestsSize - 1) {
            moveUpButton.setDisable(true);
            moveDownButton.setDisable(true);
        } else if (selectedIndex == 0) {
            moveUpButton.setDisable(true);
            moveDownButton.setDisable(false);
        } else if ((selectedIndex > 0) && (selectedIndex <= includedTestsSize - 2)) {
            moveUpButton.setDisable(false);
            moveDownButton.setDisable(false);
        } else {
            moveUpButton.setDisable(false);
            moveDownButton.setDisable(true);
        }

        return this;

    }

    public Enabler showButtons(boolean showUpDown, boolean showStoreReset) {

        moveUpButton.setVisible(showUpDown);
        moveDownButton.setVisible(showUpDown);

        storeButton.setVisible(showStoreReset);
        resetButton.setVisible(showStoreReset);

        return this;

    }

    private void hideUpDownButtons(boolean hide) {

        moveUpButton.setVisible(!hide);
        moveDownButton.setVisible(!hide);

    }

    public Enabler enableMainSectionStartToggleButton(boolean enable) {

        mainSectionStartToggleButton.setDisable(!enable);

        return this;

    }

    public void selectStaticLeakTest(boolean isSelected) {

        disableWidthFreqSpinners(isSelected);
        injectorSectionStartToggleButton.setDisable(isSelected);

    }

    private void disableWidthFreqSpinners(boolean disable) {

        widthCurrentSignalSpinner.setDisable(disable);
        freqCurrentSignalSpinner.setDisable(disable);

    }

    private void disableAllCoilPiezoRadioButtons(boolean disable) {

        piezoCoilToggleGroup.getToggles().forEach(radioButton -> ((Node) radioButton).setDisable(disable));

    }

    private void disableAllTestsRadioButtons(boolean disable) {

        testsToggleGroup.getToggles().forEach(toggle -> ((Node) toggle).setDisable(disable));

    }

    public Enabler disableAllLedsExceptFirst(boolean disable) {

        ledBeaker2.setDisable(disable);
        ledBeaker3.setDisable(disable);
        ledBeaker4.setDisable(disable);

        return this;

    }

    private void disableAllLeds(boolean disable) {

        ledBeaker1.setDisable(disable);
        ledBeaker2.setDisable(disable);
        ledBeaker3.setDisable(disable);
        ledBeaker4.setDisable(disable);

    }

    private void enableCoilPiezoRadioButton(RadioButton activeRadioButton) {

        activeRadioButton.setSelected(true);

        piezoCoilToggleGroup.getToggles().forEach(radioButton -> {

            if (radioButton == activeRadioButton)
                ((Node) radioButton).setDisable(false);
            else
                ((Node) radioButton).setDisable(true);

        });

    }

    private void showInjectorTests(boolean show) {

        timingGridPane.setVisible(show && tests.getTestType() == AUTO);
        injectorTestsVBox.setVisible(show);
        startHBox.setVisible(show);

    }

    private void showTiming(boolean show) {

        timingGridPane.setVisible(show);

    }

    private void setGaugesToNull() {

        storeRLCButton.setDisable(true);
        measureButton.setDisable(true);

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

        storeRLCButton.setDisable(false);
        measureButton.setDisable(false);

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

}
