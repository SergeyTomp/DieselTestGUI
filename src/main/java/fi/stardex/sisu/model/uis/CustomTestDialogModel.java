package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Test;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CustomTestDialogModel {

    private ObjectProperty<? extends Test> customTest = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<? extends Test> customTestProperty() {
        return customTest;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
