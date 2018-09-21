package fi.stardex.sisu.util.enums;

import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class Tests {

    private static TestType test = AUTO;

    public enum TestType {

        AUTO, TESTPLAN, CODING

    }

    public static TestType getTestType() {
        return test;
    }

    public static void setTestType(TestType newTest) {
        test = newTest;
    }
}
