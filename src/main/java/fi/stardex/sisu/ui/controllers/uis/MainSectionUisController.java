package fi.stardex.sisu.ui.controllers.uis;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomModelDialogModel;
import fi.stardex.sisu.model.uis.CustomProducerDialogModel;
import fi.stardex.sisu.model.uis.CustomTestDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.persistence.orm.uis.ManufacturerUIS;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.common.GUI_TypeController;
import fi.stardex.sisu.util.TimeProgressBar;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.enums.Move;
import fi.stardex.sisu.util.enums.TestSpeed;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.listeners.PrintButtonHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

import java.util.Collections;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.StartMeasurementCycle;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.UIS_to_CR_pulseControlSwitch;
import static fi.stardex.sisu.util.enums.Move.DOWN;
import static fi.stardex.sisu.util.enums.Move.UP;
import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;

public class MainSectionUisController {

    private static Logger logger = LoggerFactory.getLogger(MainSectionUisController.class);

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
    @FXML private ToggleGroup baseTypeToggleGroup;
    @FXML private RadioButton defaultRadioButton;
    @FXML private RadioButton customRadioButton;
    @FXML private ListView<ManufacturerUIS> manufacturerListView;
    @FXML private TextField searchModelTextField;
    @FXML private ListView<Model> modelListView;
    @FXML private ListView<InjectorUisTest> testListView;
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
    private boolean oemAlertProcessing;
    private boolean modelAlertProcessing;
    private Alert alert;
    private StringProperty alertString = new SimpleStringProperty();
    private StringProperty yesButton = new SimpleStringProperty();
    private StringProperty noButton = new SimpleStringProperty();
    private ObservableList<? extends Test> testListViewItems;

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
//        setupTestListAutoChangeListenerNew();
        setupGuiTypeModelListener();
//        setupManufacturerMenuDialogModelListener();
//        setupManufacturerListListener();
//        setupInjectorSectionPwrButtonListener();
        setupStartButtonListener();
        setupPrintButton();
//        boostUadjustmentState.boostUadjustmentStateProperty().addListener((observable, oldValue, newValue) -> disableNode(newValue, startToggleButton));
        testListView.setCellFactory(CheckBoxListCell.forListView(InjectorUisTest::includedProperty));
        showButtons(true, false);
    }

    private void setupModelListViewListener() {

        modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


        });
    }

    private void setupTestListViewListener() {

        testListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


        });
    }

    private void setupTestsToggleGroupListener() {

        testsToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {


        });
    }

    private void setupBaseTypeToggleGroupListener() {

        baseTypeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {


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

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(oemAlertProcessing) return;


        });
    }

    private void setFilteredItems(ManufacturerUIS manufacturer) {



    }

    public void pointToFirstTest() {

        testListView.getSelectionModel().select(0);
        testListView.scrollTo(0);
    }

    private void setTests() {



    }

    private void initManufacturerContextMenu() {

        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(actionEvent -> mainSectionUisModel.customProducerProperty().setValue(Operation.NEW));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(actionEvent -> mainSectionUisModel.customProducerProperty().setValue(Operation.DELETE));

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
                mainSectionUisModel.customProducerProperty().setValue(null));
    }

    private void initModelContextMenu() {

        ContextMenu modelMenu = new ContextMenu();
        MenuItem newModel = new MenuItem("New");
        newModel.setOnAction(actionEvent -> mainSectionUisModel.customModelProperty().setValue(Operation.NEW));
        MenuItem editModel = new MenuItem("Edit");
        editModel.setOnAction(actionEvent -> mainSectionUisModel.customModelProperty().setValue(Operation.EDIT));
        MenuItem copyModel = new MenuItem("Copy");
        MenuItem deleteModel = new MenuItem("Delete");
        deleteModel.setOnAction(actionEvent -> mainSectionUisModel.customModelProperty().setValue(Operation.DELETE));

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
                mainSectionUisModel.customModelProperty().setValue(null));
    }

    private void initTestContextMenu() {

        ContextMenu testMenu = new ContextMenu();
        MenuItem newTest = new MenuItem("New");
        newTest.setOnAction(actionEvent -> mainSectionUisModel.customTestProperty().setValue(Operation.NEW));
        MenuItem editTest = new MenuItem("Edit");
        editTest.setOnAction(actionEvent -> mainSectionUisModel.customTestProperty().setValue(Operation.EDIT));
        MenuItem deleteTest = new MenuItem("Delete");
        deleteTest.setOnAction(actionEvent -> mainSectionUisModel.customTestProperty().setValue(Operation.DELETE));
        testMenu.getItems().add(newTest);

        testListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                testMenu.show(testListView, event.getScreenX(), event.getScreenY());
            } else
                testMenu.hide();
        });

        customTestDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                mainSectionUisModel.customTestProperty().setValue(null));
    }

    private void setupStartButtonListener() {

        mainSectionUisModel.startButtonProperty().bind(startToggleButton.selectedProperty());
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
    }

    private void setupTimeProgressBars() {

        adjustingTime = new TimeProgressBar(adjustingTimeProgressBar, adjustingText);
        measuringTime = new TimeProgressBar(measuringTimeProgressBar, measuringText);
    }

    private void setupSpeedComboBox() {
        speedComboBox.getItems().setAll(TestSpeed.values());
        speedComboBox.getSelectionModel().selectFirst();
        speedComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            adjustingTime.setProgress((int)(currentAdjustingTime * newValue.getMultiplier()));
            measuringTime.setProgress((int)(currentMeasuringTime * newValue.getMultiplier()));
        });
    }

    //TODO add initialisation of manufacturers list
    private void setupGuiTypeModelListener() {

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == GUI_TypeController.GUIType.UIS) {

                ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, 1);
                baseTypeToggleGroup.selectToggle(defaultRadioButton);


            }
        });

    }

    private  void setupResetButton() {
         resetButton.setOnAction(event -> flowModbusWriter.add(StartMeasurementCycle, true));
    }

    private void setupPrintButton() {
        printButton.setOnAction(new PrintButtonHandler(printStage, printDialogPanel));
    }

    private void setupStoreButton() {

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
        MultipleSelectionModel<InjectorUisTest> testSelectionModel = testListView.getSelectionModel();

        if (selectedTestIndex == -1)
            return;

        isFocusMoved = true;

        switch (move) {

            case UP:
                Collections.swap(testListViewItems, selectedTestIndex, selectedTestIndex - 1);
                testListView.getSelectionModel().select(selectedTestIndex - 1);
                testListView.scrollTo(testSelectionModel.getSelectedIndex());
                enableUpDownButtons(testSelectionModel.getSelectedIndex(), testListViewItems.size() - InjectorUisTest.getListOfNonIncludedTests().size());
                break;
            case DOWN:
                Collections.swap(testListViewItems, selectedTestIndex, selectedTestIndex + 1);
                testListView.getSelectionModel().select(selectedTestIndex + 1);
                testListView.scrollTo(testSelectionModel.getSelectedIndex());
                enableUpDownButtons(testSelectionModel.getSelectedIndex(), testListViewItems.size() - InjectorUisTest.getListOfNonIncludedTests().size());
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
