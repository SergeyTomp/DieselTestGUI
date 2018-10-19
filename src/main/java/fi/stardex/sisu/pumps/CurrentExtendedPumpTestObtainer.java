package fi.stardex.sisu.pumps;

import fi.stardex.sisu.pumps.tests.ExtendedPumpTest;


public class CurrentExtendedPumpTestObtainer {
    public ExtendedPumpTest getExtendedPumpTest() {
        return extendedPumpTest;
    }

    public void setExtendedPumpTest(ExtendedPumpTest extendedPumpTest) {
        this.extendedPumpTest = extendedPumpTest;
    }

    private ExtendedPumpTest extendedPumpTest;
}
