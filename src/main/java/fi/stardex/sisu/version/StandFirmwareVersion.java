package fi.stardex.sisu.version;

public class StandFirmwareVersion<T extends Versions> extends FirmwareVersion<T> {

    public StandFirmwareVersion(T version) {
        super(version);
    }

    public enum StandVersions implements Versions {

        STAND, STAND_FORTE, UNKNOWN, NO_VERSION
    }
}
