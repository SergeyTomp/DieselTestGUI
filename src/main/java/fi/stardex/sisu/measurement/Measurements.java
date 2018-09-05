package fi.stardex.sisu.measurement;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.Enabler;
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
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

public class Measurements implements ChangeListener<Boolean> {

    private Enabler enabler;

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

    public Measurements(MainSectionController mainSectionController,
                        TestBenchSectionController testBenchSectionController,
                        HighPressureSectionController highPressureSectionController,
                        InjectorSectionController injectorSectionController, Tests tests, Enabler enabler) {

        this.tests = tests;

        this.enabler = enabler;

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

        enabler.startTest(true);

        resetButton.fire();

        TestType testType = tests.getTestType();

        switch (testType) {

            case TESTPLAN:
                start();
                break;
            case AUTO:
                break;
            case CODING:
                break;

        }

    }

    private void stopMeasurements() {

        enabler.startTest(false);

        motorPreparationTimeline.stop();
        pressurePreparationTimeline.stop();
        adjustingTimeline.stop();
        measurementTimeline.stop();

        adjustingTime.refreshProgress();
        measuringTime.refreshProgress();

        injectorSectionStartToggleButton.setSelected(false);
        highPressureStartToggleButton.setSelected(false);
        testBenchStartToggleButton.setSelected(false);
        mainSectionStartToggleButton.setSelected(false);

        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);

    }

    private void start() {

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

            // TODO: Только для Auto/Coding
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

        if (measuringTime.tick() == 0)
            stopMeasurements();

    }

}
