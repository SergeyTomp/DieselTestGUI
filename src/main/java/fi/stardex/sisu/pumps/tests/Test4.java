package fi.stardex.sisu.pumps.tests;

import fi.stardex.sisu.persistence.orm.PumpTest;

public class Test4 extends EfficiencyTest {

    @Override
    public String toString() {
        return "Efficiency test 2";
    }

    @Override
    public PumpTest getEfficiencyTest() {
        return getEfficiencyTest2();
    }
}
