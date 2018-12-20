package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjectorTestModel {

    private final ObjectProperty<InjectorTest> injectorTestProperty = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorTest> injectorTestProperty() {
        return injectorTestProperty;
    }

}
