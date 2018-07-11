package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

import java.util.List;

public class CurrentInjectorTestsObtainer {
    private List<InjectorTest> injectorTests;

    public List<InjectorTest> getInjectorTests() {
        return injectorTests;
    }

    public void setInjectorTests(List<InjectorTest> injectorTests) {
        this.injectorTests = injectorTests;
    }
}
