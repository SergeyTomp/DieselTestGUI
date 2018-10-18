package fi.stardex.sisu.measurement;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.coding.bosch.BoschCoding;
import fi.stardex.sisu.coding.delphi.c3i.DelphiC3ICoding;
import fi.stardex.sisu.coding.delphi.c3i.DelphiC3ICodingDataStorage;
import fi.stardex.sisu.coding.denso.DensoCodingDataStorage;
import fi.stardex.sisu.coding.delphi.c2i.DelphiC2ICoding;
import fi.stardex.sisu.coding.delphi.c2i.DelphiC2ICodingDataStorage;
import fi.stardex.sisu.coding.denso.DensoCoding;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
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
import fi.stardex.sisu.util.enums.Measurement;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static fi.stardex.sisu.store.FlowReport.getMapOfFlowTestResults;
import static fi.stardex.sisu.util.enums.Tests.TestType.CODING;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;
import static fi.stardex.sisu.util.enums.Tests.getTestType;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;
import static fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer.getManufacturer;

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

    private InjectorSectionController injectorSectionController;

    private ToggleButton injectorSectionStartToggleButton;

    private Spinner<Integer> widthCurrentSignalSpinner;

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

    private Iterator<Integer> densoCodingPointsIterator;

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

        this.mainSectionController = mainSectionController;
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

        this.injectorSectionController = injectorSectionController;
        injectorSectionStartToggleButton = injectorSectionController.getInjectorSectionStartToggleButton();
        widthCurrentSignalSpinner = injectorSectionController.getWidthCurrentSignalSpinner();

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

        switch (getTestType()) {
            case AUTO:
                includedAutoTestsLength = (int) testListViewItems.stream().filter(InjectorTest::isIncluded).count();
                break;
            case CODING:
                List<Integer> activeLedToggleButtonsList = injectorSectionController.getActiveLedToggleButtonsList()
                        .stream()
                        .mapToInt(toggleButton -> Integer.parseInt(toggleButton.getText()))
                        .boxed()
                        .collect(Collectors.toList());
                if (isDensoCoding())
                    DensoCodingDataStorage.initialize(activeLedToggleButtonsList,
                            testListViewItems
                                    .stream()
                                    .filter(injectorTest -> injectorTest.getTestName().getMeasurement() != Measurement.VISUAL)
                                    .collect(Collectors.toList()));
                else if (isDelphiC2ICoding())
                    DelphiC2ICodingDataStorage.initialize(activeLedToggleButtonsList,
                            testListViewItems
                                    .stream()
                                    .filter(injectorTest -> injectorTest.getTestName().isTestPoint())
                                    .map(injectorTest -> injectorTest.getTestName().toString())
                                    .collect(Collectors.toList()));
                else if (isDelphiC3ICoding())
                    DelphiC3ICodingDataStorage.initialize(activeLedToggleButtonsList);
                break;
        }

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

            DensoCodingDataStorage.clean();

            DelphiC2ICodingDataStorage.clean();

            DelphiC3ICodingDataStorage.clean();

            densoCodingPointsIterator = null;

            mainSectionController.pointToFirstTest();

            codingComplete = false;

        }

        enabler.startTest(false);

    }

    private void performCoding() {

        switch (getManufacturer().toString()) {

            case "Bosch":
                setCodingResults(BoschCoding.calculate());
                break;
            case "Denso":
                setCodingResults(DensoCoding.calculate());
                break;
            case "Delphi":
                if (isDelphiC2ICoding())
                    setCodingResults(DelphiC2ICoding.calculate());
                else if (isDelphiC3ICoding())
                    setCodingResults(DelphiC3ICoding.calculate());
                break;

        }

    }

    private void setCodingResults(List<String> codeResult) {

        injectorCode1TextField.setText(codeResult.get(0));
        injectorCode2TextField.setText(codeResult.get(1));
        injectorCode3TextField.setText(codeResult.get(2));
        injectorCode4TextField.setText(codeResult.get(3));

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

        InjectorTest injectorTest = testsSelectionModel.getSelectedItem();

        TestName testName = injectorTest.getTestName();

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
                                    start();
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
                    } else if (isDelphiC2ICoding() || isDelphiC3ICoding()) {
                        selectNextTest(selectedTestIndex);
                        start();
                    } else {
                        selectNextTest(selectedTestIndex);
                        if (testName.toString().equals("ISA Detection"))
                            startISADetection();
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

            InjectorTest injectorTest = testsSelectionModel.getSelectedItem();

            TestName testName = injectorTest.getTestName();

            switch (getTestType()) {

                case AUTO:
                    flowReport.save(injectorTest);
                    break;
                case CODING:
                    if (isDensoCoding() && testName.getMeasurement() != Measurement.VISUAL) {
                        flowReport.save(injectorTest);
                        DensoCodingDataStorage.store(widthCurrentSignalSpinner.getValue(), getMapOfFlowTestResults().get(injectorTest));
                    } else if (testName.isTestPoint()) {
                        flowReport.save(injectorTest);
                        if (isDelphiC2ICoding())
                            DelphiC2ICodingDataStorage.store(getMapOfFlowTestResults().get(injectorTest));
                        else if (isDelphiC3ICoding())
                            DelphiC3ICodingDataStorage.store(getMapOfFlowTestResults().get(injectorTest));
                    }
                    break;

            }

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
