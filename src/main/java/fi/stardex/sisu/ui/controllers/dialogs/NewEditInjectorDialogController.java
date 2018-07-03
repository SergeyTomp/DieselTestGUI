package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.ui.ViewHolder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

public class NewEditInjectorDialogController {

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
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private Stage stage;
    private State currentState;

    @Autowired
    private InjectorTypeRepository injectorTypeRepository;
    @Autowired
    private VoltAmpereProfileRepository voltAmpereProfileRepository;
    @Autowired
    private ViewHolder newEditVOAPDialog;

    private Stage newVOAPStage;

    @PostConstruct
    private void init() {
        injectorTypeRepository.findAll().forEach(injectorType -> injTypeCB.getItems().add(injectorType));

        injTypeCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            voapListView.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(!defaultRB.isSelected(), newValue));
        });

        baseType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            voapListView.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(!defaultRB.isSelected(),
                    injTypeCB.getSelectionModel().getSelectedItem()));
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

    public void setNew() {
        currentState = State.NEW;
        injTypeCB.getSelectionModel().select(0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private enum State {
        NEW
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
