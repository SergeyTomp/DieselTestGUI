package fi.stardex.sisu.measurement;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.store.FlowReport;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowReportController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.enums.Tests.TestType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;

public class Measurements implements ChangeListener<Boolean> {

    private ObservableList<FlowReport.FlowTestResult> flowTableViewItems;

    private ListView<InjectorTest> testListView;

    private ObservableList<InjectorTest> testListViewItems;

    private MultipleSelectionModel<InjectorTest> testsSelectionModel;

    private Enabler enabler;

    private FlowReport flowReport;

    private Tests tests;

    private Button resetButton;

    private ToggleButton mainSectionStartToggleButton;

    private ToggleButton testBenchStartToggleButton;

    private ToggleButton highPressureStartToggleButton;

    private ToggleButton injectorSectionStartToggleButton;

    private Spinner<Integer> targetRPMSpinner;

    private Spinner<Integer> pressReg1Spinner;

    private Lcd currentRPMLcd;

    private Lcd pressureLcd;

    private MainSectionController.TimeProgressBar adjustingTime;

    private MainSectionController.TimeProgressBar measuringTime;

    private Timeline motorPreparationTimeline;

    private Timeline pressurePreparationTimeline;

    private Timeline adjustingTimeline;

    private Timeline measurementTimeline;

    private ModbusRegisterProcessor flowModbusWriter;

    private int includedAutoTestsLength;

    public Measurements(MainSectionController mainSectionController,
                        TestBenchSectionController testBenchSectionController,
                        HighPressureSectionController highPressureSectionController,
                        InjectorSectionController injectorSectionController,
                        FlowReportController flowReportController,
                        Tests tests, Enabler enabler, FlowReport flowReport) {

        this.tests = tests;

        this.enabler = enabler;

        this.flowReport = flowReport;

        testListView = mainSectionController.getTestListView();
        testListViewItems = mainSectionController.getTestListViewItems();
        testsSelectionModel = mainSectionController.getTestsSelectionModel();
        resetButton = mainSectionController.getResetButton();
        mainSectionStartToggleButton = mainSectionController.getStartToggleButton();
        adjustingTime = mainSectionController.getAdjustingTime();
        measuringTime = mainSectionController.getMeasuringTime();
        flowModbusWriter = mainSectionController.getFlowModbusWriter();

        highPressureStartToggleButton = highPressureSectionController.getHighPressureStartToggleButton();
        pressReg1Spinner = highPressureSectionController.getPressReg1Spinner();
        pressureLcd = highPressureSectionController.getPressureLcd();

        testBenchStartToggleButton = testBenchSectionController.getTestBenchStartToggleButton();
        targetRPMSpinner = testBenchSectionController.getTargetRPMSpinner();
        currentRPMLcd = testBenchSectionController.getCurrentRPMLcd();

        injectorSectionStartToggleButton = injectorSectionController.getInjectorSectionStartToggleButton();

        flowTableViewItems = flowReportController.getFlowTableView().getItems();

    }

    @PostConstruct
    private void init() {

        mainSectionStartToggleButton.selectedProperty().addListener(this);

        motorPreparationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> motorPreparation()));
        motorPreparationTimeline.setCycleCount(Animation.INDEFINITE);

        pressurePreparationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> pressurePreparation()));
        pressurePreparationTimeline.setCycleCount(Animation.INDEFINITE);

        adjustingTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> tickAdjustingTime()));
        adjustingTimeline.setCycleCount(Animation.INDEFINITE);

        measurementTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> tickMeasurementTime()));
        measurementTimeline.setCycleCount(Animation.INDEFINITE);

    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

        if (newValue)
            startMeasurements();
        else
            stopMeasurements();

    }

    private void startMeasurements() {

        resetButton.fire();

        TestType testType = tests.getTestType();

        enabler.startTest(true, testType);

        switch (testType) {

            case AUTO:
                includedAutoTestsLength = (int) testListViewItems.stream().filter(InjectorTest::isIncluded).count();
                flowTableViewItems.clear();
                start();
                break;
            case TESTPLAN:
                start();
                break;
            case CODING:
                break;

        }

    }

    private void stopMeasurements() {

        enabler.startTest(false, tests.getTestType());

        stopTimers();

        adjustingTime.refreshProgress();
        measuringTime.refreshProgress();

        switchOffSections();

    }

    public void switchOffSections() {

        injectorSectionStartToggleButton.setSelected(false);
        highPressureStartToggleButton.setSelected(false);
        testBenchStartToggleButton.setSelected(false);

        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);

    }

    private void runNextTest() {

        int selectedTestIndex = testsSelectionModel.getSelectedIndex();

        if (selectedTestIndex < includedAutoTestsLength - 1) {
            testsSelectionModel.select(++selectedTestIndex);
            testListView.scrollTo(selectedTestIndex);
            injectorSectionStartToggleButton.setSelected(false);
            start();
        } else
            stopMeasurements();

    }

    private void stopTimers() {

        motorPreparationTimeline.stop();
        pressurePreparationTimeline.stop();
        adjustingTimeline.stop();
        measurementTimeline.stop();

    }

    public void start() {

        if (testBenchStartToggleButton.isDisabled())
            startPressure();
        else
            startMotor();

    }

    private void startMotor() {

        testBenchStartToggleButton.setSelected(true);
        motorPreparationTimeline.play();

    }

    private void startPressure() {

        highPressureStartToggleButton.setSelected(true);
        pressurePreparationTimeline.play();

    }

    private void motorPreparation() {

        if (isSectionReady(targetRPMSpinner.getValue().doubleValue(), currentRPMLcd, 0.1)) {

            motorPreparationTimeline.stop();
            startPressure();

        }

    }

    private void pressurePreparation() {

        if (isSectionReady(pressReg1Spinner.getValue().doubleValue(), pressureLcd, 0.2)) {

            injectorSectionStartToggleButton.setSelected(true);
            resetButton.fire();
            pressurePreparationTimeline.stop();

            if (tests.getTestType() != TESTPLAN)
                adjustingTimeline.play();

        }

    }

    private boolean isSectionReady(double currentValue, Lcd lcd, double margin) {

        return Math.abs((currentValue - lcd.getValue()) / currentValue) < margin;

    }

    private void tickAdjustingTime() {

        if (adjustingTime.tick() == 0) {

            adjustingTimeline.stop();
            resetButton.fire();
            measurementTimeline.play();

        }

    }

    private void tickMeasurementTime() {

        if (measuringTime.tick() == 0) {
            measurementTimeline.stop();
            flowReport.save();
            runNextTest();
        }

    }

}
