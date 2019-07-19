package fi.stardex.sisu.model;

import fi.stardex.sisu.model.pump.ManufacturerPumpModel;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.repos.pump.ManufacturerPumpRepository;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

class ManufacturerPumpModelTest extends ModelTest {

    @Test
    @DisplayName("Initializing manufacturer pump list from repository")
    void initManufacturerPumpList(@Autowired ManufacturerPumpRepository manufacturerPumpRepository,
                                  @Autowired ManufacturerPumpModel manufacturerPumpModel,
                                  TestReporter testReporter) {

        List<ManufacturerPump> temp = new ArrayList<>();
        manufacturerPumpRepository.findAll().forEach(temp::add);

        assumeFalse(temp.isEmpty());

        ObservableList<ManufacturerPump> manufacturerPumpObservableList = manufacturerPumpModel.getManufacturerPumpObservableList();
        manufacturerPumpObservableList.setAll(temp);

        testReporter.publishEntry("Number of all pump manufacturers in the list", String.valueOf(manufacturerPumpObservableList.size()));

    }

}