package fi.stardex.sisu.firmware;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;

public class FirmwareDataObtainer {

    public static int getFirmwareWidth() {
        return Math.round(Float.parseFloat(ModbusMapUltima.WidthBoardOne.getLastValue().toString()));
    }

}
