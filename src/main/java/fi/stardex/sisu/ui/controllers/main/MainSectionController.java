package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.measurement.Measurements;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.persistence.Producer;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.ManufacturerHEUI;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.repos.HEUI.ManufacturerHeuiRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.BoostUadjustmentState;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.ui.controllers.additional.tabs.info.InfoController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditInjectorDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditTestDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.PrintDialogPanelController;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.enums.Move;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest.getListOfNonIncludedTests;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.UIS_to_CR_pulseControlSwitch;
import static fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController.State;
import static fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController.State.DELETE;
import static fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController.State.NEW;
import static fi.stardex.sisu.util.enums.Measurement.DELIVERY;
import static fi.stardex.sisu.util.enums.Measurement.VISUAL;
import static fi.stardex.sisu.util.enums.Move.DOWN;
import static fi.stardex.sisu.util.enums.Move.UP;
import static fi.stardex.sisu.util.enums.Tests.*;
import static fi.stardex.sisu.util.enums.Tests.TestType.*;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.setInjector;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorTestsObtainer.getInjectorTests;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorTestsObtainer.setInjectorTests;
import static fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer.getManufacturer;
import static fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer.setManufacturer;

public class MainSectionController {

    private static Logger logger = LoggerFactory.getLogger(MainSectionController.class);

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

    @FXML private ListView<Manufacturer> manufacturerListView;

    @FXML private TextField searchModelTextField;

    @FXML private ListView<Model> modelListView;

    @FXML private ListView<InjectorTest> testListView;

    @FXML private ComboBox<String> speedComboBox;

    @FXML private VBox injectorsVBox;

    private MultipleSelectionModel<InjectorTest> testsSelectionModel;

    private SingleSelectionModel<String> speedComboBoxSelectionModel;

    private ObservableList<InjectorTest> testListViewItems;

    private ObservableList<String> startToggleButtonStyleClass;

    private static final String NORMAL_SPEED = "X1";

    private static final String DOUBLE_SPEED = "X2";

    private static final String HALF_SPEED = "X0.5";

    private TimeProgressBar adjustingTime;

    private TimeProgressBar measuringTime;

    private int currentAdjustingTime;

    private int currentMeasuringTime;

    private Measurements measurements;

    private FlowReportModel flowReportModel;

    private ModbusRegisterProcessor flowModbusWriter;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private ViewHolder newEditInjectorDialog;

    private ViewHolder newEditTestDialog;

    private ViewHolder printDialogPanel;

    private InfoController infoController;

    private DelayController delayController;

    private DelayReportModel delayReportModel;

    private RLC_ReportModel rlc_reportModel;

    private CodingReportModel codingReportModel;

    private InjectorsRepository injectorsRepository;

    private InjectorTestRepository injectorTestRepository;

    private VoltAmpereProfile dafaultVoltAmpereProfile;

    private Stage modelDialogStage;

    private Stage testDialogStage;

    private FilteredList<Model> filteredModelList;

    private I18N i18N;

    private boolean startLight;

    private boolean isFocusMoved;

    private boolean isAnotherAutoOrNewTestList;

    private Stage printStage;

    private InjectorModel injectorModel;

    private BoostUadjustmentState boostUadjustmentState;

    private InjectorTestModel injectorTestModel;

    private ManufacturerRepository manufacturerRepository;

    private ManufacturerHeuiRepository manufacturerHeuiRepository;

    private GUI_TypeModel gui_typeModel;

    private ProducerRepositoryModel producerRepositoryModel;

    private ManufacturerMenuDialogModel manufacturerMenuDialogModel;

    private MainSectionModel mainSectionModel;

    private InjectorSectionPwrState injectorSectionPwrState;

    public ToggleGroup getTestsToggleGroup() {
        return testsToggleGroup;
    }

    public ObservableList<InjectorTest> getTestListViewItems() {
        return testListViewItems;
    }

    public MultipleSelectionModel<InjectorTest> getTestsSelectionModel() {
        return testsSelectionModel;
    }

    public ComboBox<String> getSpeedComboBox() {
        return speedComboBox;
    }

    public VBox getInjectorTestsVBox() {
        return injectorTestsVBox;
    }

    public ToggleGroup getBaseTypeToggleGroup() {
        return baseTypeToggleGroup;
    }

    public StackPane getStartStackPane() {
        return startStackPane;
    }

