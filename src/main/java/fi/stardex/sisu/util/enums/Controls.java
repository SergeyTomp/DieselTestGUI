package fi.stardex.sisu.util.enums;

import fi.stardex.sisu.registers.ModbusMap;

public interface Controls {

    void setRegister (ModbusMap register);
    ModbusMap getRegister();
}
