package fi.stardex.sisu.util;

import fi.stardex.sisu.registers.modbusmaps.ModbusMap;

public class FirmwareDataConverter {

    public static int convertDataToInt(ModbusMap register) {
        return Math.round(Float.parseFloat(register.getLastValue().toString()));
    }

}
