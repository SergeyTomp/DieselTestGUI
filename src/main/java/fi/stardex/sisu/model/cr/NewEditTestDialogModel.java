package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class NewEditTestDialogModel {

    private ObjectProperty<InjectorTest> customTest = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<InjectorTest> customTestProperty() {
        return customTest;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
