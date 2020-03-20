package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomModelDialogModel;
import fi.stardex.sisu.model.uis.CustomVapUisDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.uis.InjectorUIS;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.persistence.orm.uis.ManufacturerUIS;
import fi.stardex.sisu.persistence.repos.uis.UisModelService;
import fi.stardex.sisu.persistence.repos.uis.UisTestService;
import fi.stardex.sisu.persistence.repos.uis.UisVapService;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.util.enums.GUI_type.UIS;
import static fi.stardex.sisu.util.enums.Operation.*;

public class CustomInjectorUisDialogController {

    @FXML private Label firstW2Label;
    @FXML private Label boostI2Label;
    @FXML private Label firstI2Label;
    @FXML private Label secondI2Label;
    @FXML private GridPane rootGridPane;
    @FXML private TextField injectorCodeTF;
    @FXML private Label noUniqueLabel;
    @FXML private ComboBox<InjectorType> injTypeCB;
    @FXML private ComboBox<InjectorSubType> injSubTypeCB;
    @FXML private RadioButton defaultRB;
    @FXML private ToggleGroup baseType;
    @FXML private RadioButton customRB;
    @FXML private ListView<InjectorUisVAP> voapListView;
    @FXML private Label sureLabel;
    @FXML private ButtonBar controlBtnBar;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label boostUvalue;
    @FXML private Label boostEnableValue;
    @FXML private Label boostIvalue;
    @FXML private Label firstWvalue;
    @FXML private Label firstIvalue;
    @FXML private Label batteryUvalue;
    @FXML private Label secondIvalue;
    @FXML private Label negativeUvalue;

    private MainSectionUisModel mainSectionUisModel;
    private Stage dialogStage;
    private Parent dialogViev;
    private GUI_TypeModel guiTypeModel;
    private CustomModelDialogModel customModelDialogModel;
    private CustomVapUisDialogModel customVapUisDialogModel;
    private I18N i18N;
    private UisModelService uisModelService;
    private UisTestService uisTestService;
    private UisVapService uisVapService;
    private Alert alert;
    private StringProperty alertString = new SimpleStringProperty("Please specify all values!");
    private StringProperty yesButton = new SimpleStringProperty();

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setDialogViev(Parent dialogViev) {
        this.dialogViev = dialogViev;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setCustomModelDialogModel(CustomModelDialogModel customModelDialogModel) {
        this.customModelDialogModel = customModelDialogModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisModelService(UisModelService uisModelService) {
        this.uisModelService = uisModelService;
    }
    public void setUisTestService(UisTestService uisTestService) {
        this.uisTestService = uisTestService;
    }
    public void setUisVapService(UisVapService uisVapService) {
        this.uisVapService = uisVapService;
    }
    public void setCustomVapUisDialogModel(CustomVapUisDialogModel customVapUisDialogModel) {
        this.customVapUisDialogModel = customVapUisDialogModel;
    }

    @PostConstruct
    public void init() {

        mainSectionUisModel.customModelOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            /** Additional check of GUI type is done to prevent listener invocation and dialog window irrelevant to GUI type activation.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf InjectorUisTest} but it is slower */
            if (guiTypeModel.guiTypeProperty().get() != UIS) {return;}
            if(newValue == null) return;

            if (dialogStage == null) {
                dialogStage = new Stage();
                dialogStage.setScene(new Scene(dialogViev));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setTitle(mainSectionUisModel.customModelOperationProperty().get().getTitle() + "injector");
                dialogStage.setOnCloseRequest(event -> customModelDialogModel.cancelProperty().setValue(new Object()));
            }
            switch (mainSectionUisModel.customModelOperationProperty().get()) {
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
            dialogStage.show();
        });

        injTypeCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            List<InjectorUisVAP> vapByIsCustomAndInjectorType = uisVapService.findByIsCustomAndInjectorTypeAndInjectorSubType(!defaultRB.isSelected(), newValue, injSubTypeCB.getValue());
            voapListView.getItems().setAll(vapByIsCustomAndInjectorType);
            customModelDialogModel.getInjectorType().setValue(newValue);
            selectFirst();
        });

        injSubTypeCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            List<InjectorUisVAP> vapByIsCustomAndInjectorType = uisVapService.findByIsCustomAndInjectorTypeAndInjectorSubType(!defaultRB.isSelected(), injTypeCB.getValue(), newValue);
            voapListView.getItems().setAll(vapByIsCustomAndInjectorType);
            customModelDialogModel.getInjectorSubType().setValue(newValue);
            selectFirst();
        });

        baseType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            voapListView.getItems().setAll(uisVapService.findByIsCustomAndInjectorTypeAndInjectorSubType(!defaultRB.isSelected(), injTypeCB.getValue(), injSubTypeCB.getValue()));
            selectFirst();
        });

        voapListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            customModelDialogModel.customVapProperty().setValue(newValue);
            changeLabels(newValue);
        });

        injTypeCB.getItems().setAll(InjectorType.values());
        injSubTypeCB.getItems().setAll(InjectorSubType.values());
        injTypeCB.getSelectionModel().select(0);
        injSubTypeCB.getSelectionModel().select(0);

        cancelBtn.setOnMouseClicked(event -> {

            customModelDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });

        saveBtn.setOnMouseClicked(mouseEvent -> {

            switch (mainSectionUisModel.customModelOperationProperty().get()) {
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

        guiTypeModel.guiTypeProperty().addListener((observable, oldValue, newValue) ->
                customModelDialogModel.customModelProperty().setValue(null));

        ContextMenu voapListMenu = new ContextMenu();
        MenuItem newVOAP = new MenuItem("New");
        newVOAP.setOnAction(actionEvent -> customModelDialogModel.customVapOperationProperty().setValue(NEW));
        MenuItem copyVOAP = new MenuItem("Copy");
        copyVOAP.setOnAction(actionEvent -> customModelDialogModel.customVapOperationProperty().setValue(COPY));
        MenuItem editVOAP = new MenuItem("Edit");
        editVOAP.setOnAction(actionEvent -> customModelDialogModel.customVapOperationProperty().setValue(EDIT));
        MenuItem deleteVOAP = new MenuItem("Delete");
        deleteVOAP.setOnAction(actionEvent -> customModelDialogModel.customVapOperationProperty().setValue(DELETE));

        voapListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                InjectorUisVAP currentVOAP = voapListView.getSelectionModel().getSelectedItem();
                voapListMenu.getItems().clear();
                if (defaultRB.isSelected()) {
                    if (currentVOAP != null)
                        voapListMenu.getItems().add(copyVOAP);
                } else {
                    voapListMenu.getItems().add(newVOAP);
                    if (currentVOAP != null)
                        voapListMenu.getItems().addAll(copyVOAP, editVOAP, deleteVOAP);
                }
                voapListMenu.show(voapListView, event.getScreenX(), event.getScreenY());
            } else {
                voapListMenu.hide();
            }
        });

        customVapUisDialogModel.doneProperty().addListener((observableValue, oldValue, newValue) -> {

            voapListView.getItems().clear();
            List<InjectorUisVAP> updatedList = uisVapService.findByIsCustomAndInjectorTypeAndInjectorSubType(!defaultRB.isSelected(), injTypeCB.getValue(), injSubTypeCB.getValue());
            voapListView.getItems().setAll(updatedList);
            voapListView.getSelectionModel().select((InjectorUisVAP) customVapUisDialogModel.customVapProperty().get());
            customModelDialogModel.customVapOperationProperty().setValue(null);
        });

        customVapUisDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                customModelDialogModel.customVapOperationProperty().setValue(null));

        bindingI18N();
    }

    private void create() {

        if(!isDataComplete()) return;

        if (injectorCodeTF.getText().isEmpty() || uisModelService.existsByModelCode(injectorCodeTF.getText())) {
            noUniqueLabel.setVisible(true);
            return;
        }

        noUniqueLabel.setVisible(false);
        InjectorUIS newInj = new InjectorUIS(injectorCodeTF.getText(),
                (ManufacturerUIS)mainSectionUisModel.manufacturerObjectProperty().get(),
                voapListView.getSelectionModel().getSelectedItem(), true);
        uisModelService.save(newInj);
        customModelDialogModel.customModelProperty().setValue(newInj);
        customModelDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void update() {

        if(!isDataComplete()) return;

        Model injectorForUpdate = mainSectionUisModel.modelProperty().get();
        injectorForUpdate.setVAP(voapListView.getSelectionModel().getSelectedItem());
        List<InjectorUisTest> injectorTests = uisTestService.findAllByModel(injectorForUpdate);
        injectorTests.clear();
        injectorTests.addAll(new LinkedList<>());
        uisModelService.save(injectorForUpdate);
        customModelDialogModel.customModelProperty().setValue(injectorForUpdate);
        customModelDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void delete() {

        Model injectorForUpdate = mainSectionUisModel.modelProperty().get();
        uisModelService.delete(injectorForUpdate);
        customModelDialogModel.customModelProperty().setValue(injectorForUpdate);
        customModelDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void setNew() {

        injectorCodeTF.setDisable(false);
        injTypeCB.setDisable(false);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);
        sureLabel.setVisible(false);
        injectorCodeTF.setText("");
        injTypeCB.getSelectionModel().select(0);
        defaultRB.setSelected(true);
    }

    private void setEdit() {

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);
        sureLabel.setVisible(false);
        injectorCodeTF.setText(mainSectionUisModel.modelProperty().get().getModelCode());
    }

    private void setDelete() {

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        injSubTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);
        sureLabel.setVisible(true);
        injectorCodeTF.setText(mainSectionUisModel.modelProperty().get().getModelCode());
    }

    private void selectFirst() {
        voapListView.getSelectionModel().select(0);
        voapListView.scrollTo(0);
    }
    private void changeLabels(InjectorUisVAP newValue) {
        if (newValue == null) {
            boostUvalue.setText("");
            boostEnableValue.setText("");
            boostIvalue.setText("");
            firstWvalue.setText("");
            firstIvalue.setText("");
            batteryUvalue.setText("");
            secondIvalue.setText("");
            negativeUvalue.setText("");
            boostI2Label.setText("");
            firstW2Label.setText("");
            firstI2Label.setText("");
            secondI2Label.setText("");
        } else {
            boostUvalue.setText(newValue.getBoostU().toString());
            boostEnableValue.setText(newValue.getBoostDisable().toString());
            boostIvalue.setText(newValue.getBoostI().toString());
            firstWvalue.setText(newValue.getFirstW().toString());
            firstIvalue.setText(newValue.getFirstI().toString());
            batteryUvalue.setText(newValue.getBatteryU().toString());
            secondIvalue.setText(newValue.getSecondI().toString());
            negativeUvalue.setText(newValue.getNegativeU().toString());

            if (newValue.getInjectorSubType() == InjectorSubType.DOUBLE_COIL) {
                boostI2Label.setText(newValue.getBoostI2().toString());
                firstW2Label.setText(newValue.getFirstW2().toString());
                firstI2Label.setText(newValue.getFirstI2().toString());
                secondI2Label.setText(newValue.getSecondI2().toString());
            }else{
                boostI2Label.setText("");
                firstW2Label.setText("");
                firstI2Label.setText("");
                secondI2Label.setText("");
            }
        }
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

    private boolean isDataComplete() {

        if (injectorCodeTF.getText().isEmpty() || voapListView.getSelectionModel().getSelectedItem() == null) showAlert();
        if(alert != null && alert.isShowing())return false;
        return true;
    }

    private void bindingI18N() {

        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        alertString.bind((i18N.createStringBinding("alert.customDialog")));
    }
}