    public GridPane getTimingGridPane() {
        return timingGridPane;
    }

    public Button getMoveUpButton() {
        return moveUpButton;
    }

    public Button getMoveDownButton() {
        return moveDownButton;
    }

    public ListView<Model> getModelListView() {
        return modelListView;
    }

    public ModbusRegisterProcessor getFlowModbusWriter() {
        return flowModbusWriter;
    }

    public TimeProgressBar getAdjustingTime() {
        return adjustingTime;
    }

    public TimeProgressBar getMeasuringTime() {
        return measuringTime;
    }

    public ToggleButton getStartToggleButton() {
        return startToggleButton;
    }

    public Button getStoreButton() {
        return storeButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public ListView<Manufacturer> getManufacturerListView() {
        return manufacturerListView;
    }

    public RadioButton getCodingTestRadioButton() {
        return codingTestRadioButton;
    }

    public ListView<InjectorTest> getTestListView() {
        return testListView;
    }

    public VBox getInjectorsVBox() {
        return injectorsVBox;
    }

    public void setNewEditInjectorDialog(ViewHolder newEditInjectorDialog) {
        this.newEditInjectorDialog = newEditInjectorDialog;
    }

    public void setNewEditTestDialog(ViewHolder newEditTestDialog) {
        this.newEditTestDialog = newEditTestDialog;
    }

    public void setPrintDialogPanel(ViewHolder printDialogPanel) {
        this.printDialogPanel = printDialogPanel;
    }

    public void setInjectorsRepository(InjectorsRepository injectorsRepository) {
        this.injectorsRepository = injectorsRepository;
    }

    public void setInjectorTestRepository(InjectorTestRepository injectorTestRepository) {
        this.injectorTestRepository = injectorTestRepository;
    }

    public void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }

