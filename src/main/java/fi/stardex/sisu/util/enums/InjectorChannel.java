package fi.stardex.sisu.util.enums;

public enum InjectorChannel {

    SINGLE_CHANNEL, MULTI_CHANNEL;

    private InjectorChannel lastValue;

    public InjectorChannel getLastValue() {
        return lastValue;
    }

    public void setLastValue(InjectorChannel lastValue) {
        this.lastValue = lastValue;
    }
}
