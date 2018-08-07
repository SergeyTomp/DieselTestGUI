package fi.stardex.sisu.ui.controllers;

public enum SpinnerDefaults {
    WidthCurrentSignalSpinner (90, 15500, 500, 10, 0),
    FirstWSpinner (90, 15500, 500, 10, 0),
    BoostISpinner (3, 25.5, 21.8, 0.1, 0),
    FirstISpinner (2, 25.5, 11, 0.1, 15.01),
    SecondISpinner (1, 25.5, 6.6, 0.1, 5.51),
    BatteryUSpinner (11, 32, 20, 1, 0),
    NegativeUSpinner (17, 121, 48, 1, 0),
    BoostUSpinner (30, 75, 60, 1, 21.51);

    private final double min;
    private final double max;
    private final double init;
    private final double step;
    private final double fake;


    SpinnerDefaults(double min, double max, double init, double step, double fake) {
        this.min = min;
        this.max = max;
        this.init = init;
        this.step = step;
        this.fake = fake;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getInit() {
        return init;
    }

    public double getStep() {
        return step;
    }

    public double getFake() {
        return fake;
    }
}