    public void setFlowReportModel(FlowReportModel flowReportModel) {
        this.flowReportModel = flowReportModel;
    }

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }

    public void setInfoController(InfoController infoController) {
        this.infoController = infoController;
    }

    public void setDelayController(DelayController delayController) {
        this.delayController = delayController;
    }

    public void setDelayReportModel(DelayReportModel delayReportModel) {
        this.delayReportModel = delayReportModel;
    }

    public void setRlc_reportModel(RLC_ReportModel rlc_reportModel) {
        this.rlc_reportModel = rlc_reportModel;
    }

    public void setCodingReportModel(CodingReportModel codingReportModel) {
        this.codingReportModel = codingReportModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setBoostUadjustmentState(BoostUadjustmentState boostUadjustmentState) {
        this.boostUadjustmentState = boostUadjustmentState;
    }

    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    public void setInjectorModel(InjectorModel injectorModel) {
        this.injectorModel = injectorModel;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setManufacturerRepository(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public void setManufacturerHeuiRepository(ManufacturerHeuiRepository manufacturerHeuiRepository) {
        this.manufacturerHeuiRepository = manufacturerHeuiRepository;
    }

    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    public void setProducerRepositoryModel(ProducerRepositoryModel producerRepositoryModel) {
        this.producerRepositoryModel = producerRepositoryModel;
    }

    public void setManufacturerMenuDialogModel(ManufacturerMenuDialogModel manufacturerMenuDialogModel) {
        this.manufacturerMenuDialogModel = manufacturerMenuDialogModel;
    }

    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }

    public void setInjectorSectionPwrState(InjectorSectionPwrState injectorSectionPwrState) {
        this.injectorSectionPwrState = injectorSectionPwrState;
    }

    @PostConstruct
    private void init() {

        this
                .makeReferenceToInternalObjects()
                .setupTimeProgressBars()
                .bindingI18N()
                .setupStoreButton()
                .setupResetButton()
                .initManufacturerContextMenu()
                .initModelContextMenu()
                .initTestContextMenu()
                .setupSpeedComboBox()
                .setupManufacturerListViewListener()
                .setupSearchModelTextFieldListener()
                .setupBaseTypeToggleGroupListener()
                .setupModelListViewListener()
                .setupTestListViewListener()
                .setupTestsToggleGroupListener()
                .setupMoveButtonEventHandlers()
                .setupTestListAutoChangeListenerNew();

        setupGUI_typeModelListener();
        setupManufacturerMenuDialogModelListener();
        setupManufacturerListListener();
        setupInjectorSectionPwrButtonListener();
        setupStartButtonListener();
        printButton.setOnAction(new PrintButtonEventHandler());
        boostUadjustmentState.boostUadjustmentStateProperty().addListener((observable, oldValue, newValue) -> disableNode(newValue, startToggleButton));

        testListView.setCellFactory(CheckBoxListCell.forListView(InjectorTest::includedProperty));
        showButtons(true, false);
    }

    private void setHEUIManufacturerListView() {

        Iterable<ManufacturerHEUI> manufacturers = manufacturerHeuiRepository.findAll();
        setManufacturersListView(manufacturers);
    }

    private void setCRManufacturerListView() {

        Iterable<Manufacturer> manufacturers = manufacturerRepository.findAll();
        setManufacturersListView(manufacturers);
    }

    private void setManufacturersListView(Iterable<? extends Producer> manufacturers) {

        List<Manufacturer> listOfManufacturers = new ArrayList<>();
        StreamSupport.stream(manufacturers.spliterator(), false).map(this::convert).forEach(listOfManufacturers::add);
        manufacturerListView.getItems().setAll(listOfManufacturers);
        manufacturerListView.refresh();
    }

    private Manufacturer convert(Producer producer) {

        Manufacturer p = new Manufacturer();
        p.setManufacturerName(producer.getManufacturerName());
        p.setCustom(producer.isCustom());
        return p;
    }

    private void setupGUI_typeModelListener() {

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            manufacturerListView.getSelectionModel().clearSelection();
            baseTypeToggleGroup.selectToggle(defaultRadioButton);
            if (newValue == GUI_TypeController.GUIType.HEUI) {
                setHEUIManufacturerListView();
            } else if (newValue == GUI_TypeController.GUIType.CR_Inj) {
                setCRManufacturerListView();
            }
        });
    }

    private void setupManufacturerMenuDialogModelListener() {

        manufacturerMenuDialogModel.doneProperty().addListener((observable, oldValue, newValue) -> {
//        manufacturerMenuDialogModel.customManufacturerProperty().addListener((observableValue, oldValue, newValue) -> {
//            Iterable<Manufacturer> manufacturers = null;

            switch (gui_typeModel.guiTypeProperty().get()) {
                case CR_Inj:
                    setCRManufacturerListView();
                    break;
                case HEUI:
                    setHEUIManufacturerListView();
            }
//            Iterable<Manufacturer> manufacturers = manufacturerRepository.findAll();
//            setManufacturersListView(manufacturers);
            manufacturerListView.getSelectionModel().select(manufacturerMenuDialogModel.customManufacturerProperty().get());
            mainSectionModel.makeOrDelProducerProperty().setValue(null);
        });
    }

    private void setupManufacturerListListener() {

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                mainSectionModel.manufacturerObjectProperty().setValue(newValue));

        manufacturerListView.getItems().addListener((ListChangeListener<Manufacturer>) change ->
                mainSectionModel.getManufacturerObservableList().setAll(manufacturerListView.getItems()));
    }

    private MainSectionController makeReferenceToInternalObjects() {

        testsSelectionModel = testListView.getSelectionModel();
        testListViewItems = testListView.getItems();
        startToggleButtonStyleClass = startToggleButton.getStyleClass();
        speedComboBoxSelectionModel = speedComboBox.getSelectionModel();
        return this;
    }

    private MainSectionController setupTimeProgressBars() {

        adjustingTime = new TimeProgressBar(adjustingTimeProgressBar, adjustingText);
        measuringTime = new TimeProgressBar(measuringTimeProgressBar, measuringText);
        return this;
    }

    private MainSectionController bindingI18N() {

        timingSpeedLabel.textProperty().bind(i18N.createStringBinding("main.timingSpeed.label"));
        labelAdjustTime.textProperty().bind(i18N.createStringBinding("main.adjusting.label"));
        labelMeasureTime.textProperty().bind(i18N.createStringBinding("main.measuring.label"));
        autoTestRadioButton.textProperty().bind(i18N.createStringBinding("main.auto.radiobutton"));
        testPlanTestRadioButton.textProperty().bind(i18N.createStringBinding("main.testPlan.radiobutton"));
        codingTestRadioButton.textProperty().bind(i18N.createStringBinding("main.coding.radiobutton"));
        defaultRadioButton.textProperty().bind(i18N.createStringBinding("main.defaultRB.radiobutton"));
        customRadioButton.textProperty().bind(i18N.createStringBinding("main.customRB.radiobutton"));
        return this;
    }

    private MainSectionController setupStoreButton() {

        storeButton.setOnAction((event) -> flowReportModel.storeResult());
        return this;
    }

    private void setFilteredItems(Manufacturer manufacturer) {

//        List<Injector> modelsByManufacturers = injectorsRepository.findByManufacturerAndIsCustom(manufacturer, customRadioButton.isSelected());
        List<Injector> modelsByManufacturers = injectorsRepository.findByManufacturerAndIsCustomAndIsHeui(
                manufacturer,
                customRadioButton.isSelected(),
                gui_typeModel.guiTypeProperty().get() == GUI_TypeController.GUIType.HEUI);

        ObservableList<Model> injectors = FXCollections.observableArrayList(modelsByManufacturers);
        filteredModelList = new FilteredList<>(injectors, model -> true);
        modelListView.setItems(filteredModelList);
    }

    private void fetchTestsFromRepository() {


        Task<List<InjectorTest>> task = new Task<>() {
            @Override
            protected List<InjectorTest> call() {
                return injectorTestRepository.findAllByInjector(injectorModel.injectorProperty().get());
            }
        };


        task.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {

                Injector injector = injectorModel.injectorProperty().get();
                VoltAmpereProfile defaultVAP = injector.getVoltAmpereProfile();
                newValue.stream().filter(t -> t.getVoltAmpereProfile() == null).forEach(t -> t.setVoltAmpereProfile(defaultVAP));
                setInjectorTests(newValue);

                if (!checkInjectorForCoding(injector.getCodetype()) && codingTestRadioButton.isSelected()) {

                    autoTestRadioButton.setSelected(true);
                }else{ setTests(); }
            }
        });
        Thread t = new Thread(task);
        t.start();
    }

    public void pointToFirstTest() {

        testsSelectionModel.select(0);
        testListView.scrollTo(0);
    }

    private void setTests() {

        TestType test = mainSectionModel.testTypeProperty().get();

        switch (test) {

            case AUTO:
                testListView.setCellFactory(CheckBoxListCell.forListView(InjectorTest::includedProperty));
                List<InjectorTest> injectorTests = getInjectorTests().
                        stream().
                        filter(injectorTest -> !injectorTest.getTestName().toString().equals("ISA Detection")).
                        collect(Collectors.toList());
                testListViewItems.setAll(injectorTests);
                testListViewItems.sort((Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getId())));
                Platform.runLater(this::pointToFirstTest);
                break;
            case TESTPLAN:
                testListView.setCellFactory(null);
                injectorTests = getInjectorTests().
                        stream().
                        filter(injectorTest -> !injectorTest.getTestName().toString().equals("ISA Detection")).
                        collect(Collectors.toList());
                testListViewItems.setAll(injectorTests);
                testListViewItems.sort((Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getId())));
                Platform.runLater(this::pointToFirstTest);
                break;
            case CODING:
                testListView.setCellFactory(null);
                testListViewItems.setAll(getInjectorTests()
                        .stream()
                        .filter(injectorTest -> injectorTest.getTestName().getMeasurement() == DELIVERY || injectorTest.getTestName().getMeasurement() == VISUAL)
                        .sorted(Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getDisplayOrder()))
                        .collect(Collectors.toList()));
                Platform.runLater(this::pointToFirstTest);
                break;
        }
    }

    private MainSectionController initManufacturerContextMenu() {

        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(new ManufacturerMenuEventHandler(NEW));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(new ManufacturerMenuEventHandler(DELETE));

        manufacturerListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manufacturerMenu.getItems().clear();
                manufacturerMenu.getItems().add(newManufacturer);
                if (getManufacturer() != null &&
                        getManufacturer().isCustom())
                    manufacturerMenu.getItems().addAll(deleteManufacturer);
                manufacturerMenu.show(manufacturerListView, event.getScreenX(), event.getScreenY());
            } else
                manufacturerMenu.hide();
        });
        return this;
    }

    private MainSectionController initModelContextMenu() {

        ContextMenu modelMenu = new ContextMenu();
        MenuItem newModel = new MenuItem("New");
        newModel.setOnAction(new ModelMenuEventHandler("New injector", NewEditInjectorDialogController::setNew));
        MenuItem editModel = new MenuItem("Edit");
        editModel.setOnAction(new ModelMenuEventHandler("Edit injector", NewEditInjectorDialogController::setEdit));
        MenuItem copyModel = new MenuItem("Copy");
        MenuItem deleteModel = new MenuItem("Delete");
        deleteModel.setOnAction(new ModelMenuEventHandler("Delete injector", NewEditInjectorDialogController::setDelete));

        //TODO
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
        return this;
    }

    private MainSectionController initTestContextMenu() {

        ContextMenu testMenu = new ContextMenu();
        MenuItem newTest = new MenuItem("New");
        newTest.setOnAction(new TestMenuEventHandler("New test", NewEditTestDialogController::setNew));
        MenuItem editTest = new MenuItem("Edit");
        editTest.setOnAction(new TestMenuEventHandler("Edit test", NewEditTestDialogController::setEdit));
        MenuItem deleteTest = new MenuItem("Delete");
        deleteTest.setOnAction(new TestMenuEventHandler("Delete test", NewEditTestDialogController::setDelete));
        testMenu.getItems().add(newTest);

        testListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                testMenu.show(testListView, event.getScreenX(), event.getScreenY());
            } else
                testMenu.hide();
        });
        return this;
    }

    private MainSectionController setupSpeedComboBox() {

        speedComboBox.getItems().setAll(NORMAL_SPEED, DOUBLE_SPEED, HALF_SPEED);
        speedComboBoxSelectionModel.selectFirst();
        speedComboBoxSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> setProgress(newValue));
        return this;
    }

    private class ManufacturerMenuEventHandler implements EventHandler<ActionEvent> {

        private State state;

        public ManufacturerMenuEventHandler(State state) {
            this.state = state;
        }

        @Override
        public void handle(ActionEvent event) {
            mainSectionModel.makeOrDelProducerProperty().setValue(state);
        }
    }

    private class ModelMenuEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<NewEditInjectorDialogController> dialogType;

        public ModelMenuEventHandler(String title, Consumer<NewEditInjectorDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            NewEditInjectorDialogController controller = (NewEditInjectorDialogController) newEditInjectorDialog.getController();
            if (modelDialogStage == null) {
                modelDialogStage = new Stage();
                modelDialogStage.setScene(new Scene(newEditInjectorDialog.getView()));
                modelDialogStage.setResizable(false);
                modelDialogStage.initModality(Modality.APPLICATION_MODAL);
                controller.setStage(modelDialogStage);
                controller.setModelListView(modelListView);
            }
            modelDialogStage.setTitle(title);
            dialogType.accept(controller);
            modelDialogStage.show();
        }
    }

    private class TestMenuEventHandler implements EventHandler<ActionEvent> {

        private String title;
        private Consumer<NewEditTestDialogController> dialogType;

        public TestMenuEventHandler(String title, Consumer<NewEditTestDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {

            NewEditTestDialogController controller = (NewEditTestDialogController) newEditTestDialog.getController();
            if (testDialogStage == null) {
                testDialogStage = new Stage();
                testDialogStage.setScene(new Scene(newEditTestDialog.getView()));
                testDialogStage.setResizable(false);
                testDialogStage.initModality(Modality.APPLICATION_MODAL);
                controller.setStage(testDialogStage);
                controller.setTestListView(testListView);
            }
            testDialogStage.setTitle(title);
            dialogType.accept(controller);
            testDialogStage.show();
        }
    }

    public class TimeProgressBar {

        private ProgressBar progressBar;
        private Text text;
        private int initialTime;

        TimeProgressBar(ProgressBar progressBar, Text text) {
            this.progressBar = progressBar;
            this.text = text;
        }

        private void setProgress(int time) {
            this.initialTime = time;
            text.setText(String.valueOf(initialTime));
            progressBar.setProgress(initialTime == 0 ? 0 : 1);
        }

        public void refreshProgress() {
            setProgress(initialTime);
        }

        public int tick() {

            int time = Integer.valueOf(text.getText());

            if (time > 0) {
                text.setText(String.valueOf(--time));
                progressBar.setProgress((float) time / (float) initialTime);
            }
            return time;
        }
    }

    private MainSectionController setupResetButton() {

        resetButton.setOnAction(event -> flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true));
        return this;
    }

    private void setProgress(String ratio) {

        switch (ratio) {
            case NORMAL_SPEED:
                adjustingTime.setProgress(currentAdjustingTime);
                measuringTime.setProgress(currentMeasuringTime);
                break;
            case DOUBLE_SPEED:
                adjustingTime.setProgress(currentAdjustingTime / 2);
                measuringTime.setProgress(currentMeasuringTime / 2);
                break;
            case HALF_SPEED:
                adjustingTime.setProgress(currentAdjustingTime * 2);
                measuringTime.setProgress(currentMeasuringTime * 2);
                break;
            default:
                logger.error("Wrong speed argument!");
                break;
        }
    }

    private MainSectionController setupManufacturerListViewListener() {

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {

            ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, 0);
            resetButton.fire();
            disableNode(newValue == null, injectorsVBox);
            setManufacturer(newValue);
            infoController.changeToDefault();

            delayReportModel.clearResults();
            rlc_reportModel.clearResults();
            flowReportModel.clearResults();
            codingReportModel.clearResults();

            if (newValue != null && newValue.isCustom()) {
                defaultRadioButton.setDisable(true);
                customRadioButton.setSelected(true);
            } else
                defaultRadioButton.setDisable(false);

            setFilteredItems(newValue);
        }));
        return this;
    }

    private MainSectionController setupSearchModelTextFieldListener() {

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
        return this;
    }

    private MainSectionController setupBaseTypeToggleGroupListener() {

        baseTypeToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {

            Manufacturer selectedItem = manufacturerListView.getSelectionModel().getSelectedItem();

            if (selectedItem == null)
                return;

            setFilteredItems(selectedItem);
        }));
        return this;
    }

    private MainSectionController setupModelListViewListener() {

        modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {



            returnToDefaultTestListAuto();
            resetButton.fire();
            delayReportModel.clearResults();
            rlc_reportModel.clearResults();
            flowReportModel.clearResults();
            codingReportModel.clearResults();
            injectorNumberTextField.setText((newValue != null) ? ((Injector) newValue).getInjectorCode() : null);

            if (newValue == null) {
                setInjector(null);
                setInjectorTests(null);
                injectorModel.injectorProperty().setValue(null);
                mainSectionModel.injectorProperty().setValue(null);     // in future replace above line
                injectorTestModel.injectorTestProperty().setValue(null);
                mainSectionModel.injectorTestProperty().setValue(null); // in future replace above line
                showInjectorTests(false);
                showNode(false, codingTestRadioButton);
                testListViewItems.clear();
                return;
            }

            Injector injector = (Injector) newValue;

            dafaultVoltAmpereProfile = injectorsRepository.findByInjectorCode(injector.getInjectorCode()).getVoltAmpereProfile();
            injectorModel.setInjectorIsChanging(true);              // for listener of width changes in the VoltAmpereProfileController blocking
            mainSectionModel.setInjectorIsChanging(true);           // in future replace above line
            injector.setVoltAmpereProfile(dafaultVoltAmpereProfile);
            injectorModel.injectorProperty().setValue(injector);
            mainSectionModel.injectorProperty().setValue(injector); // in future replace above line
            setInjector(injector);

            fetchTestsFromRepository();
            showInjectorTests(true);
            showNode(checkInjectorForCoding(injector.getCodetype()), codingTestRadioButton);

            String manufacturerName = injector.getManufacturer().getManufacturerName();
            switch (manufacturerName) {
                case "Bosch":
                    infoController.changeToBosch();
                    break;
                case "Denso":
                    infoController.changgeToDenso();
                    break;
                case "Delphi":
                    infoController.changeToDelphi();
                    break;
                case "Caterpillar":
                    infoController.changeToCaterpillar();
                    break;
                case "Siemens":
                    infoController.changeToSiemens();
                    break;
                case "AZPI":
                    infoController.changeToAZPI();
                    break;
                default:
                    infoController.changeToDefault();
                    break;
            }
            injectorModel.setInjectorIsChanging(false);
            mainSectionModel.setInjectorIsChanging(false);  // in future replace above line
        });
        return this;
    }

    private MainSectionController setupTestListViewListener() {

        testsSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (isFocusMoved)
                return;

            if (newValue == null) {
                return;
            }

            injectorTestModel.setTestIsChanging(true);  // for listener of width changes in the VoltAmpereProfileController blocking
            mainSectionModel.setTestIsChanging(true);   // in future replace above line
            injectorTestModel.injectorTestProperty().set(newValue);
            mainSectionModel.injectorTestProperty().set(newValue);  // in future replace above line

            if (autoTestRadioButton.isSelected()) {

                boolean startButtonNotSelected = !startToggleButton.isSelected();
                boolean notFirstTestSelected = testsSelectionModel.getSelectedIndex() != 0;
                boolean testNotIncluded = !newValue.isIncluded();
                boolean boostUadjustmentRunning = boostUadjustmentState.boostUadjustmentStateProperty().get();

                showButtons(!testNotIncluded && startButtonNotSelected, false);
                enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - getListOfNonIncludedTests().size());

                disableNode(((startButtonNotSelected) && ((notFirstTestSelected) || testNotIncluded)) || boostUadjustmentRunning, startToggleButton);

            } else if (testPlanTestRadioButton.isSelected() && startToggleButton.isSelected()) {

                measurements.switchOffInjectorSection();
                measurements.start();
            }

            currentAdjustingTime = newValue.getAdjustingTime();
