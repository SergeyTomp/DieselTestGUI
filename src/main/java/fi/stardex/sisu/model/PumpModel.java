package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PumpModel {

    private final PumpRepository pumpRepository;

    private final ObjectProperty<ObservableList<Pump>> pumpObservableListProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Pump> pumpProperty = new SimpleObjectProperty<>();

    public PumpModel(PumpRepository pumpRepository) {
        this.pumpRepository = pumpRepository;
    }

    public ObjectProperty<ObservableList<Pump>> getPumpObservableListProperty() {
        return pumpObservableListProperty;
    }

    public ObjectProperty<Pump> pumpProperty() {
        return pumpProperty;
    }

    public void initPumpList(ManufacturerPump manufacturerPump, boolean custom) {

        pumpObservableListProperty.setValue(FXCollections.observableArrayList(pumpRepository.findAllByManufacturerPumpAndCustom(manufacturerPump, custom)));

    }

}
