package fi.stardex.sisu.persistence.orm.cr.pump;

import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;



public class PumpTest implements Test, Comparable<PumpTest>{


    @Override
    public String toString() {
        return "";
    }

    @Override
    public TestName getTestName() {
        return null;
    }

    @Override
    public int compareTo(PumpTest o) {
        return 0;
    }
}