//            currentAdjustingTime = 5;

            currentMeasuringTime = newValue.getMeasurementTime();
//            currentMeasuringTime = 5;

            setProgress(speedComboBoxSelectionModel.getSelectedItem());

            Measurement measurementType = newValue.getTestName().getMeasurement();

            delayController.clearDelayResults();

            switch (measurementType) {

                case NO:
                    delayController.getSaveDelayButton().setDisable(true);
                    break;
                default:
                    delayController.setInjectorTestName(newValue.getTestName().toString());
                    delayController.getSaveDelayButton().setDisable(false);
                    break;
            }

            injectorTestModel.setTestIsChanging(false);
            mainSectionModel.setTestIsChanging(false);  // in future replace above line
        });
        return this;
    }

    private MainSectionController setupTestsToggleGroupListener() {

        testsToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == autoTestRadioButton) {
//                setTestType(AUTO);  // можно удалить, класс Tests больше не используется
                mainSectionModel.testTypeProperty().setValue(AUTO);
                setTests();
            } else {
//                setTestType(newValue == testPlanTestRadioButton ? TESTPLAN : CODING);   // можно удалить, класс Tests больше не используется
                mainSectionModel.testTypeProperty().setValue(newValue == testPlanTestRadioButton ? TESTPLAN : CODING);
                returnToDefaultTestListAuto();
                setTests();
                disableNode(boostUadjustmentState.boostUadjustmentStateProperty().get(), startToggleButton);
            }
