package fi.stardex.sisu.registers.writers;

import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.util.Pair;
import fi.stardex.sisu.util.enums.ControlsService;
import net.wimpi.modbus.ModbusException;

import javax.annotation.PreDestroy;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class ModbusRegisterProcessor {

    private final BlockingDeque<Pair<ModbusMap, Object>> writeQueue = new LinkedBlockingDeque<>();

    protected RegisterProvider registerProvider;

    protected ModbusMap[] readArray;

    protected ControlsService controlsService;

    private Thread loopThread;

    protected boolean isPaused;

    protected void setLoopThread(Thread loopThread) {
        this.loopThread = loopThread;
    }

    public ModbusRegisterProcessor(RegisterProvider registerProvider, ModbusMap[] readList) {
        this.registerProvider = registerProvider;
        this.readArray = readList;
        initThread();
    }

    public ModbusRegisterProcessor(RegisterProvider registerProvider, ModbusMap[] readList, ControlsService controlsService) {
        this.registerProvider = registerProvider;
        this.readArray = readList;
        this.controlsService = controlsService;
        initThread();
    }

    public RegisterProvider getRegisterProvider() {
        return registerProvider;
    }

    public boolean add(ModbusMap reg, Object value) {
        if (reg.isIgnoreFirstRead()) {
            reg.setFirstRead(true);
        }
        return writeQueue.add(new Pair<>(reg, value));
    }

    protected abstract void initThread();

    protected void setRegisterSetChangeListener(){}

    @PreDestroy
    private void preDestroy() {
        loopThread.interrupt();
    }

    protected abstract class ProcessExecutor implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (!registerProvider.isConnected()) {
                    continue;
                }
                if (isPaused) {
                    continue;
                }
                try {
                    writeAll();
                    readAll();
                    updateAll();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void writeAll() throws InterruptedException {
            do {
                Pair<ModbusMap, Object> toWrite;
                toWrite = writeQueue.poll(500, TimeUnit.MILLISECONDS);
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

        protected void readAll() {
            for (ModbusMap register : readArray) {
                if (register.isAutoUpdate()) {

                    registerProvider.read(register);
                }
            }
        }

        protected abstract void updateAll();

        protected boolean isStand(ModbusMap register) {
            return false;
        }

    }
}
