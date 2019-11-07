package fi.stardex.sisu.ui.controllers.uis.tabs;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.uis.UisRlcModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static fi.stardex.sisu.util.enums.InjectorSubType.*;

public class UisRlcController {

    private static final Logger logger = LoggerFactory.getLogger(UisRlcController.class);

    @FXML private TabPane measurementTabPane;
    @FXML private Tab tabCoilOne;
    @FXML private Tab tabCoilTwo;
    @FXML private Label attentionLabel;
    @FXML private Label attentionLabel2;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ProgressIndicator progressIndicatorDoubleCoil;
    @FXML private StackPane parameter1StackPane;
    @FXML private StackPane parameter2StackPane;
    @FXML private StackPane parameter3StackPane;
    @FXML private StackPane parameter4StackPane;
    @FXML private Button measureButton;
    @FXML private Button storeButton;


    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private I18N i18N;
    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private UisRlcModel uisRlcModel;

    private Gauge parameter1Gauge;
    private Gauge parameter2Gauge;
    private Gauge parameter3Gauge;
    private Gauge parameter4Gauge;

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setUltimaRegisterProvider(RegisterProvider ultimaRegisterProvider) {
        this.ultimaRegisterProvider = ultimaRegisterProvider;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setUisRlcModel(UisRlcModel uisRlcModel) {
        this.uisRlcModel = uisRlcModel;
    }

    @PostConstruct
    private void init() {

        bindingI18N();
        storeButton.setDisable(true);
        measureButton.setDisable(true);
        parameter1Gauge = GaugeCreator.createRLCGauge();
        parameter2Gauge = GaugeCreator.createRLCGauge();
        parameter3Gauge = GaugeCreator.createRLCGauge();
        parameter4Gauge = GaugeCreator.createRLCGauge();
        parameter1StackPane.getChildren().add(parameter1Gauge);
        parameter2StackPane.getChildren().add(parameter2Gauge);
        parameter3StackPane.getChildren().add(parameter3Gauge);
        parameter4StackPane.getChildren().add(parameter4Gauge);
        attentionLabel.setVisible(false);  //for this GUI this label is not used
        attentionLabel2.setVisible(false);  //for this GUI this label is not used
        tabCoilTwo.setDisable(true);

        setupButtonsListeners();
        setupModelsListeners();
    }

    private void setupButtonsListeners() {

        measureButton.setOnAction(event -> new Thread(this::measure).start());
        storeButton.setOnAction(event -> uisRlcModel.storeResult());
        mainSectionUisModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> measureButton.setDisable(newValue));
    }

    private void setupModelsListeners() {

        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> {
            uisRlcModel.clearResults();
            Optional.ofNullable(newValue).ifPresentOrElse(injector ->
                    setupGauges(injector.getVAP().getInjectorType(), injector.getVAP().getInjectorSubType()),
                    () -> setupGauges(null, null));});

