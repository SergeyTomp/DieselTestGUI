package fi.stardex.sisu.pumps.tests;

public class Test6 extends RescalincgPumpTest {
//
//    private static final int RPM = 1000;
//    private static final double PRESSURE = 500;
//
//    @Override
//    public void execute() {
//        super.execute();
//        List<String> lines = new ArrayList<>();
//        lines.add(String.format("Set feeding pressure of calibration oil: %.1f Bar", currentPumpObtainer.getPump().getSupply()));
//        lines.add(String.format("Set test bench rotation speed: %s RPM", RPM));
//        lines.add(String.format("Set rail pressure: %s Bar", PRESSURE));
//        lines.add("Set shut off valve current: 0 A");
//        lines.add("Measure pump delivery and enter the value into the results section.");
//
//
//        additionalSectionController.getPumpInstruction().setText(listToString(lines));
//        highPressureSectionController.getPressBarReg1().getValueFactory().setValue(PRESSURE);
//        testBenchSectionController.getSpinnerRPM().getValueFactory().setValue(RPM);
//        setPumpBeakersData();
//
//    }
//
//    @Override
//    public String toString() {
//        return "Shut off test stage 1";
//    }

    @Override
    public boolean skipped() {
        return !currentPumpObtainer.getPump().getShutOff();
    }
}
