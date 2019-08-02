package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CustomVapUisDialogModel {

    private ObjectProperty<VAP> customVAP = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<VAP> customVapProperty() {
        return customVAP;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }

}
