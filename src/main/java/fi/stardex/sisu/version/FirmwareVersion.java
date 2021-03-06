package fi.stardex.sisu.version;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class FirmwareVersion<T extends Versions>  {

    protected ObjectProperty<T> versionProperty;

    public FirmwareVersion(T version) {
        versionProperty = new SimpleObjectProperty<>(version);
    }

    public ObjectProperty<T> versionProperty() {
        return versionProperty;
    }

    public T getVersions() {
        return versionProperty.get();
    }

    public void setVersions(T version) {
        versionProperty.set(version);
    }
}
