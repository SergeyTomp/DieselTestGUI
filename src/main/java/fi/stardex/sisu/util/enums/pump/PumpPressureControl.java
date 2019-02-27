package fi.stardex.sisu.util.enums.pump;

public enum PumpPressureControl {

    RAIL(false, true, false),
    RAIL_AND_PUMP(true, true, true);

    private boolean rail_and_pump;
    private boolean regulator_2_ON;
    private boolean regulator_3_ON;

    PumpPressureControl(boolean rail_and_pump, boolean regulator_2_ON, boolean regulator_3_ON) {
        this.rail_and_pump = rail_and_pump;
        this.regulator_2_ON = regulator_2_ON;
        this.regulator_3_ON = regulator_3_ON;
    }

    public boolean isRail_and_Pump() {
        return rail_and_pump;
    }
    public boolean isRegulator_2_ON() {
        return regulator_2_ON;
    }
    public boolean isRegulator_3_ON() {
        return regulator_3_ON;
    }
}
