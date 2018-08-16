package fi.stardex.sisu.util.enums;

import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class Tests {

    private TestType test = MANUAL;

    public enum TestType {

        MANUAL, TESTPLAN, AUTO, CODING

    }

    public TestType getTest() {
        return test;
    }

    public void setTest(TestType test) {
        this.test = test;
    }
}
