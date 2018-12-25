package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpTestRepository;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class PumpTestModel {


    private final ObjectProperty<PumpTest> pumpTestProperty = new SimpleObjectProperty<>();

    public ObjectProperty<PumpTest> pumpTestProperty() {
        return pumpTestProperty;
    }

}
