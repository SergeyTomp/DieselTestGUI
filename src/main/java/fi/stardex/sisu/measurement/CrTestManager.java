package fi.stardex.sisu.measurement;

import fi.stardex.sisu.coding.Coder;
import fi.stardex.sisu.coding.CoderFactory;
import fi.stardex.sisu.model.TestBenchSectionModel;
import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.model.cr.PressureRegulatorOneModel;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.HighPressureSectionPwrState;
import fi.stardex.sisu.states.InjectorControllersState;
import fi.stardex.sisu.ui.controllers.common.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.MainSectionController;
import fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.enums.Tests.TestType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;

import static fi.stardex.sisu.util.enums.Tests.TestType.CODING;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;
import static fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer.getManufacturer;

public class CrTestManager implements TestManager {

    private ListView<InjectorTest> testListView;
    private ObservableList<InjectorTest> testListViewItems;
    private MultipleSelectionModel<InjectorTest> testsSelectionModel;
    private Button resetButton;
    private ToggleButton mainSectionStartToggleButton;
    private ToggleButton testBenchStartToggleButton;
    private ToggleButton injectorSectionStartToggleButton;
    private Spinner<Integer> widthCurrentSignalSpinner;
    private MainSectionController.TimeProgressBar adjustingTime;
    private MainSectionController.TimeProgressBar measuringTime;
    private Timeline motorPreparationTimeline;
    private Timeline pressurePreparationTimeline;
    private Timeline adjustingTimeline;
    private Timeline measurementTimeline;
    private Timeline autoResetTimeline;
    private Timeline preInjectionAutoResetTimeline;
    private ModbusRegisterProcessor flowModbusWriter;
    private MainSectionController mainSectionController;
    private int includedAutoTestsLength;
    private ISADetectionController isaDetectionController;
    private CodingReportModel codingReportModel;
    private FlowReportModel flowReportModel;
    private HighPressureSectionPwrState highPressureSectionPwrState;
    private PressureRegulatorOneModel pressureRegulatorOneModel;
    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;
    private TestBenchSectionModel testBenchSectionModel;
    private boolean codingComplete;
    private Iterator<Integer> densoCodingPointsIterator;
    private MainSectionModel mainSectionModel;
    private InjectorControllersState injectorControllersState;
    private CoderFactory coderFactory;
    private Coder coder;

    public void setCodingComplete(boolean codingComplete) {
        this.codingComplete = codingComplete;
    }

