package fi.stardex.sisu.pumps.tests;

public class Test8 extends RescalincgPumpTest {

//    private static final int RPM = 150;
//    private static final double PRESSURE = 1200;
//
//    @Override
//    public void execute() {
//        super.execute();
//        List<String> lines = new ArrayList<>();
//        lines.add(String.format("Set feeding pressure of calibration oil: %.1f Bar", currentPumpObtainer.getPump().getSupply()));
//        lines.addAll(currentSCVObtainer.getSCV().obtainLines());
//        lines.add(String.format("Set test bench rotation speed: %s RPM", RPM));
//        lines.add(String.format("Set rail pressure: %s Bar", PRESSURE));
//
//        additionalSectionController.getPumpInstruction().setText(listToString(lines));
//        testBenchSectionController.getSpinnerRPM().getValueFactory().setValue(RPM);
//        currentSCVObtainer.getSCV().execute();
//        measuringTime.time(0);
//        adjustingTime.time(0);
//        highPressureSectionController.getPressBarReg1().getValueFactory().setValue(PRESSURE);
//        setPumpBeakersData();
//    }
//
//    @Override
//    public String toString() {
//        return "Start test";
//    }

    @Override
    public boolean skipped() {
        return false;
    }
}
