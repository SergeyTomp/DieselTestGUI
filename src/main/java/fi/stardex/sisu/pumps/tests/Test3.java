package fi.stardex.sisu.pumps.tests;

import fi.stardex.sisu.persistence.orm.PumpTest;
import org.springframework.stereotype.Component;

public class Test3 extends EfficiencyTest {

    @Override
    public String toString() {
        return "Efficiency test 1";
    }

    @Override
    public PumpTest getEfficiencyTest() {
        return getEfficiencyTest1();
    }
}
