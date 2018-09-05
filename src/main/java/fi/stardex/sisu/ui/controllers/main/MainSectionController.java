package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditInjectorDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditTestDialogController;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentInjectorTestsObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import java.util.prefs.Preferences;

import static fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest.getChangedInjectorTest;
import static fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest.getListOfNonIncludedTests;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.Tests.TestType.*;

public class MainSectionController {

    private static Logger logger = LoggerFactory.getLogger(MainSectionController.class);

    @FXML
    private VBox injectorTestsVBox;

    @FXML
    private GridPane timingGridPane;

    @FXML
    private HBox startHBox;

    @FXML
    private Button moveUpButton;

    @FXML
    private Button moveDownButton;

    private enum Move {
        UP, DOWN
    }

    @FXML
    private Label timingSpeedLabel;

    @FXML
    private ToggleButton startToggleButton;

    @FXML
    private ProgressBar adjustingTimeProgressBar;

    @FXML
    private ProgressBar measuringTimeProgressBar;

    @FXML
    private Text adjustingText;

    @FXML
    private Text measuringText;

    @FXML
    private Label labelAdjustTime;

    @FXML
    private Label labelMeasureTime;

    @FXML
    private Button storeButton;

    @FXML
    private Button resetButton;

    @FXML
    private ToggleGroup testsToggleGroup;

    @FXML
    private RadioButton testPlanTestRadioButton;

    @FXML
    private RadioButton autoTestRadioButton;

    @FXML
    private RadioButton codingTestRadioButton;

    @FXML
    private ToggleGroup baseTypeToggleGroup;

    @FXML
    private RadioButton defaultRadioButton;

    @FXML
    private RadioButton customRadioButton;

    @FXML
    private ComboBox<GUIType> versionComboBox;

    public enum GUIType {
        CR_Inj, CR_Pump, UIS
    }

    @FXML
    private ListView<Manufacturer> manufacturerListView;

    @FXML
    private TextField searchModelTextField;

    @FXML
    private ListView<Model> modelListView;

    @FXML
    private ListView<InjectorTest> testListView;

    @FXML
    private ComboBox<String> speedComboBox;

    private MultipleSelectionModel<InjectorTest> testsSelectionModel;

    private ObservableList<InjectorTest> testListViewItems;

    private static final String NORMAL_SPEED = "X1";

    private static final String DOUBLE_SPEED = "X2";

    private static final String HALF_SPEED = "X0.5";

    private TimeProgressBar adjustingTime;

    private TimeProgressBar measuringTime;

    private int currentAdjustingTime;

    private int currentMeasuringTime;

    private Preferences rootPrefs;

    private ModbusRegisterProcessor flowModbusWriter;

    private Enabler enabler;

    private CurrentManufacturerObtainer currentManufacturerObtainer;

    private ApplicationAppearanceChanger applicationAppearanceChanger;

    private ViewHolder manufacturerMenuDialog;

    private ViewHolder newEditInjectorDialog;

    private ViewHolder newEditTestDialog;

    private VoltAmpereProfileController voltAmpereProfileController;

    private DataConverter dataConverter;

    private CurrentInjectorObtainer currentInjectorObtainer;

    private Spinner<Integer> widthCurrentSignalSpinner;

    private Spinner<Double> freqCurrentSignalSpinner;

    private HighPressureSectionController highPressureSectionController;

    private InjectorsRepository injectorsRepository;

    private InjectorTestRepository injectorTestRepository;

    private CurrentInjectorTestsObtainer currentInjectorTestsObtainer;

    private VoltAmpereProfile currentVoltAmpereProfile;

    public CurrentInjectorTestsObtainer getCurrentInjectorTestsObtainer() {
        return currentInjectorTestsObtainer;
    }

    private Stage manufacturerDialogStage;

    private Stage modelDialogStage;

    private Stage testDialogStage;

    private FilteredList<Model> filteredModelList;

    private Tests tests;

    private I18N i18N;

    private boolean startLight;

    private boolean isFocusMoved;

    private boolean isAnotherAutoOrNewTestList;

    public ComboBox<String> getSpeedComboBox() {
        return speedComboBox;
    }

