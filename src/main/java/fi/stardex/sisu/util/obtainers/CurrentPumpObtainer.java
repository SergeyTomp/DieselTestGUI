package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.pump.Pump;

public class CurrentPumpObtainer {

    private static Pump pump;

    public static Pump getPump() {
        return pump;
    }

    public static void setPump(Pump newPump) {
        pump = newPump;
    }
}
