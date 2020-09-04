package fi.stardex.sisu.registers.ultima;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.registers.RegisterType.*;

public enum ModbusMapUltima implements ModbusMap {

    FirmwareVersion(REGISTER_HOLDING, 116, 1, false, false),
    PositionRheostatOne(REGISTER_INPUT, 4096, 2, false, false),

    // ChartTask 1
    Current_graph1(REGISTER_INPUT_CHART, 0, 2047, false, false),
    Current_graph1_frame_num(REGISTER_HOLDING, 7, 1, false, false),
    Current_graph1_update(DISCRETE_COIL, 1, 1, false, false),

    // ChartTask 2
    Current_graph2(REGISTER_INPUT_CHART, 2048, 4095, false, false),
    Current_graph2_frame_num(REGISTER_HOLDING, 27, 1, false, false),
    Current_graph2_update(DISCRETE_COIL, 21, 1, false, false),

    // ChartTask 3
    Current_graph3(REGISTER_INPUT_CHART, 4500, 6547, false, false),
    Current_graph3_frame_num(REGISTER_HOLDING, 1007, 1, false, false),
    Current_graph3_update(DISCRETE_COIL, 1001, 1, false, false),

    // ChartTask 4
    Current_graph4(REGISTER_INPUT_CHART, 6548, 8595, false, false),
    Current_graph4_frame_num(REGISTER_HOLDING, 1027, 1, false, false),
    Current_graph4_update(DISCRETE_COIL, 1021, 1, false, false),

    // DelayChartTask
    Delay_graph(REGISTER_INPUT_CHART, 4220, 4476, false, false),
    Delay_graph_channel_num(REGISTER_HOLDING, 115, 1, false, false),
    Delay_graph_update(DISCRETE_COIL, 96, 1, false, false),

    //RLC measurement
    RLC_measure_channel_num(REGISTER_HOLDING, 41, 1, false, false),
    RLC_measure_request(DISCRETE_COIL, 41, 1, false, false),
    Inductance_result(REGISTER_INPUT, 4200, 2, false, false),
    Resistance_result(REGISTER_INPUT, 4202, 2, false, false),
    Capacitance_result_piezo(REGISTER_INPUT, 4204, 2, false, false),
    Resistance_result_piezo(REGISTER_INPUT, 4206, 2, false, false),

    Injectors_Running_En(DISCRETE_COIL, 42, 1, true, false),
    Double_Coil_Mode_En(DISCRETE_COIL, 45, 1, false, false),

    // Слепок параметров импульсов на форсунки
    Injector_type(REGISTER_HOLDING, 43, 1, false, false),
    GImpulsesPeriod(REGISTER_HOLDING, 140, 1, false, false),
    Ftime1(REGISTER_HOLDING, 141, 1, false, false),
    FInjectorNumber1(REGISTER_HOLDING, 142, 1, false, false),
    Ftime2(REGISTER_HOLDING, 143, 1, false, false),
    FInjectorNumber2(REGISTER_HOLDING, 144, 1, false, false),
    Ftime3(REGISTER_HOLDING, 145, 1, false, false),
    FInjectorNumber3(REGISTER_HOLDING, 146, 1, false, false),
    Ftime4(REGISTER_HOLDING, 147, 1, false, false),
    FInjectorNumber4(REGISTER_HOLDING, 148, 1, false, false),


    //слепок регистров ВАП
    Boost_U(REGISTER_HOLDING, 120, 2, true, false),            // boostUSpinner
    Battery_U(REGISTER_HOLDING, 122, 2, true, false),          // batteryUSpinner
    Negative_U(REGISTER_HOLDING, 124, 2, true, false),         // negativeUSpinner
    Negative_U2(REGISTER_HOLDING, 126, 2, true, false),
    BoostIBoardOne(REGISTER_HOLDING, 1, 1, true, false),       // boostISpinner
    FirstIBoardOne(REGISTER_HOLDING, 2, 1, true, false),       // firstISpinner
    SecondIBoardOne(REGISTER_HOLDING, 3, 1, true, false),      // secondISpinner
    FirstWBoardOne(REGISTER_HOLDING, 4, 1, true, false),       // firstWSpinner
    WidthBoardOne(REGISTER_HOLDING, 5, 1, true, false),        // widthCurrentSignal
    BoostIBoardTwo(REGISTER_HOLDING, 21, 1, true, false),
    FirstIBoardTwo(REGISTER_HOLDING, 22, 1, true, false),
    SecondIBoardTwo(REGISTER_HOLDING, 23, 1, true, false),
    FirstWBoardTwo(REGISTER_HOLDING, 24, 1, true, false),
    WidthBoardTwo(REGISTER_HOLDING, 25, 1, true, false),
    BoostIBoardThree(REGISTER_HOLDING, 1001, 1, false, false),
    FirstIBoardThree(REGISTER_HOLDING, 1002, 1, false, false),
    SecondIBoardThree(REGISTER_HOLDING, 1003, 1, false, false),
    FirstWBoardThree(REGISTER_HOLDING, 1004, 1, false, false),
    WidthBoardThree(REGISTER_HOLDING, 1005, 1, true, false),
    BoostIBoardFour(REGISTER_HOLDING, 1021, 1, false, false),
    FirstIBoardFour(REGISTER_HOLDING, 1022, 1, false, false),
    SecondIBoardFour(REGISTER_HOLDING, 1023, 1, false, false),
    FirstWBoardFour(REGISTER_HOLDING, 1024, 1, false, false),
    WidthBoardFour(REGISTER_HOLDING, 1025, 1, true, false),
    StartOnBatteryUOne(DISCRETE_COIL, 2, 1, false, false),         // Boost_U enabled - true disabled - false
    StartOnBatteryUTwo(DISCRETE_COIL, 22, 1, false, false),        // Boost_U enabled - true disabled - false
    StartOnBatteryUThree(DISCRETE_COIL, 1002, 1, false, false),    // Boost_U enabled - true disabled - false
    StartOnBatteryUFour(DISCRETE_COIL, 1022, 1, false, false),     // Boost_U enabled - true disabled - false
    SecondCoilShiftEnable(DISCRETE_COIL, 23, 1, false, false),
    SecondCoilShiftTime(REGISTER_HOLDING, 29, 1, true, false),

