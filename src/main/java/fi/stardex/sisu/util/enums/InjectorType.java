package fi.stardex.sisu.util.enums;

public enum InjectorType {
    COIL(0),
    PIEZO(1),
    PIEZO_DELPHI(2);

    private final int valueToSend;

    InjectorType(int valueToSend) {
        this.valueToSend = valueToSend;
    }

    public int getValueToSend() {
        return valueToSend;
    }
}
