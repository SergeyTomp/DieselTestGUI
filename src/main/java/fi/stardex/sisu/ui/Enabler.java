package fi.stardex.sisu.ui;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.ui.controllers.additional.tabs.RLCController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController.GUIType;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.enums.Tests.TestType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class Enabler {

    private MainSectionController mainSectionController;

    private RadioButton manualTestRadioButton;

    private RadioButton testPlanTestRadioButton;

    private RadioButton autoTestRadioButton;

    private RadioButton codingTestRadioButton;

    private ListView<InjectorTest> testListView;

    private ListView<Manufacturer> manufacturerListView;

    private ListView<Model> modelListView;

    private Tests tests;

    private CheckBox enableTimingCheckBox;

    private ComboBox<GUIType> versionComboBox;

    private ComboBox<String> speedComboBox;

    private ToggleButton startToggleButton;

    private GridPane timingGridPane;

    private ToggleButton injectorSectionStartToggleButton;

    private Spinner<Integer> widthCurrentSignalSpinner;

    private Spinner<Double> freqCurrentSignalSpinner;

    private ToggleGroup piezoCoilToggleGroup;

    private RadioButton coilRadioButton;

    private RadioButton piezoRadioButton;

    private RadioButton piezoDelphiRadioButton;

    private Button storeButton;

    private Button measureButton;

    private Gauge parameter1Gauge;

    private Gauge parameter2Gauge;

    private Tab tabCoilOne;

    private Tab tabCoilTwo;

    private TabPane measurementTabPane;

    public Enabler(MainSectionController mainSectionController, InjectorSectionController injectorSectionController,
                   RLCController rlcController) {

        this.mainSectionController = mainSectionController;
        manualTestRadioButton = mainSectionController.getManualTestRadioButton();
        testPlanTestRadioButton = mainSectionController.getTestPlanTestRadioButton();
        autoTestRadioButton = mainSectionController.getAutoTestRadioButton();
        codingTestRadioButton = mainSectionController.getCodingTestRadioButton();
        testListView = mainSectionController.getTestListView();
        manufacturerListView = mainSectionController.getManufacturerListView();
        modelListView = mainSectionController.getModelListView();
        tests = mainSectionController.getTests();
        enableTimingCheckBox = mainSectionController.getEnableTimingCheckBox();
        startToggleButton = mainSectionController.getStartToggleButton();
        timingGridPane = mainSectionController.getTimingGridPane();
        versionComboBox = mainSectionController.getVersionComboBox();
        speedComboBox = mainSectionController.getSpeedComboBox();

        injectorSectionStartToggleButton = injectorSectionController.getInjectorSectionStartToggleButton();
        widthCurrentSignalSpinner = injectorSectionController.getWidthCurrentSignalSpinner();
        freqCurrentSignalSpinner = injectorSectionController.getFreqCurrentSignalSpinner();
        coilRadioButton = injectorSectionController.getCoilRadioButton();
        piezoRadioButton = injectorSectionController.getPiezoRadioButton();
        piezoDelphiRadioButton = injectorSectionController.getPiezoDelphiRadioButton();
        piezoCoilToggleGroup = injectorSectionController.getPiezoCoilToggleGroup();

        storeButton = rlcController.getStoreButton();
        measureButton = rlcController.getMeasureButton();
        parameter1Gauge = rlcController.getParameter1Gauge();
        parameter2Gauge = rlcController.getParameter2Gauge();
        tabCoilOne = rlcController.getTabCoilOne();
        tabCoilTwo = rlcController.getTabCoilTwo();
        measurementTabPane = rlcController.getMeasurementTabPane();

    }

    public Enabler startTest(boolean isStarted) {

        enableTimingCheckBox.setDisable(isStarted);
        speedComboBox.setDisable(isStarted);

        versionComboBox.setDisable(isStarted);

        disableAllRadioButtons(isStarted);

        manufacturerListView.setDisable(isStarted);
        modelListView.setDisable(isStarted);

        if (enableTimingCheckBox.isSelected())
            testListView.setDisable(isStarted);

        return this;

    }

    public Enabler selectInjector(boolean selected) {

        if (selected) {
            testPlanTestRadioButton.setDisable(false);
            autoTestRadioButton.setDisable(false);
            codingTestRadioButton.setDisable(false);
            if (!manualTestRadioButton.isSelected())
                mainSectionController.fillTestListView();
        } else {
            testPlanTestRadioButton.setDisable(true);
            autoTestRadioButton.setDisable(true);
            codingTestRadioButton.setDisable(true);
            manualTestRadioButton.setSelected(true);
            testListView.getItems().clear();
        }

        return this;

    }

    public Enabler selectInjectorType(String injectorType) {

        if (injectorType == null) {
            disableAllRadioButtons(false);
            setGaugesToNull();
            return this;
        }

        switch (injectorType) {
            case "coil":
                enableRadioButton(coilRadioButton);
                setupGauges(0, 2, "Inductance", "\u03BCH", "\u03A9", 500d, 3d, "COIL");
                break;
            case "piezo":
                enableRadioButton(piezoRadioButton);
                setupGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 10d, 2000d, "PIEZO");
                break;
            case "piezoDelphi":
                enableRadioButton(piezoDelphiRadioButton);
                setupGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 20d, 2000d, "PIEZO");
                break;
        }

        return this;

    }

    public Enabler selectTestType() {

        TestType test = tests.getTestType();

        switch (test) {
            case MANUAL:
                testListView.setDisable(false);
                testListView.getItems().clear();
                startToggleButton.setDisable(true);
                mainSectionController.getCurrentInjectorTestsObtainer().setInjectorTests(null);
                showTiming(false);
                break;
            case TESTPLAN:
                testListView.setDisable(false);
                startToggleButton.setDisable(false);
                mainSectionController.refreshTestListView();
                showTiming(true);
                enableTimingCheckBox(false, false);
                break;
            case AUTO:
                testListView.setDisable(true);
                startToggleButton.setDisable(false);
                mainSectionController.refreshTestListView();
                showTiming(true);
                enableTimingCheckBox(true, true);
                break;
            case CODING:
                testListView.setDisable(true);
                startToggleButton.setDisable(false);
                mainSectionController.refreshTestListView();
                showTiming(true);
                enableTimingCheckBox(true, true);
                break;
            default:
                break;
        }
        return this;
    }

    public void selectStaticLeakTest(boolean isSelected) {

        widthCurrentSignalSpinner.setDisable(isSelected);
        freqCurrentSignalSpinner.setDisable(isSelected);
        injectorSectionStartToggleButton.setDisable(isSelected);

    }

    private void showTiming(boolean isVisible) {

        timingGridPane.setVisible(isVisible);

    }

    private void enableTimingCheckBox(boolean select, boolean disable) {

        enableTimingCheckBox.setSelected(select);
        enableTimingCheckBox.setDisable(disable);

    }

    private void disableAllRadioButtons(boolean disable) {

        piezoCoilToggleGroup.getToggles().forEach(radioButton -> ((Node) radioButton).setDisable(disable));

    }

    private void enableRadioButton(RadioButton activeRadioButton) {

        activeRadioButton.setSelected(true);

        piezoCoilToggleGroup.getToggles().forEach(radioButton -> {

            if (radioButton == activeRadioButton)
                ((Node) radioButton).setDisable(false);
            else
                ((Node) radioButton).setDisable(true);

        });

    }

    private void setGaugesToNull() {

        storeButton.setDisable(true);
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

        storeButton.setDisable(false);
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
