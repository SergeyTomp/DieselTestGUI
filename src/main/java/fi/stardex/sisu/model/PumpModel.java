package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PumpModel {

    private PumpRepository pumpRepository;

    private ObservableList<Pump> pumpObservableList = FXCollections.observableArrayList();

    private StringProperty pumpCodeProperty = new SimpleStringProperty();

    private BooleanProperty customProperty = new SimpleBooleanProperty();

    public PumpModel(PumpRepository pumpRepository) {
        this.pumpRepository = pumpRepository;
    }

    public ObservableList<Pump> getPumpObservableList() {
        return pumpObservableList;
    }

    public StringProperty pumpCodeProperty() {
        return pumpCodeProperty;
    }

    public BooleanProperty customProperty() {
        return customProperty;
    }

    public void initPumpList(ManufacturerPump manufacturerPump) {

        pumpObservableList.setAll(pumpRepository.findAllByManufacturerPumpAndCustom(manufacturerPump, false));

    }

}
