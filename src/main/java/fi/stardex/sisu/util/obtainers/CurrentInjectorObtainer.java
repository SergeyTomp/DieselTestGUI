package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;

public class CurrentInjectorObtainer {
    private Injector injector;

    public Injector getInjector() {
        return injector;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
