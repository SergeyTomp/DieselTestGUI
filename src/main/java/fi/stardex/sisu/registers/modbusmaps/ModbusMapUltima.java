package fi.stardex.sisu.registers.modbusmaps;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(RegisterType.REGISTER, 116, 1),
    BoostVoltage(RegisterType.REGISTER, 120, 2);

    private RegisterType type;
    private int ref;
    private int count;
    private Object lastValue;

    ModbusMapUltima(RegisterType type, int ref, int count) {
        this.type = type;
        this.ref = ref;
        this.count = count;
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
}
