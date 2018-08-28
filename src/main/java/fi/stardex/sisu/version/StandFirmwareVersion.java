package fi.stardex.sisu.version;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleButton;

import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions.*;

public class StandFirmwareVersion<T extends Versions> extends FirmwareVersion<T> implements ChangeListener<T> {

    private ToggleButton testBenchStartToggleButton;

    public StandFirmwareVersion(T version, ToggleButton testBenchStartToggleButton) {
        super(version);
        this.testBenchStartToggleButton = testBenchStartToggleButton;
        versionProperty.addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

        if (newValue == STAND)
            testBenchStartToggleButton.setDisable(false);
        else
            testBenchStartToggleButton.setDisable(true);

    }

    public enum StandVersions implements Versions {

        STAND, UNKNOWN, NO_VERSION

    }


}
