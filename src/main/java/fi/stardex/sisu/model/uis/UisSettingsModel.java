package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.util.enums.Dimension;
import fi.stardex.sisu.util.enums.uis.RpmSource;
import fi.stardex.sisu.util.i18n.Locales;
import javafx.beans.property.*;
import javafx.scene.control.Button;

public class UisSettingsModel {

    private final ObjectProperty<Locales> languageProperty = new SimpleObjectProperty<>();
    private final IntegerProperty angleOffsetProperty = new SimpleIntegerProperty();
    private final ObjectProperty<RpmSource> rpmSourceProperty = new SimpleObjectProperty<>();
    private final IntegerProperty pressureSensorProperty = new SimpleIntegerProperty();
    private final BooleanProperty instantFlowProperty = new SimpleBooleanProperty();
    private final ObjectProperty<Dimension> flowRangeViewProperty = new SimpleObjectProperty<>();
    private final Button firmwareVersionButton = new Button();
    private Button differentialFmSettingsButton = new Button();

    public ObjectProperty<Locales> languageProperty() {
        return languageProperty;
    }
    public IntegerProperty angleOffsetProperty() {
        return angleOffsetProperty;
    }
    public ObjectProperty<RpmSource> rpmSourceProperty() {
        return rpmSourceProperty;
    }
    public IntegerProperty pressureSensorProperty() {
        return pressureSensorProperty;
    }
    public BooleanProperty instantFlowProperty() {
        return instantFlowProperty;
    }
    public ObjectProperty<Dimension> getFlowRangeViewProperty() {
        return flowRangeViewProperty;
    }
    public Button getFirmwareVersionButton() {
        return firmwareVersionButton;
    }
    public Button getDifferentialFmSettingsButton() {
        return differentialFmSettingsButton;
    }
}
