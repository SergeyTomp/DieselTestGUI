package fi.stardex.sisu.registers.stand;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

import static fi.stardex.sisu.registers.RegisterType.*;

public enum ModbusMapStand implements ModbusMap {


    FirmwareVersion(REGISTER_INPUT, 0, 1, false, false, false),

    /**___________________Stand registers section__________________*/
    PumpTurnOn(DISCRETE_COIL, 2, 1, true, false, false),
    FanTurnOn(DISCRETE_COIL, 3, 1, true, false, true),
    Pressure1(REGISTER_INPUT, 16, 2, true, false, false),
    TankOilLevel(REGISTER_INPUT, 20, 1, true, false, false),
    Temperature1(REGISTER_INPUT, 21, 2, true, false, false),
    Temperature2(REGISTER_INPUT, 23, 2, true, false, false),
    PumpAutoMode(DISCRETE_COIL, 11, 1, true, false, false),

    /**--------------------Stand drive registers--------------------
     - for CR main drive for all Stand versions
     - for UIS main drive for all Stand versions except Stand_Forte
     - for Pump main drive for all Stand versions except Stand_Forte
     - for UIS secondary drive for UIS-HPI in case of Stand_Forte */
    TargetRPM(REGISTER_HOLDING, 0, 1, true, false, true),
    RotationDirection(DISCRETE_COIL, 0, 1, true, false, true),
    Rotation(DISCRETE_COIL, 1, 1, true, false, true),
    CurrentRPM(REGISTER_INPUT, 13, 1, true, false, false),

    /**--------------------Stand_Forte drive registers-----------------
    - for UIS main drive in case of Stand_Forte
    - for Pump main drive in case of Stand_Forte */
    TargetRPM_Forte(REGISTER_HOLDING, 2, 1, true, false, true),
    CurrentRPM_Forte(REGISTER_INPUT, 14, 1, true, false, false),
    RotationDirection_Forte(DISCRETE_COIL, 12, 1, true, false, true),
    Rotation_Forte(DISCRETE_COIL, 13, 1, true, false, true),

    /**_____________________Stand_FM registers section_______________*/
    PumpTurnOnStandFM(DISCRETE_COIL, 102, 1, true, true, false),
    FanTurnOnStandFM(DISCRETE_COIL, 103, 1, true, true, true),
    PumpAutoModeStandFM(DISCRETE_COIL, 111, 1, true, true, false),
    Pressure1StandFM(REGISTER_INPUT, 216, 2, true, true, false),
    TankOilLevelStandFM(REGISTER_INPUT, 220, 1, true, true, false),
    Temperature1StandFM(REGISTER_INPUT, 221, 2, true, true, false),
    Temperature2StandFM(REGISTER_INPUT, 223, 2, true, true, false),

    /** --------------------Stand_FM drive registers--------------*/
    TargetRPMStandFM(REGISTER_HOLDING, 100, 1, true, true, true),
    RotationDirectionStandFM(DISCRETE_COIL, 100, 1, true, true, true),
    RotationStandFM(DISCRETE_COIL, 101, 1, true, true, true),
    CurrentRPMStandFM(REGISTER_INPUT, 213, 1, true, true, false);

    private final RegisterType type;
    private final int ref;
    private final int count;
    private final boolean autoUpdate;
    private final boolean isStandFMRegister;
    private final boolean ignoreFirstRead;
    private Object lastValue;
    private boolean firstRead = true;

    ModbusMapStand(RegisterType type, int ref, int count, boolean autoUpdate, boolean isStandFMRegister, boolean ignoreFirstRead) {
        this.type = type;
        this.ref = ref;
        this.count = count;
        this.autoUpdate = autoUpdate;
        this.isStandFMRegister = isStandFMRegister;
        this.ignoreFirstRead = ignoreFirstRead;
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
    public boolean isFirstRead() {
        return firstRead;
    }

    public boolean isStandFMRegister() {
        return isStandFMRegister;
    }

    @Override
    public void setFirstRead(boolean firstRead) {
        this.firstRead = firstRead;
    }

    @Override
    public boolean isIgnoreFirstRead() {
        return ignoreFirstRead;
    }
}
