package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.pump.PumpInfo;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import fi.stardex.sisu.persistence.repos.pump.PumpTypeRepository;
import fi.stardex.sisu.states.CustomPumpState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.function.Consumer;

public class PumpModel {

    private final PumpRepository pumpRepository;

    private final PumpTypeRepository pumpTypeRepository;

    private final ManufacturerPumpModel manufacturerPumpModel;

    private final CustomPumpState customPumpState;

    private final ObjectProperty<ObservableList<Pump>> pumpObservableListProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Pump> pumpProperty = new SimpleObjectProperty<>();

    private PumpInfo pumpType;

    public PumpModel(PumpRepository pumpRepository, ManufacturerPumpModel manufacturerPumpModel, CustomPumpState customPumpState, PumpTypeRepository pumpTypeRepository) {

        this.pumpRepository = pumpRepository;
        this.manufacturerPumpModel = manufacturerPumpModel;
        this.customPumpState = customPumpState;
        this.pumpTypeRepository = pumpTypeRepository;

    }

    public ObjectProperty<ObservableList<Pump>> getPumpObservableListProperty() {
        return pumpObservableListProperty;
    }

    public ObjectProperty<Pump> pumpProperty() {
        return pumpProperty;
    }

    public PumpInfo getPumpType() {
        return pumpType;
    }

    public void initPumpList() {

        pumpObservableListProperty.setValue(FXCollections.observableArrayList(pumpRepository.findAllByManufacturerPumpAndCustom(
                manufacturerPumpModel.manufacturerPumpProperty().get(),
                customPumpState.customPumpProperty().get()
        )));

    }

    public void initPumpType(){

        pumpType = null;
        if(pumpProperty.get() != null){

            Optional<PumpInfo> pumpInfoById = pumpTypeRepository.findById(pumpProperty.get().getPumpCode());
            pumpInfoById.ifPresent(pumpInfo -> pumpType = pumpInfo);
        }
    }


}
