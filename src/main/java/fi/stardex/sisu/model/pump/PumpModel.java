package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import fi.stardex.sisu.states.CustomPumpState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PumpModel {

    private final PumpRepository pumpRepository;

    private final ManufacturerPumpModel manufacturerPumpModel;

    private final CustomPumpState customPumpState;

    private final ObjectProperty<ObservableList<Pump>> pumpObservableListProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Pump> pumpProperty = new SimpleObjectProperty<>();

    public PumpModel(PumpRepository pumpRepository, ManufacturerPumpModel manufacturerPumpModel, CustomPumpState customPumpState) {

        this.pumpRepository = pumpRepository;
        this.manufacturerPumpModel = manufacturerPumpModel;
        this.customPumpState = customPumpState;

    }

    public ObjectProperty<ObservableList<Pump>> getPumpObservableListProperty() {
        return pumpObservableListProperty;
    }

    public ObjectProperty<Pump> pumpProperty() {
        return pumpProperty;
    }

    public void initPumpList() {

        pumpObservableListProperty.setValue(FXCollections.observableArrayList(pumpRepository.findAllByManufacturerPumpAndCustom(
                manufacturerPumpModel.manufacturerPumpProperty().get(),
                customPumpState.customPumpProperty().get()
        )));

    }

}
