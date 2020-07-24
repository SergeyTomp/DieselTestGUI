package fi.stardex.sisu.ui.controllers.pumps.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.pump.CustomPumpProducerDialogModel;
import fi.stardex.sisu.model.pump.ManufacturerPumpModel;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.repos.pump.PumpProducerService;
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

import static fi.stardex.sisu.util.enums.GUI_type.CR_Pump;

public class CustomProducerPumpDialogController {

    @FXML private Label nameLabel;
    @FXML private TextField nameTF;
    @FXML private Button applyBtn;
    @FXML private Button cancelBtn;
    @FXML private Label notUniqueLabel;

    private Stage dialogStage;
    private Parent dialogViev;
    private GUI_TypeModel guiTypeModel;
    private I18N i18N;
    private PumpProducerService pumpProducerService;
    private CustomPumpProducerDialogModel customPumpProducerDialogModel;
    private ManufacturerPumpModel manufacturerPumpModel;

    public void setDialogViev(Parent dialogViev) {
        this.dialogViev = dialogViev;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpProducerService(PumpProducerService pumpProducerService) {
        this.pumpProducerService = pumpProducerService;
    }
    public void setCustomPumpProducerDialogModel(CustomPumpProducerDialogModel customPumpProducerDialogModel) {
        this.customPumpProducerDialogModel = customPumpProducerDialogModel;
    }
    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }

    @PostConstruct
    public void init() {

        manufacturerPumpModel.customProducerOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            /** Additional check of GUI type is done to prevent listener invocation and dialog window irrelevant to GUI type activation.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf PumpTest} but it is slower */
            if (guiTypeModel.guiTypeProperty().get() != CR_Pump) {return;}
            if(newValue == null) return;

            if (dialogStage == null) {
                dialogStage = new Stage();
                dialogStage.setScene(new Scene(dialogViev));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setOnCloseRequest(event -> customPumpProducerDialogModel.cancelProperty().setValue(new Object()));
            }
            switch (manufacturerPumpModel.customProducerOperationProperty().get()) {
                case NEW:
                    setNew();
                    break;
                case DELETE:
                    setDelete();
                    break;
            }
            dialogStage.setTitle(manufacturerPumpModel.customProducerOperationProperty().get().getTitle() + "manufacturer");
            dialogStage.show();
        });

        cancelBtn.setOnMouseClicked(mouseEvent -> {

            customPumpProducerDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });

        applyBtn.setOnMouseClicked(mouseEvent -> {

            switch (manufacturerPumpModel.customProducerOperationProperty().get()) {
                case NEW:
                    create();
                    break;
                case DELETE:
                    delete();
                    break;
            }

        });

        guiTypeModel.guiTypeProperty().addListener((observable, oldValue, newValue) ->
                customPumpProducerDialogModel.customProducerProperty().setValue(null));

        bindingI18N();
    }

    private void create() {

        ManufacturerPump newManufacturer = new ManufacturerPump();
        newManufacturer.setManufacturerName(nameTF.getText() + "_P");
//        newManufacturer.setDisplayOrder(manufacturerPumpModel.getManufacturerPumpObservableList().size() + 1);
        newManufacturer.setCustom(true);

        if (manufacturerPumpModel.getManufacturerPumpObservableList().contains(newManufacturer)) {
            notUniqueLabel.setVisible(true);
        }
        else {
            notUniqueLabel.setVisible(false);
            pumpProducerService.save(newManufacturer);
            customPumpProducerDialogModel.customProducerProperty().setValue(newManufacturer);
            customPumpProducerDialogModel.doneProperty().setValue(new Object());
            dialogStage.close();
        }
    }

    private void delete() {

        Producer producer = manufacturerPumpModel.manufacturerPumpProperty().get();
        pumpProducerService.delete(producer);
        customPumpProducerDialogModel.customProducerProperty().setValue(null);
        customPumpProducerDialogModel.doneProperty().setValue(new Object());
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
        nameTF.setText(manufacturerPumpModel.manufacturerPumpProperty().get().getManufacturerName());
        notUniqueLabel.setVisible(false);
    }

    private void bindingI18N(){
        nameLabel.textProperty().bind(i18N.createStringBinding("dialog.company.name"));
        applyBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.apply"));
        cancelBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
    }
}
