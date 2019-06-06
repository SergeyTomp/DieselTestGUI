package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.MainSectionModel;
import fi.stardex.sisu.model.ManufacturerMenuDialogModel;
import fi.stardex.sisu.persistence.Producer;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.ManufacturerHEUI;
import fi.stardex.sisu.persistence.repos.HEUI.ManufacturerHeuiRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.ui.ViewHolder;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

public class ManufacturerMenuDialogController {

    private State currentState;

    @FXML private Label nameLabel;
    @FXML private TextField nameTF;
    @FXML private Button applyBtn;
    @FXML private Button cancelBtn;
    @FXML private Label notUniqueLabel;

    private ManufacturerRepository manufacturerRepository;
    private ManufacturerHeuiRepository manufacturerHeuiRepository;
    private ManufacturerMenuDialogModel manufacturerMenuDialogModel;
    private GUI_TypeModel gui_typeModel;
    private MainSectionModel mainSectionModel;
    private Stage stage;
    private ViewHolder manufacturerMenuDialog;

    public void setManufacturerRepository(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public void setManufacturerHeuiRepository(ManufacturerHeuiRepository manufacturerHeuiRepository) {
        this.manufacturerHeuiRepository = manufacturerHeuiRepository;
    }

    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    public void setManufacturerMenuDialogModel(ManufacturerMenuDialogModel manufacturerMenuDialogModel) {
        this.manufacturerMenuDialogModel = manufacturerMenuDialogModel;
    }

    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }

    public void setManufacturerMenuDialog(ViewHolder manufacturerMenuDialog) {
        this.manufacturerMenuDialog = manufacturerMenuDialog;
    }

    @PostConstruct
    private void init() {

        applyBtn.setOnMouseClicked(event -> {
            switch (currentState) {
                case NEW:
                    create();
                    break;
                case DELETE:
                    delete();
                    break;
            }
        });

        cancelBtn.setOnMouseClicked(event -> stage.close());

        gui_typeModel.guiTypeProperty().addListener((observable, oldValue, newValue) ->
                manufacturerMenuDialogModel.customManufacturerProperty().setValue(null));

        mainSectionModel.makeOrDelProducerProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) return;
            switch (newValue) {
                case NEW:
                    setNew();
                    break;
                case DELETE:
                    setDelete();
                    break;
            }
            if (stage == null) {
                stage = new Stage();
                stage.setScene(new Scene(manufacturerMenuDialog.getView(), 200, 130));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }
            stage.setTitle(newValue.getTitle());
            nameTF.requestFocus();
            stage.show();
        });
    }

    private void create() {
        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setManufacturerName(nameTF.getText());
        newManufacturer.setCustom(true);

        if (mainSectionModel.getManufacturerObservableList().contains(newManufacturer)) {
            notUniqueLabel.setVisible(true);
        }
        else {
            notUniqueLabel.setVisible(false);
            switch (gui_typeModel.guiTypeProperty().get()) {
                case CR_Inj:
                    manufacturerRepository.save(newManufacturer);
                    break;
                case HEUI:
                    manufacturerHeuiRepository.save(convert(newManufacturer));
                    break;
                default:
                    break;
            }

            manufacturerMenuDialogModel.customManufacturerProperty().setValue(newManufacturer);
            manufacturerMenuDialogModel.doneProperty().setValue(new Object());
            stage.close();
        }
    }

    private void delete() {

        Manufacturer manufacturer = mainSectionModel.manufacturerObjectProperty().get();

        switch (gui_typeModel.guiTypeProperty().get()) {
            case CR_Inj:
                manufacturerRepository.delete(manufacturer);
                break;
            case HEUI:
                manufacturerHeuiRepository.delete(convert(manufacturer));
                break;
            default:
                break;
        }
        manufacturerMenuDialogModel.customManufacturerProperty().setValue(null);
        manufacturerMenuDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    public void setNew() {
        nameTF.setDisable(false);
        nameLabel.setVisible(true);
        nameTF.setText("");
        currentState = State.NEW;
    }


    public void setDelete() {
        nameTF.setDisable(true);
        nameLabel.setVisible(false);
        nameTF.setText(mainSectionModel.manufacturerObjectProperty().get().getManufacturerName());
        currentState = State.DELETE;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private ManufacturerHEUI convert(Producer producer) {

        ManufacturerHEUI p = new ManufacturerHEUI();
        p.setManufacturerName(producer.getManufacturerName());
        p.setCustom(producer.isCustom());
        return p;
    }

    public enum State {
        NEW("New manufacturer"),
        DELETE("Delete manufacturer");

        private String title;

        State(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
