package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.repos.pump.PumpModelService;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;

public class PumpsModelsListController {

    @FXML private VBox injectorsVBox;
    @FXML private ListView<Pump> modelListView;
    @FXML private TextField searchModelTextField;
    @FXML private RadioButton customRadioButton;
    @FXML private RadioButton defaultRadioButton;
    @FXML private ToggleGroup baseTypeToggleGroup;

    private ManufacturerPumpModel manufacturerPumpModel;
    private PumpModel pumpModel;
    private CustomPumpState customPumpState;
    private FilteredList<Pump> filteredModelList;
    private PumpTestListModel pumpTestListModel;
    private PumpReportModel pumpReportModel;
    private I18N i18N;
    private PumpsStartButtonState pumpsStartButtonState;
    private CustomPumpDialogModel customPumpDialogModel;
    private PumpModelService pumpModelService;
    private boolean modelAlertProcessing;
    private Alert alert;
    private StringProperty alertString = new SimpleStringProperty();
    private StringProperty yesButton = new SimpleStringProperty();
    private StringProperty noButton = new SimpleStringProperty();

    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }
    public void setCustomPumpState(CustomPumpState customPumpState) {
        this.customPumpState = customPumpState;
    }
    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setCustomPumpDialogModel(CustomPumpDialogModel customPumpDialogModel) {
        this.customPumpDialogModel = customPumpDialogModel;
    }
    public void setPumpModelService(PumpModelService pumpModelService) {
        this.pumpModelService = pumpModelService;
    }

    @PostConstruct
    private void init() {

        setupSearchModelTextField();
        setupListeners();
        initModelContextMenu();
        bindingI18N();
    }


    private void setupListeners() {

        baseTypeToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {

            customPumpState.customPumpProperty().setValue(newValue == customRadioButton);
            initPumpList();
        }));

        modelListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if(modelAlertProcessing)return;

            if (!pumpReportModel.getResultsList().isEmpty()) {
                showAlert();
                if (alert.getResult() != ButtonType.YES) {

                    modelAlertProcessing = true;
                    modelListView.getSelectionModel().select(oldValue);
                    modelAlertProcessing = false;
                    return;
                }
            }

            pumpModel.pumpProperty().set(newValue);
//            pumpTestListModel.initPumpTestList();
            pumpReportModel.clearResults();
        });

        customPumpDialogModel.doneProperty().addListener((observableValue, oldValue, newValue) -> {

            initPumpList();
            modelListView.getSelectionModel().select((Pump)customPumpDialogModel.customModelProperty().get());
            pumpModel.customPumpOperationProperty().setValue(null);
        });

        customPumpDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue)
                -> pumpModel.customPumpOperationProperty().setValue(null));

        manufacturerPumpModel.manufacturerPumpProperty().addListener((observableValue, oldValue, newValue) -> {

            initPumpList();
            injectorsVBox.setDisable(newValue == null);
        });

        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {

            modelListView.setDisable(newValue);
            searchModelTextField.setDisable(newValue);
            baseTypeToggleGroup.getToggles().stream().filter(toggle -> !toggle.isSelected()).forEach(radioButton -> ((Node)radioButton).setDisable(newValue));
        });
    }

    private void initPumpList() {

        pumpModel.getPumpObservableListProperty().setValue(FXCollections.observableArrayList(pumpModelService.findByProducerAndIsCustom(
                manufacturerPumpModel.manufacturerPumpProperty().get(),
                customPumpState.customPumpProperty().get()
        )));
        filteredModelList = new FilteredList<>(pumpModel.getPumpObservableListProperty().get(), pump -> true);
        modelListView.setItems(filteredModelList);
    }

    private void setupSearchModelTextField() {

        searchModelTextField.textProperty().addListener(((observable, oldValue, newValue) -> filteredModelList.setPredicate(data -> {

            if (newValue == null || newValue.isEmpty())
                return true;
            else {
                return (data.toString().contains(newValue.toUpperCase())
                        || data.toString().replace("-", "").contains(newValue.toUpperCase())
                        || data.toString().replace("#", "").contains(newValue.toUpperCase())
                        || data.toString().replace(" ", "").contains(newValue.toUpperCase())
                        || data.toString().replaceFirst("-", "").contains(newValue.toUpperCase()));
            }

        })));
    }

    private void initModelContextMenu() {

        ContextMenu modelMenu = new ContextMenu();
        MenuItem newModel = new MenuItem("New");
        newModel.setOnAction(actionEvent -> pumpModel.customPumpOperationProperty().setValue(Operation.NEW));
        MenuItem editModel = new MenuItem("Edit");
        editModel.setOnAction(actionEvent -> pumpModel.customPumpOperationProperty().setValue(Operation.EDIT));
        MenuItem copyModel = new MenuItem("Copy");
        MenuItem deleteModel = new MenuItem("Delete");
        deleteModel.setOnAction(actionEvent -> pumpModel.customPumpOperationProperty().setValue(Operation.DELETE));

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

    private void bindingI18N(){

        defaultRadioButton.textProperty().bind(i18N.createStringBinding("main.defaultRB.radiobutton"));
        customRadioButton.textProperty().bind(i18N.createStringBinding("main.customRB.radiobutton"));
        alertString.bind(i18N.createStringBinding("alert.oemOrModelChange"));
        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        noButton.bind((i18N.createStringBinding("alert.noButton")));
    }
}
