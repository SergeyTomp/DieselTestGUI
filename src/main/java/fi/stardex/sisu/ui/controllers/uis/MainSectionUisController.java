package fi.stardex.sisu.ui.controllers.uis;

import fi.stardex.sisu.measurement.TestManager;
import fi.stardex.sisu.measurement.TestManagerFactory;
import fi.stardex.sisu.measurement.Timing;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.uis.*;
import fi.stardex.sisu.persistence.orm.interfaces.*;
import fi.stardex.sisu.persistence.repos.uis.UisModelService;
import fi.stardex.sisu.persistence.repos.uis.UisProducerService;
import fi.stardex.sisu.persistence.repos.uis.UisTestService;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.BoostUadjustmentState;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.util.TimeProgressBar;
import fi.stardex.sisu.util.enums.*;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.listeners.PrintButtonHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.StartMeasurementCycle;
import static fi.stardex.sisu.util.enums.GUI_type.UIS;
import static fi.stardex.sisu.util.enums.Measurement.DELIVERY;
import static fi.stardex.sisu.util.enums.Measurement.VISUAL;
import static fi.stardex.sisu.util.enums.Move.DOWN;
import static fi.stardex.sisu.util.enums.Move.UP;
import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class MainSectionUisController {

    private static Logger logger = LoggerFactory.getLogger(MainSectionUisController.class);
    @FXML private GridPane testsToggleGroupGridPane;
    @FXML private TextField injectorNumberTextField;
    @FXML private VBox injectorTestsVBox;
    @FXML private GridPane timingGridPane;
    @FXML private StackPane startStackPane;
    @FXML private Button moveUpButton;
    @FXML private Button moveDownButton;
    @FXML private Label timingSpeedLabel;
    @FXML private ToggleButton startToggleButton;
    @FXML private ProgressBar adjustingTimeProgressBar;
    @FXML private ProgressBar measuringTimeProgressBar;
    @FXML private Text adjustingText;
    @FXML private Text measuringText;
    @FXML private Label labelAdjustTime;
    @FXML private Label labelMeasureTime;
    @FXML private Button storeButton;
    @FXML private Button resetButton;
    @FXML private Button printButton;
    @FXML private ToggleGroup testsToggleGroup;
    @FXML private RadioButton testPlanTestRadioButton;
    @FXML private RadioButton autoTestRadioButton;
    @FXML private RadioButton codingTestRadioButton;
    @FXML private ToggleGroup baseTypeToggleGroup;
    @FXML private RadioButton defaultRadioButton;
    @FXML private RadioButton customRadioButton;
    @FXML private ListView<Producer> manufacturerListView;
    @FXML private TextField searchModelTextField;
    @FXML private ListView<Model> modelListView;
    @FXML private ListView<Test> testListView;
    @FXML private ComboBox<TestSpeed> speedComboBox;
    @FXML private VBox injectorsVBox;

    private TimeProgressBar adjustingTime;
    private TimeProgressBar measuringTime;
    private int currentAdjustingTime;
    private int currentMeasuringTime;
    private ModbusRegisterProcessor flowModbusWriter;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private ViewHolder newEditInjectorDialog;
    private ViewHolder newEditTestDialog;
    private ViewHolder printDialogPanel;
    private Stage modelDialogStage;
    private Stage testDialogStage;
    private FilteredList<Model> filteredModelList;
    private I18N i18N;
    private boolean startLight;
    private boolean isFocusMoved;
    private boolean isAnotherAutoOrNewTestList;
    private Stage printStage;
    private MainSectionUisModel mainSectionUisModel;
    private GUI_TypeModel gui_typeModel;
    private CustomModelDialogModel customModelDialogModel;
    private CustomProducerDialogModel customProducerDialogModel;
    private CustomTestDialogModel customTestDialogModel;
    private UisRlcModel uisRlcModel;
    private BoostUadjustmentState boostUadjustmentState;
    private Step3Model step3Model;
    private TabSectionModel tabSectionModel;
    private PiezoRepairModel piezoRepairModel;
    private UisFlowModel uisFlowModel;
    private UisBipModel uisBipModel;
    private UisModelService uisModelService;
    private UisProducerService uisProducerService;
    private UisTestService uisTestService;
    private ModelService modelService;
    private ProducerService producerService;
    private TimingModelFactory timingModelFactory;
    private Timing timingModel;
    private TestService testService;
    private boolean oemAlertProcessing;
    private boolean modelAlertProcessing;
    private Alert alert;
    private StringProperty alertString = new SimpleStringProperty();
    private StringProperty yesButton = new SimpleStringProperty();
    private StringProperty noButton = new SimpleStringProperty();
    private IntegerProperty adjustingTimeProperty = new SimpleIntegerProperty();
    private IntegerProperty measuringTimeProperty = new SimpleIntegerProperty();
    private static final ObservableList<Test> listOfNonIncludedTests = FXCollections.observableArrayList();
    private TestManager testManager;
    private TestManagerFactory testManagerFactory;

    public ToggleButton getStartToggleButton() {
        return startToggleButton;
    }
    public ListView<Test> getTestListView() {
        return testListView;
    }
    public Button getStoreButton() {
        return storeButton;
    }

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setPrintDialogPanel(ViewHolder printDialogPanel) {
        this.printDialogPanel = printDialogPanel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setCustomModelDialogModel(CustomModelDialogModel customModelDialogModel) {
        this.customModelDialogModel = customModelDialogModel;
    }
    public void setCustomProducerDialogModel(CustomProducerDialogModel customProducerDialogModel) {
        this.customProducerDialogModel = customProducerDialogModel;
    }
    public void setCustomTestDialogModel(CustomTestDialogModel customTestDialogModel) {
        this.customTestDialogModel = customTestDialogModel;
    }
    public void setUisModelService(UisModelService uisModelService) {
        this.uisModelService = uisModelService;
    }
    public void setUisProducerService(UisProducerService uisProducerService) {
        this.uisProducerService = uisProducerService;
    }
    public void setUisTestService(UisTestService uisTestService) {
        this.uisTestService = uisTestService;
    }
    public void setBoostUadjustmentState(BoostUadjustmentState boostUadjustmentState) {
        this.boostUadjustmentState = boostUadjustmentState;
    }
    public void setStep3Model(Step3Model step3Model) {
        this.step3Model = step3Model;
    }
    public void setTabSectionModel(TabSectionModel tabSectionModel) {
        this.tabSectionModel = tabSectionModel;
    }
    public static ObservableList<Test> getListOfNonIncludedTests() {
        return listOfNonIncludedTests;
    }
    public void setPiezoRepairModel(PiezoRepairModel piezoRepairModel) {
        this.piezoRepairModel = piezoRepairModel;
    }
    public void setUisFlowModel(UisFlowModel uisFlowModel) {
        this.uisFlowModel = uisFlowModel;
    }
    public void setUisRlcModel(UisRlcModel uisRlcModel) {
        this.uisRlcModel = uisRlcModel;
    }
    public void setUisBipModel(UisBipModel uisBipModel) {
        this.uisBipModel = uisBipModel;
    }
    public void setTimingModelFactory(TimingModelFactory timingModelFactory) {
        this.timingModelFactory = timingModelFactory;
    }
    public void setTestManagerFactory(TestManagerFactory testManagerFactory) {
        this.testManagerFactory = testManagerFactory;
    }

    @PostConstruct
    public void init() {

        setupTimeProgressBars();
        bindingI18N();
        setupStoreButton();
        setupResetButton();
        setupPrintButton();
        initManufacturerContextMenu();
        initModelContextMenu();
        initTestContextMenu();
        setupSpeedComboBox();
        setupManufacturerListViewListener();
        setupSearchModelTextFieldListener();
        setupBaseTypeToggleGroupListener();
        setupModelListViewListener();
        setupTestListViewListener();
        setupTestsToggleGroupListener();
        setupMoveButtonEventHandlers();
        setupTestListAutoChangeListener();
        setupGuiTypeModelListener();
        setupCustomProducerDialogModelListener();
        setupCustomModelDialogModelListener();
//        setupInjectorSectionPwrButtonListener();
        setupStep3StartListener();
        setupStartButtonListener();
        setupPrintButton();
        testListView.setCellFactory(CheckBoxListCell.forListView(Test::includedProperty));
        showButtons(true, false);
        setupTimingsListeners();
    }

    public void setTestManager(TestManager testManager) {

        if (this.testManager != null) {
            startToggleButton.selectedProperty().removeListener(this.testManager);
        }
        this.testManager = testManager;
        startToggleButton.selectedProperty().addListener(testManager);
    }

    private void setupModelListViewListener() {

        modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(modelAlertProcessing)return;

            if (!uisFlowModel.getResultObservableMap().isEmpty() || !uisBipModel.getResultObservableMap().isEmpty()) {

                showAlert();
                if (alert.getResult() != ButtonType.YES) {

                    modelAlertProcessing = true;
                    modelListView.getSelectionModel().select(oldValue);
                    modelAlertProcessing = false;
                    return;
                }
            }

            clearAllResults();
            returnToDefaultTestListAuto();
            resetButton.fire();
            injectorNumberTextField.setText((newValue != null) ? (newValue).getModelCode() : null);

            if (newValue == null) {
                mainSectionUisModel.modelProperty().setValue(null);
                mainSectionUisModel.injectorTestProperty().setValue(null);
                showInjectorTests(false);
                showNode(false, codingTestRadioButton);
                testListView.getItems().clear();
                return;
            }
            //принудительно достаём ВАП через @EntityGraph,
            // т.к. при наполнении списка по выбранному ОЕМ ВАП не подгружается к каждому инжектору для экономии ресурсов
            VAP defaultVAP = modelService.findByModelCode(newValue.getModelCode()).getVAP();
            mainSectionUisModel.setModelIsChanging(true);
            newValue.setVAP(defaultVAP);
            mainSectionUisModel.modelProperty().setValue(newValue);
            fetchTestsFromRepository();
            showInjectorTests(true);
            showNode(checkInjectorForCoding(newValue.getCodetype()), codingTestRadioButton);
            mainSectionUisModel.setModelIsChanging(false);
        });
        step3Model.step3PauseProperty().addListener((observableValue, oldValue, newValue) -> modelListView.setDisable(newValue));
        piezoRepairModel.startMeasureProperty().addListener((observableValue, oldValue, newValue) -> modelListView.setDisable(newValue));

    }

    private void setupCustomProducerDialogModelListener() {

        customProducerDialogModel.doneProperty().addListener((observableValue, oldValue, newValue) -> {

            manufacturerListView.getItems().setAll(new ArrayList<>(producerService.findAll()));
            manufacturerListView.refresh();
            manufacturerListView.getSelectionModel().select(customProducerDialogModel.customProducerProperty().get());
            mainSectionUisModel.customProducerOperationProperty().setValue(null);
        });
        customProducerDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                mainSectionUisModel.customProducerOperationProperty().setValue(null));
    }

    private void setupCustomModelDialogModelListener() {

        customModelDialogModel.doneProperty().addListener((observableValue, oldValue, newValue) -> {

            setFilteredItems(manufacturerListView.getSelectionModel().getSelectedItem());
            mainSectionUisModel.customModelOperationProperty().setValue(null);
        });

        customModelDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                mainSectionUisModel.customModelOperationProperty().setValue(null));
    }

    private void clearAllResults() {

    }

    private void setupTestListViewListener() {

        testListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


            if (isFocusMoved)
                return;

            if (newValue == null) {
                return;
            }

            mainSectionUisModel.setTestIsChanging(true);   // for listener of width changes in the VoltAmpereProfileController blocking
            mainSectionUisModel.injectorTestProperty().set(newValue);

            if (autoTestRadioButton.isSelected()) {

                boolean startButtonNotSelected = !startToggleButton.isSelected();
                boolean notFirstTestSelected = testListView.getSelectionModel().getSelectedIndex() != 0;
                boolean testNotIncluded = listOfNonIncludedTests.contains(newValue);
                boolean boostUadjustmentRunning = boostUadjustmentState.boostUadjustmentStateProperty().get();

                showButtons(!testNotIncluded && startButtonNotSelected, false);
                enableUpDownButtons(testListView.getSelectionModel().getSelectedIndex(), testListView.getItems().size() - getListOfNonIncludedTests().size());

                disableNode(((startButtonNotSelected) && ((notFirstTestSelected) || testNotIncluded))
                        || boostUadjustmentRunning
                        || tabSectionModel.step3TabIsShowingProperty().get()
                        || tabSectionModel.piezoTabIsShowingProperty().get(), startToggleButton);

                /**MeasurementTime availability check below is necessary for further Pump_GUI covering - measurement period is not used for some Pump tests */
                Optional.ofNullable(newValue.getMeasurementTime()).ifPresentOrElse(initialTime
                        -> showMeasurementTime(true), () -> showMeasurementTime(false));
            }

            mainSectionUisModel.setTestIsChanging(false);
        });
        tabSectionModel.step3TabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> testListView.setDisable(newValue));
        tabSectionModel.piezoTabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> testListView.setDisable(newValue));
    }


    private void setupTimingsListeners() {

        adjustingTimeProperty.addListener((observableValue, oldValue, newValue)
                -> adjustingTime.refreshProgress(timingModel.getInitialAdjustingTime().get(), newValue.doubleValue()));

        measuringTimeProperty.addListener((observableValue, oldValue, newValue)
                -> measuringTime.refreshProgress(timingModel.getInitialMeasuringTime().get(), newValue.doubleValue()));
    }

    private void setupTestsToggleGroupListener() {

        testsToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == autoTestRadioButton) {
                mainSectionUisModel.testTypeProperty().setValue(AUTO);
                setTests();
            } else {
                mainSectionUisModel.testTypeProperty().setValue(newValue == testPlanTestRadioButton ? TESTPLAN : CODING);
                returnToDefaultTestListAuto();
                setTests();
                disableNode(boostUadjustmentState.boostUadjustmentStateProperty().get(), startToggleButton);
            }
            switch (mainSectionUisModel.testTypeProperty().get()) {
                case AUTO:
                    disableNode(false, testListView);
                    showButtons(true, false);
                    showNode(true, timingGridPane);
                    break;
                case TESTPLAN:
                    disableNode(false, testListView);
                    showButtons(false, true);
                    showNode(false, timingGridPane);
                    break;
                case CODING:
                    disableNode(true, testListView);
                    showButtons(false, true);
                    showNode(true, timingGridPane);
                    break;
            }
        });
        tabSectionModel.step3TabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> testsToggleGroupGridPane.setDisable(newValue));
        tabSectionModel.piezoTabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> testsToggleGroupGridPane.setDisable(newValue));
    }

    private void returnToDefaultTestListAuto() {

        isAnotherAutoOrNewTestList = true;

        if (!listOfNonIncludedTests.isEmpty()) {
            List<Test> temp = new ArrayList<>(listOfNonIncludedTests);
            temp.forEach(test -> test.includedProperty().set(true));
        }
        isAnotherAutoOrNewTestList = false;
    }

    private boolean checkInjectorForCoding(int codetype) {

        String manufacturerName = mainSectionUisModel.manufacturerObjectProperty().get().getManufacturerName();

        if (manufacturerName.equals("Bosch") || manufacturerName.equals("Denso") || manufacturerName.equals("Delphi") || manufacturerName.equals("Siemens"))
            return codetype != -1;
        else return false;
    }

    private void setupBaseTypeToggleGroupListener() {

        baseTypeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            Producer selectedItem = manufacturerListView.getSelectionModel().getSelectedItem();

            if (selectedItem == null)
                return;

            setFilteredItems(selectedItem);

        });
    }

    private void setupSearchModelTextFieldListener() {

        searchModelTextField.textProperty().addListener(((observable, oldValue, newValue) -> filteredModelList.setPredicate(data -> {

            if (newValue == null || newValue.isEmpty())
                return true;
            else {
                return (data.toString().contains(newValue.toUpperCase())
                        || data.toString().replace("-", "").contains(newValue.toUpperCase())
                        || data.toString().replace("#", "").contains(newValue.toUpperCase())
                        || data.toString().replaceFirst("-", "").contains(newValue.toUpperCase()));
            }
        })));
    }

    private void setupManufacturerListViewListener() {

        manufacturerListView.getItems().addListener((ListChangeListener<Producer>) change ->
                mainSectionUisModel.getProducerObservableList().setAll(manufacturerListView.getItems()));

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(oemAlertProcessing) return;

            if (!uisFlowModel.getResultObservableMap().isEmpty() || !uisBipModel.getResultObservableMap().isEmpty()) {

                showAlert();
                if (alert.getResult() != ButtonType.YES) {

                    oemAlertProcessing = true;
                    manufacturerListView.getSelectionModel().select(oldValue);
                    oemAlertProcessing = false;
                    return;
                }
            }

            resetButton.fire();
            disableNode(newValue == null, injectorsVBox);
            mainSectionUisModel.manufacturerObjectProperty().setValue(newValue);

            if (newValue != null && newValue.isCustom()) {
                defaultRadioButton.setDisable(true);
                customRadioButton.setSelected(true);
            } else
                defaultRadioButton.setDisable(false);

            setFilteredItems(newValue);
        });
        step3Model.step3PauseProperty().addListener((observableValue, oldValue, newValue) -> manufacturerListView.setDisable(newValue));
        piezoRepairModel.startMeasureProperty().addListener((observableValue, oldValue, newValue) -> manufacturerListView.setDisable(newValue));

    }

    //TODO uncomment upon replacement of MainSectionController by MainSectionUisController as unique for all GUI types
    private void setupStep3StartListener() {

//        step3Model.step3PauseProperty().addListener((observableValue, oldValue, newValue) -> {
//            if (newValue) {
//                testListView.getItems().stream().filter(test -> test.getTestName().toString().equals("Maximum Load")).findAny().ifPresent(test -> {
//                    testListView.getSelectionModel().select(test);
//                    testListView.scrollTo(test);
//                });
//            }
//        });
//        step3Model.pulseIsActiveProperty().addListener((observableValue, oldValue, newValue) -> {
//            if (!newValue) {
//                testListView.getSelectionModel().select(0);
//                testListView.scrollTo(0);
//            }
//        });
    }

    private void setFilteredItems(Producer producer) {

        List<? extends Model> modelsByManufacturers = modelService.findByProducerAndIsCustom(producer, customRadioButton.isSelected());
        ObservableList<Model> injectors = FXCollections.observableArrayList(modelsByManufacturers);
        filteredModelList = new FilteredList<>(injectors, model -> true);
        modelListView.setItems(filteredModelList);
    }

    private void fetchTestsFromRepository() {

        Task<List<? extends Test>> task = new Task<>() {
            @Override
            protected List<? extends Test> call() {
                return uisTestService.findAllByInjector(mainSectionUisModel.modelProperty().get());
            }
        };

        task.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {

                Model model = mainSectionUisModel.modelProperty().get();
                VAP defaultVAP = model.getVAP();
                newValue.stream().filter(t -> t.getVoltAmpereProfile() == null).forEach(t -> t.setVAP(defaultVAP));
                mainSectionUisModel.getTestObservableList().setAll(newValue);
                setTests();
            }
        });

        Thread t = new Thread(task);
        t.start();
    }

    private void pointToFirstTest() {

//        mainSectionUisModel.injectorTestProperty().setValue(null);
        testListView.getSelectionModel().select(0);
        testListView.scrollTo(0);
    }

    private void setTests() {

        Tests.TestType test = mainSectionUisModel.testTypeProperty().get();
        switch (test) {

            case AUTO:
                testListView.setCellFactory(CheckBoxListCell.forListView(Test::includedProperty));
                List<Test> injectorTests = mainSectionUisModel.getTestObservableList().
                        stream().
                        filter(injectorTest -> !injectorTest.getTestName().toString().equals("ISA Detection")).
                        collect(Collectors.toList());
                testListView.getItems().setAll(injectorTests);
                testListView.getItems().sort((Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getOrder())));
                Platform.runLater(this::pointToFirstTest);
                break;
            case TESTPLAN:
                testListView.setCellFactory(null);
                injectorTests = mainSectionUisModel.getTestObservableList().
                        stream().
                        filter(injectorTest -> !injectorTest.getTestName().toString().equals("ISA Detection")).
                        collect(Collectors.toList());
                testListView.getItems().setAll(injectorTests);
                testListView.getItems().sort((Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getOrder())));
                Platform.runLater(this::pointToFirstTest);
                break;
            case CODING:
                testListView.setCellFactory(null);
                testListView.getItems().setAll(mainSectionUisModel.getTestObservableList()
                        .stream()
                        .filter(injectorTest -> injectorTest.getTestName().getMeasurement() == DELIVERY || injectorTest.getTestName().getMeasurement() == VISUAL)
                        .sorted(Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getOrder()))
                        .collect(Collectors.toList()));
                Platform.runLater(this::pointToFirstTest);
                break;
        }
    }

    private void initManufacturerContextMenu() {

        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(actionEvent -> mainSectionUisModel.customProducerOperationProperty().setValue(Operation.NEW));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(actionEvent -> mainSectionUisModel.customProducerOperationProperty().setValue(Operation.DELETE));

        manufacturerListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manufacturerMenu.getItems().clear();
                manufacturerMenu.getItems().add(newManufacturer);
                if (mainSectionUisModel.manufacturerObjectProperty().get() != null &&
                        mainSectionUisModel.manufacturerObjectProperty().get().isCustom())
                    manufacturerMenu.getItems().addAll(deleteManufacturer);
                manufacturerMenu.show(manufacturerListView, event.getScreenX(), event.getScreenY());
            } else
                manufacturerMenu.hide();
        });

        customProducerDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                mainSectionUisModel.customProducerOperationProperty().setValue(null));
    }

    private void initModelContextMenu() {

        ContextMenu modelMenu = new ContextMenu();
        MenuItem newModel = new MenuItem("New");
        newModel.setOnAction(actionEvent -> mainSectionUisModel.customModelOperationProperty().setValue(Operation.NEW));
        MenuItem editModel = new MenuItem("Edit");
        editModel.setOnAction(actionEvent -> mainSectionUisModel.customModelOperationProperty().setValue(Operation.EDIT));
        MenuItem copyModel = new MenuItem("Copy");
        MenuItem deleteModel = new MenuItem("Delete");
        deleteModel.setOnAction(actionEvent -> mainSectionUisModel.customModelOperationProperty().setValue(Operation.DELETE));

        modelListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                modelMenu.getItems().clear();
                if (defaultRadioButton.isSelected()) {
                    modelMenu.getItems().add(copyModel);
                } else {
                    modelMenu.getItems().add(newModel);
                    if (modelListView.getSelectionModel().getSelectedItem() != null)
                        modelMenu.getItems().addAll(copyModel, editModel, deleteModel);
                }
                modelMenu.show(modelListView, event.getScreenX(), event.getScreenY());
            } else
                modelMenu.hide();
        });

        customModelDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                mainSectionUisModel.customModelOperationProperty().setValue(null));
    }

    private void initTestContextMenu() {

        ContextMenu testMenu = new ContextMenu();
        MenuItem newTest = new MenuItem("New");
        newTest.setOnAction(actionEvent -> mainSectionUisModel.customTestOperationProperty().setValue(Operation.NEW));
        MenuItem editTest = new MenuItem("Edit");
        editTest.setOnAction(actionEvent -> mainSectionUisModel.customTestOperationProperty().setValue(Operation.EDIT));
        MenuItem deleteTest = new MenuItem("Delete");
        deleteTest.setOnAction(actionEvent -> mainSectionUisModel.customTestOperationProperty().setValue(Operation.DELETE));


        testListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                testMenu.getItems().clear();

                testMenu.getItems().add(newTest);
                if (!defaultRadioButton.isSelected() && testListView.getSelectionModel().getSelectedItem() != null) {
                    testMenu.getItems().add(editTest);
                    testMenu.getItems().add(deleteTest);
                }
                testMenu.show(testListView, event.getScreenX(), event.getScreenY());
            } else
                testMenu.hide();
        });

        customTestDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                mainSectionUisModel.customTestOperationProperty().setValue(null));

        customTestDialogModel.doneProperty().addListener((observable, oldValue, newValue) -> {
            fetchTestsFromRepository();
            mainSectionUisModel.customTestOperationProperty().setValue(null);

        });
    }

    private void setupStartButtonListener() {

        mainSectionUisModel.startButtonProperty().bind(startToggleButton.selectedProperty());
        startToggleButton.visibleProperty().bind(uisRlcModel.isMeasuringProperty().not());
        Timeline startButtonTimeline = new Timeline(new KeyFrame(Duration.millis(400), event -> startBlinking()));
        startButtonTimeline.setCycleCount(Animation.INDEFINITE);

        startToggleButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue)
                startButtonTimeline.play();
            else {
                startButtonTimeline.stop();
                startToggleButton.getStyleClass().clear();
                startToggleButton.getStyleClass().add("startButton");
            }

            if (mainSectionUisModel.testTypeProperty().get() == AUTO) {

                boolean startButtonNotSelected = !newValue;
                boolean notFirstTestSelected = testListView.getSelectionModel().getSelectedIndex() != 0;

                disableNode(startButtonNotSelected && notFirstTestSelected, startToggleButton);
                disableNode(newValue, speedComboBox, testListView);
                showNode(!newValue, moveUpButton, moveDownButton);
            }
            disableRadioButtons(testsToggleGroup, newValue);
            disableRadioButtons(baseTypeToggleGroup, newValue);
            disableNode(newValue, manufacturerListView, injectorsVBox);
        });

        tabSectionModel.step3TabIsShowingProperty().addListener((observableValue, oldValue, newValue) ->
                startToggleButton.setDisable(newValue || boostUadjustmentState.boostUadjustmentStateProperty().get()));
        tabSectionModel.piezoTabIsShowingProperty().addListener((observableValue, oldValue, newValue) ->
                startToggleButton.setDisable(newValue || boostUadjustmentState.boostUadjustmentStateProperty().get()));
        boostUadjustmentState.boostUadjustmentStateProperty().addListener((observable, oldValue, newValue) ->
                disableNode(newValue
                                || tabSectionModel.step3TabIsShowingProperty().get()
                                || tabSectionModel.piezoTabIsShowingProperty().get(),
                        startToggleButton));
    }

    private void setupTimeProgressBars() {

        adjustingTime = new TimeProgressBar(adjustingTimeProgressBar, adjustingText);
        measuringTime = new TimeProgressBar(measuringTimeProgressBar, measuringText);
    }

    private void setupSpeedComboBox() {
        speedComboBox.getItems().setAll(TestSpeed.values());
        mainSectionUisModel.multiplierProperty().bind(speedComboBox.getSelectionModel().selectedItemProperty());
        speedComboBox.getSelectionModel().selectFirst();
    }

    private void setupGuiTypeModelListener() {

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (oldValue == UIS) {
                startToggleButton.setSelected(false);
            }

            /*TODO: add if-cases for other GUI types after implementation of this controller as unique for them and move outside of if-else:
            * updateTimingModelListeners(newValue)
            * manufacturerListView.getItems().setAll(.....)
            * manufacturerListView.refresh();*/
            if (newValue == UIS) {

                modelService = uisModelService;
                producerService = uisProducerService;
                testService = uisTestService;

                updateTimingModelListeners(newValue);
                manufacturerListView.getItems().setAll(new ArrayList<>(producerService.findAll()));
                manufacturerListView.refresh();
            }

            baseTypeToggleGroup.selectToggle(defaultRadioButton);

            if (testManager != null) {
                startToggleButton.selectedProperty().removeListener(testManager);
            }
            startToggleButton.selectedProperty().addListener(testManagerFactory.getTestManager(newValue));
        });

    }

    private void setupTestListAutoChangeListener(){

        listOfNonIncludedTests.addListener((ListChangeListener<? super Test>) change -> {

            if (isAnotherAutoOrNewTestList)
                return;

            testListView.getItems().sort((o1, o2) -> Boolean.compare(o2.includedProperty().get(), o1.includedProperty().get()));
            int includedQty = (int)testListView.getItems().stream().filter(t -> t.includedProperty().get()).count();
            testListView.getItems().subList(0, includedQty).sort(Comparator.comparingInt(Test::getId));
            testListView.scrollTo(0);
        });
    }

    private void updateTimingModelListeners(GUI_type guiType) {

        timingModel = timingModelFactory.getTimingModel(guiType);
        adjustingTimeProperty.bind(timingModel.getAdjustingTime());
        measuringTimeProperty.bind(timingModel.getMeasuringTime());
    }

    private  void setupResetButton() {
         resetButton.setOnAction(event -> flowModbusWriter.add(StartMeasurementCycle, true));
    }

    private void setupPrintButton() {
        printButton.setOnAction(new PrintButtonHandler(printStage, printDialogPanel));
    }

    private void setupStoreButton() {
        storeButton.setOnAction(actionEvent -> mainSectionUisModel.getStoreButton().fire());
    }

    private void showAlert() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);
            alert.initStyle(StageStyle.DECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);
        }
        ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());
        ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).textProperty().setValue(noButton.get());
        alert.setContentText(alertString.get());
        alert.showAndWait();
    }

    private void bindingI18N() {

        timingSpeedLabel.textProperty().bind(i18N.createStringBinding("main.timingSpeed.label"));
        labelAdjustTime.textProperty().bind(i18N.createStringBinding("main.adjusting.label"));
        labelMeasureTime.textProperty().bind(i18N.createStringBinding("main.measuring.label"));
        autoTestRadioButton.textProperty().bind(i18N.createStringBinding("main.auto.radiobutton"));
        testPlanTestRadioButton.textProperty().bind(i18N.createStringBinding("main.testPlan.radiobutton"));
        defaultRadioButton.textProperty().bind(i18N.createStringBinding("main.defaultRB.radiobutton"));
        customRadioButton.textProperty().bind(i18N.createStringBinding("main.customRB.radiobutton"));
        alertString.bind(i18N.createStringBinding("alert.oemOrModelChange"));
        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        noButton.bind((i18N.createStringBinding("alert.noButton")));
    }

    private void startBlinking() {

        startToggleButton.getStyleClass().clear();

        if (startLight) {
            startToggleButton.getStyleClass().add("stopButtonDark");
            startLight = false;
        } else {
            startToggleButton.getStyleClass().add("stopButtonLight");
            startLight = true;
        }
    }

    private void setupMoveButtonEventHandlers() {

        moveUpButton.setOnAction(event -> moveTest(UP));
        moveDownButton.setOnAction(event -> moveTest(DOWN));
    }

    private void moveTest(Move move) {

        int selectedTestIndex = testListView.getSelectionModel().getSelectedIndex();
        MultipleSelectionModel<Test> testSelectionModel = testListView.getSelectionModel();

        if (selectedTestIndex == -1)
            return;

        isFocusMoved = true;

        switch (move) {

            case UP:
                Collections.swap(testListView.getItems(), selectedTestIndex, selectedTestIndex - 1);
                testListView.getSelectionModel().select(selectedTestIndex - 1);
                testListView.scrollTo(testSelectionModel.getSelectedIndex());
                enableUpDownButtons(testSelectionModel.getSelectedIndex(), testListView.getItems().size() - getListOfNonIncludedTests().size());
                break;
            case DOWN:
                Collections.swap(testListView.getItems(), selectedTestIndex, selectedTestIndex + 1);
                testListView.getSelectionModel().select(selectedTestIndex + 1);
                testListView.scrollTo(testSelectionModel.getSelectedIndex());
                enableUpDownButtons(testSelectionModel.getSelectedIndex(), testListView.getItems().size() - getListOfNonIncludedTests().size());
                break;
        }
        disableNode(testListView.getSelectionModel().getSelectedIndex() != 0, startToggleButton);
        isFocusMoved = false;
    }

    //TODO do not forget to add UIS section started condition check ( + ||)
    private boolean isStarted() {
        return mainSectionUisModel.startButtonProperty().get();
    }

    private void showInjectorTests(boolean show) {

        Tests.TestType testType = mainSectionUisModel.testTypeProperty().get();
        showNode(show && (testType == AUTO), timingGridPane);
        showNode(show, injectorTestsVBox, startStackPane);
    }

    private void showMeasurementTime(boolean show) {

        labelMeasureTime.setVisible(show);
        measuringTimeProgressBar.setVisible(show);
        measuringText.setVisible(show);
        mainSectionUisModel.measurementTimeEnabledProperty().setValue(show);
    }

    private  void enableUpDownButtons(int selectedIndex, int includedTestsSize){

        if (selectedIndex == 0 && selectedIndex == includedTestsSize - 1)
            disableNode(true, moveUpButton, moveDownButton);
        else if (selectedIndex == 0) {
            disableNode(true, moveUpButton);
            disableNode(false, moveDownButton);
        } else if ((selectedIndex > 0) && (selectedIndex <= includedTestsSize - 2))
            disableNode(false, moveUpButton, moveDownButton);
        else {
            disableNode(false, moveUpButton);
            disableNode(true, moveDownButton);
        }
    }

    private void showButtons(boolean showUpDown, boolean showStoreReset){
        showNode(showUpDown, moveUpButton, moveDownButton);
        showNode(showStoreReset, storeButton, resetButton);
    }

    private void disableNode(boolean disable, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);
    }

    private void showNode(boolean show, Node... nodes) {
        for (Node node : nodes)
            node.setVisible(show);
    }

    private void disableRadioButtons(ToggleGroup targetToggleGroup, boolean disable) {
        targetToggleGroup.getToggles().stream()
                .filter(radioButton -> !radioButton.isSelected())
                .forEach(radioButton -> ((Node) radioButton).setDisable(disable));
    }
}
