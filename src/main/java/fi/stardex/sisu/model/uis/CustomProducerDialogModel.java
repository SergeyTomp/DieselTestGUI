package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CustomProducerDialogModel {

    private ObjectProperty<? extends Producer> customProducer = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<? extends Producer> customProducerProperty() {
        return customProducer;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
