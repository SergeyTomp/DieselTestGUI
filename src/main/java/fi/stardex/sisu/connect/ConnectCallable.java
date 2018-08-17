package fi.stardex.sisu.connect;

import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import javafx.application.Platform;
import net.wimpi.modbus.net.TCPMasterConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.Callable;

public class ConnectCallable implements Callable<Boolean> {

    private final Logger logger = LoggerFactory.getLogger(ConnectCallable.class);

    private final Devices devices;

    private final String addressLine;

    private final int port;

    private final StatusBarWrapper statusBar;

    private final Device dieselDevice;

    private final SchedulerNotifier schedularNotifier;

    private final InetAddressWrapper inetAddressWrapper;

    private TCPMasterConnection connection;

    public ConnectCallable(Devices devices, String addressLine, int port, StatusBarWrapper statusBar,
                           Device dieselDevice, SchedulerNotifier schedularNotifier, InetAddressWrapper inetAddressWrapper) {

        this.devices = devices;
        this.addressLine = addressLine;
        this.port = port;
        this.statusBar = statusBar;
        this.dieselDevice = dieselDevice;
        this.schedularNotifier = schedularNotifier;
        this.inetAddressWrapper = inetAddressWrapper;

    }

    public TCPMasterConnection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    @Override
    public Boolean call() {

        try {

            if (!inetAddressWrapper.isReachable(addressLine, 2000)) {

                logger.info("Inet address {} is not reachable.", addressLine);
                return false;

            }

            connection = newConnection(inetAddressWrapper.byName(addressLine), port, 2000);
            connection.connect();
            return connection.isConnected();

        } catch (Exception e) {

            logger.info("Exception while connecting to {}", addressLine);
            return false;

        } finally {

            if (isConnected()) {

                if (devices.connect(dieselDevice)) {
                    schedularNotifier.signal();
                }
                logger.info("Connected to {}", addressLine);

            } else {

                devices.disconnect(dieselDevice);
                logger.info("Cannot connect to {}", addressLine);

            }

            Platform.runLater(statusBar::refresh);

        }
    }

    public TCPMasterConnection newConnection(InetAddress inetAddress, int port, int timeout) {

        TCPMasterConnection tcpMasterConnection = new TCPMasterConnection(inetAddress);
        tcpMasterConnection.setPort(port);
        tcpMasterConnection.setTimeout(timeout);
        return tcpMasterConnection;

    }

}
