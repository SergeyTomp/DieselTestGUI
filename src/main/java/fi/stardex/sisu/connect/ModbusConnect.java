package fi.stardex.sisu.connect;

import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.util.Pair;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import javafx.application.Platform;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModbusConnect {

    private Logger logger = LoggerFactory.getLogger(ModbusConnect.class);

    private static final int RETRY = 1;

    private String addressLine;
    private int port;

    private ConnectProcessor connectProcessor;

    private ConnectCallable connectCallable;

    private Future<Boolean> connectionStatus;
    private ModbusTransaction modbusTransaction;
    private final Lock lock;

    private final Condition condition;
    private final Devices devices;
    private final StatusBarWrapper statusBar;
    private final Device dieselDevice;
    private Pair<String, String> connectInfo;
    private final InetAddressWrapper inetAddressWrapper;
    private final SchedulerNotifier schedularNotifier;


    public ModbusConnect(Pair<String, String> connectInfo, ConnectProcessor connectProcessor,
                         Devices devices, StatusBarWrapper statusBar, Device dieselDevice, InetAddressWrapper inetAddressWrapper) {
        this.connectInfo = connectInfo;
        this.connectProcessor = connectProcessor;
        this.devices = devices;
        this.statusBar = statusBar;
        this.dieselDevice = dieselDevice;
        this.inetAddressWrapper = inetAddressWrapper;

        lock = new ReentrantLock();
        condition = lock.newCondition();
        schedularNotifier = new SchedulerNotifier(lock, condition);

        addressLine = connectInfo.getKey();
        port = Integer.valueOf(connectInfo.getValue());
    }

    @Scheduled(cron = "*/3 * * * * *")
    public void connect() {
        if (isConnected() && isSame(addressLine, port)) {
            if (!devices.isAtLeastOneConnected(Device.getPairedDevices(dieselDevice))) {
                schedularNotifier.signal();
                devices.connect(dieselDevice);
            }
            Platform.runLater(statusBar::refresh);
            return;
        }
        connect(connectInfo.getKey(), Integer.valueOf(connectInfo.getValue()));
    }

    public void connect(String addressLine, int port) {
        this.addressLine = addressLine;
        this.port = port;
        disconnect2();
        connectCallable = new ConnectCallable(devices, addressLine, port, statusBar, dieselDevice, schedularNotifier, inetAddressWrapper);
        if (connectionStatus == null || connectionStatus.isDone()) {
            logger.info("Make new Future, old is null or done!");
            connectionStatus = connectProcessor.submit(connectCallable);
        } else {
            logger.info("WARNING!WARNING!WARNING! Old future not null and not done, waiting!");
        }
    }

    public void disconnect2() {
        lock.lock();
        try {
            if (connectCallable != null && connectCallable.getConnection() != null) {
                connectCallable.getConnection().close();
            }
            modbusTransaction = null;
            devices.disconnect(dieselDevice);

        } finally {
            lock.unlock();
        }
    }

    public boolean isConnected() {
        return connectionStatus != null && connectCallable.isConnected();
    }

    public void disconnect() {
        disconnect2();
        Platform.runLater(statusBar::refresh);
    }

    public ModbusTransaction getTransaction(ModbusRequest request) throws ModbusException {
        if (isConnected()) {
            if (modbusTransaction == null) {
                modbusTransaction = new ModbusTCPTransaction(connectCallable.getConnection());
                modbusTransaction.setRetries(RETRY);
            }
            modbusTransaction.setRequest(request);
            return modbusTransaction;
        }
        throw new ModbusException(String.format("Trying to getTransaction while - no connection to %s:%s", addressLine, port));
    }

    private boolean isSame(String addressLine, int port) {
        String newAddressLine = connectInfo.getKey();
        int newPort = Integer.valueOf(connectInfo.getValue());

        return newAddressLine.equals(addressLine) && newPort == port;
    }

    @PreDestroy
    public void preDestroy() {
        disconnect();
    }
}
