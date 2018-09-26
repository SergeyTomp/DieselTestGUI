package fi.stardex.sisu.measurement;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.coding.BoschCoding;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.store.FlowReport;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.CodingController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Tests.TestType;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.util.List;

import static fi.stardex.sisu.util.enums.Tests.TestType.*;
import static fi.stardex.sisu.util.enums.Tests.getTestType;

public class Measurements implements ChangeListener<Boolean> {

    private ListView<InjectorTest> testListView;

    private ObservableList<InjectorTest> testListViewItems;

    private MultipleSelectionModel<InjectorTest> testsSelectionModel;

    private Enabler enabler;

    private FlowReport flowReport;

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

    private MainSectionController mainSectionController;

    private TextField injectorCode1TextField;

    private TextField injectorCode2TextField;

    private TextField injectorCode3TextField;

    private TextField injectorCode4TextField;

    private int includedAutoTestsLength;

    private ISADetectionController isaDetectionController;

    private boolean codingComplete;

    public void setCodingComplete(boolean codingComplete) {
        this.codingComplete = codingComplete;
    }

    public Measurements(MainSectionController mainSectionController,
                        TestBenchSectionController testBenchSectionController,
                        HighPressureSectionController highPressureSectionController,
                        InjectorSectionController injectorSectionController,
                        Enabler enabler, FlowReport flowReport, CodingController codingController,
                        ISADetectionController isaDetectionController) {

        this.enabler = enabler;

        this.flowReport = flowReport;

        this.mainSectionController= mainSectionController;
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

        injectorCode1TextField = codingController.getInjectorCode1TextField();
        injectorCode2TextField = codingController.getInjectorCode2TextField();
        injectorCode3TextField = codingController.getInjectorCode3TextField();
        injectorCode4TextField = codingController.getInjectorCode4TextField();

        this.isaDetectionController = isaDetectionController;

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

        enabler.startTest(true);

        flowReport.clear();

        clearCodingResults();

        if (getTestType() == AUTO)
            includedAutoTestsLength = (int) testListViewItems.stream().filter(InjectorTest::isIncluded).count();

        start();

    }

    private void stopMeasurements() {

        stopTimers();

        adjustingTime.refreshProgress();
        measuringTime.refreshProgress();

        switchOffSections();

        if (getTestType() == CODING) {

            if (codingComplete)
                performCoding();

            mainSectionController.pointToFirstTest();

            codingComplete = false;

        }

        enabler.startTest(false);

    }

    private void performCoding() {

        String manufacturer = CurrentManufacturerObtainer.getManufacturer().toString();

        List<String> codeResult;

        switch (manufacturer) {
            case "Bosch":
                codeResult = BoschCoding.calculate();

                injectorCode1TextField.setText(codeResult.get(0));
                injectorCode2TextField.setText(codeResult.get(1));
                injectorCode3TextField.setText(codeResult.get(2));
                injectorCode4TextField.setText(codeResult.get(3));
                break;
        }

    }

    private void clearCodingResults() {

        injectorCode1TextField.setText("");
        injectorCode2TextField.setText("");
        injectorCode3TextField.setText("");
        injectorCode4TextField.setText("");

    }

    public void switchOffSections() {

        injectorSectionStartToggleButton.setSelected(false);
        highPressureStartToggleButton.setSelected(false);
        testBenchStartToggleButton.setSelected(false);

        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);

    }

    public void runNextTest() {

        int selectedTestIndex = testsSelectionModel.getSelectedIndex();

        TestType testType = getTestType();

        switch (testType) {

            case AUTO:
                if (selectedTestIndex < includedAutoTestsLength - 1) {
                    selectNextTest(selectedTestIndex);
                    start();
                } else
                    mainSectionStartToggleButton.setSelected(false);
                break;
            case CODING:
                if (selectedTestIndex < testListViewItems.size() - 1) {
                    selectNextTest(selectedTestIndex);
                    if (testsSelectionModel.getSelectedItem().getTestName().toString().equals("ISA Detection"))
                        startISADetection();
                    else
                        start();
                } else {
                    codingComplete = true;
                    mainSectionStartToggleButton.setSelected(false);
                }
                break;

        }

    }

    private void selectNextTest(int testIndex) {

        testsSelectionModel.select(++testIndex);
        testListView.scrollTo(testIndex);
        injectorSectionStartToggleButton.setSelected(false);

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

            if (getTestType() != TESTPLAN)
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

    private void startISADetection() {

        isaDetectionController.work();

    }

}
