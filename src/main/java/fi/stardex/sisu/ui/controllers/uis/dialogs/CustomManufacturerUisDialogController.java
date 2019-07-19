package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomProducerDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
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

import static fi.stardex.sisu.ui.controllers.common.GUI_TypeController.GUIType.UIS;

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

    @PostConstruct
    public void init() {

        mainSectionUisModel.customProducerProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;
            if (guiTypeModel.guiTypeProperty().get() == UIS) {

                if (dialogStage == null) {
                    dialogStage = new Stage();
                    dialogStage.setScene(new Scene(dialogViev));
                    dialogStage.setResizable(false);
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.setTitle(mainSectionUisModel.customProducerProperty().get().getTitle() + "manufacturer");
                    dialogStage.setOnCloseRequest(event -> customProducerDialogModel.cancelProperty().setValue(new Object()));
                }
                dialogStage.show();
            }
        });

        cancelBtn.setOnMouseClicked(event -> {

            customProducerDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });

    }

    private void create() {


        customProducerDialogModel.doneProperty().setValue(new Object());
    }

    private void delete() {


        customProducerDialogModel.doneProperty().setValue(new Object());
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