        uisInjectorSectionModel.activeLedToggleButtonsListProperty().addListener((observableValue, oldValue, newValue) ->
            measureButton.setDisable(newValue.isEmpty() || mainSectionUisModel.modelProperty().get() == null));
    }

    private void measure() {

        logger.warn("Measure Button Pressed");
        uisRlcModel.isMeasuringProperty().setValue(true);

        ToggleButton ledController = selectedButton();
        InjectorType injectorType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorType();
        InjectorSubType injectorSubType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorSubType();
        boolean isDoubleCoil = injectorSubType == DOUBLE_COIL ||injectorSubType == HPI;


        Double parameter1;
        Double parameter2;
        Double parameter3;
        Double parameter4;

        if (ledController != null) {
            disableNode(true, storeButton, measureButton);
            Platform.runLater(() -> progressIndicator.setVisible(true));

            try {
                ultimaModbusWriter.add(ModbusMapUltima.RLC_measure_channel_num, 1);
                ultimaModbusWriter.add(ModbusMapUltima.RLC_measure_request, true);
                boolean ready;
                do {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        ready = (boolean) ultimaRegisterProvider.read(ModbusMapUltima.RLC_measure_request);
                        logger.warn("Measure Flag Status: {}", ready ? "In Process" : "Completed");
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        return;
                    }
                } while (ready);
                progressIndicator.setVisible(false);

                if (injectorType == InjectorType.COIL) {

                    parameter1 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Inductance_result);
                    parameter2 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result);
                    Platform.runLater(() -> UisRlcController.this.setCoilOneData(parameter1, parameter2));
                } else {
                    parameter1 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Capacitance_result_piezo);
                    parameter2 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result_piezo);
                    Platform.runLater(() -> UisRlcController.this.setPiezoData(parameter1, parameter2 > 2000 ? 2000 : parameter2));
                }

                uisRlcModel.setResultGauge1((int)Math.round(parameter1));
                uisRlcModel.setResultGauge2((double)Math.round(parameter2 * 100)/100);

                logger.warn("Parameter 1: {}    Parameter 2: {}", parameter1, parameter2);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            if (isDoubleCoil) {

                try {
                    Platform.runLater(() -> progressIndicatorDoubleCoil.setVisible(true));
                    ultimaModbusWriter.add(ModbusMapUltima.RLC_measure_channel_num, 2);
                    ultimaModbusWriter.add(ModbusMapUltima.RLC_measure_request, true);
                    boolean ready;
                    do {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            ready = (boolean) ultimaRegisterProvider.read(ModbusMapUltima.RLC_measure_request);
                            logger.warn("Measure Flag Status: {}", ready ? "In Process" : "Completed");
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            return;
                        }
                    } while (ready);
                    progressIndicatorDoubleCoil.setVisible(false);

                    parameter3 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Inductance_result);
                    parameter4 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result);
                    Platform.runLater(() -> UisRlcController.this.setCoilTwoData(parameter3, parameter4));

                    uisRlcModel.setResultGauge3((int) Math.round(parameter3));
                    uisRlcModel.setResultGauge4((double) Math.round(parameter4 * 100) / 100);
                    logger.warn("Parameter 3: {}    Parameter 4: {}", parameter3, parameter4);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            disableNode(false, storeButton, measureButton);
            uisRlcModel.isMeasuringProperty().setValue(false);
            logger.warn("Measure & Store Buttons Ready (Enabled)");
        }
    }

    private void activateGauges(int gauge1Decimals, int gauge2Decimals, String gauge1Title, String gauge1Unit, String gauge2Unit,
                                double gauge1MaxValue, double gauge2MaxValue, InjectorSubType injectorSubType) {

        disableNode(false, measureButton);

        parameter1Gauge.setDecimals(gauge1Decimals);
        parameter1Gauge.setTitle(gauge1Title);
        parameter1Gauge.setUnit(gauge1Unit);
        parameter1Gauge.setMaxValue(gauge1MaxValue);

        parameter2Gauge.setDecimals(gauge2Decimals);
        parameter2Gauge.setTitle("Resistance");
        parameter2Gauge.setUnit(gauge2Unit);
        parameter2Gauge.setMaxValue(gauge2MaxValue);

        parameter3Gauge.setDecimals(gauge1Decimals);
        parameter3Gauge.setTitle(gauge1Title);
        parameter3Gauge.setUnit(gauge1Unit);
        parameter3Gauge.setMaxValue(gauge1MaxValue);

        parameter4Gauge.setDecimals(gauge2Decimals);
        parameter4Gauge.setTitle("Resistance");
        parameter4Gauge.setUnit(gauge2Unit);
        parameter4Gauge.setMaxValue(gauge2MaxValue);

        measurementTabPane.getSelectionModel().select(0);
        tabCoilTwo.setDisable(!(injectorSubType == DOUBLE_COIL || injectorSubType == HPI));

        clearGauges();
    }

    private void deactivateGauges() {

        parameter1Gauge.setDecimals(1);
        parameter1Gauge.setTitle("");
        parameter1Gauge.setUnit("");
        parameter1Gauge.setMaxValue(500d);

        parameter2Gauge.setDecimals(1);
        parameter2Gauge.setTitle("");
        parameter2Gauge.setUnit("");
        parameter2Gauge.setMaxValue(3d);

        parameter3Gauge.setDecimals(1);
        parameter3Gauge.setTitle("");
        parameter3Gauge.setUnit("");
        parameter3Gauge.setMaxValue(500d);

        parameter4Gauge.setDecimals(1);
        parameter4Gauge.setTitle("");
        parameter4Gauge.setUnit("");
        parameter4Gauge.setMaxValue(3d);

        clearGauges();
        disableNode(true, storeButton, measureButton);
        measurementTabPane.getSelectionModel().select(0);
        tabCoilTwo.setDisable(true);
    }

    private void setupGauges(InjectorType injectorType, InjectorSubType injectorSubType) {

        if (injectorType == null) { deactivateGauges(); }
        else{
            activateGauges(injectorType.getGauge1Decimals(),
                    injectorType.getGauge2Decimals(),
                    injectorType.getGauge1Title(),
                    injectorType.getGauge1Unit(),
                    injectorType.getGauge2Unit(),
                    injectorType.getGauge1MaxValue(),
                    injectorType.getGauge2MaxValue(),
                    injectorSubType);
        }
    }

    private void clearGauges() {

        parameter1Gauge.valueProperty().setValue(0d);
        parameter2Gauge.valueProperty().setValue(0d);
        parameter3Gauge.valueProperty().setValue(0d);
        parameter3Gauge.valueProperty().setValue(0d);

        uisRlcModel.setResultGauge1(0);
        uisRlcModel.setResultGauge2(0);
        uisRlcModel.setResultGauge3(0);
        uisRlcModel.setResultGauge4(0);
    }

    private void setCoilOneData(Double parameter1, Double parameter2) {
        parameter1Gauge.setValue(parameter1);
        parameter2Gauge.setValue(parameter2);
    }

    private void setCoilTwoData(Double parameter1, Double parameter2) {
        parameter3Gauge.setValue(parameter1);
        parameter4Gauge.setValue(parameter2);
    }

    private void setPiezoData(Double parameter1, Double parameter2) {
        parameter1Gauge.setValue(parameter1);
        parameter2Gauge.setValue(parameter2);
    }

    private void disableNode(boolean disable, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);
    }

    private int getNumber(ToggleButton ledBeakerController) {
        return Integer.parseInt(ledBeakerController.getText());
    }

    private ToggleButton selectedButton() {
        List<ToggleButton> toggleButtons = uisInjectorSectionModel.activeLedToggleButtonsListProperty().get();
        if (toggleButtons.size() != 1) { return null;}
        ToggleButton ledController = toggleButtons.get(0);
        if (getNumber(ledController) > 8) { return null;}
        return ledController;
    }

    private void bindingI18N() {

        storeButton.textProperty().bind(i18N.createStringBinding("rlc.store.button"));
        measureButton.textProperty().bind(i18N.createStringBinding("rlc.measure.button"));
    }
}
