package fi.stardex.sisu.version;

public enum FlowFirmwareVersion {

    FLOW_MASTER, FLOW_STREAM;

    private static FlowFirmwareVersion flowFirmwareVersion;

    public static FlowFirmwareVersion getFlowFirmwareVersion() {
        return flowFirmwareVersion;
    }

    public static void setFlowFirmwareVersion(FlowFirmwareVersion flowFirmwareVersion) {
        FlowFirmwareVersion.flowFirmwareVersion = flowFirmwareVersion;
    }
}