    // регистры регуляторов
    // секция регулятора 1
    PressureReg1_ON(DISCRETE_COIL, 81, 1, false, false),

    PressureReg1_PressMode(DISCRETE_COIL, 91, 1, false, false),
    PressureReg1_PressTask(REGISTER_HOLDING, 101, 2, false, false),
    PressureReg1_PressFact(REGISTER_INPUT, 4096, 2, true, false),

    PressureReg1_DutyMode(DISCRETE_COIL, 82, 1, false, false),     // не используется, вместо него PressureReg1_I_Mode (1/0 - ток/скважность)
    PressureReg1_DutyTask(REGISTER_HOLDING, 80, 2, false, false),
    PressureReg1_DutyFact(REGISTER_INPUT, 4104, 2, true, false),

    PressureReg1_I_Mode(DISCRETE_COIL, 82, 1, false, false),       //реж. тока - 1, реж.скважности - 0
    PressureReg1_I_Task(REGISTER_HOLDING, 82, 2, false, false),
    PressureReg1_I_Fact(REGISTER_INPUT, 4106, 2, true, false),

    // секция регулятора 2
    PressureReg2_ON(DISCRETE_COIL, 83, 1, false, false),

    PressureReg2_DutyMode(DISCRETE_COIL, 84, 1, false, false),     // реж. скважности - 0
    PressureReg2_DutyTask(REGISTER_HOLDING, 84, 2, false, false),
    PressureReg2_DutyFact(REGISTER_INPUT, 4108, 2, true, false),

    PressureReg2_I_Mode(DISCRETE_COIL, 84, 1, false, false),       // реж. тока - 1
    PressureReg2_I_Task(REGISTER_HOLDING, 86, 2, false, false),
    PressureReg2_I_Fact(REGISTER_INPUT, 4110, 2, true, false),

    // секция регулятора 3
    PressureReg3_ON(DISCRETE_COIL, 85, 1, false, false),

    PressureReg3_DutyMode(DISCRETE_COIL, 86, 1, false, false),     // реж. скважности - 0
    PressureReg3_DutyTask(REGISTER_HOLDING, 88, 2, false, false),
    PressureReg3_DutyFact(REGISTER_INPUT, 4112, 2, true, false),

    PressureReg3_I_Mode(DISCRETE_COIL, 86, 1, false, false),       // реж. тока - 1
    PressureReg3_I_Task(REGISTER_HOLDING, 90, 2, false, false),
    PressureReg3_I_Fact(REGISTER_INPUT, 4114, 2, true, false),

    // тахометр
    Tachometer(REGISTER_HOLDING, 44, 1, true, false),

    // ремонт piezo Siemens и coil Bosch
    HoldingPulseMode(DISCRETE_COIL, 11, 1, false, false),
    PulseFrontSlump(DISCRETE_COIL, 40, 1, false, false),
    BoardNumber(REGISTER_HOLDING, 40, 1, false, false),
    CurrentLimit(REGISTER_HOLDING, 16, 1, false, false),
    TouchDetection(REGISTER_INPUT, 4098, 2, true, false),
    CoilHoldingWidth(REGISTER_HOLDING, 250, 1, false, false),

    // режим зеркалирования регулятора 1 на регулятор 2 и 3(только для насосов)
    Reg1_To_Reg2_Mirror(DISCRETE_COIL, 102, 1, false, false),
    Reg1_To_Reg3_Mirror(DISCRETE_COIL, 103, 1, false, false),

    // обработка флага короткого замыкания инжектора
    Inj_Process_Global_Error(DISCRETE_COIL, 7, 1, true, false),
    // переключение типа управления импульсами, 0 - CR, 1 - UIS
    UIS_to_CR_pulseControlSwitch(REGISTER_HOLDING, 157, 1, false, false),

