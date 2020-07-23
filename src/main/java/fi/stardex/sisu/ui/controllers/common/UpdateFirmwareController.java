package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.model.SettingsModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.cr.tabs.settings.ConnectionController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static fi.stardex.sisu.registers.ultima.ModbusMapFirmware.*;

public class UpdateFirmwareController implements EventHandler<ActionEvent>, ChangeListener<UpdateFirmwareController.UpdatingState> {

    private final Logger logger = LoggerFactory.getLogger(UpdateFirmwareController.class);

    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label passwordStateLabel;
    @FXML private PasswordField passwordField;
    @FXML private Button startProcessButton;

    @Value("${hardware.updates.main}")
    private String path;
    private Process process;
    private UpdatingState updatingState = UpdatingState.NO;
    private ConnectionController connectionController;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private SettingsModel settingsModel;
    private Stage updateFirmwareStage;
    private Parent rootParent;
    private Parent updateFirmware;

    enum UpdatingState {
        NO, RUNNING, FINISHED, SOCKET_IN_USE, WRONG_PASSWORD, EXCEPTION, TIMED_OUT
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
        ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();
    }
    public void setSettingsModel(SettingsModel settingsModel) {
        this.settingsModel = settingsModel;
    }
    public void setRootParent(Parent rootParent) {
        this.rootParent = rootParent;
    }
    public void setUpdateFirmware(Parent updateFirmware) {
        this.updateFirmware = updateFirmware;
    }

