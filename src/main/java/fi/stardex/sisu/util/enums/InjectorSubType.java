package fi.stardex.sisu.util.enums;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;

import java.util.HashMap;
import java.util.Map;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

public enum InjectorSubType {

    SINGLE_COIL (
            false,  // hpiEnable
            false,  // doubleCoilEnable
            false,  // doubleSignalSlotOneEnable
            false,  // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            0xFF,   // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            0xFF),  // angleSlotNumberTwo
    DOUBLE_COIL (
            false,  // hpiEnable
            true,   // doubleCoilEnable
            false,  // doubleSignalSlotOneEnable
            false,  // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            0xFF,   // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            0xFF),  // angleSlotNumberTwo
    DOUBLE_SIGNAL (
            false,  // hpiEnable
            false,  // doubleCoilEnable
            true,   // doubleSignalSlotOneEnable
            true,   // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            1,      // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            1),     // angleSlotNumberTwo
    F2E (
            false,  // hpiEnable
            false,  // doubleCoilEnable
            false,  // doubleSignalSlotOneEnable
            false,  // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            0xFF,   // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            0xFF),  // angleSlotNumberTwo
    MECHANIC (
            false,  // hpiEnable
            false,  // doubleCoilEnable
            false,  // doubleSignalSlotOneEnable
            false,  // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            0xFF,   // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            0xFF),  // angleSlotNumberTwo
    HPI (
            true,   // hpiEnable
            false,  // doubleCoilEnable
            false,  // doubleSignalSlotOneEnable
            false,  // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            2,      // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            2),     // angleSlotNumberTwo
    SINGLE_PIEZO (
            false,  // hpiEnable
            false,  // doubleCoilEnable
            false,  // doubleSignalSlotOneEnable
            false,  // doubleSignalSlotTwoEnable
            1,      // injectorSlotNumberOne
            0xFF,   // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            0xFF),  // angleSlotNumberTwo;
    F2E_COMMON (
            false,  // hpiEnable
            false,  // doubleCoilEnable
            false,  // doubleSignalSlotOneEn
            false,  // doubleSignalSlotTwoEn
            1,      // injectorSlotNumberOne
            2,      // injectorSlotNumberTwo
            1,      // angleSlotNumberOne
            2);     // angleSlotNumberTwo;


    private Map<ModbusMapUltima, Boolean> modeSwitchRegisters = new HashMap<>();
    private Map<ModbusMapUltima, Integer> slotConfigureRegisters = new HashMap<>();

    InjectorSubType(boolean hpiEnable,
                    boolean doubleCoilEnable,
                    boolean doubleSignalSlotOneEnable,
                    boolean doubleSignalSlotTwoEnable,
                    int injectorSlotNumberOne,
                    int injectorSlotNumberTwo,
                    int angleSlotNumberOne,
                    int angleSlotNumberTwo) {
        modeSwitchRegisters.put(HpiModeOn, hpiEnable);
        modeSwitchRegisters.put(SecondCoilShiftEnable, doubleCoilEnable);
        modeSwitchRegisters.put(DoubleSignalModeOn_1, doubleSignalSlotOneEnable);
        modeSwitchRegisters.put(DoubleSignalModeOn_2, doubleSignalSlotTwoEnable);
        slotConfigureRegisters.put(FInjectorNumber1, injectorSlotNumberOne);
        slotConfigureRegisters.put(FInjectorNumber2, injectorSlotNumberTwo);
        slotConfigureRegisters.put(AngleInjector_1,  angleSlotNumberOne);
        slotConfigureRegisters.put(AngleInjector_2,  angleSlotNumberTwo);
    }

    public Map<ModbusMapUltima, Boolean> getModeSwitchRegisters() {
        return modeSwitchRegisters;
    }

    public Map<ModbusMapUltima, Integer> getSlotConfigureRegisters() {
        return slotConfigureRegisters;
    }
}
