package fi.stardex.sisu.version;

public class FlowFirmwareVersion<T extends Versions> extends FirmwareVersion<T> {

    public FlowFirmwareVersion(T version) {
        super(version);
    }

    public enum FlowVersions implements Versions {

        MASTER, STREAM, STAND_FM, STAND_FM_4_CH, NO_VERSION

    }

}
