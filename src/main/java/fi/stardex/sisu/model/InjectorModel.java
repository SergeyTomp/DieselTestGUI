package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class InjectorModel {

    private ObjectProperty<Injector> injector = new SimpleObjectProperty<>();
    private boolean injectorIsChanging;

    public ObjectProperty<Injector> injectorProperty() {
        return injector;
    }
    public boolean isInjectorIsChanging() {
        return injectorIsChanging;
    }
    public void setInjectorIsChanging(boolean injectorIsChanging) {
        this.injectorIsChanging = injectorIsChanging;
    }
}
