package fi.stardex.sisu.registers.stand;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

public enum ModbusMapStand implements ModbusMap {

    FirmwareVersion(RegisterType.REGISTER_INPUT, 0, 1, false),

    TargetRPM(RegisterType.REGISTER_HOLDING, 0, 1, true),

    RotationDirection(RegisterType.DISCRETE_COIL, 0, 1, true),
    Rotation(RegisterType.DISCRETE_COIL, 1, 1, true),
    PumpTurnOn(RegisterType.DISCRETE_COIL, 2, 1, true),
    FanTurnOn(RegisterType.DISCRETE_COIL, 3, 1, true),
    LightTurnOn(RegisterType.DISCRETE_COIL, 4, 1, true),
    HeatingTurnOn(RegisterType.DISCRETE_COIL, 5, 1, true),

    CurrentRPM(RegisterType.REGISTER_INPUT, 13, 1, true),
    Pressure1(RegisterType.REGISTER_INPUT, 16, 2, true);



    private RegisterType type;
    private int ref;
    private int count;
    private boolean autoUpdate;
    private Object lastValue;

    ModbusMapStand(RegisterType type, int ref, int count, boolean autoUpdate) {
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
