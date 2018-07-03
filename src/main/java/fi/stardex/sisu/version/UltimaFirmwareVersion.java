package fi.stardex.sisu.version;

public enum UltimaFirmwareVersion {

    MULTI_CHANNEL_FIRMWARE_W_ACTIVATION, MULTI_CHANNEL_FIRMWARE_WO_ACTIVATION, MULTI_CHANNEL_FIRMWARE_WO_FILTER;

    public double getPulseLengthStep() {
        return 32.888;
    }

    public int getDelaySampleSize() {
        return 256;
    }

    private static UltimaFirmwareVersion ultimaFirmwareVersion;

    public static UltimaFirmwareVersion getUltimaFirmwareVersion() {
        return ultimaFirmwareVersion;
    }

    public static void setUltimaFirmwareVersion(UltimaFirmwareVersion ultimaFirmware) {
        ultimaFirmwareVersion = ultimaFirmware;
    }
}
