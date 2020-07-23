package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapFirmware;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.wimpi.modbus.ModbusException;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapFirmware.Command;

public class FirmwareUpdater implements ChangeListener<Boolean> {

    @Value("${hardware.updates.periphery}")
    private String url;
    private Thread updateThread;
    private List<Integer> dataRegisterNumbers;
    private List<List<Byte>> firmwareBytes;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private final Logger logger = LoggerFactory.getLogger(FirmwareUpdater.class);

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {

        if (newValue) {
            if (updateThread == null || !updateThread.isAlive()) {
                updateThread = new Thread(new Updater(), "Updater thread");
                updateThread.start();
            }
        } else {
            logger.error("Disconnected");
            if (updateThread.isAlive())
                updateThread.interrupt();
        }
    }

    public FirmwareUpdater(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
        this.ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();
        dataRegisterNumbers = new ArrayList<>(ModbusMapFirmware.firmwareUpdateDataRegisters().keySet());
        dataRegisterNumbers.sort(Comparator.naturalOrder());
    }

    @PreDestroy
    private void destroy() {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
    }

    private class Updater implements Runnable{

        @Override
        public void run() {
            ExecutorService es = Executors.newSingleThreadExecutor();
            Future<List<List<Byte>>> readFirmwareBytes = es.submit(this::readBinaryFile);
            Future<Void> rebootToLoader = es.submit(() -> executeQuery(0x700, "Reboot to loader command", "87"));
            Future<Void> startNewFirmware = es.submit(this::startNewFirmware);
            Future<Void> writeFirmware = es.submit(this::writeNewFirmware);
            es.shutdown();
            try {
                firmwareBytes = readFirmwareBytes.get();
                rebootToLoader.get(5, TimeUnit.SECONDS);
                startNewFirmware.get(5, TimeUnit.SECONDS);
                writeFirmware.get(15, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {

                logger.error("{} was interrupted", Thread.currentThread().getName());
                logger.error("Exception while updating firmware: {}", e);
                Thread.currentThread().interrupt();
            } finally {
                readFirmwareBytes.cancel(false);
                rebootToLoader.cancel(true);
                startNewFirmware.cancel(true);
                writeFirmware.cancel(true);
            }
        }

        private List<List<Byte>> readBinaryFile() throws IOException {

            List<Byte> bytes = new LinkedList<>();

            try (InputStream inputStream = FirmwareUpdater.class.getResourceAsStream(System.getProperty("user.dir") + "/" + url)) {

                int b;
                while ((b = inputStream.read()) != -1) {
                    bytes.add((byte) b);
                }
            }
            return ListUtils.partition(bytes, 2048);
        }

        private Void executeQuery(int request, String description, String response) {

            ultimaModbusWriter.add(Command, request);
            logger.error("{} write: {}", description, Integer.toHexString(request));

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Thread.sleep(100);
                    byte received = (byte)(((Integer)ultimaRegisterProvider.read(Command)) >> 8);
                    String result = String.format("%02x", received);
                    logger.error("{} read: {}", description, result);
                    if (result.equals(response))
                        return null;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }

        private Void writeNewFirmware() {

            for (int page = 0; page < firmwareBytes.size(); page++) {
                logger.error("Page: {}", page);

                // номер страницы
                ultimaModbusWriter.add(ModbusMapFirmware.firmwareUpdateDataRegisters().get(1), page);

                List<Byte> pageBytes = firmwareBytes.get(page);
                int pageSize = pageBytes.size();
                logger.error("Size of the page: {}", pageSize);

                // размер страницы
                ultimaModbusWriter.add(ModbusMapFirmware.firmwareUpdateDataRegisters().get(2), pageSize);

                // crc страница
                ultimaModbusWriter.add(ModbusMapFirmware.firmwareUpdateDataRegisters().get(3), 4660);

                executeQuery(0x300, "Start new page", "83");

                if (!Thread.currentThread().isInterrupted()) {
                    List<List<Byte>> chunks = ListUtils.partition(pageBytes, 64);
                    for (List<Byte> chunk : chunks) {
                        Iterator<Byte> chunkIterator = chunk.iterator();
                        for (Integer key : dataRegisterNumbers) {
                            byte[] twoBytes = new byte[2];
                            for (int count = 0; count < 2; count++) {
                                if (chunkIterator.hasNext())
                                    twoBytes[count] = chunkIterator.next();
                            }
                            // на каждый регистр пишу данные
                            ultimaModbusWriter.add(ModbusMapFirmware.firmwareUpdateDataRegisters().get(key), twoBytesToInt(twoBytes));

                            if (!chunkIterator.hasNext())
                                break;
                        }
                        // данные страницы
                        executeQuery(0x400, "Page data", "84");
                        if (Thread.currentThread().isInterrupted()) break;
                    }
                } else break;

                // подтверждение отправки страницы
                executeQuery(0x500, "Page write", "85");
                if (Thread.currentThread().isInterrupted()) break;
            }
            ultimaModbusWriter.add(Command, 0x600);
            return null;
        }

        private Void startNewFirmware() throws ModbusException {

            int numberOfBytes = (int) firmwareBytes.stream().mapToLong(List::size).sum();
            logger.error("Number of bytes in the file: {}", numberOfBytes);

            ultimaModbusWriter.add(ModbusMapFirmware.firmwareUpdateDataRegisters().get(1), numberOfBytes);
            // startNewFirmware pages
            return executeQuery(0x200, "Start new firmware", "82");
        }

        private int twoBytesToInt(byte[] b) {
            return ((b[0] & 0xFF) | (b[1] << 8));
        }
    }
}
