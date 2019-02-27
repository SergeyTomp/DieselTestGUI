package fi.stardex.sisu.measurement;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.states.TestBenchSectionPwrState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.ListView;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;

public class PumpMeasurementManager {

    private Timeline motorPreparationTimeline;
    private Timeline pressurePreparationTimeline;
    private Timeline adjustingTimeline;
    private Timeline measurementTimeline;
    private Timeline autoResetTimeline;
    private int includedAutoTestsLength;

    private PumpTestListModel pumpTestListModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private PumpTestModel pumpTestModel;
    private TargetRpmModel targetRpmModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private PumpReportModel pumpReportModel;
    private PumpTestModeModel pumpTestModeModel;
    private TestBenchSectionPwrState testBenchSectionPwrState;
    private CurrentRpmModel currentRpmModel;
    private PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private PumpTimeProgressModel pumpTimeProgressModel;
    private IntegerProperty adjustingTimeProperty;
    private IntegerProperty measuringTimeProperty;
    private PumpPressureRegulatorModel pumpPressureRegulatorModel;
    private ListView<AutoTestListLastChangeModel.PumpTestWrapper> testListView;

    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setTargetRpmModel(TargetRpmModel targetRpmModel) {
        this.targetRpmModel = targetRpmModel;
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
    public void setTestBenchSectionPwrState(TestBenchSectionPwrState testBenchSectionPwrState) {
        this.testBenchSectionPwrState = testBenchSectionPwrState;
    }
    public void setCurrentRpmModel(CurrentRpmModel currentRpmModel) {
        this.currentRpmModel = currentRpmModel;
    }
    public void setPumpHighPressureSectionPwrState(PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState) {
        this.pumpHighPressureSectionPwrState = pumpHighPressureSectionPwrState;
    }
    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }
    public void setPumpTimeProgressModel(PumpTimeProgressModel pumpTimeProgressModel) {
        this.pumpTimeProgressModel = pumpTimeProgressModel;
    }
    public void setPumpPressureRegulatorModel(PumpPressureRegulatorModel pumpPressureRegulatorModel) {
        this.pumpPressureRegulatorModel = pumpPressureRegulatorModel;
    }
    public void setTestListView(ListView<AutoTestListLastChangeModel.PumpTestWrapper> testListView) {
        this.testListView = testListView;
    }

    @PostConstruct
    public void init() {

        adjustingTimeProperty = pumpTimeProgressModel.adjustingTimeProperty();
        measuringTimeProperty = pumpTimeProgressModel.measurementTimeProperty();

        setupTimeLines();
        setupListeners();
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
    }

    private void startMeasurements() {

        resetFlowData();
        pumpReportModel.clearResults();
        if (pumpTestModeModel.testModeProperty().get() == AUTO) {

            includedAutoTestsLength = (int)pumpTestListModel.getPumpTestObservableList().stream().filter(t -> t.isIncludedProperty().get()).count();
        }

        start();
    }

    private void stopMeasurements() {

        stopTimers();
        resetFlowData();
        pumpHighPressureSectionPwrState.powerButtonProperty().setValue(false);
    }

    public void start(){

        if (testBenchSectionPwrState.isPowerButtonDisabledProperty().get())
            startPressure();
        else
            startMotor();
    }

    private void startMotor() {

        testBenchSectionPwrState.isPowerButtonOnProperty().setValue(true);
        motorPreparationTimeline.play();
    }

    private void startPressure() {

        pumpHighPressureSectionPwrState.powerButtonProperty().setValue(true);
        pressurePreparationTimeline.play();
    }

    private void motorPreparation() {

        if (isSectionReady(targetRpmModel.targetRPMProperty().getValue().doubleValue(), currentRpmModel.currentRPMProperty().getValue(), 0.1)) {

            motorPreparationTimeline.stop();
            startPressure();
        }
    }

    private void pressurePreparation() {

        int targetValue = pumpPressureRegulatorModel.pressureRegProperty().get();
        int lcdValue = highPressureSectionUpdateModel.lcdPressureProperty().get();

        if(isSectionReady(targetValue, lcdValue, 0.2) || targetValue == 0){
            pressurePreparationTimeline.stop();
            resetFlowData();

            if (pumpTestModeModel.testModeProperty().get() != TESTPLAN) {
                adjustingTimeline.play();
            }
            autoResetTimeline.play();
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
                autoResetTimeline.play();
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

    public void runNextTest() {

        if (pumpTestModeModel.testModeProperty().get() == AUTO) {

            int selectedTestIndex = testListView.getSelectionModel().getSelectedIndex();

            if (selectedTestIndex < includedAutoTestsLength - 1) {

                selectNextTest(selectedTestIndex);
                start();
            }
            else{

                pumpsStartButtonState.startButtonProperty().setValue(false);
            }
        }
    }

    private void resetFlowData() {

        flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
    }

    public int tick(IntegerProperty timeProperty) {

        int time = timeProperty.get();

        if (time > 0) {

            timeProperty.setValue(--time);
        }
        return time;
    }
}
