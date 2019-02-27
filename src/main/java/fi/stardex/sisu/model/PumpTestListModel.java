package fi.stardex.sisu.model;

import fi.stardex.sisu.model.AutoTestListLastChangeModel.PumpTestWrapper;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpTestRepository;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
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

    public void setSelectedTestIndex(int selectedTestIndex) {
        this.selectedTestIndex.set(selectedTestIndex);
    }

    public IntegerProperty selectedTestIndexProperty() {
        return selectedTestIndex;
    }
    public ObservableList<PumpTestWrapper> getPumpTestObservableList() {
        return pumpTestObservableList;
    }

    public void setAutoTestListLastChangeModel(AutoTestListLastChangeModel autoTestListLastChangeModel) {
        this.autoTestListLastChangeModel = autoTestListLastChangeModel;
    }

    public PumpTestListModel(PumpTestRepository pumpTestRepository, PumpModel pumpModel) {
        this.pumpTestRepository = pumpTestRepository;
        this.pumpModel = pumpModel;
    }

    public void initPumpTestList(){

//        if (pumpModel.pumpProperty().get() != null) {

            List<PumpTest> allByPump = pumpTestRepository.findAllByPump(pumpModel.pumpProperty().get());

            allByPump.sort(Comparator.comparingInt(PumpTest::getId));

            List<PumpTestWrapper> pumpTestWrappers = IntStream.rangeClosed(0, allByPump.size()-1)
                    .mapToObj(i -> autoTestListLastChangeModel.new PumpTestWrapper(allByPump.get(i), i))
                    .collect(Collectors.toList());

            pumpTestObservableList.setAll(pumpTestWrappers);
//        }
    }
}
