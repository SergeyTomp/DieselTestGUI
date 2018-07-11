package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Consumer;

public class NewEditInjectorDialogController {


    @FXML
    private GridPane rootGridPane;
    @FXML
    private TextField injectorCodeTF;
    @FXML
    private Label noUniqueLabel;
    @FXML
    private ComboBox<InjectorType> injTypeCB;
    @FXML
    private RadioButton defaultRB;
    @FXML
    private ToggleGroup baseType;
    @FXML
    private RadioButton customRB;
    @FXML
    private ListView<VoltAmpereProfile> voapListView;
    @FXML
    private Label sureLabel;
    @FXML
    private ButtonBar controlBtnBar;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;


    @FXML
    private Label boostUvalue;
    @FXML
    private Label boostEnableValue;
    @FXML
    private Label boostIvalue;
    @FXML
    private Label firstWvalue;
    @FXML
    private Label firstIvalue;
    @FXML
    private Label batteryUvalue;
    @FXML
    private Label secondIvalue;
    @FXML
    private Label negativeUvalue;

    private Stage stage;
    private State currentState;
    private ListView<Model> modelListView;

    public void setModelListView(ListView<Model> modelListView) {
        this.modelListView = modelListView;
    }

    @Autowired
    private InjectorTypeRepository injectorTypeRepository;
    @Autowired
    private VoltAmpereProfileRepository voltAmpereProfileRepository;
    @Autowired
    private InjectorsRepository injectorsRepository;
    @Autowired
    private ViewHolder newEditVOAPDialog;
    @Autowired
    private CurrentManufacturerObtainer currentManufacturerObtainer;
    @Autowired
    private CurrentInjectorObtainer currentInjectorObtainer;

    private Stage newVOAPStage;

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
                case DELETE:
                    deleteInjector();
            }
        });

        cancelBtn.setOnMouseClicked(event -> stage.close());

        injectorTypeRepository.findAll().forEach(injectorType -> injTypeCB.getItems().add(injectorType));

        injTypeCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                voapListView.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(!defaultRB.isSelected(), newValue)));

        baseType.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                voapListView.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(!defaultRB.isSelected(),
                injTypeCB.getSelectionModel().getSelectedItem())));

        voapListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeLabels(newValue);
        });

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
    }

    private void checkAndSaveInjector() {
        if (injectorCodeTF.getText().isEmpty() || injectorsRepository.existsById(injectorCodeTF.getText())) {
            noUniqueLabel.setVisible(true);
            return;
        }

        noUniqueLabel.setVisible(false);
        Injector newInj = new Injector(injectorCodeTF.getText(), currentManufacturerObtainer.getCurrentManufacturer(),
                voapListView.getSelectionModel().getSelectedItem(), true);

        injectorsRepository.save(newInj);
        modelListView.getItems().add(newInj);
        modelListView.getSelectionModel().select(newInj);
        modelListView.scrollTo(newInj);
        stage.close();
    }

    private void updateInjector() {
        Injector injectorForUpdate = currentInjectorObtainer.getInjector();
        injectorForUpdate.setVoltAmpereProfile(voapListView.getSelectionModel().getSelectedItem());
        injectorsRepository.save(injectorForUpdate);

        stage.close();
    }

    private void deleteInjector() {
        Injector injector = currentInjectorObtainer.getInjector();
        injectorsRepository.delete(injector);
        modelListView.getItems().remove(injector);
        currentManufacturerObtainer.getCurrentManufacturer().getInjectors().remove(injector);

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
        } else {
            boostUvalue.setText(newValue.getBoostU().toString());
            boostEnableValue.setText(newValue.getBoostDisable().toString());
            boostIvalue.setText(newValue.getBoostI().toString());
            firstWvalue.setText(newValue.getFirstW().toString());
            firstIvalue.setText(newValue.getFirstI().toString());
            batteryUvalue.setText(newValue.getBatteryU().toString());
            secondIvalue.setText(newValue.getSecondI().toString());
            negativeUvalue.setText(newValue.getNegativeU().toString());
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
        voapListView.getSelectionModel().select(0);
        voapListView.scrollTo(0);
    }

    public void setEdit() {
        currentState = State.EDIT;
        Injector injector = currentInjectorObtainer.getInjector();

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);

        sureLabel.setVisible(false);

        injectorCodeTF.setText(injector.getInjectorCode());
        injTypeCB.getSelectionModel().select(injector.getVoltAmpereProfile().getInjectorType());
        if(injector.getVoltAmpereProfile().getCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);

        System.err.println(injector.getVoltAmpereProfile());
        System.err.println(voapListView.getItems().contains(injector.getVoltAmpereProfile()));

        voapListView.scrollTo(injector.getVoltAmpereProfile());
        voapListView.getSelectionModel().select(injector.getVoltAmpereProfile());
    }

    public void setDelete() {
        currentState = State.DELETE;
        Injector injector = currentInjectorObtainer.getInjector();

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);

        sureLabel.setVisible(true);

        injectorCodeTF.setText(injector.getInjectorCode());
        injTypeCB.getSelectionModel().select(injector.getVoltAmpereProfile().getInjectorType());
        if(injector.getVoltAmpereProfile().getCustom())
            customRB.setSelected(true);
        else
            defaultRB.setSelected(true);
        voapListView.getSelectionModel().select(injector.getVoltAmpereProfile());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private enum State {
        NEW, EDIT, DELETE
    }

    private class VoapListEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<NewEditVOAPDialogController> dialogType;

        public VoapListEventHandler(String title, Consumer<NewEditVOAPDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            NewEditVOAPDialogController controller = (NewEditVOAPDialogController) newEditVOAPDialog.getController();
            if (newVOAPStage == null) {
                newVOAPStage = new Stage();
                newVOAPStage.setScene(new Scene(newEditVOAPDialog.getView(), 600, 400));
                newVOAPStage.setResizable(false);
                newVOAPStage.initModality(Modality.APPLICATION_MODAL);
//                manufacturerDialogStage.initStyle(StageStyle.UNDECORATED);
                controller.setStage(newVOAPStage);
                controller.setVoapList(voapListView);
            }
            newVOAPStage.setTitle(title);
            dialogType.accept(controller);
            controller.setInjectorType(injTypeCB.getSelectionModel().getSelectedItem());
            newVOAPStage.show();
        }
    }
}
