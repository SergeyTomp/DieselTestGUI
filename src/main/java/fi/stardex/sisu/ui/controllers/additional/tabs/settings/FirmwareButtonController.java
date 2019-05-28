package fi.stardex.sisu.ui.controllers.additional.tabs.settings;

import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.additional.dialogs.FirmwareDialogController;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;

public class FirmwareButtonController {

    @FXML private Button firmwareButton;

    private ViewHolder firmwareWindow;

    private I18N i18N;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setFirmwareWindow(ViewHolder firmwareWindow) {
        this.firmwareWindow = firmwareWindow;
    }

    @PostConstruct
    public void init(){
        firmwareButton.textProperty().bind(i18N.createStringBinding("settings.firmware.Button"));
        setButtonListener();
    }

    private void setButtonListener(){
        firmwareButton.setOnAction(actionEvent -> ((FirmwareDialogController) firmwareWindow.getController()).showInfo());
    }
}
