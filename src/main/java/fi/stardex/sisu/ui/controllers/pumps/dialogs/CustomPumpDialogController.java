package fi.stardex.sisu.ui.controllers.pumps.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.pump.CustomPumpDialogModel;
import fi.stardex.sisu.model.pump.ManufacturerPumpModel;
import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.repos.pump.PumpModelService;
import fi.stardex.sisu.persistence.repos.pump.PumpTestService;
import fi.stardex.sisu.util.enums.pump.PumpPressureControl;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorType;
import fi.stardex.sisu.util.enums.pump.PumpRotation;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static fi.stardex.sisu.util.enums.GUI_type.CR_Pump;

public class CustomPumpDialogController {

    @FXML private Label notUniqueLabel;
    @FXML private Label nameLabel;
    @FXML private ComboBox<PumpRegulatorConfig> regConfigComboBox;
    @FXML private Button applyBtn;
    @FXML private Button cancelBtn;
    @FXML private ComboBox<PumpRegulatorType> regTypeComboBox;
    @FXML private TextField nameTextField;
    @FXML private Label regConfigLabel;
    @FXML private Label regTypeLabel;
    @FXML private Label pressureControlLabel;
    @FXML private ComboBox<PumpPressureControl> pressureControlComboBox;
    @FXML private Label rotationLabel;
    @FXML private ComboBox<PumpRotation> rotationComboBox;
    @FXML private Label feedPressureLabel;
    @FXML private Spinner<Double> feedPressureSpinner;

    private Stage dialogStage;
    private Parent dialogViev;
    private GUI_TypeModel guiTypeModel;
    private I18N i18N;
    private PumpModelService pumpModelService;
    private PumpTestService pumpTestService;
    private PumpModel pumpModel;
    private CustomPumpDialogModel customPumpDialogModel;
    private ManufacturerPumpModel manufacturerPumpModel;
    private Alert alert;
    private StringProperty alertString = new SimpleStringProperty("Please specify all values!");
    private StringProperty yesButton = new SimpleStringProperty();

