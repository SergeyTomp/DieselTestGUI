package fi.stardex.sisu.states;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjConfigurationModel {

    private final ObjectProperty <InjectorChannel> injConfigurationProperty = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorChannel> injConfigurationProperty() {
        return injConfigurationProperty;
    }
}
