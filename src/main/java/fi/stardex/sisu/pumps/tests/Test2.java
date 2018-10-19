package fi.stardex.sisu.pumps.tests;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


public class Test2 extends RescalincgPumpTest {

    private static final int RPM = 1500;
    private static final int PRESSURE = 500;
    private static final int TIME = 60;

    @Override
    public void withTiming() {
//        enabler.timingCheckbox(true, false);
    }
//
//
    @Override
    public void execute() {
        super.execute();
        List<String> lines = new ArrayList<>();
        lines.add(String.format("Set feeding pressure of calibration oil: %.1f Bar", currentPumpObtainer.getPump().getSupply()));
        lines.addAll(currentSCVObtainer.getSCV().obtainLines());
        lines.add(String.format("Set test bench rotation speed: %s RPM", RPM));
        lines.add(String.format("Set rail pressure: %s Bar", PRESSURE));
        lines.add(String.format("Test time %s seconds.", TIME));

//        additionalSectionController.getPumpInstruction().setText(listToString(lines));
        highPressureSectionController.getPressReg1Spinner().getValueFactory().setValue(PRESSURE);
        testBenchSectionController.getTargetRPMSpinner().getValueFactory().setValue(RPM);
        measuringTime.time(TIME);
        adjustingTime.time(0);
        currentSCVObtainer.getSCV().execute();
        setPumpBeakersData();

    }
//
    @Override
    public String toString() {
        return "Air cleaning and stabilization";
    }

    @Override
    public boolean skipped() {
        return false;
    }
}
