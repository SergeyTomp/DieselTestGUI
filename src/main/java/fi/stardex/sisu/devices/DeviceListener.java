package fi.stardex.sisu.devices;

public interface DeviceListener {

    void deviceChanged(Device device, final boolean connect);
}