    public void setDialogViev(Parent dialogViev) {
        this.dialogViev = dialogViev;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpModelService(PumpModelService pumpModelService) {
        this.pumpModelService = pumpModelService;
    }
    public void setPumpTestService(PumpTestService pumpTestService) {
        this.pumpTestService = pumpTestService;
    }
    public void setCustomPumpDialogModel(CustomPumpDialogModel customPumpDialogModel) {
        this.customPumpDialogModel = customPumpDialogModel;
    }
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }
    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }

    @PostConstruct
    public void init() {

        regConfigComboBox.getItems().setAll(PumpRegulatorConfig.values());
        regTypeComboBox.setDisable(true);
        pressureControlComboBox.getItems().setAll(PumpPressureControl.values());
        rotationComboBox.getItems().setAll(PumpRotation.values());
        feedPressureSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 5, 0.1, 0.1));
        SpinnerManager.setupDoubleSpinner(feedPressureSpinner);
        feedPressureSpinner.getValueFactory().setValue(0.1);
        setupCustomPumpOperationListener();
        setupGuiTypeModel();
        setupButtonsListeners();
        setupRegulatorTypeListener();
        bindingI18N();
    }

    private void setupGuiTypeModel() {

        guiTypeModel.guiTypeProperty().addListener((observable, oldValue, newValue) ->
                customPumpDialogModel.customModelProperty().setValue(null));
    }

    private void setupCustomPumpOperationListener() {

        pumpModel.customPumpOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            if (guiTypeModel.guiTypeProperty().get() != CR_Pump) {return;}
            if(newValue == null) return;

            if (dialogStage == null) {
                dialogStage = new Stage();
                dialogStage.setScene(new Scene(dialogViev));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setOnCloseRequest(event -> customPumpDialogModel.cancelProperty().setValue(new Object()));
            }

            switch (pumpModel.customPumpOperationProperty().get()) {
                case NEW:
                    setNew();
                    break;
                case DELETE:
                    setDelete();
                    break;
                case EDIT:
                    setEdit();
                    break;
            }

            notUniqueLabel.setVisible(false);
            dialogStage.setTitle(pumpModel.customPumpOperationProperty().get().getTitle() + "CR-Pump");
            dialogStage.show();
        });
    }

    private void setupButtonsListeners() {

        cancelBtn.setOnMouseClicked(event -> {

            customPumpDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });

        applyBtn.setOnMouseClicked(mouseEvent -> {

            switch (pumpModel.customPumpOperationProperty().get()) {
                case NEW:
                    create();
                    break;
                case DELETE:
                    delete();
                    break;
                case EDIT:
                    update();
                    break;
            }
        });
    }

    private void setupRegulatorTypeListener() {

        regConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;

            regTypeComboBox.getSelectionModel().clearSelection();
            regTypeComboBox.getItems().clear();

            switch (newValue) {
                case SCV:
                case EAV:
                    regTypeComboBox.getItems().setAll(getRegTypesList(false));
                    regTypeComboBox.getSelectionModel().select(0);
                    regTypeComboBox.setDisable(false);
                    break;
                case NO_CONFIG:
                    regTypeComboBox.getItems().setAll(getRegTypesList(true));
                    regTypeComboBox.getSelectionModel().select(0);
                    regTypeComboBox.setDisable(false);
                    break;
                default:
                    regTypeComboBox.getItems().clear();
                    regTypeComboBox.setDisable(true);

            }
        });
    }

    private List<PumpRegulatorType> getRegTypesList(boolean is_NO_TYPE) {

        return Arrays.stream(PumpRegulatorType.values())
                .filter(rt -> (rt == PumpRegulatorType.NO_TYPE) == is_NO_TYPE)
                .collect(Collectors.toList());
    }

    private void setNew() {

        nameTextField.clear();
        nameTextField.setDisable(false);
        configForm(false);
    }

    private void setEdit() {

        Pump pump = pumpModel.pumpProperty().get();

        nameTextField.setText(pump.getPumpCode());

        regConfigComboBox.getSelectionModel().select(pump.getPumpRegulatorConfig());
        regTypeComboBox.getSelectionModel().select(pump.getPumpRegulatorType());
        rotationComboBox.getSelectionModel().select(pump.getPumpRotation());
        pressureControlComboBox.getSelectionModel().select(pump.getPumpPressureControl());
        feedPressureSpinner.getValueFactory().setValue(pump.getFeedPressure());

        nameTextField.setDisable(true);
    }

    private void setDelete() {

        nameTextField.setText(pumpModel.pumpProperty().get().getPumpCode());
        nameTextField.setDisable(true);
        regTypeComboBox.setDisable(true);
        configForm(true);
    }

    private void create() {

        if(!isDataComplete()) return;

        if (nameTextField.getText().isEmpty() || pumpModelService.existsByModelCode(nameTextField.getText())) {
            notUniqueLabel.setVisible(true);
            return;
        }

        PumpRegulatorConfig regulatorConfig = regConfigComboBox.getValue();
        PumpRegulatorType regulatorType = regTypeComboBox.getValue();
        PumpPressureControl pressureControl = pressureControlComboBox.getValue();
        PumpRotation rotation = rotationComboBox.getValue();
        Double feedPressure = feedPressureSpinner.getValue();
        String code = nameTextField.getText();
        ManufacturerPump manufacturer = manufacturerPumpModel.manufacturerPumpProperty().get();

        Model customPump = new Pump(code,
                manufacturer,
                true,
                feedPressure,
                rotation,
                regulatorConfig,
                pressureControl,
                regulatorType);

        pumpModelService.save(customPump);
        customPumpDialogModel.customModelProperty().setValue(customPump);
        customPumpDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void update() {

        if(!isDataComplete()) return;

        PumpRegulatorConfig regulatorConfig = regConfigComboBox.getValue();
        PumpRegulatorType regulatorType = regTypeComboBox.getValue();
        PumpPressureControl pressureControl = pressureControlComboBox.getValue();
        PumpRotation rotation = rotationComboBox.getValue();
        Double feedPressure = feedPressureSpinner.getValue();
        Pump pumpToUpdate = pumpModel.pumpProperty().get();
        pumpToUpdate.setFeedPressure(feedPressure);
        pumpToUpdate.setPumpPressureControl(pressureControl);
        pumpToUpdate.setPumpRegulatorType(regulatorType);
        pumpToUpdate.setPumpRegulatorConfig(regulatorConfig);
        pumpToUpdate.setPumpRotation(rotation);
        List<PumpTest> pumpTests = pumpTestService.findAllByModel(pumpToUpdate);
        pumpTests.clear();
        pumpTests.addAll(new LinkedList<>());

        pumpModelService.save(pumpToUpdate);
        customPumpDialogModel.customModelProperty().setValue(pumpToUpdate);
        customPumpDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void delete() {

        Model modelToDelete = pumpModel.pumpProperty().get();
        pumpModelService.delete(modelToDelete);
        customPumpDialogModel.customModelProperty().setValue(null);
        customPumpDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void showAlert() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        }
        ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());
        alert.setContentText(alertString.get());
        alert.show();
    }

    private void configForm(boolean isDelete) {

        regConfigComboBox.getSelectionModel().clearSelection();
        regTypeComboBox.getSelectionModel().clearSelection();
        rotationComboBox.getSelectionModel().clearSelection();
        pressureControlComboBox.getSelectionModel().clearSelection();
        feedPressureSpinner.getValueFactory().setValue(0d);
        regConfigComboBox.setDisable(isDelete);
        rotationComboBox.setDisable(isDelete);
        feedPressureSpinner.setDisable(isDelete);
        pressureControlComboBox.setDisable(isDelete);
    }

    private boolean isDataComplete(){

        if (regConfigComboBox.getSelectionModel().getSelectedItem() == null
                || regTypeComboBox.getSelectionModel().getSelectedItem() == null
                || rotationComboBox.getSelectionModel().getSelectedItem() == null
                || pressureControlComboBox.getSelectionModel().getSelectedItem() == null
                || nameTextField.getText().isEmpty()) {
            showAlert();
        }
        return alert == null || !alert.isShowing();
    }

    private void bindingI18N() {

        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        alertString.bind((i18N.createStringBinding("alert.customDialog")));
    }
}
