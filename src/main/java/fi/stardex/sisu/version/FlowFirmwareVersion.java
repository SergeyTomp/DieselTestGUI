package fi.stardex.sisu.version;

public enum FlowFirmwareVersion {

    FLOW_MASTER, FLOW_STREAM, STAND_FM;

    private static FlowFirmwareVersion flowFirmwareVersion = FLOW_MASTER;

    public static FlowFirmwareVersion getFlowFirmwareVersion() {
        return flowFirmwareVersion;
    }

    public static void setFlowFirmwareVersion(FlowFirmwareVersion flowVersion) {
        flowFirmwareVersion = flowVersion;
    }
}
