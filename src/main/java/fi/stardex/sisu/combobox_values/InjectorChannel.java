package fi.stardex.sisu.combobox_values;

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
