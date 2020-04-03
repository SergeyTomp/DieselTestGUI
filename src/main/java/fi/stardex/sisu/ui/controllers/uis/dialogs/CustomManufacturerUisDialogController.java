package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomProducerDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.uis.ManufacturerUIS;
import fi.stardex.sisu.persistence.repos.uis.UisProducerService;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.GUI_type.UIS;

public class CustomManufacturerUisDialogController {

    @FXML private Label nameLabel;
    @FXML private TextField nameTF;
    @FXML private Button applyBtn;
    @FXML private Button cancelBtn;
    @FXML private Label notUniqueLabel;

    private MainSectionUisModel mainSectionUisModel;
    private Stage dialogStage;
    private Parent dialogViev;
    private GUI_TypeModel guiTypeModel;
    private CustomProducerDialogModel customProducerDialogModel;
    private I18N i18N;
    private UisProducerService uisProducerService;

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setDialogViev(Parent dialogViev) {
        this.dialogViev = dialogViev;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setCustomProducerDialogModel(CustomProducerDialogModel customProducerDialogModel) {
        this.customProducerDialogModel = customProducerDialogModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisProducerService(UisProducerService uisProducerService) {
        this.uisProducerService = uisProducerService;
    }

    @PostConstruct
    public void init() {

        mainSectionUisModel.customProducerOperationProperty().addListener((observableValue, oldValue, newValue) -> {

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
                    dialogStage.setOnCloseRequest(event -> customProducerDialogModel.cancelProperty().setValue(new Object()));
                }
                switch (mainSectionUisModel.customProducerOperationProperty().get()) {
                    case NEW:
                        setNew();
                        break;
                    case DELETE:
                        setDelete();
                        break;
                }
                dialogStage.setTitle(mainSectionUisModel.customProducerOperationProperty().get().getTitle() + " manufacturer");
                dialogStage.show();
        });

        cancelBtn.setOnMouseClicked(mouseEvent -> {

            customProducerDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });

        applyBtn.setOnMouseClicked(mouseEvent -> {

            switch (mainSectionUisModel.customProducerOperationProperty().get()) {
                case NEW:
                    create();
                    break;
                case DELETE:
                    delete();
                    break;
            }
        });

        guiTypeModel.guiTypeProperty().addListener((observable, oldValue, newValue) ->
                customProducerDialogModel.customProducerProperty().setValue(null));
    }

    private void create() {

        ManufacturerUIS newManufacturer = new ManufacturerUIS();
        newManufacturer.setManufacturerName(nameTF.getText() + "_U");
        newManufacturer.setDisplayOrder(mainSectionUisModel.getProducerObservableList().size() + 1);
        newManufacturer.setCustom(true);

        if (mainSectionUisModel.getProducerObservableList().contains(newManufacturer)) {
            notUniqueLabel.setVisible(true);
        }
        else {
            notUniqueLabel.setVisible(false);
            uisProducerService.save(newManufacturer);
            customProducerDialogModel.customProducerProperty().setValue(newManufacturer);
            customProducerDialogModel.doneProperty().setValue(new Object());
            dialogStage.close();
        }
    }

    private void delete() {

        Producer producer = mainSectionUisModel.manufacturerObjectProperty().get();
        uisProducerService.delete(producer);
        customProducerDialogModel.customProducerProperty().setValue(null);
        customProducerDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void setNew() {

        nameTF.setDisable(false);
        nameLabel.setVisible(true);
        nameTF.setText("");
    }

    private void setDelete() {

        nameTF.setDisable(true);
        nameLabel.setVisible(false);
        nameTF.setText(mainSectionUisModel.manufacturerObjectProperty().get().getManufacturerName());
    }
}
