package fi.stardex.sisu.states;

import fi.stardex.sisu.util.enums.InjectorType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjectorTypeToggleState {

    private ObjectProperty<InjectorType> injectorTypeObjectProperty = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorType> injectorTypeObjectPropertyProperty() {
        return injectorTypeObjectProperty;
    }
}
