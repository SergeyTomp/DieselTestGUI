package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomModelDialogModel;
import fi.stardex.sisu.model.uis.CustomVapUisDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.util.enums.GUI_type.UIS;
import static fi.stardex.sisu.util.enums.Operation.*;

public class CustomInjectorUisDialogController {

    @FXML private Label nameLabel;
    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
    @FXML private Label voltageLabel;
    @FXML private Label boostULabel;
    @FXML private Label boostOnLabel;
    @FXML private Label batteryULabel;
    @FXML private Label boostI1Label;
    @FXML private Label firstW1Label;
    @FXML private Label firstI1Label;
    @FXML private Label secondI1Label;
    @FXML private Label negativeULabel;
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
    @FXML private Label firstW2Value;
    @FXML private Label boostI2Value;
    @FXML private Label firstI2Value;
    @FXML private Label secondI2Value;

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
    private StringBuilder codeBuilder = new StringBuilder();

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
                case COPY:
                    setCopy();
                    break;
            }
            dialogStage.setTitle(mainSectionUisModel.customModelOperationProperty().get().getTitle() + "injector");
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
                case COPY:
                    copy();
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

            if (defaultRB.isSelected()) {
                customRB.setSelected(true);
            } else {
                List<InjectorUisVAP> updatedList = uisVapService.findByIsCustomAndInjectorTypeAndInjectorSubType(true, injTypeCB.getValue(), injSubTypeCB.getValue());
                voapListView.getItems().setAll(updatedList);
            }
            if (customModelDialogModel.customVapOperationProperty().get() == DELETE) {
                voapListView.getSelectionModel().clearSelection();
            } else {
                voapListView.getSelectionModel().select((InjectorUisVAP) customVapUisDialogModel.customVapProperty().get());
            }
            customModelDialogModel.customVapOperationProperty().setValue(null);
        });

        customVapUisDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                customModelDialogModel.customVapOperationProperty().setValue(null));

        bindingI18N();
    }

    private void create() {

        if(!isDataComplete()) return;

        if (uisModelService.existsByModelCode(injectorCodeTF.getText())) {
            noUniqueLabel.setVisible(true);
            return;
        }

        noUniqueLabel.setVisible(false);
        InjectorUIS newInj = new InjectorUIS(injectorCodeTF.getText(),
                (ManufacturerUIS)mainSectionUisModel.manufacturerObjectProperty().get(),
                voapListView.getSelectionModel().getSelectedItem(), true);
        uisModelService.save(newInj);
        complete(newInj);
    }

    private void update() {

        if(!isDataComplete()) return;

        Model injectorForUpdate = mainSectionUisModel.modelProperty().get();
        injectorForUpdate.setVAP(voapListView.getSelectionModel().getSelectedItem());
        List<InjectorUisTest> injectorTests = uisTestService.findAllByModel(injectorForUpdate);
        injectorTests.clear();
        injectorTests.addAll(new LinkedList<>());
        uisModelService.save(injectorForUpdate);
        complete(injectorForUpdate);
    }

    private void delete() {

        Model injectorForUpdate = mainSectionUisModel.modelProperty().get();
        uisModelService.delete(injectorForUpdate);
        complete(null);
    }

    private void copy() {

        if(!isDataComplete()) return;

        Model injectorForCopy = mainSectionUisModel.modelProperty().get();
        Producer producer = mainSectionUisModel.manufacturerObjectProperty().get();
        List<Test> testList = mainSectionUisModel.getTestObservableList();
        VAP vap = injectorForCopy.getVAP();

        String newCode = makeCode(injectorCodeTF.getText());
        InjectorUIS copy = new InjectorUIS(newCode, (ManufacturerUIS)producer, (InjectorUisVAP)vap, true);
        List<InjectorUisTest> newTestList = new ArrayList<>();
        testList.forEach(test -> newTestList.add(new InjectorUisTest((InjectorUisTest) test, copy)));
        copy.getInjectorTests().addAll(newTestList);

        uisModelService.save(copy);
        complete(copy);
    }

    private void complete(Model injector) {
        customModelDialogModel.customModelProperty().setValue(injector);
        customModelDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void setCopy() {

        injectorCodeTF.setDisable(false);

        injTypeCB.setDisable(true);
        injSubTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);
        sureLabel.setVisible(false);

        Model model = mainSectionUisModel.modelProperty().get();
        InjectorUisVAP vap = (InjectorUisVAP)model.getVAP();
        injectorCodeTF.setText(model.getModelCode());
        injTypeCB.getSelectionModel().select(vap.getInjectorType());
        injSubTypeCB.getSelectionModel().select(vap.getInjectorSubType());
        if (vap.isCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);
        voapListView.scrollTo(vap);
        voapListView.getSelectionModel().select(vap);
    }

    private void setNew() {

        injectorCodeTF.setDisable(false);
        injTypeCB.setDisable(false);
        injSubTypeCB.setDisable(false);
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
        injSubTypeCB.setDisable(true);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);
        sureLabel.setVisible(false);
        Model model = mainSectionUisModel.modelProperty().get();
        injectorCodeTF.setText(model.getModelCode());
        InjectorUisVAP vap = (InjectorUisVAP)model.getVAP();
        injTypeCB.getSelectionModel().select(vap.getInjectorType());
        injSubTypeCB.getSelectionModel().select(vap.getInjectorSubType());
        if (vap.isCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);
        voapListView.scrollTo(vap);
        voapListView.getSelectionModel().select(vap);
    }

    private void setDelete() {

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        injSubTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);
        sureLabel.setVisible(true);
        Model model = mainSectionUisModel.modelProperty().get();
        injectorCodeTF.setText(model.getModelCode());
        InjectorUisVAP vap = (InjectorUisVAP)model.getVAP();
        injTypeCB.getSelectionModel().select(vap.getInjectorType());
        injSubTypeCB.getSelectionModel().select(vap.getInjectorSubType());
        if (vap.isCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);
        voapListView.scrollTo(vap);
        voapListView.getSelectionModel().select(vap);
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
            boostI2Value.setText("");
            firstW2Value.setText("");
            firstI2Value.setText("");
            secondI2Value.setText("");
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
                boostI2Value.setText(newValue.getBoostI2().toString());
                firstW2Value.setText(newValue.getFirstW2().toString());
                firstI2Value.setText(newValue.getFirstI2().toString());
                secondI2Value.setText(newValue.getSecondI2().toString());
            }else{
                boostI2Value.setText("");
                firstW2Value.setText("");
                firstI2Value.setText("");
                secondI2Value.setText("");
            }
        }
    }

    private String makeCode(String code) {
        codeBuilder.setLength(0);
        if (uisModelService.existsByModelCode(code)) {
            if (code.contains("(")) {
                int indexBKT1 = code.indexOf('(');
                int indexBKT2 = code.indexOf(')');
                int count = Integer.valueOf(code.substring(indexBKT1 + 1, indexBKT2));
                count++;
                codeBuilder.append(code, 0, indexBKT1).append("(").append(count).append(")");
            } else {
                codeBuilder.append(code).append("(1)");
            }
            return makeCode(codeBuilder.toString());
        }else {
            return code;
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

        nameLabel.textProperty().bind(i18N.createStringBinding("h4.report.table.label.injectorName"));
        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        alertString.bind((i18N.createStringBinding("alert.customDialog")));
        saveBtn.textProperty().bind(i18N.createStringBinding("h4.delay.button.save"));
        cancelBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
        defaultRB.textProperty().bind(i18N.createStringBinding("main.defaultRB.radiobutton"));
        customRB.textProperty().bind(i18N.createStringBinding("main.customRB.radiobutton"));
        boostI2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        firstW2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstI2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        secondI2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        boostI1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        firstW1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstI1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        secondI1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        boostOnLabel.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUdisabled"));
        boostULabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostU"));
        batteryULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltageHold"));
        negativeULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.firstNegativeVoltage"));
        coil1Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil1"));
        coil2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil2"));
        voltageLabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltage"));
    }
}
