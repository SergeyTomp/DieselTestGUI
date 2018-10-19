package fi.stardex.sisu.pumps.tests;

import fi.stardex.sisu.pumps.scv.Close2;


public class Test5 extends RescalincgPumpTest {
//
//    private static final int RPM = 2000;
//    private static final double PRESSURE = 500d;
//
//    @Override
//    public void execute() {
//        super.execute();
//        List<String> lines = new ArrayList<>();
//        lines.add(String.format("Set feeding pressure of calibration oil: %.1f Bar", currentPumpObtainer.getPump().getSupply()));
//        lines.addAll(currentSCVObtainer.getSCV().obtainLines());
//        lines.add(String.format("Set test bench rotation speed: %s RPM", RPM));
//        lines.add(String.format("Set rail pressure: %s Bar", PRESSURE));
//        if(currentSCVObtainer.getSCV().getClass() == SCVEnum.OPEN.getScvClass()) {
//            lines.add("Increase gradually the current in the pressure regulator on the pump.");
//            lines.add("If the regulator is in order, then with the increase of the current in regulator");
//            lines.add("the pump will gradually decrease to practically complete absence of delivery.");
//        }
//        else if(currentSCVObtainer.getSCV().getClass() == SCVEnum.CLOSE.getScvClass()) {
//            lines.add("Decrease gradually the current in the pressure regulator on the pump.");
//            lines.add("If the regulator is in order, then with the decrease of the current in regulator");
//            lines.add("the pump will gradually decrease to practically complete absence of delivery.");
//        }
//        else if(currentSCVObtainer.getSCV().getClass() == SCVEnum.CLOSED.getScvClass()) {
//            lines.add("Decrease gradually the current in the SCV on the pump.");
//            lines.add("If the SCV is in order, then with the decrease of the current in SCV the pump will");
//            lines.add("gradually decrease to practically complete absence of delivery.");
//        }
//        additionalSectionController.getPumpInstruction().setText(listToString(lines));
//        highPressureSectionController.getPressBarReg1().getValueFactory().setValue(PRESSURE);
//        testBenchSectionController.getSpinnerRPM().getValueFactory().setValue(RPM);
//        setPumpBeakersData();
//
//    }
//
//    @Override
//    public String toString() {
//        return "SCV test";
//    }

    @Override
    public boolean skipped() {
        return currentSCVObtainer.getSCV().getClass().equals(Close2.class);
    }
}