//            enabler.selectTestType();
            switch (mainSectionModel.testTypeProperty().get()) {
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
        return this;
    }

    private void setupInjectorSectionPwrButtonListener() {

        injectorSectionPwrState.powerButtonProperty().addListener((observableValue, oldValue, newValue) -> {
            if (CurrentInjectorObtainer.getInjector() == null) {
                disableNode(newValue, injectorsVBox);
            }
            disableNode(isStarted(), injectorsVBox, manufacturerListView);
        });
    }

    private void setupStartButtonListener() {

        mainSectionModel.startButtonProperty().bind(startToggleButton.selectedProperty());

        Timeline startButtonTimeline = new Timeline(new KeyFrame(Duration.millis(400), event -> startBlinking()));
        startButtonTimeline.setCycleCount(Animation.INDEFINITE);

        startToggleButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue)
                startButtonTimeline.play();
            else {
                startButtonTimeline.stop();
                startToggleButtonStyleClass.clear();
                startToggleButtonStyleClass.add("startButton");
            }

            if (mainSectionModel.testTypeProperty().get() == AUTO) {

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
    }

    private void startBlinking() {

        startToggleButtonStyleClass.clear();

        if (startLight) {
            startToggleButtonStyleClass.add("stopButtonDark");
            startLight = false;
        } else {
            startToggleButtonStyleClass.add("stopButtonLight");
            startLight = true;
        }
    }

    private MainSectionController setupMoveButtonEventHandlers() {

        moveUpButton.setOnAction(event -> moveTest(UP));
        moveDownButton.setOnAction(event -> moveTest(DOWN));
        return this;
    }

    private void moveTest(Move move) {

        int selectedTestIndex = testsSelectionModel.getSelectedIndex();

        if (selectedTestIndex == -1)
            return;

        isFocusMoved = true;

        switch (move) {

            case UP:
                Collections.swap(testListViewItems, selectedTestIndex, selectedTestIndex - 1);
                testsSelectionModel.select(selectedTestIndex - 1);
                testListView.scrollTo(testsSelectionModel.getSelectedIndex());
                enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - getListOfNonIncludedTests().size());
                break;
            case DOWN:
                Collections.swap(testListViewItems, selectedTestIndex, selectedTestIndex + 1);
                testsSelectionModel.select(selectedTestIndex + 1);
                testListView.scrollTo(testsSelectionModel.getSelectedIndex());
                enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - getListOfNonIncludedTests().size());
                break;
        }
        disableNode(testsSelectionModel.getSelectedIndex() != 0, startToggleButton);
        isFocusMoved = false;
    }

    //Old version of resorting upon checkBox check/uncheck - original included tests order was not restored after checkBox check/uncheck
