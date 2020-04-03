package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Test;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

//TODO: delete class after implementation of MainSectionUisController as a unique one for all GUI types.
public class CustomPumpTestDialogModel {

    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();
    private ObjectProperty<Test> customTest = new SimpleObjectProperty<>();

    public ObjectProperty<Test> customTestProperty() {
        return customTest;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
