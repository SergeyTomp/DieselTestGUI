package fi.stardex.sisu.registers.writers;

import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMap;
import fi.stardex.sisu.util.Pair;
import net.wimpi.modbus.ModbusException;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ModbusWriter {
    private final BlockingDeque<Pair<ModbusMap, Object>> writeQueue = new LinkedBlockingDeque<>();

    private RegisterProvider registerProvider;

    public ModbusWriter(RegisterProvider registerProvider) {
        this.registerProvider = registerProvider;
    }

    public boolean add(ModbusMap reg, Object value) {
        return writeQueue.add(new Pair<>(reg, value));
    }

    public void execute(long timeout, TimeUnit timeUnit) {
        do {
            Pair<ModbusMap, Object> toWrite = null;
            try {
                toWrite = writeQueue.poll(timeout, timeUnit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (toWrite != null) {

                registerProvider.write(toWrite.getKey(), toWrite.getValue());

            }
        } while (!writeQueue.isEmpty());
    }
}