//    private void setupTestListAutoChangeListener() {
//
//        getListOfNonIncludedTests().addListener((ListChangeListener<? super InjectorTest>) change -> {
//
//            if (isAnotherAutoOrNewTestList)
//                return;
//
//            int index = testListViewItems.indexOf(getChangedInjectorTest());
//
//            while (change.next()) {
//
//                if (change.wasAdded()) {
//
//                    int lastIndex = testListViewItems.size() - 1;
//
//                    if (index == lastIndex)
//                        enabler.showButtons(false, false);
//
//                    for (int i = index; i < lastIndex; i++) {
//                        Collections.swap(testListViewItems, i, i + 1);
//                    }
//
//                } else if (change.wasRemoved()) {
//
//                    if (index == 0)
//                        enabler.showButtons(true, false);
//
//                    for (int i = index; i > 0; i--) {
//                        Collections.swap(testListViewItems, i, i - 1);
//                    }
//
//                }
//
//            }
//            enabler.enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - change.getList().size());
//        });
//    }
    
    //TODO FIXME - after MVC-refactoring use AutoTestLastChangeModel approach similarly to pump section
    private void setupTestListAutoChangeListenerNew(){

        getListOfNonIncludedTests().addListener((ListChangeListener<? super InjectorTest>) change -> {

            if (isAnotherAutoOrNewTestList)
                return;

            testListView.getItems().sort((o1, o2) -> Boolean.compare(o2.includedProperty().get(), o1.includedProperty().get()));
            int includedQty = (int)testListView.getItems().stream().filter(t -> t.includedProperty().get()).count();
            testListView.getItems().subList(0, includedQty).sort(Comparator.comparingInt(InjectorTest::getId));
            testListView.scrollTo(0);
        });
    }

    private void returnToDefaultTestListAuto() {

        isAnotherAutoOrNewTestList = true;

        ObservableList<InjectorTest> listOfNonIncludedTests = getListOfNonIncludedTests();
        if (!listOfNonIncludedTests.isEmpty()) {
            List<InjectorTest> temp = new ArrayList<>(listOfNonIncludedTests);
            temp.forEach(injectorTest -> injectorTest.setIncluded(true));
        }
        isAnotherAutoOrNewTestList = false;
    }

    private boolean checkInjectorForCoding(int codetype) {

        String manufacturerName = getManufacturer().getManufacturerName();

        if (manufacturerName.equals("Bosch") || manufacturerName.equals("Denso") || manufacturerName.equals("Delphi") || manufacturerName.equals("Siemens"))
            return codetype != -1;
        else return false;
    }

    private class PrintButtonEventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            if (printStage == null) {
                printStage = new Stage();
                printStage.setTitle("PDF export");
                printStage.setResizable(false);
                printStage.initModality(Modality.APPLICATION_MODAL);
                ((PrintDialogPanelController) printDialogPanel.getController()).setStage(printStage);
            }
            /* Check is necessary due to possibility of dialog window invocation from different controllers.
                In this case we need to avoid repeated set of printDialogPanel as root of another new scene -
                IllegalArgumentException: .... is already set as root of another scene. */
            if (printDialogPanel.getView().getScene() == null) {
                printStage.setScene(new Scene(printDialogPanel.getView()));
            }else{
                printStage.setScene(printDialogPanel.getView().getScene());
            }
            printStage.show();
        }
    }

    private boolean isStarted() {
        return mainSectionModel.startButtonProperty().get() || injectorSectionPwrState.powerButtonProperty().get();
    }

    private void showInjectorTests(boolean show) {

        TestType testType = mainSectionModel.testTypeProperty().get();
        showNode(show && (testType == AUTO || testType == CODING), timingGridPane);
        showNode(show, injectorTestsVBox, startStackPane);
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
