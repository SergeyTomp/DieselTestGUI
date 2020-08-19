package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.updateModels.InjectorSectionUpdateModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.CurrentTime_seconds;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.Version_controllable_1;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.Version_controllable_2;

public class ActivationController {

    @FXML private TextField paymentCodeTextField;
    @FXML private TextField activationTextField;
    @FXML private TextField finalPaymentTextField;
    @FXML private Label codeLabel;
    @FXML private Label finalCodeLabel;
    @FXML private Label activationCodeLabel;
    @FXML private Button copyButton;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;
    @FXML private Label activationLabel;
    @FXML private Button finalCopyButton;

    private Parent dialogViev;
    private Stage dialogStage;
    private I18N i18N;
    private StringProperty windowTitleProperty = new SimpleStringProperty("");
    private ClipboardContent content = new ClipboardContent();
    private ModbusRegisterProcessor ultimaModbusWriter;
    private ModbusConnect ultimaModbusConnect;
    private RegisterProvider ultimaRegisterProvider;
    private InjectorSectionUpdateModel injectorSectionUpdateModel;


    public void setDialogViev(Parent dialogViev) {
        this.dialogViev = dialogViev;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setUltimaModbusConnect(ModbusConnect ultimaModbusConnect) {
        this.ultimaModbusConnect = ultimaModbusConnect;
    }
    public void setInjectorSectionUpdateModel(InjectorSectionUpdateModel injectorSectionUpdateModel) {
        this.injectorSectionUpdateModel = injectorSectionUpdateModel;
    }

    @PostConstruct
    public void init() {

        ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();

        applyButton.setOnMouseClicked(mouseEvent -> {

        });
        copyButton.setOnMouseClicked(mouseEvent -> {
            content.put(DataFormat.PLAIN_TEXT, paymentCodeTextField.getText());
            Clipboard.getSystemClipboard().setContent(content);
        });
        finalCopyButton.setOnMouseClicked(mouseEvent -> {
            content.put(DataFormat.PLAIN_TEXT, finalPaymentTextField.getText());
            Clipboard.getSystemClipboard().setContent(content);
        });
        cancelButton.setOnMouseClicked(mouseEvent -> Platform.exit());
        ultimaModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {

                ultimaRegisterProvider.read(Version_controllable_1);
                ultimaRegisterProvider.read(Version_controllable_2);
                Object lastValue_1 = Version_controllable_1.getLastValue();
                Object lastValue_2 = Version_controllable_2.getLastValue();

//                if ((lastValue_1 != null && lastValue_2 != null && (int)lastValue_1 == 0xF1 && (int)lastValue_2 == 0xAA)) {
//
//                }
                ultimaModbusWriter.add(CurrentTime_seconds, (int)(System.currentTimeMillis() / 1000));
            }
        });
        bindingI18N();
    }

    private void showDialog() {

        if (dialogStage == null) {
            dialogStage = new Stage();
            dialogStage.setScene(new Scene(dialogViev));
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
        }
        dialogStage.setTitle(windowTitleProperty.get());
        dialogStage.show();
    }

    private void bindingI18N() {

        copyButton.textProperty().bind(i18N.createStringBinding("activation.copy.button"));
        finalCopyButton.textProperty().bind(i18N.createStringBinding("activation.copy.button"));
        cancelButton.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
        applyButton.textProperty().bind(i18N.createStringBinding("alert.applyButton"));
        windowTitleProperty.bind(i18N.createStringBinding("activation.windowTitle"));
        activationLabel.textProperty().bind(i18N.createStringBinding("activation.incorrectCode.label"));
        codeLabel.textProperty().bind(i18N.createStringBinding("activation.paymentCode.label"));
        finalCodeLabel.textProperty().bind(i18N.createStringBinding("activation.finalCode.label"));
        activationCodeLabel.textProperty().bind(i18N.createStringBinding("activation.activationCode.label"));
    }
}
