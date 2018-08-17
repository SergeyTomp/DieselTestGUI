package fi.stardex.sisu.devices;

import java.util.ArrayList;
import java.util.List;

public enum Device {

    ULTIMA(DeviceType.COMMON, "ULTIMA", true),
    MODBUS_FLOW(DeviceType.FLOW, "FM"),
    MODBUS_STAND(DeviceType.STAND, "BENCH V.2");

    public enum DeviceType {

        COMMON, FLOW, STAND

    }

    private final DeviceType deviceType;

    private final String label;

    private boolean major;

    Device(DeviceType deviceType, String label) {
        this(deviceType, label, false);
    }

    Device(DeviceType deviceType, String label, boolean major) {

        this.deviceType = deviceType;
        this.label = label;
        this.major = major;

    }

    public String getLabel() {
        return label;
    }

    public static List<Device> getPairedDevices(Device device) {

        List<Device> result = new ArrayList<>();

        for (Device paired : values()) {
            if (paired != device && paired.deviceType == device.deviceType)
                result.add(paired);
        }

        return checkMajor(result);
    }

    private static List<Device> checkMajor(List<Device> devices) {

        List<Device> result = new ArrayList<>();

        devices.stream().filter(device -> device.major).forEach(result::add);

        return result.isEmpty() ? devices : result;

    }
}
