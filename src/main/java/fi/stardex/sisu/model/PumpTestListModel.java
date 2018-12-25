package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpTestRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<PumpTestWrapper> pumpTestWrappers = allByPump
                .stream()
                .map(PumpTestWrapper::new)
                .sorted((Comparator.comparingInt(pumpTestWrapper -> pumpTestWrapper.getPumpTest().getId())))
                .collect(Collectors.toList());
        pumpTestObservableList.setAll(pumpTestWrappers);
    }
}
