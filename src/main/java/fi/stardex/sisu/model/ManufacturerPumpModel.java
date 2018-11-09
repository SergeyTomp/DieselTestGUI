package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.repos.pump.ManufacturerPumpRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ManufacturerPumpModel {

    private ManufacturerPumpRepository manufacturerPumpRepository;

    private ObservableList<ManufacturerPump> manufacturerPumpObservableList = FXCollections.observableArrayList();

    public ManufacturerPumpModel(ManufacturerPumpRepository manufacturerPumpRepository) {
        this.manufacturerPumpRepository = manufacturerPumpRepository;
    }

    public ObservableList<ManufacturerPump> getManufacturerPumpObservableList() {
        return manufacturerPumpObservableList;
    }

    public void initManufacturerPumpList() {

        if (manufacturerPumpObservableList.isEmpty()) {

            List<ManufacturerPump> temp = new ArrayList<>();
            manufacturerPumpRepository.findAll().forEach(temp::add);
            manufacturerPumpObservableList.setAll(temp);

        }

    }

}
