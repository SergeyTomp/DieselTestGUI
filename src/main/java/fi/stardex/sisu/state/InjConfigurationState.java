package fi.stardex.sisu.state;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjConfigurationState {

    private final ObjectProperty <InjectorChannel> injConfigurationState = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorChannel> injConfigurationStateProperty() {
        return injConfigurationState;
    }
}
