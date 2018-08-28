package fi.stardex.sisu.util.enums;

import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class Tests {

    private TestType test = MANUAL;

    public enum TestType {

        MANUAL, TESTPLAN, AUTO, CODING

    }

    public TestType getTestType() {
        return test;
    }

    public void setTestType(TestType test) {
        this.test = test;
    }
}
