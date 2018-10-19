package fi.stardex.sisu.pumps.tests;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


public class Test1 extends RescalincgPumpTest {


    private static final int RPM = 100;

    @Override
    public void execute() {
        super.execute();
        List<String> lines = new ArrayList<>();
        lines.add(String.format("Set feeding pressure of calibration oil: %.1f Bar", currentPumpObtainer.getPump().getSupply()));
        lines.addAll(currentSCVObtainer.getSCV().obtainLines());
        lines.add(String.format("Set test bench rotation: %s", currentPumpObtainer.getPump().getRotationDirection()));
        lines.add(String.format("Set test bench rotation speed: %s RPM", RPM));
        lines.add("Make sure that there no losses and system functions in regular mode.");

//        additionalSectionController.getPumpInstruction().setText(listToString(lines));
        testBenchSectionController.getTargetRPMSpinner().getValueFactory().setValue(RPM);
        currentSCVObtainer.getSCV().execute();
        measuringTime.time(0);
        adjustingTime.time(0);
        highPressureSectionController.getPressReg1Spinner().getValueFactory().setValue(0);
        setPumpBeakersData();

    }

    @Override
    public String toString() {
        return "Test bench settings";
    }
//
    @Override
    public boolean skipped() {
        return false;
    }

}
