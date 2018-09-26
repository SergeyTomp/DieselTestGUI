package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

import java.util.List;

public class CurrentInjectorTestsObtainer {

    private static List<InjectorTest> injectorTests;

    public static List<InjectorTest> getInjectorTests() {
        return injectorTests;
    }

    public static void setInjectorTests(List<InjectorTest> newInjectorTests) {
        injectorTests = newInjectorTests;
    }

}
