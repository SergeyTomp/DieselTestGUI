package fi.stardex.sisu.registers.modbusmaps;

import java.util.LinkedList;
import java.util.List;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(RegisterType.REGISTER_HOLDING, 116, 1, false),
    PositionRheostatOne(RegisterType.REGISTER_INPUT, 4096, 2, false),
    FirstIBoardOne(RegisterType.REGISTER_HOLDING, 2, 1, false),
    FirstIBoardTwo(RegisterType.REGISTER_HOLDING, 22, 1, false),

    Current_graph1(RegisterType.REGISTER_INPUT_CHART, 0, 2047, false),
    Current_graph1_frame_num(RegisterType.REGISTER_HOLDING, 7, 1, false),
    Current_graph1_update(RegisterType.DISCRETE_COIL, 1, 1, false),

    Injectors_Running_En(RegisterType.DISCRETE_COIL, 42, 1, false),


    // Слепок параметров импульсов на форсунки
    Injector_type(RegisterType.REGISTER_HOLDING, 43, 1, false),
    GImpulsesPeriod(RegisterType.REGISTER_HOLDING, 140, 1, false),
    Ftime1(RegisterType.REGISTER_HOLDING, 141, 1, false),
    FInjectorNumber1(RegisterType.REGISTER_HOLDING, 142, 1, false),
    Ftime2(RegisterType.REGISTER_HOLDING, 143, 1, false),
    FInjectorNumber2(RegisterType.REGISTER_HOLDING, 144, 1, false),
    Ftime3(RegisterType.REGISTER_HOLDING, 145, 1, false),
    FInjectorNumber3(RegisterType.REGISTER_HOLDING, 146, 1, false),
    Ftime4(RegisterType.REGISTER_HOLDING, 147, 1, false),
    FInjectorNumber4(RegisterType.REGISTER_HOLDING, 148, 1, false);





    private RegisterType type;
    private int ref;
    private int count;
    private boolean autoUpdate;
    private Object lastValue;

    static final List<ModbusMapUltima> impulseToInjectorsList = new LinkedList<>();

    static {
        impulseToInjectorsList.add(Injector_type);
        impulseToInjectorsList.add(GImpulsesPeriod);
        impulseToInjectorsList.add(Ftime1);
        impulseToInjectorsList.add(FInjectorNumber1);
        impulseToInjectorsList.add(Ftime2);
        impulseToInjectorsList.add(FInjectorNumber2);
        impulseToInjectorsList.add(Ftime3);
        impulseToInjectorsList.add(FInjectorNumber3);
        impulseToInjectorsList.add(Ftime4);
        impulseToInjectorsList.add(FInjectorNumber4);
    }

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
