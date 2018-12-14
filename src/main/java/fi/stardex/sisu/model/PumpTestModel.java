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

    private PumpTestRepository pumpTestRepository;

    private PumpModel pumpModel;

    private final ObservableList<PumpTest> pumpTestObservableList = FXCollections.observableList(new ArrayList<>());

    private final ObjectProperty<PumpTest> pumpTestObjectProperty = new SimpleObjectProperty<>();

    public ObservableList<PumpTest> getPumpTestObservableList() {
        return pumpTestObservableList;
    }

    public ObjectProperty<PumpTest> pumpTestObjectProperty() {
        return pumpTestObjectProperty;
    }

    public PumpTestModel(PumpTestRepository pumpTestRepository, PumpModel pumpModel) {
        this.pumpTestRepository = pumpTestRepository;
        this.pumpModel = pumpModel;
    }

    public void initPumpTestList(){

        List<PumpTest> allByPump = pumpTestRepository.findAllByPump(pumpModel.pumpProperty().get());
        pumpTestObservableList.setAll(allByPump);

    }
}
