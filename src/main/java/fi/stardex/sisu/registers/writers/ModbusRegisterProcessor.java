package fi.stardex.sisu.registers.writers;

import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMap;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.Pair;
import javafx.application.Platform;
import net.wimpi.modbus.ModbusException;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ModbusRegisterProcessor {
    private final BlockingDeque<Pair<ModbusMap, Object>> writeQueue = new LinkedBlockingDeque<>();

    private RegisterProvider registerProvider;
    private ModbusMap[] readArray;

    private List<Updater> updaters;

    private Thread loopThread;

    public ModbusRegisterProcessor(RegisterProvider registerProvider, ModbusMap[] readList, String threadName, List<Updater> updaters) {
        this.registerProvider = registerProvider;
        this.readArray = readList;
        this.updaters = updaters;

        loopThread = new Thread(new ProcessExecutor());
        loopThread.setName(threadName);
        loopThread.start();
    }

    public boolean add(ModbusMap reg, Object value) {
        return writeQueue.add(new Pair<>(reg, value));
    }

    @PreDestroy
    private void preDestroy() {
        loopThread.interrupt();
    }

    private class ProcessExecutor implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (!registerProvider.isConnected())
                    continue;
                try {
                    writeAll(2000, TimeUnit.MILLISECONDS);
                    readAll();
                    updateAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void writeAll(long timeout, TimeUnit timeUnit) throws InterruptedException {
            do {
                Pair<ModbusMap, Object> toWrite;
                toWrite = writeQueue.poll(timeout, timeUnit);
                if (toWrite != null) {
                    if (!registerProvider.isConnected()) {
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
            System.err.println(System.currentTimeMillis());
            for (ModbusMap register : readArray) {
                if (register.isAutoUpdate()) {
                    registerProvider.read(register);

                    System.err.println(register + " " + register.getLastValue());
                }
            }
        }

        private void updateAll() {
            for (Updater updater : updaters) {
                Platform.runLater(updater);
            }
        }
    }
}
