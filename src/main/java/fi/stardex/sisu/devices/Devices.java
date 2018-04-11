package fi.stardex.sisu.devices;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class Devices {
    private final EnumSet<Device> connectedDevices = EnumSet.noneOf(Device.class);
    private final EnumSet<Device> pluggedInDevices = EnumSet.noneOf(Device.class);

    private final List<DeviceListener> listeners = new ArrayList<>();

    public boolean connect(Device device) {
        if (isConnected(device)) {
            return false;
        }
        pluggedInDevices.add(device);
        if (!isAtLeastOneConnected(Device.getPairedDevices(device))) {
            boolean result = connectedDevices.add(device);
            if (result){
                notifyListeners(device, true);
            }
            return result;
        }
        return false;
    }

    public boolean isAtLeastOneConnected(List<Device> devices){
        for (Device device: devices) {
            if (isConnected(device)) {
                return true;
            }
        }
        return false;
    }

    public boolean disconnect(Device device) {
        boolean result = connectedDevices.remove(device);
        pluggedInDevices.remove(device);
        for (Device paired: Device.getPairedDevices(device)) {
            if (isPluggedIn(paired)) {
                connect(paired);
            }
        }
        if (result) {
            notifyListeners(device, false);
        }
        return result;
    }

    private void notifyListeners(Device device, final boolean connect){
        listeners.forEach(deviceListener -> deviceListener.deviceChanged(device, connect));
    }

    public boolean isConnected(Device device) {
        return connectedDevices.contains(device);
    }

    public boolean isPluggedIn(Device device) {
        return pluggedInDevices.contains(device);
    }

    public boolean isNoDeviceConnected() {
        return connectedDevices.isEmpty();
    }

    public Iterator<Device> connectedDevices() {
        return connectedDevices.iterator();
    }

    public void addListener(DeviceListener deviceListener) {
        listeners.add(deviceListener);
    }
}
