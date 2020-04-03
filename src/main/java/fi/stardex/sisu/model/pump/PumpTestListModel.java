package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.model.pump.AutoTestListLastChangeModel.PumpTestWrapper;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpTestRepository;
import fi.stardex.sisu.util.enums.Operation;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PumpTestListModel {

    private final ObservableList<PumpTestWrapper> pumpTestObservableList = FXCollections.observableList(new ArrayList<>());
    private PumpTestRepository pumpTestRepository;
    private PumpModel pumpModel;
    private AutoTestListLastChangeModel autoTestListLastChangeModel;
    private IntegerProperty selectedTestIndex = new SimpleIntegerProperty();
    private ObjectProperty<Operation> customTestOperation = new SimpleObjectProperty<>();

    public void setSelectedTestIndex(int selectedTestIndex) {
        this.selectedTestIndex.set(selectedTestIndex);
    }
    public void setAutoTestListLastChangeModel(AutoTestListLastChangeModel autoTestListLastChangeModel) {
        this.autoTestListLastChangeModel = autoTestListLastChangeModel;
    }

    public IntegerProperty selectedTestIndexProperty() {
        return selectedTestIndex;
    }
    public ObservableList<PumpTestWrapper> getPumpTestObservableList() {
        return pumpTestObservableList;
    }
    public ObjectProperty<Operation> customTestOperationProperty() {
        return customTestOperation;
    }

    public PumpTestListModel(PumpTestRepository pumpTestRepository, PumpModel pumpModel) {
        this.pumpTestRepository = pumpTestRepository;
        this.pumpModel = pumpModel;
    }

//    public void initPumpTestList(){
//
////        if (pumpModel.pumpProperty().get() != null) {
//
//            List<PumpTest> allByPump = pumpTestRepository.findAllByPump(pumpModel.pumpProperty().get());
//
//            allByPump.sort(Comparator.comparingInt(PumpTest::getId));
//
//            List<PumpTestWrapper> pumpTestWrappers = IntStream.rangeClosed(0, allByPump.size()-1)
//                    .mapToObj(i -> autoTestListLastChangeModel.new PumpTestWrapper(allByPump.get(i), i))
//                    .collect(Collectors.toList());
//
//            pumpTestObservableList.setAll(pumpTestWrappers);
////        }
//    }
}
