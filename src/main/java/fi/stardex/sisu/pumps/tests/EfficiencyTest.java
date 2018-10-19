package fi.stardex.sisu.pumps.tests;

import fi.stardex.sisu.persistence.orm.PumpTest;
import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.pumps.CurrentPumpObtainer;
import fi.stardex.sisu.ui.data.SavedPumpBeakerData;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EfficiencyTest extends ExtendedPumpTest {


    private SavedPumpBeakerData savedDataForBeakersPump;

    private static final BigDecimal SUPPLY = new BigDecimal(0.8d);

    public void setSavedDataForBeakersPump(SavedPumpBeakerData savedDataForBeakersPump) {
        this.savedDataForBeakersPump = savedDataForBeakersPump;
    }

    @Override
    public void execute() {
        super.execute();
        executeEfficiency(getEfficiencyTest());
    }

    @Override
    public boolean skipped() {
        return false;
    }

    @Override
    protected void withTiming() {
        DefaultGroovyMethods.invokeMethod(enabler, "timingCheckbox", new Object[]{false, false});
    }

    public PumpTest getEfficiencyTest1() {
        return DefaultGroovyMethods.sort(CurrentPumpObtainer.getPump().getPumpType().getPumpTests()).get(0);
    }

    public PumpTest getEfficiencyTest2() {
        return DefaultGroovyMethods.sort(CurrentPumpObtainer.getPump().getPumpType().getPumpTests()).get(1);
    }

    public abstract PumpTest getEfficiencyTest();

    protected void executeEfficiency(PumpTest pumpTest) {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(String.format("Set feeding pressure of calibration oil: %.1f Bar", CurrentPumpObtainer.getPump().getSupply()));
        lines.addAll(currentSCVObtainer.getSCV().obtainLines());
        lines.add(String.format("Set test bench rotation speed: %s RPM", pumpTest.getRpm()));
        lines.add(String.format("Set rail pressure: %s Bar", pumpTest.getPressure()));
        if (CurrentPumpObtainer.getPump().getSupply().compareTo(SUPPLY) == 0) {
            lines.add("Measure pump delivery, pump back flow and enter the values into");
            lines.add("the results section.");
        } else {
            lines.add("Measure pump delivery and enter the value into the results section");
        }

//        additionalSectionController.getPumpInstruction().setText(listToString(lines));
        testBenchSectionController.getTargetRPMSpinner().getValueFactory().setValue(pumpTest.getRpm());
        highPressureSectionController.getPressReg1Spinner().getValueFactory().setValue(pumpTest.getPressure());

        measuringTime.time(0);
        adjustingTime.time(0);
        currentSCVObtainer.getSCV().execute();

        setPumpBeakersData();
    }

    private void setPumpBeakersData() {
//
//        beakers.reInstallPumpBeakers();
//
//        if (savedDataForBeakersPump.getSavedDataForBeakers().containsKey(currentPumpObtainer.getPump())) {
//            Map<Test, ArrayList<Double>> testData = savedDataForBeakersPump.getSavedDataForBeakers().get(currentPumpObtainer.getPump());
//            if (testData.containsKey(this)) {
//                valuesRestorer.recalculateAccordingToFormula(
//                        additionalSectionController.getDeliveryPumpComboBox().selectionModel.selectedItem,
//                        additionalSectionController.getBeakerPump1Controller(),
//                        testData.get(this).get(0));
//                valuesRestorer.recalculateAccordingToFormula(
//                        additionalSectionController.getBackFlowPumpComboBox().selectionModel.selectedItem,
//                        additionalSectionController.getBeakerPump2Controller(),
//                        testData.get(this).get(1));
//            }
//        }

    }

}
