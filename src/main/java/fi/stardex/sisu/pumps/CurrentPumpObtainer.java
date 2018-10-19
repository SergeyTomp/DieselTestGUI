package fi.stardex.sisu.pumps;

import fi.stardex.sisu.persistence.orm.Pump;

public class CurrentPumpObtainer {

    private static Pump pump;

    public static Pump getPump() {
        return pump;
    }

    public static void setPump(Pump newPump) {
        pump = newPump;
    }


}
