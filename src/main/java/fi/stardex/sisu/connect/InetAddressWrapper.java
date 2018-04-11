package fi.stardex.sisu.connect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressWrapper {

    public boolean isReachable(String ipAddressLine, int timeout) throws IOException {
        return byName(ipAddressLine).isReachable(timeout);
    }


    public InetAddress byName(String ipAddressLine) throws UnknownHostException {
        return InetAddress.getByName(ipAddressLine);
    }
}
