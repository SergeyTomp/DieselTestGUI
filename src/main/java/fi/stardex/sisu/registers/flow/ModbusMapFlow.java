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
    Channel4Temperature2(RegisterType.REGISTER_INPUT, 56, 2, true),

    Channel5Level(RegisterType.REGISTER_INPUT, 58, 2, true),
    Channel5Temperature1(RegisterType.REGISTER_INPUT, 68, 2, true),
    Channel5Temperature2(RegisterType.REGISTER_INPUT, 70, 2, true),

    Channel6Level(RegisterType.REGISTER_INPUT, 72, 2, true),
    Channel6Temperature1(RegisterType.REGISTER_INPUT, 82, 2, true),
    Channel6Temperature2(RegisterType.REGISTER_INPUT, 84, 2, true),

    Channel7Level(RegisterType.REGISTER_INPUT, 86, 2, true),
    Channel7Temperature1(RegisterType.REGISTER_INPUT, 96, 2, true),
    Channel7Temperature2(RegisterType.REGISTER_INPUT, 98, 2, true),

    Channel8Level(RegisterType.REGISTER_INPUT, 100, 2, true),
    Channel8Temperature1(RegisterType.REGISTER_INPUT, 110, 2, true),
    Channel8Temperature2(RegisterType.REGISTER_INPUT, 112, 2, true),

    Channel9Level(RegisterType.REGISTER_INPUT, 114, 2, true),
    Channel9Temperature1(RegisterType.REGISTER_INPUT, 124, 2, true),
    Channel9Temperature2(RegisterType.REGISTER_INPUT, 126, 2, true),

    Channel10Level(RegisterType.REGISTER_INPUT, 114, 2, true),
    Channel10Temperature1(RegisterType.REGISTER_INPUT, 124, 2, true),
    Channel10Temperature2(RegisterType.REGISTER_INPUT, 126, 2, true);

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
