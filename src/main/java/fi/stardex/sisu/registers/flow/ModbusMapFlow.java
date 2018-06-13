package fi.stardex.sisu.registers.flow;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

public enum ModbusMapFlow implements ModbusMap {

    FlowMeterVersion(RegisterType.REGISTER_INPUT, 0, 1, false),

    StartMeasurementCycle(RegisterType.DISCRETE_COIL, 0, 1, false),
    StopMeasurementCycle(RegisterType.DISCRETE_COIL, 1, 1, false),

    Channel1Level(RegisterType.REGISTER_INPUT, 2, 2, true),
    Channel1Temperature1(RegisterType.REGISTER_INPUT, 12, 2, true),
    Channel1Temperature2(RegisterType.REGISTER_INPUT, 14, 2, true),

    Channel2Level(RegisterType.REGISTER_INPUT, 16, 2, true),
    Channel2Temperature1(RegisterType.REGISTER_INPUT, 26, 2, true),
    Channel2Temperature2(RegisterType.REGISTER_INPUT, 28, 2, true),

    Channel3Level(RegisterType.REGISTER_INPUT, 30, 2, true),
    Channel3Temperature1(RegisterType.REGISTER_INPUT, 40, 2, true),
    Channel3Temperature2(RegisterType.REGISTER_INPUT, 42, 2, true),

    Channel4Level(RegisterType.REGISTER_INPUT, 44, 2, true),
    Channel4Temperature1(RegisterType.REGISTER_INPUT, 54, 2, true),
    Channel4Temperature2(RegisterType.REGISTER_INPUT, 56, 2, true);

    private RegisterType type;
    private int ref;
    private int count;
    private boolean autoUpdate;
    private Object lastValue;

    ModbusMapFlow(RegisterType type, int ref, int count, boolean autoUpdate) {
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
