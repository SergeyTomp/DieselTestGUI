package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javax.annotation.PostConstruct;

public class PumpTestListController {

    @FXML private ListView<PumpTest> testListView;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private HBox testListHBox;

    private PumpTestModel pumpTestModel;

    public ListView<PumpTest> getPumpTestListView() {
        return testListView;
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDownButton() {
        return downButton;
    }

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }

    @PostConstruct
    public void init(){

        pumpTestModel.getPumpTestObservableList().addListener((ListChangeListener<PumpTest>) change -> {

            testListView.getItems().setAll(change.getList());
            pumpTestModel.pumpTestObjectProperty().setValue(null);
            if(!change.getList().isEmpty()){
                testListView.getSelectionModel().selectFirst();


            }
        });

        testListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> pumpTestModel.pumpTestObjectProperty().setValue(newValue));
    }
}
