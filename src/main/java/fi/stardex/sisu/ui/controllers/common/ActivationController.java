package fi.stardex.sisu.ui.controllers.common;

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

    public void setDialogViev(Parent dialogViev) {
        this.dialogViev = dialogViev;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {

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
