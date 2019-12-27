package fi.stardex.sisu.measurement;

import fi.stardex.sisu.model.TestBenchSectionModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.uis.UisTestTimingModel;
import fi.stardex.sisu.model.updateModels.UisHardwareUpdateModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.common.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.uis.MainSectionUisController;
import fi.stardex.sisu.ui.controllers.uis.UisInjectorSectionController;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;

public class UisTestManager implements TestManager {

    private MainSectionUisController mainSectionUisController;
    private UisInjectorSectionController uisInjectorSectionController;
    private TestBenchSectionController testBenchSectionController;
    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private TestBenchSectionModel testBenchSectionModel;
    private UisHardwareUpdateModel uisHardwareUpdateModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private UisTestTimingModel uisTestTimingModel;

    private ToggleButton mainSectionStartToggleButton;
    private ToggleButton testBenchStartToggleButton;
    private ToggleButton regulatorToggleButton;
    private Button storeButton;
    private ListView<Test> testListView;
    private Timeline motorPreparationTimeline;
    private Timeline pressurePreparationTimeline;
    private Timeline adjustingTimeline;
    private Timeline measurementTimeline;
    private Timeline autoResetTimeline;
    private IntegerProperty adjustingTimeProperty;
    private IntegerProperty measuringTimeProperty;
    private IntegerProperty initialAdjustingTime;
    private IntegerProperty initialMeasuringTime;
    private int includedAutoTestsLength;
    private BooleanProperty injectorSectionStart;
    private Alert settingsAlert;
    private StringProperty alertString = new SimpleStringProperty();
    private StringProperty applyButton = new SimpleStringProperty();
    private I18N i18N;

    public void setMainSectionUisController(MainSectionUisController mainSectionUisController) {
        this.mainSectionUisController = mainSectionUisController;
    }
    public void setUisInjectorSectionController(UisInjectorSectionController uisInjectorSectionController) {
        this.uisInjectorSectionController = uisInjectorSectionController;
    }
    public void setTestBenchSectionController(TestBenchSectionController testBenchSectionController) {
        this.testBenchSectionController = testBenchSectionController;
    }
    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setTestBenchSectionModel(TestBenchSectionModel testBenchSectionModel) {
        this.testBenchSectionModel = testBenchSectionModel;
    }
    public void setUisHardwareUpdateModel(UisHardwareUpdateModel uisHardwareUpdateModel) {
        this.uisHardwareUpdateModel = uisHardwareUpdateModel;
    }
    public void setUisTestTimingModel(UisTestTimingModel uisTestTimingModel) {
        this.uisTestTimingModel = uisTestTimingModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {

        setupReferences();
        setupTimeLines();
        setupListeners();
        bindingI18N();
    }

    private void setupReferences() {

        adjustingTimeProperty = uisTestTimingModel.getAdjustingTime();
        measuringTimeProperty = uisTestTimingModel.getMeasuringTime();
        initialAdjustingTime = uisTestTimingModel.getInitialAdjustingTime();
        initialMeasuringTime = uisTestTimingModel.getInitialMeasuringTime();
        mainSectionStartToggleButton = mainSectionUisController.getStartToggleButton();
        storeButton = mainSectionUisController.getStoreButton();
        testListView = mainSectionUisController.getTestListView();
        testBenchStartToggleButton = testBenchSectionController.getTestBenchStartToggleButton();
        regulatorToggleButton = uisInjectorSectionController.getRegulatorToggleButton();
        injectorSectionStart = uisInjectorSectionController.sectionStartProperty();
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

//        mainSectionUisModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {
//
//            if (newValue)
//                startMeasurements();
//            else
//                stopMeasurements();
//        });

        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

            setTimings(newValue);
            if (mainSectionUisModel.testTypeProperty().get() == TESTPLAN && mainSectionUisModel.startButtonProperty().get()) {

                checkInjectorTypeAndStart();
            }
        });

