package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestListModel;
import fi.stardex.sisu.model.PumpTestModeModel;
import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.model.PumpTestWrapper;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static fi.stardex.sisu.util.enums.Tests.*;

public class PumpTestListController {

    @FXML private ListView<PumpTestWrapper> testListView;
    @FXML private Button upButton;
    @FXML private Button downButton;

    private PumpTestModel pumpTestModel;

    private PumpTestListModel pumpTestListModel;

    private PumpTestModeModel pumpTestModeModel;

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }

    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }

    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }

    @PostConstruct
    public void init() {

        pumpTestListModel.getPumpTestObservableList().addListener((ListChangeListener<PumpTestWrapper>) change -> {
            testListView.getItems().setAll(change.getList());
            setTestMode();
        });

        testListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) ->
                Optional.ofNullable(newValue).ifPresent(value -> pumpTestModel.pumpTestProperty().setValue(value.getPumpTest())));

        pumpTestModeModel.testModeProperty().addListener((observableValue, oldValue, newValue) -> setTestMode());
    }

    private void setTestMode() {

        TestType test = pumpTestModeModel.testModeProperty().get();

        switch (test) {

            case AUTO:
                testListView.setCellFactory(CheckBoxListCell.forListView(PumpTestWrapper::isIncludedProperty));
                Platform.runLater(() -> {
                    testListView.getSelectionModel().select(0);
                    testListView.scrollTo(0);
                });
                break;
            case TESTPLAN:
                testListView.setCellFactory(null);
                break;
        }
            testListView.getSelectionModel().selectFirst();
            testListView.scrollTo(0);
    }
}
