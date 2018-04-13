package fi.stardex.sisu.registers.writers;

import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMap;
import fi.stardex.sisu.util.Pair;
import net.wimpi.modbus.ModbusException;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ModbusRegisterProcessor {
    private final BlockingDeque<Pair<ModbusMap, Object>> writeQueue = new LinkedBlockingDeque<>();

    private RegisterProvider registerProvider;
    private ModbusMap[] readArray;

    private Thread loopThread;

    public ModbusRegisterProcessor(RegisterProvider registerProvider, ModbusMap[] readList, String threadName) {
        this.registerProvider = registerProvider;
        this.readArray = readList;

        loopThread = new Thread(new ProcessExecutor());
        loopThread.setName(threadName);
        loopThread.start();
    }

    public boolean add(ModbusMap reg, Object value) {
        return writeQueue.add(new Pair<>(reg, value));
    }

    @PreDestroy
    private void destroy() {
        loopThread.interrupt();
    }

    private class ProcessExecutor implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if(!registerProvider.isConnected())
                    continue;

                writeAll(500, TimeUnit.MILLISECONDS);
                readAll();
            }
        }

        private void writeAll(long timeout, TimeUnit timeUnit) {
            do {
                Pair<ModbusMap, Object> toWrite = null;
                try {
                    toWrite = writeQueue.poll(timeout, timeUnit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (toWrite != null) {
                    if(!registerProvider.isConnected()) {
                        writeQueue.addFirst(toWrite);
                        return;
                    }
                    try {
                        registerProvider.write(toWrite.getKey(), toWrite.getValue());
                    } catch (ModbusException e) {
                        writeQueue.addFirst(toWrite);
                    }
                }
            } while (!writeQueue.isEmpty());
        }

        private void readAll() {
            for (ModbusMap register : readArray) {
                if(register.isAutoUpdate()) {
                    registerProvider.read(register);
                    System.err.println(register.getLastValue());
                }
            }
        }
    }
}
