package fi.stardex.sisu.registers.modbusmaps;

import net.wimpi.modbus.procimg.Register;

import java.util.LinkedList;
import java.util.List;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(RegisterType.REGISTER_HOLDING, 116, 1, false),
    PositionRheostatOne(RegisterType.REGISTER_INPUT, 4096, 2, false),

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
    FInjectorNumber4(RegisterType.REGISTER_HOLDING, 148, 1, false),


    //слепок регистров ВАП
    Boost_U(RegisterType.REGISTER_HOLDING, 120, 2, false),
    Battery_U(RegisterType.REGISTER_HOLDING, 122, 2, false),
    Negative_U1(RegisterType.REGISTER_HOLDING, 124, 2, false),
    Negative_U2(RegisterType.REGISTER_HOLDING, 126, 2, false),
    Fets_Resistance(RegisterType.REGISTER_HOLDING, 130, 2, false),
    BoostIBoardOne(RegisterType.REGISTER_HOLDING, 1, 1, false),
    FirstIBoardOne(RegisterType.REGISTER_HOLDING, 2, 1, false),
    SecondIBoardOne(RegisterType.REGISTER_HOLDING, 3, 1, false),
    FirstWBoardOne(RegisterType.REGISTER_HOLDING, 4, 1, false),
    WidthBoardOne(RegisterType.REGISTER_HOLDING, 5, 1, false),
    BoostIBoardTwo(RegisterType.REGISTER_HOLDING, 21, 1, false),
    FirstIBoardTwo(RegisterType.REGISTER_HOLDING, 22, 1, false),
    SecondIBoardTwo(RegisterType.REGISTER_HOLDING, 23, 1, false),
    FirstWBoardTwo(RegisterType.REGISTER_HOLDING, 24, 1, false),
    WidthBoardTwo(RegisterType.REGISTER_HOLDING, 25, 1, false),
    BoostIBoardThree(RegisterType.REGISTER_HOLDING, 1001, 1, false),
    FirstIBoardThree(RegisterType.REGISTER_HOLDING, 1002, 1, false),
    SecondIBoardThree(RegisterType.REGISTER_HOLDING, 1003, 1, false),
    FirstWBoardThree(RegisterType.REGISTER_HOLDING, 1004, 1, false),
    WidthBoardThree(RegisterType.REGISTER_HOLDING, 1005, 1, false),
    BoostIBoardFour(RegisterType.REGISTER_HOLDING, 1021, 1, false),
    FirstIBoardFour(RegisterType.REGISTER_HOLDING, 1022, 1, false),
    SecondIBoardFour(RegisterType.REGISTER_HOLDING, 1023, 1, false),
    FirstWBoardFour(RegisterType.REGISTER_HOLDING, 1024, 1, false),
    WidthBoardFour(RegisterType.REGISTER_HOLDING, 1025, 1, false),
    StartOnBatteryUOne(RegisterType.DISCRETE_COIL, 2, 1, false),
    StartOnBatteryUTwo(RegisterType.DISCRETE_COIL, 22, 1, false),
    StartOnBatteryUThree(RegisterType.DISCRETE_COIL, 1002, 1, false),
    StartOnBatteryUFour(RegisterType.DISCRETE_COIL, 1022, 1, false);



    private RegisterType type;
    private int ref;
    private int count;
    private boolean autoUpdate;
    private Object lastValue;

    public static final List<ModbusMapUltima> slotNumbersList = new LinkedList<>();

    static {
        slotNumbersList.add(FInjectorNumber1);
        slotNumbersList.add(FInjectorNumber2);
        slotNumbersList.add(FInjectorNumber3);
        slotNumbersList.add(FInjectorNumber4);
    }

    public static final List<ModbusMapUltima> slotPulsesList = new LinkedList<>();

    static {
        slotPulsesList.add(Ftime1);
        slotPulsesList.add(Ftime2);
        slotPulsesList.add(Ftime3);
        slotPulsesList.add(Ftime4);
    }

    public static List<ModbusMapUltima> getSlotNumbersList() {
        return slotNumbersList;
    }

    public static List<ModbusMapUltima> getSlotPulsesList() {
        return slotPulsesList;
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
