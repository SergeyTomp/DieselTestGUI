package fi.stardex.sisu.registers.modbusmaps;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(RegisterType.REGISTER, 116, 1, false),
    PositionRheostatOne(RegisterType.REGISTER, 4096, 2, true);

    private RegisterType type;
    private int ref;
    private int count;
    private boolean autoUpdate;
    private Object lastValue;

    ModbusMapUltima(RegisterType type, int ref, int count, boolean autoUpdate) {
        this.type = type;
        this.ref = ref;
        this.count = count;
        this.autoUpdate = autoUpdate;
    }

    @Override
    public RegisterType getType() {
        return type;
    }

    @Override
    public int getRef() {
        return ref;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getLastValue() {
        return lastValue;
    }

    @Override
    public void setLastValue(Object lastValue) {
        this.lastValue = lastValue;
    }

    @Override
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
}
