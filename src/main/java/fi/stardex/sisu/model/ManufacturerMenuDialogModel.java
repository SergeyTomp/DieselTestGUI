package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ManufacturerMenuDialogModel {

    private ObjectProperty<Manufacturer> customManufacturer = new SimpleObjectProperty<>();
    private ObjectProperty<Object> done = new SimpleObjectProperty<>();

    public ObjectProperty<Manufacturer> customManufacturerProperty() {
        return customManufacturer;
    }
    public ObjectProperty<Object> doneProperty() {
        return done;
    }
}
