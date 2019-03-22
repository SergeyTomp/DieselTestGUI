package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjectorTestModel {

    private final ObjectProperty<InjectorTest> injectorTestProperty = new SimpleObjectProperty<>();
    private boolean testIsChanging;

    public ObjectProperty<InjectorTest> injectorTestProperty() {
        return injectorTestProperty;
    }

    public boolean isTestIsChanging() {
        return testIsChanging;
    }

    public void setTestIsChanging(boolean testIsChanging) {
        this.testIsChanging = testIsChanging;
    }
}
