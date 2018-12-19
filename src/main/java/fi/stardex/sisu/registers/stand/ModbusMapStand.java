package fi.stardex.sisu.registers.stand;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

import static fi.stardex.sisu.registers.RegisterType.*;

public enum ModbusMapStand implements ModbusMap {

    // --------------------Stand registers--------------------
    FirmwareVersion(REGISTER_INPUT, 0, 1, false, false),

    TargetRPM(REGISTER_HOLDING, 0, 1, true, false),

    RotationDirection(DISCRETE_COIL, 0, 1, true, false),
    Rotation(DISCRETE_COIL, 1, 1, true, false),
    PumpTurnOn(DISCRETE_COIL, 2, 1, true, false),
    FanTurnOn(DISCRETE_COIL, 3, 1, true, false),
    PumpAutoMode(DISCRETE_COIL, 11, 1, true, false),

    CurrentRPM(REGISTER_INPUT, 13, 1, true, false),
    Pressure1(REGISTER_INPUT, 16, 2, true, false),
    TankOilLevel(REGISTER_INPUT, 20, 1, true, false),
    Temperature1(REGISTER_INPUT, 21, 2, true, false),
    Temperature2(REGISTER_INPUT, 23, 2, true, false),

    // --------------------Stand_FM registers--------------------
    TargetRPMStandFM(REGISTER_HOLDING, 100, 1, true, true),

    RotationDirectionStandFM(DISCRETE_COIL, 100, 1, true, true),
    RotationStandFM(DISCRETE_COIL, 101, 1, true, true),
    PumpTurnOnStandFM(DISCRETE_COIL, 102, 1, true, true),
    FanTurnOnStandFM(DISCRETE_COIL, 103, 1, true, true),
    PumpAutoModeStandFM(DISCRETE_COIL, 111, 1, true, true),

    CurrentRPMStandFM(REGISTER_INPUT, 213, 1, true, true),
    Pressure1StandFM(REGISTER_INPUT, 216, 2, true, true),
    TankOilLevelStandFM(REGISTER_INPUT, 220, 1, true, true),
    Temperature1StandFM(REGISTER_INPUT, 221, 2, true, true),
    Temperature2StandFM(REGISTER_INPUT, 223, 2, true, true);


    private final RegisterType type;
    private final int ref;
    private final int count;
    private final boolean autoUpdate;
    private final boolean isStandFMRegister;
    private Object lastValue;
    private boolean syncWriteRead = true;

    ModbusMapStand(RegisterType type, int ref, int count, boolean autoUpdate, boolean isStandFMRegister) {
        this.type = type;
        this.ref = ref;
        this.count = count;
        this.autoUpdate = autoUpdate;
        this.isStandFMRegister = isStandFMRegister;
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

    public boolean isSyncWriteRead() {
        return syncWriteRead;
    }

    public boolean isStandFMRegister() {
        return isStandFMRegister;
    }

    public void setSyncWriteRead(boolean syncWriteRead) {
        this.syncWriteRead = syncWriteRead;
    }
}
