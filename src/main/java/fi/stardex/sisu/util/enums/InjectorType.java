package fi.stardex.sisu.util.enums;

public enum InjectorType {

    COIL(0, 0, 2, "Inductance", "Resistance", "\u03BCH", "\u03A9", 500d, 3d),
    PIEZO(1, 1, 0, "Capacitance", "Resistance", "\u03BCF", "k\u03A9", 10d, 2000d),
    PIEZO_DELPHI(2, 1, 0, "Capacitance", "Resistance", "\u03BCF", "k\u03A9", 20d, 2000d);

    private final int valueToSend;
    private final int gauge1Decimals;
    private final int gauge2Decimals;
    private final String gauge1Title;
    private final String gauge2Title;
    private final String gauge1Unit;
    private final String gauge2Unit;
    private final double gauge1MaxValue;
    private final double gauge2MaxValue;

    InjectorType(int valueToSend,
                 int gauge1Decimals,
                 int gauge2Decimals,
                 String gauge1Title,
                 String gauge2Title,
                 String gauge1Unit,
                 String gauge2Unit,
                 double gauge1MaxValue,
                 double gauge2MaxValue) {

        this.valueToSend = valueToSend;
        this.gauge1Decimals = gauge1Decimals;
        this.gauge2Decimals = gauge2Decimals;
        this.gauge1Title = gauge1Title;
        this.gauge2Title = gauge2Title;
        this.gauge1Unit = gauge1Unit;
        this.gauge2Unit = gauge2Unit;
        this.gauge1MaxValue = gauge1MaxValue;
        this.gauge2MaxValue = gauge2MaxValue;
    }

    public int getValueToSend() {
        return valueToSend;
    }
    public int getGauge1Decimals() {
        return gauge1Decimals;
    }
    public int getGauge2Decimals() {
        return gauge2Decimals;
    }
    public String getGauge1Title() {
        return gauge1Title;
    }
    public String getGauge2Title() {
        return gauge2Title;
    }
    public String getGauge1Unit() {
        return gauge1Unit;
    }
    public String getGauge2Unit() {
        return gauge2Unit;
    }
    public double getGauge1MaxValue() {
        return gauge1MaxValue;
    }
    public double getGauge2MaxValue() {
        return gauge2MaxValue;
    }
}
