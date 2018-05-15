package fi.stardex.sisu.registers.modbusmaps;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(RegisterType.REGISTER_HOLDING, 116, 1, false),
    PositionRheostatOne(RegisterType.REGISTER_INPUT, 4096, 2, false),
    FirstIBoardOne(RegisterType.REGISTER_HOLDING, 2, 1, false),
    FirstIBoardTwo(RegisterType.REGISTER_HOLDING, 22, 1, false),

    Current_graph1(RegisterType.REGISTER_INPUT_CHART, 0, 2047, false),
    Current_graph1_frame_num(RegisterType.REGISTER_HOLDING, 7, 1, false),
    Current_graph1_update(RegisterType.DISCRETE_COIL, 1, 1, false),

    Injectors_Running_En(RegisterType.DISCRETE_COIL, 42, 1, false),
    GImpulsesPeriod(RegisterType.REGISTER_HOLDING, 140, 1, false),
    Ftime(RegisterType.REGISTER_HOLDING, 141, 1, false),
    FInjectorNumber(RegisterType.REGISTER_HOLDING, 142, 1, false);

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
