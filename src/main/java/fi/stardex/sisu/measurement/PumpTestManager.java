package fi.stardex.sisu.measurement;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.ui.controllers.common.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.pumps.CalibrationTestErrorController;
import fi.stardex.sisu.ui.controllers.pumps.SCVCalibrationController;
import fi.stardex.sisu.ui.controllers.pumps.main.StartButtonController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpHighPressureSectionPwrController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpRegulatorSectionTwoController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;

public class PumpTestManager implements TestManager{

    private Timeline motorPreparationTimeline;
    private Timeline pressurePreparationTimeline;
    private Timeline adjustingTimeline;
    private Timeline measurementTimeline;
    private Timeline autoResetTimeline;
    private int includedAutoTestsLength;
    private ToggleButton startButton;
    private ToggleButton highPressureSectionPwrButton;
    private Spinner<Double> regulatorTwoCurrentSpinner;
    private ToggleButton testBenchPwrButton;

    private PumpTestListModel pumpTestListModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private PumpTestModel pumpTestModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private PumpReportModel pumpReportModel;
    private PumpTestModeModel pumpTestModeModel;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private PumpTimeProgressModel pumpTimeProgressModel;
    private SCVCalibrationModel scvCalibrationModel;
    private TestBenchSectionModel testBenchSectionModel;
    private IntegerProperty adjustingTimeProperty;
    private IntegerProperty measuringTimeProperty;
    private PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel;
    private PumpModel pumpModel;
    private ListView<AutoTestListLastChangeModel.PumpTestWrapper> testListView;
    private SCVCalibrationController scvCalibrationController;
    private PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController;
    private CalibrationTestErrorController calibrationTestErrorController;
    private StartButtonController startButtonController;
    private PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController;
    private TestBenchSectionController testBenchSectionController;


    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }
    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }
    public void setPumpTimeProgressModel(PumpTimeProgressModel pumpTimeProgressModel) {
        this.pumpTimeProgressModel = pumpTimeProgressModel;
    }
    public void setPumpPressureRegulatorOneModel(PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel) {
        this.pumpPressureRegulatorOneModel = pumpPressureRegulatorOneModel;
    }
    public void setTestListView(ListView<AutoTestListLastChangeModel.PumpTestWrapper> testListView) {
        this.testListView = testListView;
    }
    public void setScvCalibrationModel(SCVCalibrationModel scvCalibrationModel) {
        this.scvCalibrationModel = scvCalibrationModel;
    }
    public void setScvCalibrationController(SCVCalibrationController scvCalibrationController) {
        this.scvCalibrationController = scvCalibrationController;
    }
    public void setPumpRegulatorSectionTwoController(PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController) {
        this.pumpRegulatorSectionTwoController = pumpRegulatorSectionTwoController;
    }
    public void setCalibrationTestErrorController(CalibrationTestErrorController calibrationTestErrorController) {
        this.calibrationTestErrorController = calibrationTestErrorController;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    public void setStartButtonController(StartButtonController startButtonController) {
        this.startButtonController = startButtonController;
    }

    public void setPumpHighPressureSectionPwrController(PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController) {
        this.pumpHighPressureSectionPwrController = pumpHighPressureSectionPwrController;
    }

    public void setTestBenchSectionController(TestBenchSectionController testBenchSectionController) {
        this.testBenchSectionController = testBenchSectionController;
    }

    public void setTestBenchSectionModel(TestBenchSectionModel testBenchSectionModel) {
        this.testBenchSectionModel = testBenchSectionModel;
    }

    @PostConstruct
    public void init() {

        adjustingTimeProperty = pumpTimeProgressModel.adjustingTimeProperty();
        measuringTimeProperty = pumpTimeProgressModel.measurementTimeProperty();
        setupReferences();
        setupTimeLines();
        setupListeners();
    }

    private void setupReferences() {

        startButton = startButtonController.getStartToggleButton();
        highPressureSectionPwrButton = pumpHighPressureSectionPwrController.getPwrButtonToggleButton();
        regulatorTwoCurrentSpinner = pumpRegulatorSectionTwoController.getCurrentSpinner();
        testBenchPwrButton = testBenchSectionController.getTestBenchStartToggleButton();
    }

    private void setupTimeLines() {

        motorPreparationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> motorPreparation()));
        motorPreparationTimeline.setCycleCount(Animation.INDEFINITE);

        pressurePreparationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> pressurePreparation()));
        pressurePreparationTimeline.setCycleCount(Animation.INDEFINITE);

        adjustingTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> tickAdjustingTime()));
        adjustingTimeline.setCycleCount(Animation.INDEFINITE);

        measurementTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> tickMeasurementTime()));
        measurementTimeline.setCycleCount(Animation.INDEFINITE);

        autoResetTimeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> resetFlowData()));
    }

    private void setupListeners() {

        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue)
                startMeasurements();
            else
                stopMeasurements();
        });

        testListView.getSelectionModel().selectedIndexProperty().addListener((observableValue, oldValue, newValue) -> {

            if (pumpsStartButtonState.startButtonProperty().get() && pumpTestModeModel.testModeProperty().get() == TESTPLAN) {
                start();
            }
        });

        scvCalibrationModel.isFinishedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                if (pumpTestModeModel.testModeProperty().get() == AUTO) {
                    runNextTest();
                }else{
                    startButton.setSelected(false);
                }
            }
        });

        pumpModel.pumpProperty().addListener((observableValue, oldValue, newValue) -> scvCalibrationController.clearScvCalibrationResults());
    }

    private void startMeasurements() {

        resetFlowData();
        if (pumpTestModeModel.testModeProperty().get() == AUTO) {

            includedAutoTestsLength = (int)pumpTestListModel.getPumpTestObservableList().stream().filter(t -> t.isIncludedProperty().get()).count();

            testListView.getSelectionModel().clearSelection();
            runNextTest();
        }else{

            String testName = pumpTestModel.pumpTestProperty().get().getTestName().toString();
            switch (testName) {
                case "SCV Calibration":
                    runScvCalibrationTest();
                    break;
                case "Calibration test":
                    if (scvCalibrationModel.isSuccessfulProperty().get()) {
                        runCalibrationTest();
                    } else {
                        calibrationTestErrorController.initErrorStage();
                        Platform.runLater(() -> startButton.setSelected(false));
                    }
                    break;
                default:
                    start();
                    break;
            }
        }
    }

    private void stopMeasurements() {

        stopTimers();
        resetFlowData();
        highPressureSectionPwrButton.setSelected(false);
        testBenchPwrButton.setSelected(false);
        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);
    }

    public void start(){

        if (testBenchSectionModel.isPowerButtonDisabledProperty().get())
            startPressure();
        else
            startMotor();
    }

    private void startMotor() {

        testBenchPwrButton.setSelected(true);
        motorPreparationTimeline.play();
    }

    private void startPressure() {

        highPressureSectionPwrButton.setSelected(true);
        pressurePreparationTimeline.play();
    }

    private void motorPreparation() {

        if (isSectionReady(testBenchSectionModel.targetRPMProperty().get(), testBenchSectionModel.currentRPMProperty().get(), 0.1)) {

            motorPreparationTimeline.stop();
            startPressure();
        }
    }

    private void pressurePreparation() {

        int targetValue = pumpPressureRegulatorOneModel.pressureRegProperty().get();
        int lcdValue = highPressureSectionUpdateModel.lcdPressureProperty().get();

        if(isSectionReady(targetValue, lcdValue, 0.2) || targetValue == 0){
            pressurePreparationTimeline.stop();
            resetFlowData();

            if (pumpTestModeModel.testModeProperty().get() != TESTPLAN) {
                adjustingTimeline.play();
            }
//            autoResetTimeline.play();
        }
    }

    private boolean isSectionReady(double targetValue, double lcdValue, double margin) {

        return Math.abs((targetValue - lcdValue) / targetValue) < margin;
    }

    private void tickAdjustingTime() {

        if (tick(adjustingTimeProperty) == 0) {

            adjustingTimeline.stop();

            if (pumpTimeProgressModel.measurementTimeEnabledProperty().get()) {
                measurementTimeline.play();
//                autoResetTimeline.play();
            }
            else{
                pumpReportModel.storeResult();
                runNextTest();
            }
        }
    }

    private void tickMeasurementTime() {

        if (tick(measuringTimeProperty) == 0) {

            measurementTimeline.stop();
            pumpReportModel.storeResult();
            runNextTest();
        }
    }

    private void selectNextTest(int testIndex) {

        testListView.getSelectionModel().select(++testIndex);
        testListView.scrollTo(testIndex);
    }

    private void stopTimers() {

        motorPreparationTimeline.stop();
        pressurePreparationTimeline.stop();
        adjustingTimeline.stop();
        measurementTimeline.stop();
    }

    private void runNextTest() {

        if (pumpTestModeModel.testModeProperty().get() == AUTO) {

            int selectedTestIndex = testListView.getSelectionModel().getSelectedIndex();

            if (selectedTestIndex < includedAutoTestsLength - 1) {

                selectNextTest(selectedTestIndex);
                String testName = pumpTestModel.pumpTestProperty().get().getTestName().toString();

                switch (testName) {
                    case "SCV Calibration":
                        runScvCalibrationTest();
                        break;
                    case "Calibration test":

                        if (scvCalibrationModel.isSuccessfulProperty().get()) {
                            runCalibrationTest();
                        } else {
                            calibrationTestErrorController.initErrorStage();
                            runNextTest();
                        }
//                    selectedTestIndex = testListView.getSelectionModel().getSelectedIndex();
//                    if(selectedTestIndex < includedAutoTestsLength - 1){
//                        calibrationTestErrorController.initErrorStage();
//                        selectNextTest(selectedTestIndex);
//                        start();
//                    }
//                    else {
//                        Platform.runLater(()-> startButton.setSelected(false));
//                    }
                        break;
                    default:
                        start();
                        break;
                }
            }
            else{
                Platform.runLater(()-> startButton.setSelected(false));
            }
        }
    }

    private void runScvCalibrationTest() {
        scvCalibrationController.work();
    }

    private void runCalibrationTest() {

        double initialCurrent = pumpTestModel.pumpTestProperty().get().getCalibrationIoffset() + scvCalibrationModel.initialCurrentProperty().get();
        regulatorTwoCurrentSpinner.getValueFactory().setValue(initialCurrent);
        start();
    }

    private void resetFlowData() {

        flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
    }

    private int tick(IntegerProperty timeProperty) {

        int time = timeProperty.get();

        if (time > 0) {

            timeProperty.setValue(--time);
        }
        return time;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {

        if (newValue)
            startMeasurements();
        else
            stopMeasurements();

    }
}
