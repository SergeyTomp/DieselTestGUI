package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.util.enums.Operation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ManufacturerPumpModel {

    private final ObservableList<ManufacturerPump> manufacturerPumpObservableList = FXCollections.observableArrayList();

    private final ObjectProperty<ManufacturerPump> manufacturerPumpProperty = new SimpleObjectProperty<>();

    private ObjectProperty<Operation> customProducerOperation = new SimpleObjectProperty<>();

    public ObservableList<ManufacturerPump> getManufacturerPumpObservableList() {
        return manufacturerPumpObservableList;
    }

    public ObjectProperty<ManufacturerPump> manufacturerPumpProperty() {
        return manufacturerPumpProperty;
    }

    public ObjectProperty<Operation> customProducerOperationProperty() {
        return customProducerOperation;
    }

}
