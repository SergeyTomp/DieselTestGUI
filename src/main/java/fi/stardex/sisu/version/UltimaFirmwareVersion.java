package fi.stardex.sisu.version;

public class UltimaFirmwareVersion<T extends Versions> extends FirmwareVersion<T> {

    public UltimaFirmwareVersion(T version) {
        super(version);
    }

    public enum UltimaVersions implements Versions{

        WITH_A, WITHOUT_A, WITHOUT_F, NO_VERSION

    }

}