    public CrTestManager(MainSectionController mainSectionController,
                         TestBenchSectionController testBenchSectionController,
                         InjectorSectionController injectorSectionController,
                         ISADetectionController isaDetectionController,
                         CodingReportModel codingReportModel,
                         FlowReportModel flowReportModel,
                         HighPressureSectionPwrState highPressureSectionPwrState,
                         PressureRegulatorOneModel pressureRegulatorOneModel,
                         HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                         MainSectionModel mainSectionModel,
                         InjectorControllersState injectorControllersState,
                         TestBenchSectionModel testBenchSectionModel,
                         CoderFactory coderFactory) {

        this.flowReportModel = flowReportModel;
        this.mainSectionController = mainSectionController;
        this.codingReportModel = codingReportModel;
        this.pressureRegulatorOneModel = pressureRegulatorOneModel;
        this.highPressureSectionPwrState = highPressureSectionPwrState;
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
        this.coderFactory = coderFactory;
        testListView = mainSectionController.getTestListView();
        testListViewItems = mainSectionController.getTestListViewItems();
        testsSelectionModel = mainSectionController.getTestsSelectionModel();
        resetButton = mainSectionController.getResetButton();
        mainSectionStartToggleButton = mainSectionController.getStartToggleButton();
        adjustingTime = mainSectionController.getAdjustingTime();
        measuringTime = mainSectionController.getMeasuringTime();
        flowModbusWriter = mainSectionController.getFlowModbusWriter();
        testBenchStartToggleButton = testBenchSectionController.getTestBenchStartToggleButton();
        injectorSectionStartToggleButton = injectorSectionController.getInjectorSectionStartToggleButton();
        widthCurrentSignalSpinner = injectorSectionController.getWidthCurrentSignalSpinner();
        this.isaDetectionController = isaDetectionController;
        this.mainSectionModel = mainSectionModel;
        this.injectorControllersState = injectorControllersState;
        this.testBenchSectionModel = testBenchSectionModel;
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

        autoResetTimeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> autoReset()));
        preInjectionAutoResetTimeline = new Timeline(new KeyFrame(Duration.seconds(18), event -> autoReset()));

        pressureRegulatorOneModel.overPressureProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue && mainSectionStartToggleButton.isSelected()) {
                mainSectionStartToggleButton.setSelected(false);
            }
        });
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
        TestType testType = mainSectionModel.testTypeProperty().get();

        switch (testType) {
            case AUTO:
                includedAutoTestsLength = (int) testListViewItems.stream().filter(InjectorTest::isIncluded).count();
                break;
            case CODING:
                coder = coderFactory.getCoder(mainSectionModel.injectorProperty().get());
                break;
        }
        start();
    }

    private void stopMeasurements() {

        stopTimers();
        adjustingTime.refreshProgress();
        measuringTime.refreshProgress();
        switchOffSections();

        if (mainSectionModel.testTypeProperty().get() == CODING) {

            if (codingComplete)
                codingReportModel.storeResult(coder.buildCode());
            /** If Denso-coding: results corresponding to nominal pulse-width value
             * should be set into the report model after coding completion - not last calculated incremented values!
             * This values are used for PDF report only.
             */
            if (isDensoCoding()) {
                flowReportModel.setDensoCodingFlowReport();
            }

            densoCodingPointsIterator = null;
            mainSectionController.pointToFirstTest();
            codingComplete = false;
        }

        resetButton.fire();
    }

    private void autoReset(){

        mainSectionController.getResetButton().fire();
    }

    private void switchOffSections() {

        injectorSectionStartToggleButton.setSelected(false);
        highPressureSectionPwrState.powerButtonProperty().setValue(false);
        testBenchStartToggleButton.setSelected(false);
        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);
    }

    public void switchOffInjectorSection() {

        injectorSectionStartToggleButton.setSelected(false);
        flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);
    }

    public void runNextTest() {

        int selectedTestIndex = testsSelectionModel.getSelectedIndex();
        InjectorTest injectorTest = testsSelectionModel.getSelectedItem();
        TestName testName = injectorTest.getTestName();
        TestType testType = mainSectionModel.testTypeProperty().get();

        resetButton.fire();

        switch (testType) {

            case AUTO:
                if (selectedTestIndex < includedAutoTestsLength - 1) {
                    selectNextTest(selectedTestIndex);
                    start();
                } else {
                    mainSectionStartToggleButton.setSelected(false);
                }
                break;
            case CODING:
                if (selectedTestIndex < testListViewItems.size() - 1) {
                    if (isDensoCoding()) {
                        if (testName.getMeasurement() == Measurement.VISUAL) {
                            selectNextTest(selectedTestIndex);
                            runNextTest();
                        } else {
                            if (densoCodingPointsIterator == null) {
                                densoCodingPointsIterator = getDensoCodingPointsIterator(injectorTest);
                                if (!densoCodingPointsIterator.hasNext()) {
                                    densoCodingPointsIterator = null;
                                    selectNextTest(selectedTestIndex);
                                    start(); // TODO : Is it correct? In this case next test will be run not as DensoTest. May be runNextTest()?
                                } else
                                    runDensoTest();
                            } else {
                                if (!densoCodingPointsIterator.hasNext()) {
                                    densoCodingPointsIterator = null;
                                    selectNextTest(selectedTestIndex);
                                    runNextTest();
                                } else
                                    runDensoTest();
                            }
                        }
                    } else if (isDelphiC2ICoding() || isDelphiC3ICoding() || isDelphiC4ICoding()) {
                        selectNextTest(selectedTestIndex);
                        start();
                    } else {
                        selectNextTest(selectedTestIndex);
                        if(testsSelectionModel.getSelectedItem().getTestName().toString().equals("ISA Detection")){
                            startISADetection();
                        }
                        else
                            start();

                    }
                } else {
                    if (isDensoCoding()) {
                        if (densoCodingPointsIterator == null) {
                            densoCodingPointsIterator = getDensoCodingPointsIterator(injectorTest);
                            if (!densoCodingPointsIterator.hasNext())
                                finishCoding();
                            else
                                runDensoTest();
                        } else {
                            if (!densoCodingPointsIterator.hasNext())
                                finishCoding();
                            else
                                runDensoTest();
                        }
                    } else
                        finishCoding();
                }
                break;
        }
    }

    private void finishCoding() {

        densoCodingPointsIterator = null;
        codingComplete = true;
        mainSectionStartToggleButton.setSelected(false);
    }

    private void runDensoTest() {

        adjustingTime.refreshProgress();
        measuringTime.refreshProgress();
        /** For Denso-coding it is necessary to store result corresponding with test value of totalPulseTime
         * but not additionally calculated incremented and decremented width values
         * Below we initiate temporal result storage got by test-defined value of width*/
        if (widthCurrentSignalSpinner.getValue().intValue() == testsSelectionModel.getSelectedItem().getTotalPulseTime()) {
            flowReportModel.storeDensoCodingFlowResult();
        }
        widthCurrentSignalSpinner.getValueFactory().setValue(densoCodingPointsIterator.next());
        start();
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

        highPressureSectionPwrState.powerButtonProperty().setValue(true);
        pressurePreparationTimeline.play();
    }

    private void motorPreparation() {

            if (isSectionReady(testBenchSectionModel.targetRPMProperty().get(), testBenchSectionModel.currentRPMProperty().get(), 0.1)){

            motorPreparationTimeline.stop();
            startPressure();
        }
    }

    private void pressurePreparation() {

        if(isSectionReady(pressureRegulatorOneModel.pressureRegOneProperty().get(), highPressureSectionUpdateModel.lcdPressureProperty().get(), 0.2)){
            pressurePreparationTimeline.stop();
            resetButton.fire();
            injectorSectionStartToggleButton.setSelected(true);

            if (mainSectionModel.testTypeProperty().get() != TESTPLAN) {
                adjustingTimeline.play();
            }
            playResetTimeLine();
        }
    }

    //не стал удалять пока этот метод, просто деактивировал сброс, возможно надо будет вернуть всё обратно.
    private void playResetTimeLine() {

        Integer testId = testsSelectionModel.getSelectedItem().getTestName().getId();
        if (testId == 11 || testId == 12 || testId == 33) {
//            preInjectionAutoResetTimeline.play();
            return;
        }
//        autoResetTimeline.play();
    }

    private boolean isSectionReady(double currentValue, double lcdValue, double margin) {

        return Math.abs((currentValue - lcdValue) / currentValue) < margin;
    }

    private void tickAdjustingTime() {

        if (adjustingTime.tick() == 0) {

            adjustingTimeline.stop();
            resetButton.fire();
            measurementTimeline.play();
            playResetTimeLine();
        }
    }

    private void tickMeasurementTime() {

        if (measuringTime.tick() == 0) {
            measurementTimeline.stop();
            flowReportModel.storeResult();
            runNextTest();
        }
    }

    private void startISADetection() {

        isaDetectionController.work();
    }

    private boolean isDensoCoding() {

        return getManufacturer().getManufacturerName().equals("Denso");
    }

    private boolean isDelphiC2ICoding() {

        return getManufacturer().getManufacturerName().equals("Delphi") && getInjector().getCodetype() == 1;
    }

    private boolean isDelphiC3ICoding() {

        return getManufacturer().getManufacturerName().equals("Delphi") && getInjector().getCodetype() == 2;
    }

    private boolean isDelphiC4ICoding() {

        return getManufacturer().getManufacturerName().equals("Delphi") && getInjector().getCodetype() == 3;
    }

    private Iterator<Integer> getDensoCodingPointsIterator(InjectorTest injectorTest) {

        Integer totalPulseTime = injectorTest.getTotalPulseTime();

        int count = 3;
        int range = 60;

        ArrayList<Integer> codingPointsList = new ArrayList<>();
        codingPointsList.add(totalPulseTime - range);

        for (int i = 1; i < count; i++)
            codingPointsList.add(codingPointsList.get(i - 1) + (range * 2) / (count - 1));

        return codingPointsList.iterator();
    }
}
