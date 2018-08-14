package fi.stardex.sisu.registers.ultima;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.registers.RegisterType.*;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(REGISTER_HOLDING, 116, 1, false),
    PositionRheostatOne(REGISTER_INPUT, 4096, 2, false),

    // ChartTask 1
    Current_graph1(REGISTER_INPUT_CHART, 0, 2047, false),
    Current_graph1_frame_num(REGISTER_HOLDING, 7, 1, false),
    Current_graph1_update(DISCRETE_COIL, 1, 1, false),

    // ChartTask 2
    Current_graph2(REGISTER_INPUT_CHART, 2048, 4095, false),
    Current_graph2_frame_num(REGISTER_HOLDING, 27, 1, false),
    Current_graph2_update(DISCRETE_COIL, 21, 1, false),

    // ChartTask 3
    Current_graph3(REGISTER_INPUT_CHART, 4500, 6547, false),
    Current_graph3_frame_num(REGISTER_HOLDING, 1007, 1, false),
    Current_graph3_update(DISCRETE_COIL, 1001, 1, false),

    // ChartTask 4
    Current_graph4(REGISTER_INPUT_CHART, 6548, 8595, false),
    Current_graph4_frame_num(REGISTER_HOLDING, 1027, 1, false),
    Current_graph4_update(DISCRETE_COIL, 1021, 1, false),

    // DelayChartTask
    Delay_graph(REGISTER_INPUT_CHART, 4220, 4476, false),
    Delay_graph_channel_num(REGISTER_HOLDING, 115, 1, false),
    Delay_graph_update(DISCRETE_COIL, 96, 1, false),

    //RLC measurement
    RLC_measure_channel_num(RegisterType.REGISTER_HOLDING, 41, 1, false),
    RLC_measure_request(RegisterType.DISCRETE_COIL, 41, 1, false),
    Inductance_result(RegisterType.REGISTER_INPUT, 4200, 2, false),
    Resistance_result(RegisterType.REGISTER_INPUT, 4202, 2, false),
    Capacitance_result_piezo(RegisterType.REGISTER_INPUT, 4204, 2, false),
    Resistance_result_piezo(RegisterType.REGISTER_INPUT, 4206, 2, false),

    Injectors_Running_En(DISCRETE_COIL, 42, 1, false),


    // Слепок параметров импульсов на форсунки
    Injector_type(REGISTER_HOLDING, 43, 1, false),
    GImpulsesPeriod(REGISTER_HOLDING, 140, 1, false),
    Ftime1(REGISTER_HOLDING, 141, 1, false),
    FInjectorNumber1(REGISTER_HOLDING, 142, 1, false),
    Ftime2(REGISTER_HOLDING, 143, 1, false),
    FInjectorNumber2(REGISTER_HOLDING, 144, 1, false),
    Ftime3(REGISTER_HOLDING, 145, 1, false),
    FInjectorNumber3(REGISTER_HOLDING, 146, 1, false),
    Ftime4(REGISTER_HOLDING, 147, 1, false),
    FInjectorNumber4(REGISTER_HOLDING, 148, 1, false),


    //слепок регистров ВАП
    Boost_U(REGISTER_HOLDING, 120, 2, true), // boostUSpinner
    Battery_U(REGISTER_HOLDING, 122, 2, true), // batteryUSpinner
    Negative_U(REGISTER_HOLDING, 124, 2, true), // negativeUSpinner
    BoostIBoardOne(REGISTER_HOLDING, 1, 1, true), // boostISpinner
    FirstIBoardOne(REGISTER_HOLDING, 2, 1, true), // firstISpinner
    SecondIBoardOne(REGISTER_HOLDING, 3, 1, true), // secondISpinner
    FirstWBoardOne(REGISTER_HOLDING, 4, 1, true), // firstWSpinner
    WidthBoardOne(REGISTER_HOLDING, 5, 1, true), // widthCurrentSignal
    BoostIBoardTwo(REGISTER_HOLDING, 21, 1, false),
    FirstIBoardTwo(REGISTER_HOLDING, 22, 1, false),
    SecondIBoardTwo(REGISTER_HOLDING, 23, 1, false),
    FirstWBoardTwo(REGISTER_HOLDING, 24, 1, false),
    WidthBoardTwo(REGISTER_HOLDING, 25, 1, true),
    BoostIBoardThree(REGISTER_HOLDING, 1001, 1, false),
    FirstIBoardThree(REGISTER_HOLDING, 1002, 1, false),
    SecondIBoardThree(REGISTER_HOLDING, 1003, 1, false),
    FirstWBoardThree(REGISTER_HOLDING, 1004, 1, false),
    WidthBoardThree(REGISTER_HOLDING, 1005, 1, true),
    BoostIBoardFour(REGISTER_HOLDING, 1021, 1, false),
    FirstIBoardFour(REGISTER_HOLDING, 1022, 1, false),
    SecondIBoardFour(REGISTER_HOLDING, 1023, 1, false),
    FirstWBoardFour(REGISTER_HOLDING, 1024, 1, false),
    WidthBoardFour(REGISTER_HOLDING, 1025, 1, true),
    StartOnBatteryUOne(DISCRETE_COIL, 2, 1, false), // Boost_U enabled - true disabled - false
    StartOnBatteryUTwo(DISCRETE_COIL, 22, 1, false), // Boost_U enabled - true disabled - false
    StartOnBatteryUThree(DISCRETE_COIL, 1002, 1, false), // Boost_U enabled - true disabled - false
    StartOnBatteryUFour(DISCRETE_COIL, 1022, 1, false); // Boost_U enabled - true disabled - false



    private final RegisterType type;
    private final int ref;
    private final int count;
    private final boolean autoUpdate;
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
    public String toString() {
        return "ModbusMapUltima{" +
                "type=" + type +
                ", ref=" + ref +
                ", count=" + count +
                ", autoUpdate=" + autoUpdate +
                ", lastValue=" + lastValue +
                '}';
    }
}
