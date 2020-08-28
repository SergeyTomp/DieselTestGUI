package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.updateModels.InjectorSectionUpdateModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
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
import net.wimpi.modbus.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.stream.IntStream;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

public class ActivationController {

    private Logger logger = LoggerFactory.getLogger(ActivationController.class);

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
    private Task<Integer> requestTask;
    private Thread codeCheck;
    private final int CODE_SIZE = Activation_key.getCount() * 4;

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
        activationLabel.setVisible(false);

        applyButton.setOnMouseClicked(mouseEvent -> {

            if (requestTask != null && requestTask.isRunning()) {
                requestTask.cancel(true);
                logger.warn("Activation code check cancelled!");
            }

            ultimaModbusWriter.add(Activation_key, swapPairs(activationTextField.getText().toLowerCase()));

            requestTask = new Task<>() {

                int reply = 0;
                @Override
                protected Integer call() {

                    try {
                        while (reply == 0 && !isCancelled()) {
                            reply = (Integer) ultimaRegisterProvider.read(Activation_errorCode);
                            Thread.sleep(500);
                        }
                    }catch (InterruptedException e){
                        logger.error("Activation code check interrupted!");
                        e.printStackTrace();
                        return -1;
                    }
                    if (isCancelled()) {
                        return -1;
                    }
                    return reply;
                }
            };

            requestTask.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == 1) {
                    dialogStage.close();
                } else {
                    activationLabel.setVisible(true);
                }
            });

            codeCheck = new Thread(requestTask);
            codeCheck.setName("Activation code check");
            codeCheck.start();
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

//            if (newValue) {
//
//                ultimaRegisterProvider.read(Version_controllable_1);
//                ultimaRegisterProvider.read(Version_controllable_2);
//                Object lastValue_1 = Version_controllable_1.getLastValue();
//                Object lastValue_2 = Version_controllable_2.getLastValue();
////                if ((lastValue_1 != null && lastValue_2 != null && (int)lastValue_1 == 0xF1 && (int)lastValue_2 == 0xAA)) {
//                sendTime();
////                }
//            }
        });

        injectorSectionUpdateModel.activationErrorProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                try {
                    Integer[] integers = ultimaRegisterProvider.readBytePacket(Activation_paymentKey.getRef(), Activation_paymentKey.getCount());
                    StringBuilder sb = new StringBuilder();

                    for (int number : integers){

                        number = number & 0xFFFF;
                        if (number < 0xF) {
                            sb.append("0");
                        }
                        if (number < 0xFF) {
                            sb.append("0");
                        }
                        if(number < 0xFFF){
                            sb.append("0");
                        }
                        sb.append(Integer.toHexString(number));
                    }
                    paymentCodeTextField.setText(swapPairs(sb.toString().toUpperCase()));
                    activationTextField.clear();
                    ultimaModbusWriter.add(Activation_errorCode, 0);
                    showDialog();
                } catch (ModbusException e) {
                    logger.error("Activation Code reading failed!");
                    e.printStackTrace();
                }
            }
        });

        bindingI18N();
    }

//    @Scheduled(cron = "0 */10 * * * *")
    public void sendTime() {
        int time = (int) (System.currentTimeMillis() / 1000);
        ultimaModbusWriter.add(CurrentTime_seconds, time);
    }

    private void showDialog() {

        if (dialogStage == null) {
            dialogStage = new Stage();
            dialogStage.setScene(new Scene(dialogViev));
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
        }
        dialogStage.setTitle(windowTitleProperty.get());
        activationLabel.setVisible(false);
        dialogStage.show();
    }

    private String swapPairs(String input) {

        if (input.length() != CODE_SIZE) {
            logger.error("Incorrect string length for swapping: required - {}, received - {}!", CODE_SIZE, input.length());
            throw new IllegalArgumentException("Incorrect string length for swapping!");
        }
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, CODE_SIZE)
                .filter(i -> i % 4 == 0)
                .forEach(i -> sb.append(input, i + 2, i + 4).append(input, i, i + 2));
        return sb.toString();
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
