package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomModelDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.ui.controllers.common.GUI_TypeController.GUIType.UIS;

public class CustomInjectorUisDialogController {

    @FXML private Label firstW2Label;
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
    public void setCustomModelDialogModel(CustomModelDialogModel customModelDialogModel) {
        this.customModelDialogModel = customModelDialogModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {

        mainSectionUisModel.customModelProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null) return;
            if (guiTypeModel.guiTypeProperty().get() == UIS) {

                if (dialogStage == null) {
                    dialogStage = new Stage();
                    dialogStage.setScene(new Scene(dialogViev));
                    dialogStage.setResizable(false);
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.setTitle(mainSectionUisModel.customModelProperty().get().getTitle() + "injector");
                    dialogStage.setOnCloseRequest(event -> customModelDialogModel.cancelProperty().setValue(new Object()));
                }
                dialogStage.show();
            }
        });

        cancelBtn.setOnMouseClicked(event -> {

            customModelDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });
    }

    private void create() {


        customModelDialogModel.doneProperty().setValue(new Object());
    }

    private void update() {


        customModelDialogModel.doneProperty().setValue(new Object());
    }

    private void delete() {


        customModelDialogModel.doneProperty().setValue(new Object());
    }

    public void setNew() {

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

    public void setEdit() {

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(false);
        defaultRB.setDisable(false);
        customRB.setDisable(false);
        sureLabel.setVisible(false);
    }

    public void setDelete() {

        injectorCodeTF.setDisable(true);
        injTypeCB.setDisable(true);
        voapListView.setDisable(true);
        defaultRB.setDisable(true);
        customRB.setDisable(true);
        sureLabel.setVisible(true);


    }
}
