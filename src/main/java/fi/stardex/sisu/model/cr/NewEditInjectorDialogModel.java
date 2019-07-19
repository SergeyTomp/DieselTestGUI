package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class NewEditInjectorDialogModel {

    private ObjectProperty<Injector> customInjector = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<Injector> customInjectorProperty() {
        return customInjector;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
