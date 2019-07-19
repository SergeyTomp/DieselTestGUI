package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.util.enums.InjectorChannel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjConfigurationModel {

    private final ObjectProperty <InjectorChannel> injConfigurationProperty = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorChannel> injConfigurationProperty() {
        return injConfigurationProperty;
    }

}
