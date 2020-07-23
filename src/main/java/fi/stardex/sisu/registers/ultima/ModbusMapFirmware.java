package fi.stardex.sisu.registers.ultima;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterType;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.stardex.sisu.registers.RegisterType.DISCRETE_COIL;
import static fi.stardex.sisu.registers.RegisterType.REGISTER_HOLDING;

public enum ModbusMapFirmware implements ModbusMap {

    Bootloader(DISCRETE_COIL, 9, 1, false, false),
    MAC_AddressByte1(REGISTER_HOLDING, 166, 1, false, false),
    MAC_AddressByte2(REGISTER_HOLDING, 167, 1, false, false),
    MAC_AddressByte3(REGISTER_HOLDING, 168, 1, false, false),
    Command(REGISTER_HOLDING, 200, 1, false, false),
    Data1(REGISTER_HOLDING, 201, 1, false, false),
    Data2(REGISTER_HOLDING, 202, 1, false, false),
    Data3(REGISTER_HOLDING, 203, 1, false, false),
    Data4(REGISTER_HOLDING, 204, 1, false, false),
    Data5(REGISTER_HOLDING, 205, 1, false, false),
    Data6(REGISTER_HOLDING, 206, 1, false, false),
    Data7(REGISTER_HOLDING, 207, 1, false, false),
    Data8(REGISTER_HOLDING, 208, 1, false, false),
    Data9(REGISTER_HOLDING, 209, 1, false, false),
    Data10(REGISTER_HOLDING, 210, 1, false, false),
    Data11(REGISTER_HOLDING, 211, 1, false, false),
    Data12(REGISTER_HOLDING, 212, 1, false, false),
    Data13(REGISTER_HOLDING, 213, 1, false, false),
    Data14(REGISTER_HOLDING, 214, 1, false, false),
    Data15(REGISTER_HOLDING, 215, 1, false, false),
    Data16(REGISTER_HOLDING, 216, 1, false, false),
    Data17(REGISTER_HOLDING, 217, 1, false, false),
    Data18(REGISTER_HOLDING, 218, 1, false, false),
    Data19(REGISTER_HOLDING, 219, 1, false, false),
    Data20(REGISTER_HOLDING, 220, 1, false, false),
    Data21(REGISTER_HOLDING, 221, 1, false, false),
    Data22(REGISTER_HOLDING, 222, 1, false, false),
    Data23(REGISTER_HOLDING, 223, 1, false, false),
    Data24(REGISTER_HOLDING, 224, 1, false, false),
    Data25(REGISTER_HOLDING, 225, 1, false, false),
    Data26(REGISTER_HOLDING, 226, 1, false, false),
    Data27(REGISTER_HOLDING, 227, 1, false, false),
    Data28(REGISTER_HOLDING, 228, 1, false, false),
    Data29(REGISTER_HOLDING, 229, 1, false, false),
    Data30(REGISTER_HOLDING, 230, 1, false, false),
    Data31(REGISTER_HOLDING, 231, 1, false, false),
    Data32(REGISTER_HOLDING, 232, 1, false, false);

    private final RegisterType type;
    private final int ref;
    private final int count;
    private final boolean autoUpdate;
    private final boolean ignoreFirstRead;
    private Object lastValue;
    private boolean firstRead = false;

    ModbusMapFirmware(RegisterType type, int ref, int count, boolean autoUpdate, boolean ignoreFirstRead) {
        this.type = type;
        this.ref = ref;
        this.count = count;
        this.autoUpdate = autoUpdate;
        this.ignoreFirstRead = ignoreFirstRead;
    }

    private static final Map<Integer, ModbusMapFirmware> firmwareUpdateDataRegisters;

    static {
        firmwareUpdateDataRegisters = Stream.of(ModbusMapFirmware.values())
                .filter(r -> r.name().contains("Data"))
                .collect(Collectors.toMap(r -> r.getRef() % 200, r -> r));
    }

    public static Map<Integer, ModbusMapFirmware> firmwareUpdateDataRegisters() {
        return firmwareUpdateDataRegisters;
    }

    @Override public RegisterType getType() {
        return type;
    }
    @Override public int getRef() {
        return ref;
    }
    @Override public int getCount() {
        return count;
    }
    @Override public Object getLastValue() {
        return lastValue;
    }
    @Override public void setLastValue(Object lastValue) {

    }
    @Override public boolean isAutoUpdate() {
        return autoUpdate;
    }
    @Override public boolean isIgnoreFirstRead() {
        return ignoreFirstRead;
    }
    @Override public void setFirstRead(boolean firstRead) {
        this.firstRead = firstRead;
    }
    @Override public boolean isFirstRead() {
        return firstRead;
    }
}