    public VBox getInjectorTestsVBox() {
        return injectorTestsVBox;
    }

    public HBox getStartHBox() {
        return startHBox;
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

    public ComboBox<GUIType> getVersionComboBox() {
        return versionComboBox;
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

    public Tests getTests() {
        return tests;
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

    public RadioButton getAutoTestRadioButton() {
        return autoTestRadioButton;
    }

    public RadioButton getTestPlanTestRadioButton() {
        return testPlanTestRadioButton;
    }

    public ListView<InjectorTest> getTestListView() {
        return testListView;
    }

    public void setEnabler(Enabler enabler) {
        this.enabler = enabler;
    }

    public void setCurrentManufacturerObtainer(CurrentManufacturerObtainer currentManufacturerObtainer) {
        this.currentManufacturerObtainer = currentManufacturerObtainer;
    }

    public void setApplicationAppearanceChanger(ApplicationAppearanceChanger applicationAppearanceChanger) {
        this.applicationAppearanceChanger = applicationAppearanceChanger;
    }

    public void setManufacturerMenuDialog(ViewHolder manufacturerMenuDialog) {
        this.manufacturerMenuDialog = manufacturerMenuDialog;
    }

    public void setNewEditInjectorDialog(ViewHolder newEditInjectorDialog) {
        this.newEditInjectorDialog = newEditInjectorDialog;
    }

    public void setNewEditTestDialog(ViewHolder newEditTestDialog) {
        this.newEditTestDialog = newEditTestDialog;
    }

    public void setVoltAmpereProfileController(VoltAmpereProfileController voltAmpereProfileController) {
        this.voltAmpereProfileController = voltAmpereProfileController;
    }

    public void setDataConverter(DataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public void setCurrentInjectorObtainer(CurrentInjectorObtainer currentInjectorObtainer) {
        this.currentInjectorObtainer = currentInjectorObtainer;
    }

    public void setWidthCurrentSignalSpinner(Spinner<Integer> widthCurrentSignalSpinner) {
        this.widthCurrentSignalSpinner = widthCurrentSignalSpinner;
    }

    public void setFreqCurrentSignalSpinner(Spinner<Double> freqCurrentSignalSpinner) {
        this.freqCurrentSignalSpinner = freqCurrentSignalSpinner;
    }

    public void setInjectorsRepository(InjectorsRepository injectorsRepository) {
        this.injectorsRepository = injectorsRepository;
    }

    public void setInjectorTestRepository(InjectorTestRepository injectorTestRepository) {
        this.injectorTestRepository = injectorTestRepository;
    }

    public void setCurrentInjectorTestsObtainer(CurrentInjectorTestsObtainer currentInjectorTestsObtainer) {
        this.currentInjectorTestsObtainer = currentInjectorTestsObtainer;
    }

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }

    public void setHighPressureSectionController(HighPressureSectionController highPressureSectionController) {
        this.highPressureSectionController = highPressureSectionController;
    }

    public void setTests(Tests tests) {
        this.tests = tests;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    private void init() {

        this
                .initTestListViewUtilityObjects()
                .setupTimeProgressBars()
                .bindingI18N()
                .setupResetButton()
                .setupVersionComboBox()
                .initManufacturerContextMenu()
                .initModelContextMenu()
                .initTestContextMenu()
                .setupSpeedComboBox()
                .initStartToggleButtonBlinking()
                .setupManufacturerListViewListener()
                .setupSearchModelTextFieldListener()
                .setupBaseTypeToggleGroupListener()
                .setupModelListViewListener()
                .setupTestListViewListener()
                .setupTestsToggleGroupListener()
                .setupMoveButtonEventHandlers()
                .setupTestListAutoChangeListener();

    }

    private MainSectionController initTestListViewUtilityObjects() {

        testsSelectionModel = testListView.getSelectionModel();

        testListViewItems = testListView.getItems();

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

    private void setFilteredItems(Manufacturer manufacturer) {

        List<Injector> modelsByManufacturers = injectorsRepository.findByManufacturerAndIsCustom(manufacturer, customRadioButton.isSelected());
        ObservableList<Model> injectors = FXCollections.observableArrayList(modelsByManufacturers);
        filteredModelList = new FilteredList<>(injectors, model -> true);
        modelListView.setItems(filteredModelList);

    }

    public void fillTestListView() {

        setupTaskExecution();

    }

    public void refreshTestListView() {

        if (currentInjectorObtainer.getInjector() == null)
            return;

        if (currentInjectorTestsObtainer.getInjectorTests() == null)
            setupTaskExecution();
        else
            pointToFirstTest();

    }

    private void setupTaskExecution() {

        ObjectProperty<List<InjectorTest>> property = new SimpleObjectProperty<>();

        Task<List<InjectorTest>> task = new Task<List<InjectorTest>>() {
            @Override
            protected List<InjectorTest> call() {
                return injectorTestRepository.findAllByInjector(currentInjectorObtainer.getInjector());
            }
        };

        property.bind(task.valueProperty());

        property.addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                currentInjectorTestsObtainer.setInjectorTests(newValue);
                testListViewItems.setAll(currentInjectorTestsObtainer.getInjectorTests());
                Platform.runLater(this::pointToFirstTest);
            }

        });

        new Thread(task).start();

    }

    private void pointToFirstTest() {

        testsSelectionModel.select(0);
        testListView.scrollTo(0);

    }

    private MainSectionController initManufacturerContextMenu() {
        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(new ManufacturerMenuEventHandler("New manufacturer", ManufacturerMenuDialogController::setNew));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(new ManufacturerMenuEventHandler("Delete manufacturer", ManufacturerMenuDialogController::setDelete));

        manufacturerListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manufacturerMenu.getItems().clear();
                manufacturerMenu.getItems().add(newManufacturer);
                if (currentManufacturerObtainer.getCurrentManufacturer() != null &&
                        currentManufacturerObtainer.getCurrentManufacturer().isCustom())
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
        speedComboBox.getSelectionModel().selectFirst();

        speedComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setProgress(newValue));

        return this;

    }

    private class ManufacturerMenuEventHandler implements EventHandler<ActionEvent> {

        private String title;
        private Consumer<ManufacturerMenuDialogController> dialogType;

        public ManufacturerMenuEventHandler(String title, Consumer<ManufacturerMenuDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            if (manufacturerDialogStage == null) {
                manufacturerDialogStage = new Stage();
                manufacturerDialogStage.setScene(new Scene(manufacturerMenuDialog.getView(), 200, 130));
                manufacturerDialogStage.setResizable(false);
                manufacturerDialogStage.initModality(Modality.APPLICATION_MODAL);
                ((ManufacturerMenuDialogController) manufacturerMenuDialog.getController()).setStage(manufacturerDialogStage);
            }
            manufacturerDialogStage.setTitle(title);
            dialogType.accept((ManufacturerMenuDialogController) manufacturerMenuDialog.getController());
            manufacturerDialogStage.show();
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
                modelDialogStage.setScene(new Scene(newEditInjectorDialog.getView(), 600, 400));
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

    private MainSectionController setupVersionComboBox() {

        versionComboBox.getItems().addAll(GUIType.values());

        versionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            switch (newValue) {
                case UIS:
                    applicationAppearanceChanger.changeToUIS();
                    break;
                case CR_Inj:
                    applicationAppearanceChanger.changeToCRInj();
                    break;
                case CR_Pump:
                    applicationAppearanceChanger.changeToCRPump();
                    break;
            }

            rootPrefs.put("GUI_Type", newValue.name());

        });

        GUIType currentGUIType = GUIType.valueOf(rootPrefs.get("GUI_Type", GUIType.CR_Inj.name()));

        versionComboBox.getSelectionModel().select(currentGUIType);

        return this;

    }

    private void setDefaultSpinnerValueFactories(boolean isDefault) {

        if (isDefault) {

            widthCurrentSignalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    WIDTH_CURRENT_SIGNAL_SPINNER_MIN,
                    WIDTH_CURRENT_SIGNAL_SPINNER_MAX,
                    WIDTH_CURRENT_SIGNAL_SPINNER_INIT,
                    WIDTH_CURRENT_SIGNAL_SPINNER_STEP));
            freqCurrentSignalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                    FREQ_CURRENT_SIGNAL_SPINNER_MIN,
                    FREQ_CURRENT_SIGNAL_SPINNER_MAX,
                    FREQ_CURRENT_SIGNAL_SPINNER_INIT,
                    FREQ_CURRENT_SIGNAL_SPINNER_STEP));

        } else {

            widthCurrentSignalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0));
            freqCurrentSignalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0d, 0d));

        }

    }

    private void setInjectorTypeValues(Integer freq, Integer firstW, Integer width) {

        freqCurrentSignalSpinner.getValueFactory().setValue((freq != null) ? 1000d / freq : 0);

        voltAmpereProfileController.getFirstWSpinner().getValueFactory().setValue(firstW);

        widthCurrentSignalSpinner.getValueFactory().setValue(width);

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

    private MainSectionController initStartToggleButtonBlinking() {

        Timeline startButtonTimeline = new Timeline(new KeyFrame(Duration.millis(400), event -> startBlinking()));
        startButtonTimeline.setCycleCount(Animation.INDEFINITE);

        startToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                startButtonTimeline.play();
            else {
                startButtonTimeline.stop();
                startToggleButton.getStyleClass().clear();
                startToggleButton.getStyleClass().add("startButton");
            }
        });

        return this;

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

    private MainSectionController setupManufacturerListViewListener() {

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {

            currentManufacturerObtainer.setCurrentManufacturer(newValue);

            if (newValue.isCustom()) {
                defaultRadioButton.setDisable(true);
                customRadioButton.setSelected(true);
            } else
                defaultRadioButton.setDisable(false);


            switch (versionComboBox.getSelectionModel().getSelectedItem()) {
                case CR_Inj:
                    setFilteredItems(newValue);
                    break;
                case CR_Pump:
                    //TODO
                    break;
                case UIS:
                    //TODO
                    break;
            }

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

            if (newValue == null) {
                currentInjectorObtainer.setInjector(null);
                currentInjectorTestsObtainer.setInjectorTests(null);
                enabler.selectInjector(false).selectInjectorType(null);
                return;
            }

            Injector inj = (Injector) newValue;
            currentVoltAmpereProfile = injectorsRepository.findByInjectorCode(inj.getInjectorCode()).getVoltAmpereProfile();
            inj.setVoltAmpereProfile(currentVoltAmpereProfile);
            currentInjectorObtainer.setInjector(inj);

            enabler.selectInjector(true).selectInjectorType(currentVoltAmpereProfile.getInjectorType().getInjectorType());

            Double firstI = currentVoltAmpereProfile.getFirstI();
            Double secondI = currentVoltAmpereProfile.getSecondI();
            Double boostI = currentVoltAmpereProfile.getBoostI();

            firstI = (boostI - firstI >= 0.5) ? firstI : boostI - 0.5;
            secondI = (firstI - secondI >= 0.5) ? secondI : firstI - 0.5;

            voltAmpereProfileController.getBoostUSpinner().getValueFactory().setValue(currentVoltAmpereProfile.getBoostU());
            voltAmpereProfileController.getFirstWSpinner().getValueFactory().setValue(currentVoltAmpereProfile.getFirstW());
            voltAmpereProfileController.getFirstISpinner().getValueFactory().setValue((firstI * 100 % 10 != 0) ? dataConverter.round(firstI) : firstI);
            voltAmpereProfileController.getSecondISpinner().getValueFactory().setValue((secondI * 100 % 10 != 0) ? dataConverter.round(secondI) : secondI);
            voltAmpereProfileController.getBoostISpinner().getValueFactory().setValue((boostI * 100 % 10 != 0) ? dataConverter.round(boostI) : boostI);
            voltAmpereProfileController.getBatteryUSpinner().getValueFactory().setValue(currentVoltAmpereProfile.getBatteryU());
            voltAmpereProfileController.getNegativeUSpinner().getValueFactory().setValue(currentVoltAmpereProfile.getNegativeU());
            voltAmpereProfileController.getEnableBoostToggleButton().setSelected(currentVoltAmpereProfile.getBoostDisable());

            voltAmpereProfileController.getApplyButton().fire();

        });

        return this;

    }

    private MainSectionController setupTestListViewListener() {

        testsSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (isFocusMoved)
                return;

            if (newValue == null) {
                highPressureSectionController.regulator1pressModeOFF();
                setDefaultSpinnerValueFactories(true);
                enabler.selectStaticLeakTest(false);
                return;
            }

            if (autoTestRadioButton.isSelected()) {

                enabler.showButtons(newValue.isIncluded(), false)
                        .enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - getListOfNonIncludedTests().size())
                        .enableMainSectionStartToggleButton(testsSelectionModel.getSelectedIndex() == 0 && newValue.isIncluded());

            }


            currentAdjustingTime = newValue.getAdjustingTime();

            currentMeasuringTime = newValue.getMeasurementTime();

            setProgress(speedComboBox.getSelectionModel().getSelectedItem());

            highPressureSectionController.regulator1pressModeON(newValue.getSettedPressure());

            Measurement measurementType = newValue.getTestName().getMeasurement();

            switch (measurementType) {

                case NO:
                    setDefaultSpinnerValueFactories(false);
                    enabler.selectStaticLeakTest(true);
                    break;
                default:
                    setDefaultSpinnerValueFactories(true);
                    enabler.selectStaticLeakTest(false);
                    Integer freq = newValue.getInjectionRate();
                    Integer width = newValue.getTotalPulseTime();
                    Integer firstW = currentVoltAmpereProfile.getFirstW();
                    firstW = (width - firstW >= 30) ? firstW : width - 30;
                    setInjectorTypeValues(freq, firstW, width);
                    break;

            }

        });

        return this;

    }

    //FIXME: при сортировке несколько раз вызывается листенер списка
    private MainSectionController setupTestsToggleGroupListener() {

        testsToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == autoTestRadioButton)
                tests.setTestType(AUTO);
            else {
                returnToDefaultTestListAuto();
                testListViewItems.sort((Comparator.comparingInt(injectorTest -> injectorTest.getTestName().getId())));
                tests.setTestType((newValue == testPlanTestRadioButton) ? TESTPLAN : CODING);
                enabler.enableMainSectionStartToggleButton(true);
            }

            enabler.selectTestType();

        });

        return this;

    }

    private MainSectionController setupMoveButtonEventHandlers() {

        moveUpButton.setOnAction(event -> moveTest(Move.UP));

        moveDownButton.setOnAction(event -> moveTest(Move.DOWN));

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
                enabler.enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - getListOfNonIncludedTests().size());
                break;
            case DOWN:
                Collections.swap(testListViewItems, selectedTestIndex, selectedTestIndex + 1);
                testsSelectionModel.select(selectedTestIndex + 1);
                testListView.scrollTo(testsSelectionModel.getSelectedIndex());
                enabler.enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - getListOfNonIncludedTests().size());
                break;

        }

        enabler.enableMainSectionStartToggleButton(testsSelectionModel.getSelectedIndex() == 0);

        isFocusMoved = false;

    }

    private void setupTestListAutoChangeListener() {

        getListOfNonIncludedTests().addListener((ListChangeListener<? super InjectorTest>) change -> {

            if (isAnotherAutoOrNewTestList)
                return;

            int index = testListViewItems.indexOf(getChangedInjectorTest());

            while (change.next()) {

                if (change.wasAdded()) {

                    int lastIndex = testListViewItems.size() - 1;

                    if (index == lastIndex)
                        enabler.showButtons(false, false);

                    for (int i = index; i < lastIndex; i++) {
                        Collections.swap(testListViewItems, i, i + 1);
                    }

                } else if (change.wasRemoved()) {

                    if (index == 0)
                        enabler.showButtons(true, false);

                    for (int i = index; i > 0; i--) {
                        Collections.swap(testListViewItems, i, i - 1);
                    }

                }

            }

            enabler.enableUpDownButtons(testsSelectionModel.getSelectedIndex(), testListViewItems.size() - change.getList().size());

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

}
