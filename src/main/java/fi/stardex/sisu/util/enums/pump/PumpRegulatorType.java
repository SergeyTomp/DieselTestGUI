package fi.stardex.sisu.util.enums.pump;

public enum PumpRegulatorType {

    N_O (- 1, "Normal Open"),
    N_C (1, "Normal closed"),
    NO_TYPE (0, "No regulation");

    int multiplier;
    String type;

    PumpRegulatorType(int multiplier, String type) {
        this.multiplier = multiplier;
        this.type = type;
    }

    public int getMultiplier() {
        return multiplier;
    }


    @Override
    public String toString() {
        return type;
    }
}
