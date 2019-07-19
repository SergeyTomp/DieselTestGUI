package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.pump.AutoTestListLastChangeModel;
import fi.stardex.sisu.model.pump.AutoTestListLastChangeModel.PumpTestWrapper;
import fi.stardex.sisu.model.pump.PumpTestListModel;
import fi.stardex.sisu.model.pump.PumpTestModeModel;
import fi.stardex.sisu.model.pump.PumpTestModel;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.Move;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import static fi.stardex.sisu.util.enums.Move.DOWN;
import static fi.stardex.sisu.util.enums.Move.UP;
import static fi.stardex.sisu.util.enums.Tests.TestType;

public class PumpTestListController {

    @FXML private ListView<PumpTestWrapper> testListView;
    @FXML private Button upButton;
    @FXML private Button downButton;

    private boolean isFocusMoved;
    private PumpTestModel pumpTestModel;
    private PumpTestListModel pumpTestListModel;
    private PumpTestModeModel pumpTestModeModel;
    private AutoTestListLastChangeModel autoTestListLastChangeModel;
    private PumpsStartButtonState pumpsStartButtonState;

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }
    public void setAutoTestListLastChangeModel(AutoTestListLastChangeModel autoTestListLastChangeModel) {
        this.autoTestListLastChangeModel = autoTestListLastChangeModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public ListView<PumpTestWrapper> getTestListView() {
        return testListView;
    }



    @PostConstruct
    public void init() {

        setupMoveButtonEventHandlers();
        setupListeners();
    }

    private void setupListeners() {

        pumpTestListModel.getPumpTestObservableList().addListener((ListChangeListener<PumpTestWrapper>) change -> {
            if (!change.getList().isEmpty()) {

                testListView.getItems().setAll(change.getList());
                setTestMode();
            }
            else{
                pumpTestModel.pumpTestProperty().setValue(null);
            }
        });

        testListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if(isFocusMoved) return;
            Optional.ofNullable(newValue).ifPresent(value -> {
                pumpTestModel.pumpTestProperty().setValue(value.getPumpTest());
                pumpTestListModel.setSelectedTestIndex(testListView.getSelectionModel().getSelectedIndex());
                if(!pumpsStartButtonState.startButtonProperty().get()){

                    showUpDownButtons(newValue.isIncludedProperty().get());
                }
            });
            enableUpDownButtons(testListView.getSelectionModel().getSelectedIndex());
        });

        pumpTestModeModel.testModeProperty().addListener((observableValue, oldValue, newValue) -> setTestMode());

        autoTestListLastChangeModel.changedParametersProperty().addListener((observableValue, oldValue, newValue) -> {
//            PumpTestWrapper selectedItem = testListView.getSelectionModel().getSelectedItem();
//
//            int changedIndex = newValue.getLastChangeIndex();
//
//            if(!newValue.isInclude()){
//
//                int listEndIndex = testListView.getItems().size() - 1;
//
//                if (changedIndex == listEndIndex)
//                    showUpDownButtons(false);
//
//                for (int i = changedIndex; i < listEndIndex; i++) {
//                    Collections.swap(testListView.getItems(), i, i + 1);
//                }
//            }else{
//
//                if (changedIndex == 0)
//                    showUpDownButtons(true);
//
//                for (int i = changedIndex; i > 0; i--) {
//                    Collections.swap(testListView.getItems(), i, i - 1);
//                }
//            }
//            reindexWrappers();
//            testListView.getSelectionModel().select(selectedItem);
//            testListView.scrollTo(0);
            testListView.getItems().sort((o1, o2) -> Boolean.compare(o2.isIncludedProperty().get(), o1.isIncludedProperty().get()));
            int includedQty = (int)testListView.getItems().stream().filter(t -> t.isIncludedProperty().get()).count();
            testListView.getItems().subList(0, includedQty).sort(Comparator.comparingInt(wrapper -> wrapper.getPumpTest().getId()));
            testListView.scrollTo(0);

        });
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {

            testListView.setDisable(newValue && pumpTestModeModel.testModeProperty().get() == TestType.AUTO);
            showUpDownButtons(!newValue);
        });

        pumpTestModeModel.testModeProperty().addListener((observableValue, oldValue, newValue) -> {

            switch (newValue) {
                case AUTO:
                    showUpDownButtons(true);
                    break;
                case TESTPLAN:
                    showUpDownButtons(false);
                    break;
            }
        });
    }

    private void setTestMode() {

        TestType test = pumpTestModeModel.testModeProperty().get();

        switch (test) {

            case AUTO:
                testListView.setCellFactory(CheckBoxListCell.forListView(PumpTestWrapper::isIncludedProperty));
                break;
            case TESTPLAN:
                testListView.setCellFactory(null);
                break;
        }
        testListView.getSelectionModel().selectFirst();
        testListView.scrollTo(0);
    }

    private void setupMoveButtonEventHandlers(){

        upButton.setOnAction(event -> moveTest(UP));
        downButton.setOnAction(event -> moveTest(DOWN));
    }

    private void moveTest(Move move) {

        int selectedTestIndex = testListView.getSelectionModel().getSelectedIndex();
        if (selectedTestIndex == -1)
            return;

        isFocusMoved = true;

        switch (move) {

            case UP:
                Collections.swap(testListView.getItems(), selectedTestIndex, selectedTestIndex - 1);
                testListView.getSelectionModel().select(selectedTestIndex - 1);
                testListView.scrollTo(testListView.getSelectionModel().getSelectedIndex());
                break;
            case DOWN:
                Collections.swap(testListView.getItems(), selectedTestIndex, selectedTestIndex + 1);
                testListView.getSelectionModel().select(selectedTestIndex + 1);
                testListView.scrollTo(testListView.getSelectionModel().getSelectedIndex());
                break;

        }
        enableUpDownButtons(testListView.getSelectionModel().getSelectedIndex());
//        reindexWrappers();
        isFocusMoved = false;

    }

//    private void reindexWrappers(){
//        testListView.getItems().forEach(wrapper -> wrapper.setListViewIndex(testListView.getItems().indexOf(wrapper)));
//    }

    private void enableUpDownButtons(int selectedIndex) {

        int nonIncludedTestsQty = (int)testListView.getItems().stream().filter(tw -> !tw.isIncludedProperty().get()).count();
        int includedTestsSize = testListView.getItems().size() - nonIncludedTestsQty;

        if (selectedIndex == 0 && selectedIndex == includedTestsSize - 1){
            upButton.setDisable(true);
            downButton.setDisable(true);
        } else if (selectedIndex == 0) {
            upButton.setDisable(true);
            downButton.setDisable(false);
        } else if ((selectedIndex > 0) && (selectedIndex <= includedTestsSize - 2)){
            upButton.setDisable(false);
            downButton.setDisable(false);
        } else {
            upButton.setDisable(false);
            downButton.setDisable(true);
        }
    }

    private void showUpDownButtons(boolean show){
        upButton.setVisible(show);
        downButton.setVisible(show);
    }
}