    @PostConstruct
    private void init() {

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty())
                startProcessButton.setDisable(true);
            else
                startProcessButton.setDisable(false);
        });
        startProcessButton.setOnAction(this);

        settingsModel.firmwareUpdateProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue) {
                if (updateFirmwareStage == null) {
                    updateFirmwareStage = new Stage(StageStyle.DECORATED);
                    updateFirmwareStage.setScene(new Scene(updateFirmware));
                    updateFirmwareStage.initModality(Modality.WINDOW_MODAL);
                    updateFirmwareStage.initOwner(rootParent.getScene().getWindow());
                }
                updateFirmwareStage.show();
                updateFirmwareStage.setOnHidden(event -> returnToDefaultView());
            }
        });
    }

    private class ProcessTask extends Task<UpdatingState> {

        @Override
        protected UpdatingState call() {

            try {

                String ultimaIP = connectionController.getUltimaConnect().getKey();
                String address = fetchAddress(ultimaIP.substring(0, ultimaIP.lastIndexOf(".") + 1)).orElseThrow(IllegalArgumentException::new);

                logger.info("ip-address: {}", address);

                String mac = fetchMAC();
                logger.info("MAC: {}", mac);

                ProcessBuilder builder = new ProcessBuilder();
                builder.command("sudo", "-S", "./eflash",
                        "-i", String.format("%s", ultimaIP),
                        "-m", mac,
                        "-l", address,
                        "./MainCPU.bin", "--verbose")
                        .directory(new File(System.getProperty("user.dir"), path))
                        .redirectInput(ProcessBuilder.Redirect.PIPE)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectErrorStream(true);

                process = builder.start();
                updatingState = UpdatingState.RUNNING;

                ExecutorService es = Executors.newFixedThreadPool(2);

                es.submit(new InputStreamGobbler(process.getInputStream(), this::checkState));
                es.submit(new OutputStreamGobbler(process.getOutputStream()));
                es.shutdown();

                Thread.sleep(4000);

                if (updatingState == UpdatingState.WRONG_PASSWORD || updatingState == UpdatingState.SOCKET_IN_USE)
                    ultimaModbusWriter.add(Bootloader,false);
                else
                    ultimaModbusWriter.add(Bootloader,true);

                boolean success = process.waitFor(30, TimeUnit.SECONDS);
                logger.info("Process succeeded: {}", success);

                Thread.sleep(1000);
                if (!success)
                    updatingState = UpdatingState.TIMED_OUT;

                if (updatingState != UpdatingState.RUNNING) {
                    killProcess();
                    return updatingState;
                } else
                    return (updatingState = UpdatingState.FINISHED);

            } catch (IOException | InterruptedException | IllegalArgumentException ex) {
                logger.info("Exception while processing firmware update: ", ex);
                Optional.ofNullable(process).ifPresent(proc -> killProcess());
                return (updatingState = UpdatingState.EXCEPTION);
            }
        }

        private String fetchMAC() {

            String mac1 = Integer.toHexString((Integer) ultimaRegisterProvider.read(MAC_AddressByte1));
            String mac2 = Integer.toHexString((Integer) ultimaRegisterProvider.read(MAC_AddressByte2));
            String mac3 = Integer.toHexString((Integer) ultimaRegisterProvider.read(MAC_AddressByte3));
            String mac1First = mac1.substring(0, 2);
            String mac1Second = mac1.substring(2, 4);
            String mac2First = mac2.substring(0, 2);
            String mac2Second = mac2.substring(2, 4);
            String mac3First = mac3.substring(0, 2);
            String mac3Second = mac3.substring(2, 4);
            return mac1First + ":" + mac1Second + ":" + mac2First + ":" + mac2Second + ":" + mac3First + ":" + mac3Second;
        }

//        private Optional<String> fetchAddress() throws SocketException {
//
//            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
//            String regex = "192.168." + "0" + ".\\d{3}";
//            String found = null;
//
//            OUTER:
//            for (NetworkInterface netInt : Collections.list(nets)) {
//                Enumeration<InetAddress> addresses = netInt.getInetAddresses();
//                for (InetAddress address : Collections.list(addresses)) {
//                    String currentAddress = address.getCanonicalHostName();
//                    if (currentAddress.matches(regex)) {
//                        found = currentAddress;
//                        break OUTER;
//                    }
//                }
//            }
//            return Optional.ofNullable(found);
//        }

        private Optional<String> fetchAddress(String subNet) throws SocketException {

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            String regex = subNet + "\\d{3}";
            String found = null;

            OUTER:
            for (NetworkInterface netInt : Collections.list(nets)) {
                Enumeration<InetAddress> addresses = netInt.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    String currentAddress = address.getCanonicalHostName();
                    if (currentAddress.matches(regex)) {
                        found = currentAddress;
                        break OUTER;
                    }
                }
            }
            return Optional.ofNullable(found);
        }

        private void checkState(String line) {

            logger.info("{}", line);

            if (line.contains("incorrect password attempt"))
                updatingState = UpdatingState.WRONG_PASSWORD;
            else if (line.contains("Address already in use"))
                updatingState = UpdatingState.SOCKET_IN_USE;
        }
    }

    private void killProcess() {

        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sudo", "-S", "pkill", "eflash")
                .directory(new File(System.getProperty("user.dir"), path))
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectErrorStream(true);

        try {
            Process process = builder.start();
            ExecutorService es = Executors.newFixedThreadPool(2);
            es.submit(new InputStreamGobbler(process.getInputStream(), System.out::println));
            es.submit(new OutputStreamGobbler(process.getOutputStream()));
            es.shutdown();
            boolean success = process.waitFor(5, TimeUnit.SECONDS);
            logger.info("Killing process succeeded: {}", success);
            this.process = null;
        } catch (IOException | InterruptedException ex) {
            logger.info("Exception while killing firmware update process: ", ex);
        }
    }

    public void returnToDefaultView() {

        Optional.ofNullable(process).ifPresent(proc -> killProcess());
        passwordStateLabel.setText("Type in your password:");
        passwordStateLabel.setStyle(null);
        passwordField.clear();
        passwordField.setDisable(false);
        progressIndicator.setVisible(false);
        startProcessButton.setDisable(true);
        startProcessButton.setVisible(true);
    }

    @Override
    public void changed(ObservableValue<? extends UpdatingState> observableValue, UpdatingState oldValue, UpdatingState newValue) {

        logger.info("Final updating state: {}", newValue);

        switch (newValue) {
            case EXCEPTION:
            case SOCKET_IN_USE:
                passwordStateLabel.setText("Error occurred. Please restart the program");
                passwordStateLabel.setStyle("-fx-text-fill: red;");
                break;
            case TIMED_OUT:
                passwordStateLabel.setText("Timed out error. Please restart the program");
                passwordStateLabel.setStyle("-fx-text-fill: red;");
                break;
            case WRONG_PASSWORD:
                passwordStateLabel.setText("Incorrect password!");
                passwordStateLabel.setStyle("-fx-text-fill: red;");
                break;
            case FINISHED:
                passwordStateLabel.setText("Firmware updated successfully");
                break;
        }
        startProcessButton.setDisable(true);
        startProcessButton.setVisible(false);
        progressIndicator.setVisible(false);
        updatingState = UpdatingState.NO;
    }

    @Override
    public void handle(ActionEvent actionEvent) {

        startProcessButton.setDisable(true);
        passwordField.setDisable(true);
        progressIndicator.setVisible(true);
        ObjectProperty<UpdatingState> property = new SimpleObjectProperty<>();
        Task<UpdatingState> processTask = new ProcessTask();
        property.bind(processTask.valueProperty());
        property.addListener(this);
        new Thread(processTask).start();
    }

    private class InputStreamGobbler implements Runnable {

        private InputStream inputStream;
        private Consumer<String> consumer;

        InputStreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                reader.lines().forEach(consumer);
            } catch (IOException ex) {
                logger.info("Exception while reading process input: ", ex);
            }
        }
    }

    private class OutputStreamGobbler implements Runnable {

        private OutputStream outputStream;

        OutputStreamGobbler(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
                writer.println(passwordField.getText());
            }
        }
    }
}
