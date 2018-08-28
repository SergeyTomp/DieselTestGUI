package fi.stardex.sisu.version;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleButton;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.*;

public class FlowFirmwareVersion<T extends Versions> extends FirmwareVersion<T> implements ChangeListener<T> {

    private ToggleButton testBenchStartToggleButton;

    public FlowFirmwareVersion(T version, ToggleButton testBenchStartToggleButton) {
        super(version);
        this.testBenchStartToggleButton = testBenchStartToggleButton;
        versionProperty.addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

        if (newValue == STAND_FM)
            testBenchStartToggleButton.setDisable(false);
        else
            testBenchStartToggleButton.setDisable(true);

    }

    public enum FlowVersions implements Versions {

        MASTER, STREAM, STAND_FM, NO_VERSION

    }

}
