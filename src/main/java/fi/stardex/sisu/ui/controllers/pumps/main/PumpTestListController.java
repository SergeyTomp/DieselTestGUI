package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.cr.CrSettingsModel;
import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.model.pump.AutoTestListLastChangeModel.PumpTestWrapper;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpTestService;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.Move;
import fi.stardex.sisu.util.enums.Operation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private PumpModel pumpModel;
    private PumpTestService pumpTestService;
    private CustomPumpState customPumpState;
    private CustomPumpTestDialogModel customPumpTestDialogModel;
    private CrSettingsModel crSettingsModel;

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
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }
    public void setPumpTestService(PumpTestService pumpTestService) {
        this.pumpTestService = pumpTestService;
    }
    public void setCustomPumpState(CustomPumpState customPumpState) {
        this.customPumpState = customPumpState;
    }
    public void setCustomPumpTestDialogModel(CustomPumpTestDialogModel customPumpTestDialogModel) {
        this.customPumpTestDialogModel = customPumpTestDialogModel;
    }
    public void setCrSettingsModel(CrSettingsModel crSettingsModel) {
        this.crSettingsModel = crSettingsModel;
    }

    public ListView<PumpTestWrapper> getTestListView() {
        return testListView;
    }



    @PostConstruct
    public void init() {

        setupMoveButtonEventHandlers();
        setupListeners();
        initTestContextMenu();
    }

    private void setupListeners() {

        pumpModel.pumpProperty().addListener((observableValue, oldValue, newValue) -> fillPumpTestList());

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

//    private void reindexWrappers(){
//        testListView.getItems().forEach(wrapper -> wrapper.setListViewIndex(testListView.getItems().indexOf(wrapper)));
//    }

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

        customPumpTestDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                pumpTestListModel.customTestOperationProperty().setValue(null));

        customPumpTestDialogModel.doneProperty().addListener((observable, oldValue, newValue) -> {
            fillPumpTestList();
            PumpTestWrapper wrapper = testListView.getItems().stream()
                    .filter(w -> w.getPumpTest().equals(customPumpTestDialogModel.customTestProperty().get()))
                    .findFirst()
                    .orElse(null);
            testListView.getSelectionModel().select(wrapper);
            pumpTestListModel.customTestOperationProperty().setValue(null);
        });
    }

    private void fillPumpTestList(){

        pumpTestModel.pumpTestProperty().setValue(null);
        List<PumpTest> allByPump = pumpTestService.findAllByModel(pumpModel.pumpProperty().get());
        adjustNominals(allByPump);

        allByPump.sort(Comparator.comparingInt(PumpTest::getId));

        List<PumpTestWrapper> pumpTestWrappers = IntStream.rangeClosed(0, allByPump.size()-1)
                .mapToObj(i -> autoTestListLastChangeModel.new PumpTestWrapper(allByPump.get(i), i))
                .collect(Collectors.toList());

        pumpTestListModel.getPumpTestObservableList().setAll(pumpTestWrappers);

        if (pumpTestWrappers.size() != 0) {
            testListView.getItems().setAll(pumpTestWrappers);
            setTestMode();
        }else{
            testListView.getItems().clear();
        }
    }

    private void initTestContextMenu() {

        ContextMenu testMenu = new ContextMenu();
        MenuItem newTest = new MenuItem("New");
        newTest.setOnAction(actionEvent -> pumpTestListModel.customTestOperationProperty().setValue(Operation.NEW));
        MenuItem editTest = new MenuItem("Edit");
        editTest.setOnAction(actionEvent -> pumpTestListModel.customTestOperationProperty().setValue(Operation.EDIT));
        MenuItem deleteTest = new MenuItem("Delete");
        deleteTest.setOnAction(actionEvent -> pumpTestListModel.customTestOperationProperty().setValue(Operation.DELETE));


        testListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                testMenu.getItems().clear();

                testMenu.getItems().add(newTest);
                if (customPumpState.customPumpProperty().get() && testListView.getSelectionModel().getSelectedItem() != null) {
                    testMenu.getItems().add(editTest);
                    testMenu.getItems().add(deleteTest);
                }
                testMenu.show(testListView, event.getScreenX(), event.getScreenY());
            } else
                testMenu.hide();
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

    private void adjustNominals(List<PumpTest> testsList) {

        testsList.forEach(pumpTest -> {

            Optional<Double> maxDirectFlow = Optional.ofNullable(pumpTest.getMaxDirectFlow());
            Optional<Double> minDirectFlow = Optional.ofNullable(pumpTest.getMinDirectFlow());
            Optional<Double> maxBackFlow = Optional.ofNullable(pumpTest.getMaxBackFlow());
            Optional<Double> minBackFlow = Optional.ofNullable(pumpTest.getMinBackFlow());

            int rpm = pumpTest.getMotorSpeed();
            int maxRpm = crSettingsModel.pumpMaxRpmPropertyProperty().get();
            double relation = (double)maxRpm / rpm;

            if (relation < 1) {

                pumpTest.setMotorSpeed(maxRpm);
                maxDirectFlow.ifPresent(f -> pumpTest.setMaxDirectFlow(f * relation));
                minDirectFlow.ifPresent(f -> pumpTest.setMinDirectFlow(f * relation));
                maxBackFlow.ifPresent(f -> pumpTest.setMaxBackFlow(f * relation));
                minBackFlow.ifPresent(f -> pumpTest.setMinBackFlow(f * relation));
            }
        });
    }
}
