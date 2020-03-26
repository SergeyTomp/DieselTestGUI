package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.util.enums.Operation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class PumpModel {

    private final ObjectProperty<ObservableList<Pump>> pumpObservableListProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Pump> pumpProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customPumpOperation = new SimpleObjectProperty<>();

    public ObjectProperty<ObservableList<Pump>> getPumpObservableListProperty() {
        return pumpObservableListProperty;
    }
    public ObjectProperty<Pump> pumpProperty() {
        return pumpProperty;
    }
    public ObjectProperty<Operation> customPumpOperationProperty() {
        return customPumpOperation;
    }
}
