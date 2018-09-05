package fi.stardex.sisu.util.enums;

import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class Tests {

    private TestType test = AUTO;

    public enum TestType {

        AUTO, TESTPLAN, CODING

    }

    public TestType getTestType() {
        return test;
    }

    public void setTestType(TestType test) {
        this.test = test;
    }
}
