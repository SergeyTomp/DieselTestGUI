package fi.stardex.sisu.ui.controllers.cr.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.model.cr.NewEditInjectorDialogModel;
import fi.stardex.sisu.persistence.orm.cr.inj.*;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static fi.stardex.sisu.util.enums.GUI_type.HEUI;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;
import static fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer.getManufacturer;

public class NewEditInjectorDialogController {

    @FXML private Label boostOnLabel;
    @FXML private Label nameLabel;
    @FXML private Label boostULabel;
    @FXML private Label boostI_1Label;
    @FXML private Label firstW1Label;
    @FXML private Label firstI_1Label;
    @FXML private Label batteryULabel;
    @FXML private Label secondI_1Label;
    @FXML private Label negativeULabel;
    @FXML private Label boostI_2Label;
    @FXML private Label firstI_2Label;
    @FXML private Label secondI_2Label;
    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
    @FXML private Label voltageLabel;
    @FXML private Label firstW2Label;

    @FXML private Label firstW2value;
    @FXML private Label boostI2Label;
    @FXML private Label firstI2Label;
    @FXML private Label secondI2Label;
    @FXML private GridPane rootGridPane;
    @FXML private TextField injectorCodeTF;
    @FXML private Label noUniqueLabel;
    @FXML private ComboBox<InjectorType> injTypeCB;
    @FXML private RadioButton defaultRB;
    @FXML private ToggleGroup baseType;
    @FXML private RadioButton customRB;
    @FXML private ListView<VoltAmpereProfile> voapListView;
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

    private Stage stage;
    private Stage newVOAPStage;
    private State currentState;
    private ListView<Model> modelListView;
    private InjectorTypeRepository injectorTypeRepository;
    private InjectorTestRepository injectorTestRepository;
    private VoltAmpereProfileRepository voltAmpereProfileRepository;
    private InjectorsRepository injectorsRepository;
    private ViewHolder newEditVOAPDialog;
    private GUI_TypeModel gui_typeModel;
    private NewEditInjectorDialogModel newEditInjectorDialogModel;
    private MainSectionModel mainSectionModel;
    private StringBuilder codeBuilder = new StringBuilder();
    private I18N i18N;

    public void setModelListView(ListView<Model> modelListView) {
        this.modelListView = modelListView;
    }
    public void setInjectorTypeRepository(InjectorTypeRepository injectorTypeRepository) {
        this.injectorTypeRepository = injectorTypeRepository;
    }
    public void setVoltAmpereProfileRepository(VoltAmpereProfileRepository voltAmpereProfileRepository) {
        this.voltAmpereProfileRepository = voltAmpereProfileRepository;
    }
    public void setInjectorsRepository(InjectorsRepository injectorsRepository) {
        this.injectorsRepository = injectorsRepository;
    }
    public void setNewEditVOAPDialog(ViewHolder newEditVOAPDialog) {
        this.newEditVOAPDialog = newEditVOAPDialog;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setNewEditInjectorDialogModel(NewEditInjectorDialogModel newEditInjectorDialogModel) {
        this.newEditInjectorDialogModel = newEditInjectorDialogModel;
    }
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }
    public void setInjectorTestRepository(InjectorTestRepository injectorTestRepository) {
        this.injectorTestRepository = injectorTestRepository;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }


    @PostConstruct
    private void init() {
        saveBtn.setOnMouseClicked(event -> {
            switch (currentState) {
                case NEW:
                    checkAndSaveInjector();
                    break;
                case EDIT:
                    updateInjector();
                    break;
                case COPY:
                    copyInjector();
                    break;
                case DELETE:
                    deleteInjector();
            }
        });

        cancelBtn.setOnMouseClicked(event -> {
            newEditInjectorDialogModel.cancelProperty().setValue(new Object());
            stage.close();
        });

        injectorTypeRepository.findAll().forEach(injectorType -> injTypeCB.getItems().add(injectorType));

        injTypeCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            voapListView.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(!defaultRB.isSelected(), newValue));
            selectFirst();
        });

        baseType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            voapListView.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(!defaultRB.isSelected(),
                    injTypeCB.getSelectionModel().getSelectedItem()));
            selectFirst();
        });

        voapListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeLabels(newValue));

        ContextMenu voapListMenu = new ContextMenu();
        MenuItem newVOAP = new MenuItem("New");
        newVOAP.setOnAction(new VoapListEventHandler("New VOAP", NewEditVOAPDialogController::setNew));
        MenuItem copyVOAP = new MenuItem("Copy");

        MenuItem editVOAP = new MenuItem("Edit");
        MenuItem deleteVOAP = new MenuItem("Delete");

        voapListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                VoltAmpereProfile currentVOAP = voapListView.getSelectionModel().getSelectedItem();
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
        bindingI18N();
    }

    private void checkAndSaveInjector() {
        if (injectorCodeTF.getText().isEmpty() || injectorsRepository.existsById(injectorCodeTF.getText())) {
            noUniqueLabel.setVisible(true);
            return;
        }

        noUniqueLabel.setVisible(false);
        Injector newInj = new Injector(injectorCodeTF.getText(), getManufacturer(),
                voapListView.getSelectionModel().getSelectedItem(), true, -1);
        if (gui_typeModel.guiTypeProperty().get() == HEUI) {
            newInj.setHeui(true);
        }
        else newInj.setHeui(false);

        injectorsRepository.save(newInj);
        newEditInjectorDialogModel.customInjectorProperty().setValue(newInj);
        newEditInjectorDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    private void updateInjector() {
        Injector injectorForUpdate = getInjector();
        injectorForUpdate.setVoltAmpereProfile(voapListView.getSelectionModel().getSelectedItem());
        List<InjectorTest> injectorTests = injectorTestRepository.findAllByInjector(injectorForUpdate);
        injectorTests.clear();
        injectorTests.addAll(new LinkedList<>());
        injectorsRepository.save(injectorForUpdate);
        stage.close();
    }

    private void copyInjector() {

        Injector baseInjector = mainSectionModel.injectorProperty().get();
        Manufacturer manufacturer = mainSectionModel.manufacturerObjectProperty().get();
        List<InjectorTest> testList = mainSectionModel.getInjectorTests();
        VoltAmpereProfile voltAmpereProfile = baseInjector.getVoltAmpereProfile();
        Integer codetype = baseInjector.getCodetype();
        String newCode = makeCode(injectorCodeTF.getText());

        Injector copy = new Injector(newCode, manufacturer, voltAmpereProfile, true, codetype);
        copy.setHeui(baseInjector.isHeui());
        copy.setCalibrationId(baseInjector.getCalibrationId());
        copy.setCodetype(baseInjector.getCodetype());
        copy.setCoefficient(baseInjector.getCoefficient());
        List<InjectorTest> newTestsList = new ArrayList<>();
        testList.forEach(test -> newTestsList.add(new InjectorTest(test, copy)));
        copy.getInjectorTests().addAll(newTestsList);

        injectorsRepository.save(copy);
        newEditInjectorDialogModel.customInjectorProperty().setValue(copy);
        newEditInjectorDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    private void deleteInjector() {
        Injector injector = getInjector();
        injectorsRepository.delete(injector);
        newEditInjectorDialogModel.customInjectorProperty().setValue(null);
        newEditInjectorDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    private void changeLabels(VoltAmpereProfile newValue) {
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
            firstW2value.setText("");
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

            if (newValue.isDoubleCoil()) {
                boostI2Label.setText(newValue.getBoostI2().toString());
                firstW2value.setText(newValue.getFirstW2().toString());
                firstI2Label.setText(newValue.getFirstI2().toString());
                secondI2Label.setText(newValue.getSecondI2().toString());
            }else{
                boostI2Label.setText("");
                firstW2value.setText("");
                firstI2Label.setText("");
                secondI2Label.setText("");
            }
        }
    }

    public void setNew() {
        currentState = State.NEW;

        injectorCodeTF.setDisable(false);
        injTypeCB.setDisable(false);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);

        sureLabel.setVisible(false);

        injectorCodeTF.setText("");

        injTypeCB.getSelectionModel().select(0);
        defaultRB.setSelected(true);
        selectFirst();
    }

    public void setEdit() {
        currentState = State.EDIT;
        Injector injector = getInjector();

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);

        sureLabel.setVisible(false);

        VoltAmpereProfile voltAmpereProfile = injector.getVoltAmpereProfile();

        injectorCodeTF.setText(injector.getInjectorCode());
        injTypeCB.getSelectionModel().select(voltAmpereProfile.getInjectorType());
        if (voltAmpereProfile.isCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);

        voapListView.scrollTo(voltAmpereProfile);
        voapListView.getSelectionModel().select(voltAmpereProfile);
    }

    public void setCopy() {

        currentState = State.COPY;

        injectorCodeTF.setDisable(false);
        injTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);
        sureLabel.setVisible(false);

        Injector injector = mainSectionModel.injectorProperty().get();
        VoltAmpereProfile voltAmpereProfile = injector.getVoltAmpereProfile();
        injectorCodeTF.setText(injector.getInjectorCode());
        injTypeCB.getSelectionModel().select(voltAmpereProfile.getInjectorType());
        if (voltAmpereProfile.isCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);
        voapListView.scrollTo(voltAmpereProfile);
        voapListView.getSelectionModel().select(voltAmpereProfile);
    }

    public void setDelete() {
        currentState = State.DELETE;
        Injector injector = getInjector();

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);

        sureLabel.setVisible(true);

        injectorCodeTF.setText(injector.getInjectorCode());

        VoltAmpereProfile voltAmpereProfile = injector.getVoltAmpereProfile();

        injTypeCB.getSelectionModel().select(voltAmpereProfile.getInjectorType());
        if (voltAmpereProfile.isCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);
        voapListView.getSelectionModel().select(voltAmpereProfile);
    }

    private String makeCode(String code) {
        codeBuilder.setLength(0);
        if (injectorsRepository.existsByInjectorCode(code)) {
            if (code.contains("(")) {
                int indexBKT1 = code.indexOf('(');
                int indexBKT2 = code.indexOf(')');
                int count = Integer.valueOf(code.substring(indexBKT1 + 1, indexBKT2));
                count++;
                codeBuilder.append(code.substring(0, indexBKT1)).append("(").append(count).append(")");
            } else {
                codeBuilder.append(code).append("(1)");
            }
            return makeCode(codeBuilder.toString());
        }else {
            return code;
        }
    }

    private enum State {
        NEW, EDIT, COPY, DELETE
    }

    private void selectFirst() {
        voapListView.getSelectionModel().select(0);
        voapListView.scrollTo(0);
    }

    private class VoapListEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<NewEditVOAPDialogController> dialogType;

        VoapListEventHandler(String title, Consumer<NewEditVOAPDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            NewEditVOAPDialogController controller = (NewEditVOAPDialogController) newEditVOAPDialog.getController();
            if (newVOAPStage == null) {
                newVOAPStage = new Stage();
                newVOAPStage.setScene(new Scene(newEditVOAPDialog.getView()));
                newVOAPStage.setResizable(false);
                newVOAPStage.initModality(Modality.APPLICATION_MODAL);
                controller.setStage(newVOAPStage);
                controller.setVoapList(voapListView);
            }
            newVOAPStage.setTitle(title);
            dialogType.accept(controller);
            controller.setInjectorType(injTypeCB.getSelectionModel().getSelectedItem());
            newVOAPStage.show();
        }
    }

    private void bindingI18N(){
        nameLabel.textProperty().bind(i18N.createStringBinding("h4.report.table.label.injectorName"));
        coil1Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil1"));
        coil2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil2"));
        boostOnLabel.textProperty().bind(i18N.createStringBinding("voapProfile.button.boostUdisabled"));
        boostULabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostU"));
        voltageLabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltage"));
        batteryULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltageHold"));
        negativeULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.firstNegativeVoltage"));
        boostI_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        boostI_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        firstW1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstW2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstI_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        firstI_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        secondI_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        secondI_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        saveBtn.textProperty().bind(i18N.createStringBinding("h4.delay.button.save"));
        cancelBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
        defaultRB.textProperty().bind(i18N.createStringBinding("main.defaultRB.radiobutton"));
        customRB.textProperty().bind(i18N.createStringBinding("main.customRB.radiobutton"));
    }
}
