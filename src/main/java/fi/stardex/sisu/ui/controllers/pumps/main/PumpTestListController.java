package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javax.annotation.PostConstruct;
import java.util.Optional;

public class PumpTestListController {

    @FXML
    private ListView<PumpTest> testListView;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;

    private PumpTestModel pumpTestModel;

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }

    @PostConstruct
    public void init() {

        pumpTestModel.getPumpTestObservableList().addListener((ListChangeListener<PumpTest>) change -> {

            testListView.getItems().setAll(change.getList());
            testListView.getSelectionModel().selectFirst();

        });

        testListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->
                Optional.ofNullable(newValue).ifPresent(value -> pumpTestModel.pumpTestProperty().setValue(value)));

    }

}
