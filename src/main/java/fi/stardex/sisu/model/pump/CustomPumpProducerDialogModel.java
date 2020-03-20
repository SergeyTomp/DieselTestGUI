package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

//TODO: delete class after implementation of MainSectionUisController as a unique one for all GUI types.
public class CustomPumpProducerDialogModel {

    private ObjectProperty<Producer> customProducer = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<Producer> customProducerProperty() {
        return customProducer;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