        mainSectionUisModel.multiplierProperty().addListener((observableValue, oldValue, newValue)
                -> setTimings(mainSectionUisModel.injectorTestProperty().get()));
    }

    private void setTimings(Test test) {

        if (test != null) {
            double multiplier = mainSectionUisModel.multiplierProperty().get().getMultiplier();
            int adjustingTime = (int)(test.getAdjustingTime() * multiplier);
            int measurementTime = (int)(test.getMeasurementTime() * multiplier);
            /**Uncomment string below to set short timings for debugging */
//            adjustingTime = measurementTime = (int)(5 * multiplier);
            initialAdjustingTime.setValue(adjustingTime);
            initialMeasuringTime.setValue(measurementTime);
            adjustingTimeProperty.setValue(adjustingTime);
            measuringTimeProperty.setValue(measurementTime);
        }else {
            initialAdjustingTime.setValue(0);
            initialMeasuringTime.setValue(0);
            adjustingTimeProperty.setValue(0);
            measuringTimeProperty.setValue(0);
        }
    }

    private void runNextTest() {

        if (mainSectionUisModel.testTypeProperty().get() == AUTO) {

            int selectedTestIndex = testListView.getSelectionModel().getSelectedIndex();
            if (selectedTestIndex < includedAutoTestsLength - 1) {

                selectNextTest(selectedTestIndex);
                checkInjectorTypeAndStart();

            } else {
                Platform.runLater(() -> mainSectionStartToggleButton.setSelected(false));
            }
        }
    }

    private void motorPreparation() {

        if (isSectionReady(testBenchSectionModel.targetRPMProperty().get(), testBenchSectionModel.currentRPMProperty().get(), 0.1)) {

            motorPreparationTimeline.stop();
            startPressure();
        }
    }

    private void startPressure() {

        if (!regulatorToggleButton.isDisabled()) {
            injectorSectionStart.setValue(false);
            regulatorToggleButton.setSelected(true);
            pressurePreparationTimeline.play();
        } else {
            injectorSectionStart.setValue(true);
            adjustingTimeline.play();
        }
    }

    private void pressurePreparation() {

        int targetValue = uisInjectorSectionModel.pressureSpinnerProperty().get();
        int lcdValue = uisHardwareUpdateModel.lcdPressureProperty().get();

        if(isSectionReady(targetValue, lcdValue, 0.2) || targetValue == 0){
            pressurePreparationTimeline.stop();
            resetFlowData();
            injectorSectionStart.setValue(true);

            if (mainSectionUisModel.testTypeProperty().get() != TESTPLAN) {
                adjustingTimeline.play();
            }
            autoResetTimeline.play();
        }
    }

    private void tickAdjustingTime() {

        if (tick(adjustingTimeProperty) == 0) {

            adjustingTimeline.stop();
            if (mainSectionUisModel.measurementTimeEnabledProperty().get()) {
                measurementTimeline.play();
                autoResetTimeline.play();
            } else {
                storeButton.fire();
                runNextTest();
            }
        }
    }

    private void tickMeasurementTime() {

        if (tick(measuringTimeProperty) == 0) {

            measurementTimeline.stop();
            storeButton.fire();
            runNextTest();
        }
    }

    private int tick(IntegerProperty timeProperty) {

        int time = timeProperty.get();

        if (time > 0) {

            timeProperty.setValue(--time);
        }
        return time;
    }

    public void start(){

//        if (testBenchSectionModel.isPowerButtonDisabledProperty().get())
//            startPressure();
//        else
            startMotor();
    }

    private void startMotor() {
        if (!testBenchSectionModel.isPowerButtonDisabledProperty().get()) {
            testBenchStartToggleButton.setSelected(true);
        }
        motorPreparationTimeline.play();
    }

    private void startMeasurements() {

        resetFlowData();
        if (mainSectionUisModel.testTypeProperty().get() == AUTO) {

            includedAutoTestsLength = (int)mainSectionUisModel.getTestObservableList().stream().filter(t -> t.includedProperty().get()).count();

            testListView.getSelectionModel().clearSelection();
            runNextTest();
        }else{
            checkInjectorTypeAndStart();
        }
    }

    private void checkInjectorTypeAndStart() {

        if (mainSectionUisModel.modelProperty().get().getVAP().getInjectorSubType() == InjectorSubType.MECHANIC) {
            injectorSectionStart.setValue(false);
            showAlert();
        } else {
            start();
        }
    }

    private void stopMeasurements() {

        stopTimers();
        resetFlowData();
        regulatorToggleButton.setSelected(false);
        testBenchStartToggleButton.setSelected(false);
        injectorSectionStart.setValue(false);
        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);
        setTimings(mainSectionUisModel.injectorTestProperty().get());
    }

    private void stopTimers() {

        motorPreparationTimeline.stop();
        pressurePreparationTimeline.stop();
        adjustingTimeline.stop();
        measurementTimeline.stop();
    }

    private void selectNextTest(int testIndex) {

        testListView.getSelectionModel().select(++testIndex);
        testListView.scrollTo(testIndex);

    }

    private void resetFlowData() {

        flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
    }

    private boolean isSectionReady(double targetValue, double lcdValue, double margin) {

        return Math.abs((targetValue - lcdValue) / targetValue) < margin;
    }

    private void showAlert() {

        if (settingsAlert == null) {
            settingsAlert = new Alert(Alert.AlertType.NONE, "", ButtonType.APPLY);
            settingsAlert.initStyle(StageStyle.UNDECORATED);
            settingsAlert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            settingsAlert.getDialogPane().getStyleClass().add("alertDialog");
            settingsAlert.setResizable(true);
            settingsAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            settingsAlert.getDialogPane().setMinWidth(550);
            Button button = (Button) settingsAlert.getDialogPane().lookupButton(ButtonType.APPLY);
            button.setDefaultButton(true);
            button.setOnAction(event -> start());
        }
        int rackPosition = ((InjectorUisTest) (mainSectionUisModel.injectorTestProperty().get())).getRackPosition();
        ((Button)settingsAlert.getDialogPane().lookupButton(ButtonType.APPLY)).textProperty().setValue(applyButton.get());
        settingsAlert.setContentText(alertString.get() + " " + rackPosition + " mm");
        settingsAlert.show();
    }

    private void bindingI18N() {
        alertString.bind(i18N.createStringBinding("alert.rackSettings"));
        applyButton.bind((i18N.createStringBinding("alert.applyButton")));
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {

        if (newValue)
            startMeasurements();
        else
            stopMeasurements();

    }
}
