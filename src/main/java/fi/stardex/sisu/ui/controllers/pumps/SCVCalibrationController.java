package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpHighPressureSectionPwrController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpRegulatorSectionTwoController;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

public class SCVCalibrationController {

    @FXML private Label titleLabel;
    @FXML private Label currentLabel;
    @FXML private Label testLabel;
    @FXML private Label ampereLabel;
    @FXML private Label processLabel;
    @FXML private Label timerLabel;
    @FXML private Label secondsLabel;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button stopSCVButton;

    private PumpTestModel pumpTestModel;
    private PumpModel pumpModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel;
    private TestBenchSectionModel testBenchSectionModel;
    private PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController;
    private PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController;
    private TestBenchSectionController testBenchSectionController;
    private SCVCalibrationModel scvCalibrationModel;
    private PumpReportModel pumpReportModel;
    private ToggleButton highPressureSectionPwrButton;
    private ToggleButton testBenchSectionPwrButton;
    private Spinner<Double> regulatorTwoCurrentSpinner;
    private I18N i18N;

    private final int DEFAULT_WAITING_TIME = 20;
    private final double TOLERANCE = 0.02;
    private int currentPressureTime = DEFAULT_WAITING_TIME;
    private int currentRpmTime = DEFAULT_WAITING_TIME;
    private Parent scvParent;
    private Parent rootParent;
    private Stage scvStage;
    private Timeline gapTimeline;
    private Timeline adjustingTimeline;
    private Timeline measurementTimeline;
    private Timeline pressurePreparationTimeline;
    private Timeline motorPreparationTimeline;
    private CalibrationState processState;
    private double minDirectFlow;
    private double maxDirectFlow;
    private double targetFlow;
    private double testCurrentPoint;
    private double delta_I;
    private int regulatonDirection;
    private double measuredFlow;
    private int adjustingTime;
    private int measuringTime;
    private int restAdjustingTime;
    private int restMeasuringTime;
    private StringProperty adjustingLabelText = new SimpleStringProperty();
    private StringProperty measuringLabelText = new SimpleStringProperty();
    private StringProperty testText = new SimpleStringProperty();
    private StringProperty secondsText = new SimpleStringProperty();


    private enum CalibrationState {

        RUNNING, FINISHED, NO_TARGET
    }

    public void setScvParent(Parent scvParent) {
        this.scvParent = scvParent;
    }

