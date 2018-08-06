package fi.stardex.sisu.version;

public enum StandFirmwareVersion {

    STAND;

    private static StandFirmwareVersion standFirmwareVersion;

    public static StandFirmwareVersion getStandFirmwareVersion() {
        return standFirmwareVersion;
    }

    public static void setStandFirmwareVersion(StandFirmwareVersion standVersion) {
        standFirmwareVersion = standVersion;
    }
}
