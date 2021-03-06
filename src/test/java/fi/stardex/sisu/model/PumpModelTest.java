package fi.stardex.sisu.model;

import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.repos.pump.PumpRepository;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

class PumpModelTest extends ModelTest{

    @Test
    @DisplayName("Initializing pump list from repository")
    void initPumpList(@Autowired PumpRepository pumpRepository,
                      @Autowired PumpModel pumpModel,
                      TestReporter testReporter) {

        List<Pump> temp = pumpRepository.findAllByManufacturerAndCustom(new ManufacturerPump("Delphi", false), false);

        assumeFalse(temp.isEmpty());

        ObjectProperty<ObservableList<Pump>> pumpObservableListProperty = pumpModel.getPumpObservableListProperty();

        pumpObservableListProperty.setValue(FXCollections.observableArrayList(temp));

        testReporter.publishEntry("Number of Delphi non-custom pumps in the list", String.valueOf(pumpObservableListProperty.get().size()));

    }

}