package fi.stardex.sisu.model;

import fi.stardex.sisu.util.enums.InjectorType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjectorTypeModel {

    private ObjectProperty<InjectorType> injectorTypeProperty = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorType> injectorTypeProperty() {
        return injectorTypeProperty;
    }

}