    // регистры UIS
    Angle_1(REGISTER_HOLDING, 153, 1, false, false),               //Угловой слот 1: Угол запуска в градусах
    Angle_2(REGISTER_HOLDING, 155, 1, false, false),               //Угловой слот 2: Угол запуска в градусах
    AngleInjector_1(REGISTER_HOLDING, 154, 1, false, false),       //Угловой слот 1: Номер форсунки
    AngleInjector_2(REGISTER_HOLDING, 156, 1, false, false),       //Угловой слот 2: Номер форсунки
    BipModeInterval_1(REGISTER_HOLDING, 10, 1, true, false),       //Время фазы BIP канала 1 (в мкс)
    BipModeDuty_1(REGISTER_HOLDING, 11, 1, true, false),           //Скважность BIP канала 1
    SecondSignalInterval(REGISTER_HOLDING, 12, 1, false, false),   //Время окончания фазы удерджания для второго сигнала (в мкс)
    BipModeInterval_2(REGISTER_HOLDING, 30, 1, true, false),       //Время фазы BIP канала 2 (в мкс)
    BipModeDuty_2(REGISTER_HOLDING, 31, 1, true, false),           //Скважность BIP канала 2
    MaxPressureRegistered(REGISTER_INPUT, 4124, 2, true, false),   //Максимальное зарегестрированное значение  давления
    HpiModeOn(DISCRETE_COIL, 10, 1, false, false),                 //Форсунка HPI флаг
    BipModeOn_1(DISCRETE_COIL, 4, 1, false, false),                //Режим  BIP включен
    BipModeOn_2(DISCRETE_COIL, 24, 1, false, false),               //Режим  BIP включен
    DoubleSignalModeOn_1(DISCRETE_COIL, 5, 1, false, false),       //Режим ДаблСигнал включен
    DoubleSignalModeOn_2(DISCRETE_COIL, 25, 1, false, false),      //Режим ДаблСигнал включен
    FirstPulseFlag(DISCRETE_COIL, 44, 1, false, false),            //Флаг первого импульса в регулярной последовательности
    OpeningPressureReset(DISCRETE_COIL, 101, 1, true, false),      //Обнулить максимальное давление

    // Отображение версий прошивок
    Version_controllable_1(REGISTER_HOLDING, 116, 1, true, false),
    Version_controllable_2(REGISTER_HOLDING, 117, 1, true, false),
    Main_version_0(REGISTER_INPUT, 4210, 1, true, false),
    Main_version_1(REGISTER_INPUT, 4211, 1, true, false),
    MeasureCPU_version_0(REGISTER_INPUT, 8606, 1, true, false),
    MeasureCPU_version_1(REGISTER_INPUT, 8607, 1, true, false),
    PowerCPU_version_0(REGISTER_INPUT, 8608, 1, true, false),
    PowerCPU_version_1(REGISTER_INPUT, 8609, 1, true, false),
    InjectorCPU_1_version_0(REGISTER_HOLDING, 14, 1, true, false),
    InjectorCPU_1_version_1(REGISTER_HOLDING, 15, 1, true, false),
    InjectorCPU_2_version_0(REGISTER_HOLDING, 34, 1, true, false),
    InjectorCPU_2_version_1(REGISTER_HOLDING, 35, 1, true, false),
    InjectorCPU_3_version_0(REGISTER_HOLDING, 1014, 1, true, false),
    InjectorCPU_3_version_1(REGISTER_HOLDING, 1015, 1, true, false),
    InjectorCPU_4_version_0(REGISTER_HOLDING, 1034, 1, true, false),
    InjectorCPU_4_version_1(REGISTER_HOLDING, 1035, 1, true, false),
    RLC_MeasureCPU_version_0(REGISTER_INPUT, 4208, 1, true, false),
    RLC_MeasureCPU_version_1(REGISTER_INPUT, 4209, 1, true, false),

    // Активация продукта при лизинге
    CurrentTime_seconds(REGISTER_HOLDING, 240, 2, false, false),
    Activation_error(DISCRETE_COIL, 200, 1, true, false),
    Activation_errorCode(REGISTER_HOLDING, 242, 1, false, false),
    Activation_paymentKey(REGISTER_INPUT, 4480, 8, false, false),
    Activation_key(REGISTER_HOLDING, 252, 8, false, false);

    private final RegisterType type;
    private final int ref;
    private final int count;
    private final boolean autoUpdate;
    private final boolean ignoreFirstRead;
    private Object lastValue;
    private boolean firstRead = false;

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

    ModbusMapUltima(RegisterType type, int ref, int count, boolean autoUpdate, boolean ignoreFirstRead) {
        this.type = type;
        this.ref = ref;
        this.count = count;
        this.autoUpdate = autoUpdate;
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
    public boolean isIgnoreFirstRead() {
        return ignoreFirstRead;
    }

    @Override
    public boolean isFirstRead() {
        return firstRead;
    }

    @Override
    public void setFirstRead(boolean firstRead) {
        this.firstRead = firstRead;
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
