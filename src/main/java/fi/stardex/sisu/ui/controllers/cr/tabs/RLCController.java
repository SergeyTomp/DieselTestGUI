package fi.stardex.sisu.ui.controllers.cr.tabs;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.model.cr.*;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.util.enums.InjectorChannel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.InjectorControllersState;
import fi.stardex.sisu.util.GaugeCreator;
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

public class RLCController {
    private static final Logger logger = LoggerFactory.getLogger(RLCController.class);

    @FXML
    private Label attentionLabel;
    @FXML
    private Label attentionLabel2;
    @FXML
    private StackPane parameter1StackPane;
    @FXML
    private StackPane parameter2StackPane;
    @FXML
    private StackPane parameter3StackPane;
    @FXML
    private StackPane parameter4StackPane;
    @FXML
    private Tab tabCoilOne;
    @FXML
    private Tab tabCoilTwo;
    @FXML
    private TabPane measurementTabPane;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ProgressIndicator progressIndicatorDoubleCoil;
    @FXML
    private Button measureButton;
    @FXML
    private Button storeButton;

    private InjectorTypeModel injectorTypeModel;
    private InjConfigurationModel injConfigurationModel;
    private RLC_ReportModel rlc_reportModel;
    private InjectorControllersState injectorControllersState;
    private InjectorSectionPwrState injectorSectionPwrState;
    private InjectorModel injectorModel;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private I18N i18N;

    private Gauge parameter1Gauge;
    private Gauge parameter2Gauge;
    private Gauge parameter3Gauge;
    private Gauge parameter4Gauge;

    private int ledNumber;
    private String titleGauge1;
    private String titleGauge2;
    private String titleGauge3;
    private String titleGauge4;
    private String unitsGauge1;
    private String unitsGauge2;
    private String unitsGauge3;
    private String unitsGauge4;

    private  int resultGauge1;
    private double resultGauge2;
    private  int resultGauge3;
    private double resultGauge4;

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setUltimaRegisterProvider(RegisterProvider ultimaRegisterProvider) {
        this.ultimaRegisterProvider = ultimaRegisterProvider;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setInjConfigurationModel(InjConfigurationModel injConfigurationModel) {
        this.injConfigurationModel = injConfigurationModel;
    }

    public void setInjectorTypeModel(InjectorTypeModel injectorTypeModel) {
        this.injectorTypeModel = injectorTypeModel;
    }

    public void setRlc_reportModel(RLC_ReportModel rlc_reportModel) {
        this.rlc_reportModel = rlc_reportModel;
    }

    public void setInjectorControllersState(InjectorControllersState injectorControllersState) {
        this.injectorControllersState = injectorControllersState;
    }

    public void setInjectorSectionPwrState(InjectorSectionPwrState injectorSectionPwrState) {
        this.injectorSectionPwrState = injectorSectionPwrState;
    }


    public void setInjectorModel(InjectorModel injectorModel) {
        this.injectorModel = injectorModel;
    }

    private List<ToggleButton> activeLedToggleButtonsList;

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
        storeButton.setOnAction(event -> {

            rlc_reportModel.storeResult(unitsGauge1, unitsGauge2, titleGauge1, titleGauge2, ledNumber, resultGauge1, resultGauge2);
            if (injectorModel.injectorProperty().get().getVoltAmpereProfile().isDoubleCoil()) {
                rlc_reportModel.storeResult(unitsGauge3, unitsGauge4, titleGauge3, titleGauge4, 2, resultGauge3, resultGauge4);
            }
        });

        injectorSectionPwrState.powerButtonProperty().addListener((observableValue, oldValue, newValue) -> measureButton.setDisable(newValue));
    }

    private void setupModelsListeners() {

        injectorModel.injectorProperty().addListener((observableValue, oldValue, newValue) -> {

            Optional.ofNullable(newValue).ifPresentOrElse(injector -> setupGauges(injector.getVoltAmpereProfile().getInjectorType().getInjectorType()), () -> setupGauges(null));
            storeButton.setDisable(true);
        });

        injectorControllersState.activeLedToggleButtonsListProperty().addListener((observableValue, oldValue, newValue) ->
                measureButton.setDisable(newValue.isEmpty() || injectorModel.injectorProperty().get() == null));
    }

    private void bindingI18N() {

        storeButton.textProperty().bind(i18N.createStringBinding("rlc.store.button"));
        measureButton.textProperty().bind(i18N.createStringBinding("rlc.measure.button"));
    }

