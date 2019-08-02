package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.enums.Operation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CustomModelDialogModel {


    // custom Model creation partition
    private ObjectProperty<Model> customModel = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();
    private ObjectProperty<Object> cancel = new SimpleObjectProperty<>();

    public ObjectProperty<Model> customModelProperty() {
        return customModel;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
    public ObjectProperty<Object> cancelProperty() {
        return cancel;
    }


    // custom VAP creation partition
    private ObjectProperty<VAP> customVAP = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customVapOperation = new SimpleObjectProperty<>();
    private ObjectProperty<InjectorType> injectorType = new SimpleObjectProperty<>();
    private ObjectProperty<InjectorSubType> injectorSubType = new SimpleObjectProperty<>();

    public ObjectProperty<VAP> customVapProperty() {
        return customVAP;
    }
    public ObjectProperty<InjectorType> getInjectorType() {
        return injectorType;
    }
    public ObjectProperty<InjectorSubType> getInjectorSubType() {
        return injectorSubType;
    }
    public ObjectProperty<Operation> customVapOperationProperty() {
        return customVapOperation;
    }

}
