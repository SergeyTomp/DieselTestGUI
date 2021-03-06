package fi.stardex.sisu.util.enums;

public enum TestSpeed {

    NORM (1, "Normal"),
    PRECISE(2, "Precise"),
    FAST(0.5, "Fast");

    double multiplier;
    private String text;

    TestSpeed(double multiplier, String text) {
        this.multiplier = multiplier;
        this.text = text;
    }

    public double getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return text;
    }
}
