package fi.stardex.sisu.util.enums.pump;

public enum PumpRegulatorType {

    N_O (- 1),
    N_C (1),
    NO_TYPE (0);

    int multiplier;

    PumpRegulatorType(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
