package fi.stardex.sisu.coding;

import java.util.stream.Stream;

public enum CodeField {

    EMISSION_POINT ("Emission Point", true),
    IDLE ("Idle", true),
    MAX_LOAD ("Maximum Load", true),
    PRE_INJ ("Pre-injection", true),
    PRE_INJ_2 ("Pre-injection 2", true),
    KEM ("Kem", true),
    PRE_INJ_3 ("Pre-injection 3", true),
    RESERVE ("Reserve", true),
    REST ("Rest", true),
    TP1("Test Point 01", true),
    TP2("Test Point 02", true),
    TP4("Test Point 04", true),
    TP5("Test Point 05", true),
    TP6("Test Point 06", true),
    TP7("Test Point 07", true),
    PART_LOAD("Part Load", true),
    U_CHAR ("U_char", false),
    CHECK_SUM ("Check Sum", false),
    COEFF ("Coefficient", false),
    ADD_0("Add_0", false),
    ADD_00("Add_00", false),
    ADD_000("Add_000", false),
    ADD_00000("Add_00000", false);

    private final String testName;
    private final boolean isTest;

    CodeField(String testName, boolean isTest) {
        this.testName = testName;
        this.isTest = isTest;
    }

    public static CodeField getField(String test) {
        return Stream.of(values()).filter(f -> f.testName.equals(test)).findFirst().orElseThrow();
    }

    public boolean isTest() {
        return isTest;
    }

    @Override
    public String toString() {
        return testName;
    }
}
