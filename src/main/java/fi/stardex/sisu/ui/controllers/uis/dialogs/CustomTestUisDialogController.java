package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomTestDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTestName;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.ui.controllers.common.GUI_TypeController.GUIType.UIS;

public class CustomTestUisDialogController {

    @FXML private ComboBox<InjectorUisTestName> testComboBox;
    @FXML private TextField barTF;
    @FXML private TextField rpmTF;
    @FXML private TextField widthTF;
    @FXML private TextField freqTF;
    @FXML private TextField nominalTF;
    @FXML private TextField flowRangeTF;
    @FXML private TextField adjTimeTF;
    @FXML private TextField measureTimeTF;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private Stage dialogStage;
    private Parent dialogView;

    private MainSectionUisModel mainSectionUisModel;
    private CustomTestDialogModel customTestDialogModel;
    private GUI_TypeModel guiTypeModel;
    private I18N i18N;

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setCustomTestDialogModel(CustomTestDialogModel customTestDialogModel) {
        this.customTestDialogModel = customTestDialogModel;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setDialogView(Parent dialogView) {
        this.dialogView = dialogView;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {

        mainSectionUisModel.customTestProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;
            if (guiTypeModel.guiTypeProperty().get() == UIS) {

                if (dialogStage == null) {
                    dialogStage = new Stage();
                    dialogStage.setScene(new Scene(dialogView));
                    dialogStage.setResizable(false);
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.setTitle(mainSectionUisModel.customModelProperty().get().getTitle() + "test");
                    dialogStage.setOnCloseRequest(event -> customTestDialogModel.cancelProperty().setValue(new Object()));
                }
                dialogStage.show();
            }
        });

        cancelBtn.setOnMouseClicked(event -> {

            customTestDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });
    }

    private void create() {


        customTestDialogModel.doneProperty().setValue(new Object());
    }

    private void update() {


        customTestDialogModel.doneProperty().setValue(new Object());
    }

    private void delete() {


        customTestDialogModel.doneProperty().setValue(new Object());
    }

    private void setNew() {


    }

    private void setDelete() {


    }

    private void setEdit() {


    }
}
