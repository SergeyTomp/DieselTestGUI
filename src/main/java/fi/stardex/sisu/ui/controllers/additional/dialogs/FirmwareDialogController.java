package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

public class FirmwareDialogController {

    @FXML private Label ultimaLabel;

    @FXML private Label flowMeterLabel;

    @FXML private Label standLabel;

    @FXML private Label mainCPUVersionLabel;

    @FXML private Label MeasuringCPUVersionLabel;

    @FXML private Label powerCPUVersionLabel;

    @FXML private Label injectorCPUVersionLabel;

    @FXML private Label injMeasuringCPUVersionLabel;

    @FXML private Label fmStreamVersionLabel;

    @FXML private Label benchVersionLabel;

    @FXML private Label benchV2VersionLabel;

    @FXML private Button okButton;

    private Stage windowStage;

    private I18N i18N;

    public void setWindowStage(Stage windowStage) {
        windowStage.titleProperty().bind(i18N.createStringBinding("settings.firmware.Button"));
        this.windowStage = windowStage;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){
        okButton.setOnAction(actionEvent -> windowStage.close());
        ultimaLabel.textProperty().bind(i18N.createStringBinding("link.ultima.label"));
        flowMeterLabel.textProperty().bind(i18N.createStringBinding("link.flowmeter.label"));
        standLabel.textProperty().bind(i18N.createStringBinding("link.stand.label"));
    }
}
