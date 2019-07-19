package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CustomModelDialogModel {

    private ObjectProperty<? extends Model> customModel = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<? extends Model> customModelProperty() {
        return customModel;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }
}