    private void setupGauges(String injectorType) {

        if (injectorType == null) {

            deactivateGauges();
        }
        else{

            switch (injectorType) {
                case "coil":
                    activateGauges(0, 2, "Inductance", "\u03BCH", "\u03A9", 500d, 3d);
                    break;
                case "piezo":
                    activateGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 10d, 2000d);
                    break;
                case "piezoDelphi":
                    activateGauges(1, 0, "Capacitance", "\u03BCF", "k\u03A9", 20d, 2000d);
                    break;
            }
        }
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

    private void activateGauges(int gauge1Decimals, int gauge2Decimals, String gauge1Title, String gauge1Unit, String gauge2Unit,
                                double gauge1MaxValue, double gauge2MaxValue) {

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
        tabCoilTwo.setDisable(!injectorModel.injectorProperty().get().getVoltAmpereProfile().isDoubleCoil());
        clearGauges();
    }

    private void clearGauges() {
        parameter1Gauge.valueProperty().setValue(0d);
        parameter2Gauge.valueProperty().setValue(0d);
        parameter3Gauge.valueProperty().setValue(0d);
        parameter3Gauge.valueProperty().setValue(0d);
        resultGauge1 = 0;
        resultGauge2 = 0;
        resultGauge3 = 0;
        resultGauge4 = 0;
    }

    private void measure() {
        logger.warn("Measure Button Pressed");
        rlc_reportModel.isMeasuringProperty().setValue(true);

        activeLedToggleButtonsList = injectorControllersState.activeLedToggleButtonsListProperty().get();
        Boolean isDoubleCoil = injectorModel.injectorProperty().get().getVoltAmpereProfile().isDoubleCoil();
        ToggleButton ledController = singleSelected();
        Double parameter1;
        Double parameter2;
        Double parameter3;
        Double parameter4;

        if (ledController != null) {

            disableNode(true, storeButton, measureButton);
            titleGauge1 = parameter1Gauge.getTitle();
            titleGauge2 = parameter2Gauge.getTitle();
            unitsGauge1 = parameter1Gauge.getUnit();
            unitsGauge2 = parameter2Gauge.getUnit();

            ledNumber = getNumber(ledController);
            Platform.runLater(() -> progressIndicator.setVisible(true));
            try {
                int injectorModbusChannel = injConfigurationModel.injConfigurationProperty().get() == InjectorChannel.SINGLE_CHANNEL ? 1 : getNumber(ledController);
                ultimaModbusWriter.add(ModbusMapUltima.RLC_measure_channel_num, injectorModbusChannel);
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

                if (injectorTypeModel.injectorTypeProperty().get() == InjectorType.COIL) {

                    parameter1 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Inductance_result);
                    parameter2 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result);
                    Platform.runLater(() -> RLCController.this.setCoilOneData(parameter1, parameter2));
                } else {
                    parameter1 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Capacitance_result_piezo);
                    parameter2 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result_piezo);
                    Platform.runLater(() -> RLCController.this.setPiezoData(parameter1, parameter2 > 2000 ? 2000 : parameter2));
                }
                resultGauge1 = (int)Math.round(parameter1);
                resultGauge2 = (double)Math.round(parameter2 * 100)/100;

                logger.warn("Parameter 1: {}    Parameter 2: {}", parameter1, parameter2);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            if (isDoubleCoil) {

                titleGauge3 = parameter3Gauge.getTitle();
                titleGauge4 = parameter4Gauge.getTitle();
                unitsGauge3 = parameter3Gauge.getUnit();
                unitsGauge4 = parameter4Gauge.getUnit();

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
                    Platform.runLater(() -> RLCController.this.setCoilTwoData(parameter3, parameter4));
                    resultGauge3 = (int) Math.round(parameter3);
                    resultGauge4 = (double) Math.round(parameter4 * 100) / 100;
                    logger.warn("Parameter 3: {}    Parameter 4: {}", parameter3, parameter4);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            disableNode(false, storeButton, measureButton);
            rlc_reportModel.isMeasuringProperty().setValue(false);
            logger.warn("Measure & Store Buttons Ready (Enabled)");
        }
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

    private ToggleButton singleSelected() {
        if (activeLedToggleButtonsList.size() != 1) { return null;}
        ToggleButton ledController = activeLedToggleButtonsList.get(0);
        if (getNumber(ledController) > 4) { return null;}
        return ledController;
    }

    private int getNumber(ToggleButton ledBeakerController) {
        return Integer.parseInt(ledBeakerController.getText());
    }

    private void disableNode(boolean disable, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);
    }
}