    public void setRootParent(Parent rootParent) {
        this.rootParent = rootParent;
    }

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }

    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }

    public void setPumpPressureRegulatorOneModel(PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel) {
        this.pumpPressureRegulatorOneModel = pumpPressureRegulatorOneModel;
    }

    public void setPumpRegulatorSectionTwoController(PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController) {
        this.pumpRegulatorSectionTwoController = pumpRegulatorSectionTwoController;
    }

    public void setScvCalibrationModel(SCVCalibrationModel scvCalibrationModel) {
        this.scvCalibrationModel = scvCalibrationModel;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }

    public void setPumpHighPressureSectionPwrController(PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController) {
        this.pumpHighPressureSectionPwrController = pumpHighPressureSectionPwrController;
    }
    public void setTestBenchSectionController(TestBenchSectionController testBenchSectionController) {
        this.testBenchSectionController = testBenchSectionController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setTestBenchSectionModel(TestBenchSectionModel testBenchSectionModel) {
        this.testBenchSectionModel = testBenchSectionModel;
    }

    @PostConstruct
    public void init() {

        setupReferences();
        setupTimeLines();
        setupStopButtonActionListener();
        bindingI18N();
    }

    private void setupReferences() {

        highPressureSectionPwrButton = pumpHighPressureSectionPwrController.getPwrButtonToggleButton();
        testBenchSectionPwrButton = testBenchSectionController.getTestBenchStartToggleButton();
        regulatorTwoCurrentSpinner = pumpRegulatorSectionTwoController.getCurrentSpinner();
    }

    private void initSCVStage() {

        if (scvStage == null) {

            scvStage = new Stage(StageStyle.UNDECORATED);
            scvStage.setScene(new Scene(scvParent));
            scvStage.initModality(Modality.NONE);
            //TODO uncomment below
            scvStage.initModality(Modality.WINDOW_MODAL);
            scvStage.initOwner(rootParent.getScene().getWindow());
        }
        scvStage.show();
    }

    private void setupStopButtonActionListener() {

        stopSCVButton.setOnAction(e -> {

            switch (processState) {

                case RUNNING:
                case NO_TARGET:
                    setCalibrationModelValues(false, 0, true);
                    scvStage.close();
                    break;
                case FINISHED:
                    setCalibrationModelValues(measuredFlow >= minDirectFlow && measuredFlow <= maxDirectFlow, testCurrentPoint, true);
                    scvStage.close();
                    break;
            }
            clearLabels();
            stopTimelines();
        });
    }

    private void setupTimeLines() {

        gapTimeline = new Timeline(new KeyFrame(Duration.seconds(1)));
        gapTimeline.setOnFinished(event -> {
            processLabel.setText(adjustingLabelText.get() + " ");
            timerLabel.setText(String.valueOf(adjustingTime));
            secondsLabel.setText(" " + secondsText.get());
            adjustingTimeline.play();
        });

        adjustingTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> timerLabel.setText(String.valueOf(--restAdjustingTime))));
        adjustingTimeline.setOnFinished(event -> {
            processLabel.setText(measuringLabelText.get() + " ");
            timerLabel.setText(String.valueOf(measuringTime));
            resetFlowData();
            startMeasurement();
        });

        measurementTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> timerLabel.setText(String.valueOf(--restMeasuringTime))));
        measurementTimeline.setOnFinished(event -> {
            pumpReportModel.storeResult();
            processLabel.setText("");
            secondsLabel.setText("");
            timerLabel.setText("");
            checkFlow();});

        pressurePreparationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> preparePressure()));
        pressurePreparationTimeline.setCycleCount(Animation.INDEFINITE);

        motorPreparationTimeline = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> prepareMotor()));
        motorPreparationTimeline.setCycleCount(Animation.INDEFINITE);
    }

    private void start() {

        if (testBenchSectionPwrButton.isDisabled()) {
            startPressure();
        }
        else{
            startMotor();
        }
    }

    private void startMotor() {

        if (!testBenchSectionPwrButton.isSelected()) {
            testBenchSectionPwrButton.setSelected(true);
        }
        motorPreparationTimeline.play();
    }

    private void startPressure() {

        if (!highPressureSectionPwrButton.isSelected()) {
            highPressureSectionPwrButton.setSelected(true);
        }
        pressurePreparationTimeline.play();
    }

    private void checkFlow(){

        //TODO uncomment below!!!
        String flow = pumpReportModel.getResultObservableMap().get(pumpTestModel.pumpTestProperty().get()).deliveryFlowProperty().get();
        measuredFlow = flow.equals("-") ? 0 : Double.parseDouble(flow);
//        //TODO delete testing line!!!
//        measuredFlow = 2.1;

        double flowDelta = targetFlow - measuredFlow;

        if (Math.abs(flowDelta) <= TOLERANCE) {
            finish();
        }
        else {
            int stepDirection = (int)(flowDelta / Math.abs(flowDelta)) * regulatonDirection;
            nextCurrent(stepDirection );
        }
    }

    private void preparePressure() {

        if (isSectionReady(pumpPressureRegulatorOneModel.pressureRegProperty().get(), highPressureSectionUpdateModel.lcdPressureProperty().get(), 0.2)) {
            pressurePreparationTimeline.stop();
            currentPressureTime = DEFAULT_WAITING_TIME;
            testLabel.setText(testText.get());
            ampereLabel.setText("A");
            nextCurrent(1);
        }

        if (--currentPressureTime == 0) {
            pressurePreparationTimeline.stop();
            currentPressureTime = DEFAULT_WAITING_TIME;
            changeCalibrationState(CalibrationState.NO_TARGET);
        }
    }

    private void prepareMotor() {

        if (isSectionReady(testBenchSectionModel.targetRPMProperty().get(), testBenchSectionModel.currentRPMProperty().get(), 0.1)) {
            motorPreparationTimeline.stop();
            currentRpmTime = DEFAULT_WAITING_TIME;
            pressurePreparationTimeline.play();
        }

        if (--currentRpmTime == 0) {
            motorPreparationTimeline.stop();
            currentRpmTime = DEFAULT_WAITING_TIME;
            changeCalibrationState(CalibrationState.NO_TARGET);
        }
    }

    private boolean isSectionReady(int targetValue, int currentValue, double tolerance){

        //TODO delete testing return!!!
        if (targetValue == 0) return true;

        return Math.abs((targetValue - currentValue) / targetValue) < tolerance;
    }

    private void changeCalibrationState(CalibrationState state) {

        processState = state;

        switch (state) {

            case RUNNING:
            case FINISHED:
                errorLabel.setVisible(false);
                progressIndicator.setVisible(true);
                break;
            case NO_TARGET:
                errorLabel.setVisible(true);
                progressIndicator.setVisible(false);
                break;
        }
    }

    private void startMeasurement() {

        resetFlowData();
        measurementTimeline.play();
    }

    private void resetFlowData() {

        flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
    }

    public void work() {

        initSCVStage();
        clearScvCalibrationResults();

        double calibrationMinI = pumpTestModel.pumpTestProperty().get().getCalibrationMinI();
        double calibrationMaxI = pumpTestModel.pumpTestProperty().get().getCalibrationMaxI();
        minDirectFlow = pumpTestModel.pumpTestProperty().get().getMinDirectFlow();
        maxDirectFlow = pumpTestModel.pumpTestProperty().get().getMaxDirectFlow();
        targetFlow = (minDirectFlow + maxDirectFlow) / 2;
        testCurrentPoint = calibrationMinI;
        delta_I = calibrationMaxI - calibrationMinI;
        regulatonDirection = pumpModel.pumpProperty().get().getPumpRegulatorType().getMultiplier();
        changeCalibrationState(CalibrationState.RUNNING);
        //TODO uncomment below!!!
//        adjustingTime = pumpTestModel.pumpTestProperty().get().getAdjustingTime();
//        measuringTime = pumpTestModel.pumpTestProperty().get().getMeasuringTime();
        //TODO delete test setCycleCounts!!!
        adjustingTime = 3;
        measuringTime = 3;

        adjustingTimeline.setCycleCount(adjustingTime);
        measurementTimeline.setCycleCount(measuringTime);

        start();
    }

    private void resetTimers() {

        restAdjustingTime = adjustingTime;
        restMeasuringTime = measuringTime;
    }

    private void nextCurrent(int stepDirection) {

        delta_I = (((double)(Math.round((Math.abs(delta_I) / 2) * 100)) / 100)) * stepDirection;

        if (Math.abs(delta_I) > 0.01) {

            testCurrentPoint = testCurrentPoint + delta_I;
            regulatorTwoCurrentSpinner.getValueFactory().setValue(testCurrentPoint);
            currentLabel.setText(String.format("%1.2f", testCurrentPoint));
            resetTimers();
            gapTimeline.play();
        }
        else {

            finish();
        }
    }

    private void finish() {

        changeCalibrationState(CalibrationState.FINISHED);
        stopSCVButton.fire();
    }

    private void setCalibrationModelValues(boolean isSuccessful, double calibrationCurrent, boolean isFinished) {

        scvCalibrationModel.isSuccessfulProperty().setValue(isSuccessful);
        scvCalibrationModel.initialCurrentProperty().setValue(calibrationCurrent);
        scvCalibrationModel.isFinishedProperty().setValue(isFinished);
    }

    public void clearScvCalibrationResults() {

        setCalibrationModelValues(false, 0, false);
    }

    private void stopTimelines() {

        gapTimeline.stop();
        adjustingTimeline.stop();
        measurementTimeline.stop();
        pressurePreparationTimeline.stop();
        motorPreparationTimeline.stop();
    }

    private void clearLabels() {

        testLabel.setText("");
        currentLabel.setText("");
        ampereLabel.setText("");
        processLabel.setText("");
        secondsLabel.setText("");
        timerLabel.setText("");
    }

    private void bindingI18N() {

        errorLabel.textProperty().bind(i18N.createStringBinding("pump.test.scvTargetFailure"));
        adjustingLabelText.bind(i18N.createStringBinding("pump.test.scvAdjustingText"));
        measuringLabelText.bind(i18N.createStringBinding("pump.test.scvMeasuringText"));
        testText.bind(i18N.createStringBinding("pump.test.scvTestText"));
        titleLabel.textProperty().bind(i18N.createStringBinding("pump.test.ScvTitle"));
        secondsText.bind(i18N.createStringBinding("pump.test.seconds"));
    }
}
