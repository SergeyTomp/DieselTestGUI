package fi.stardex.sisu.registers.flow;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

import static fi.stardex.sisu.registers.RegisterType.*;

public enum ModbusMapFlow implements ModbusMap {

    FirmwareVersion(REGISTER_INPUT, 0, 1, false),

    StartMeasurementCycle(DISCRETE_COIL, 0, 1, false),
    StopMeasurementCycle(DISCRETE_COIL, 1, 1, false),

    Channel1Level(REGISTER_INPUT, 2, 2, true),
    Channel1Temperature1(REGISTER_INPUT, 12, 2, true),
    Channel1Temperature2(REGISTER_INPUT, 14, 2, true),

    Channel2Level(REGISTER_INPUT, 16, 2, true),
    Channel2Temperature1(REGISTER_INPUT, 26, 2, true),
    Channel2Temperature2(REGISTER_INPUT, 28, 2, true),

    Channel3Level(REGISTER_INPUT, 30, 2, true),
    Channel3Temperature1(REGISTER_INPUT, 40, 2, true),
    Channel3Temperature2(REGISTER_INPUT, 42, 2, true),

    Channel4Level(REGISTER_INPUT, 44, 2, true),
    Channel4Temperature1(REGISTER_INPUT, 54, 2, true),
    Channel4Temperature2(REGISTER_INPUT, 56, 2, true),

    Channel5Level(REGISTER_INPUT, 58, 2, true),
    Channel5Temperature1(REGISTER_INPUT, 68, 2, true),
    Channel5Temperature2(REGISTER_INPUT, 70, 2, true),

    Channel6Level(REGISTER_INPUT, 72, 2, true),
    Channel6Temperature1(REGISTER_INPUT, 82, 2, true),
    Channel6Temperature2(REGISTER_INPUT, 84, 2, true),

    Channel7Level(REGISTER_INPUT, 86, 2, true),
    Channel7Temperature1(REGISTER_INPUT, 96, 2, true),
    Channel7Temperature2(REGISTER_INPUT, 98, 2, true),

    Channel8Level(REGISTER_INPUT, 100, 2, true),
    Channel8Temperature1(REGISTER_INPUT, 110, 2, true),
    Channel8Temperature2(REGISTER_INPUT, 112, 2, true),

    Channel9Level(REGISTER_INPUT, 114, 2, true),
    Channel9Temperature1(REGISTER_INPUT, 124, 2, true),
    Channel9Temperature2(REGISTER_INPUT, 126, 2, true),

    Channel10Level(REGISTER_INPUT, 128, 2, true),
    Channel10Temperature1(REGISTER_INPUT, 138, 2, true),
    Channel10Temperature2(REGISTER_INPUT, 140, 2, true),

    // Differential flowmeter registers
    ShiftingDuration(REGISTER_HOLDING, 2, 1, false),
    ShiftingPeriod(REGISTER_HOLDING, 3, 1, false),
    ShiftingAutoStartIsOn(DISCRETE_COIL, 2, 1, true),
    ShiftingManualStart(DISCRETE_COIL, 3, 1, true),
    ShiftingIsInProgress(DISCRETE_INPUT, 11, 1, true);

    private final RegisterType type;
    private final int ref;
    private final int count;
    private final boolean autoUpdate;
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

}
