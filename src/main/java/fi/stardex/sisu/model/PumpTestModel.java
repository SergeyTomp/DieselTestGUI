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

    private final ObjectProperty<PumpTest> pumpTestProperty = new SimpleObjectProperty<>();

    public ObservableList<PumpTest> getPumpTestObservableList() {
        return pumpTestObservableList;
    }

    public ObjectProperty<PumpTest> pumpTestProperty() {
        return pumpTestProperty;
    }

    public PumpTestModel(PumpTestRepository pumpTestRepository, PumpModel pumpModel) {

        this.pumpTestRepository = pumpTestRepository;
        this.pumpModel = pumpModel;

    }

    // TODO: добавить тестирование метода
    public void initPumpTestList(){

        List<PumpTest> allByPump = pumpTestRepository.findAllByPump(pumpModel.pumpProperty().get());
        pumpTestObservableList.setAll(allByPump);

    }

}
