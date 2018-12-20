package fi.stardex.sisu.ui.controllers.additional.tabs;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.model.InjConfigurationModel;
import fi.stardex.sisu.model.InjectorTypeModel;
import fi.stardex.sisu.model.RLC_ReportModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

public class RLCController {
    private static final Logger logger = LoggerFactory.getLogger(RLCController.class);

    @FXML
    private Label attentionLabel;
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
    private InjectorSectionController injectorSectionController;
    private InjConfigurationModel injConfigurationModel;
    private RLC_ReportModel rlc_reportModel;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private I18N i18N;

    private Gauge parameter1Gauge;
    private Gauge parameter2Gauge;
    private int ledNumber;
    private String titleGauge1;
    private String titleGauge2;
    private String unitsGauge1;
    private String unitsGauge2;
    private  int resultGauge1;
    private double resultGauge2;

    public Button getMeasureButton() {
        return measureButton;
    }

    public Button getStoreButton() {
        return storeButton;
    }

    public Tab getTabCoilOne() {
        return tabCoilOne;
    }

    public Tab getTabCoilTwo() {
        return tabCoilTwo;
    }

    public TabPane getMeasurementTabPane() {
        return measurementTabPane;
    }

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
    }

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

    public Gauge getParameter1Gauge() {
        return parameter1Gauge;
    }

    public Gauge getParameter2Gauge() {
        return parameter2Gauge;
    }

    private List<ToggleButton> activeLedToggleButtonsList;

    @PostConstruct
    private void init() {

        bindingI18N();
        storeButton.setDisable(true);
        measureButton.setDisable(true);
        parameter1Gauge = GaugeCreator.createRLCGauge();
        parameter2Gauge = GaugeCreator.createRLCGauge();
        Gauge parameter3Gauge = GaugeCreator.createRLCGauge();
        Gauge parameter4Gauge = GaugeCreator.createRLCGauge();
        parameter1StackPane.getChildren().add(parameter1Gauge);
        parameter2StackPane.getChildren().add(parameter2Gauge);
        parameter3StackPane.getChildren().add(parameter3Gauge);
        parameter4StackPane.getChildren().add(parameter4Gauge);
        measurementTabPane.getTabs().remove(tabCoilTwo);
        attentionLabel.setVisible(false);                                    //for this GUI this label is not used
        measureButton.setOnAction(event -> {
            measureButton.setDisable(true);
            new Thread(this::measure).start();
        });
        storeButton.setOnAction(event -> rlc_reportModel.storeResult(unitsGauge1, unitsGauge2, titleGauge1, titleGauge2, ledNumber, resultGauge1, resultGauge2));
    }

    private void bindingI18N() {

        storeButton.textProperty().bind(i18N.createStringBinding("rlc.store.button"));
        measureButton.textProperty().bind(i18N.createStringBinding("rlc.measure.button"));
    }

    private void measure() {
        logger.warn("Measure Button Pressed");
        activeLedToggleButtonsList = injectorSectionController.getActiveLedToggleButtonsList();
        ToggleButton ledController = singleSelected();
        Double parameter1;
        Double parameter2;
        if (ledController != null) {

            titleGauge1 = parameter1Gauge.getTitle();
            titleGauge2 = parameter2Gauge.getTitle();
            unitsGauge1 = parameter1Gauge.getUnit();
            unitsGauge2 = parameter2Gauge.getUnit();

            ledNumber = getNumber(ledController);
            Platform.runLater(() -> {
                progressIndicator.setVisible(true);
                progressIndicatorDoubleCoil.setVisible(true);
            });
            try {
                int injectorModbusChannel = injConfigurationModel.injConfigurationProperty().get() == InjectorChannel.SINGLE_CHANNEL ?
                        1 : getNumber(ledController);
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
                    Platform.runLater(() -> RLCController.this.setCoilData(parameter1, parameter2));
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
        } else {
            Platform.runLater(() -> measureButton.setDisable(false));
        }
    }

    private void setCoilData(Double parameter1, Double parameter2) {
        parameter1Gauge.setValue(parameter1);
        parameter2Gauge.setValue(parameter2);
        measureButton.setDisable(false);
        logger.warn("Measure Button Ready (Enabled)");
    }

    private void setPiezoData(Double parameter1, Double parameter2) {
        parameter1Gauge.setValue(parameter1);
        parameter2Gauge.setValue(parameter2);
        measureButton.setDisable(false);
        logger.warn("Measure Button Ready (Enabled)");
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
}