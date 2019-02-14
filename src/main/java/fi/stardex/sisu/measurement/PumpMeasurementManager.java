package fi.stardex.sisu.measurement;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PumpsStartButtonState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.MultipleSelectionModel;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;

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
    private PressureRegulatorOneModel pressureRegulatorOneModel;
    private TargetRpmModel targetRpmModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private PumpReportModel pumpReportModel;
    private PumpTestModeModel pumpTestModeModel;
    private MultipleSelectionModel<AutoTestListLastChangeModel.PumpTestWrapper> testListSelectionModel;

    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPressureRegulatorOneModel(PressureRegulatorOneModel pressureRegulatorOneModel) {
        this.pressureRegulatorOneModel = pressureRegulatorOneModel;
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

    @PostConstruct
    public void init() {

        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {

            if (pumpTestModeModel.testModeProperty().get() == AUTO) {

                if (newValue)
                    startMeasurements();
                else
                    stopMeasurements();
            }
        });

        motorPreparationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> motorPreparation()));
        motorPreparationTimeline.setCycleCount(Animation.INDEFINITE);

        pressurePreparationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> pressurePreparation()));
        pressurePreparationTimeline.setCycleCount(Animation.INDEFINITE);

        adjustingTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> tickAdjustingTime()));
        adjustingTimeline.setCycleCount(Animation.INDEFINITE);

        measurementTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> tickMeasurementTime()));
        measurementTimeline.setCycleCount(Animation.INDEFINITE);

        autoResetTimeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> resetFlowData()));

        testListSelectionModel = pumpTestListModel.getTestsSelectionModel();
    }

    private void startMeasurements() {

        resetFlowData();
        pumpReportModel.clearResults();
        includedAutoTestsLength = (int)pumpTestListModel.getPumpTestObservableList().stream().filter(t -> t.isIncludedProperty().get()).count();

        start();
    }

    private void stopMeasurements() {

        stopTimers();

        resetFlowData();
    }

    public void start(){

    }

    private void startMotor() {


        motorPreparationTimeline.play();
    }

    private void startPressure() {


        pressurePreparationTimeline.play();
    }

    private void motorPreparation() {

    }

    private void pressurePreparation() {

    }

    private void playResetTimeLine() {

    }

    private boolean isSectionReady(double currentValue, double lcdValue, double margin) {

        return Math.abs((currentValue - lcdValue) / currentValue) < margin;
    }

    private void tickAdjustingTime() {

    }

    private void tickMeasurementTime() {

    }

    private void selectNextTest(int testIndex) {

        testListSelectionModel.select(++testIndex);
    }

    private void stopTimers() {

        motorPreparationTimeline.stop();
        pressurePreparationTimeline.stop();
        adjustingTimeline.stop();
        measurementTimeline.stop();
    }

    public void runNextTest() {

        int selectedTestIndex = testListSelectionModel.getSelectedIndex();

        if (selectedTestIndex < includedAutoTestsLength - 1) {


            selectNextTest(selectedTestIndex);
            start();
        }
        else{

            pumpsStartButtonState.startButtonProperty().setValue(false);
        }

    }

    private void resetFlowData() {

        flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
    }
}
