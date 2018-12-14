package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.repos.pump.ManufacturerPumpRepository;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ManufacturerPumpModel {

    private final ManufacturerPumpRepository manufacturerPumpRepository;

    private final ObservableList<ManufacturerPump> manufacturerPumpObservableList = FXCollections.observableArrayList();

    private final ObjectProperty<ManufacturerPump> manufacturerPumpProperty = new SimpleObjectProperty<>();

    public ManufacturerPumpModel(ManufacturerPumpRepository manufacturerPumpRepository) {
        this.manufacturerPumpRepository = manufacturerPumpRepository;
    }

    public ObservableList<ManufacturerPump> getManufacturerPumpObservableList() {
        return manufacturerPumpObservableList;
    }

    public ObjectProperty<ManufacturerPump> manufacturerPumpProperty() {
        return manufacturerPumpProperty;
    }

    public void initManufacturerPumpList() {

        if (manufacturerPumpObservableList.isEmpty()) {

            List<ManufacturerPump> temp = new ArrayList<>();

            manufacturerPumpRepository.findAll().forEach(temp::add);
            manufacturerPumpObservableList.setAll(temp);

        }

    }

}
