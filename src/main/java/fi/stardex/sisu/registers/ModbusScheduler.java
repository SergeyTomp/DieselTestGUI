package fi.stardex.sisu.registers;

import fi.stardex.sisu.registers.writers.ModbusWriter;

public class ModbusScheduler {

    private ModbusWriter modbusWriter;

    public ModbusScheduler(ModbusWriter modbusWriter) {
        this.modbusWriter = modbusWriter;
    }


}
