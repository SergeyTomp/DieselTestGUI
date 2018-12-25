package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpTestRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class PumpTestListModel {

    private final ObservableList<PumpTestWrapper> pumpTestObservableList = FXCollections.observableList(new ArrayList<>());
    private PumpTestRepository pumpTestRepository;
    private PumpModel pumpModel;

    public ObservableList<PumpTestWrapper> getPumpTestObservableList() {
        return pumpTestObservableList;
    }

    public PumpTestListModel(PumpTestRepository pumpTestRepository, PumpModel pumpModel) {
        this.pumpTestRepository = pumpTestRepository;
        this.pumpModel = pumpModel;
    }

    public void initPumpTestList(){

        List<PumpTest> allByPump = pumpTestRepository.findAllByPump(pumpModel.pumpProperty().get());
        allByPump.stream().map(p -> new PumpTestWrapper(p, true)).forEach(pumpTestObservableList::add);
    }
}
