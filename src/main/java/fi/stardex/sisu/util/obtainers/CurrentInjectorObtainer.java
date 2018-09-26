package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;

public class CurrentInjectorObtainer {

    private static Injector injector;

    public static Injector getInjector() {
        return injector;
    }

    public static void setInjector(Injector newInjector) {
        injector = newInjector;
    }

}
