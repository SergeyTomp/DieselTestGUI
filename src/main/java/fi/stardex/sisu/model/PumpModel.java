package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PumpModel {

    private PumpRepository pumpRepository;

    private ObservableList<Pump> pumpObservableList = FXCollections.observableArrayList();

    public PumpModel(PumpRepository pumpRepository) {
        this.pumpRepository = pumpRepository;
    }

    public ObservableList<Pump> getPumpObservableList() {
        return pumpObservableList;
    }

    public void initPumpList(ManufacturerPump manufacturerPump) {

        pumpObservableList.setAll(pumpRepository.findAllByManufacturerPumpAndCustom(manufacturerPump, false));

    }

}
