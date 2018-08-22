package fi.stardex.sisu.ui.controllers.additional.tabs;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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

    private Gauge parameter1Gauge;
    private Gauge parameter2Gauge;
    private Gauge parameter3Gauge;
    private Gauge parameter4Gauge;
    private RadioButton coilRadioButton;

    private InjectorSectionController injectorSectionController;
    private SettingsController settingsController;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private CurrentInjectorObtainer currentInjectorObtainer;
    private I18N i18N;

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
    }
    public void setSettingsController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setUltimaRegisterProvider(RegisterProvider ultimaRegisterProvider) {
        this.ultimaRegisterProvider = ultimaRegisterProvider;
    }
    public void setCurrentInjectorObtainer(CurrentInjectorObtainer currentInjectorObtainer) {
        this.currentInjectorObtainer = currentInjectorObtainer;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public Gauge getParameter1Gauge() {
        return parameter1Gauge;
    }

    public Gauge getParameter2Gauge() {
        return parameter2Gauge;
    }

    public Gauge getParameter3Gauge() {
        return parameter3Gauge;
    }

    public Gauge getParameter4Gauge() {
        return parameter4Gauge;
    }

    private List<LedController> activeControllers;
    private ComboBox<InjectorChannel> injectorChannelComboBox;

    @PostConstruct
    private void init() {

        bindingI18N();
        storeButton.setDisable(true);
        measureButton.setDisable(true);
        parameter1Gauge = createGauge();
        parameter2Gauge = createGauge();
        parameter3Gauge = createGauge();
        parameter4Gauge = createGauge();
        parameter1StackPane.getChildren().add(parameter1Gauge);
        parameter2StackPane.getChildren().add(parameter2Gauge);
        parameter3StackPane.getChildren().add(parameter3Gauge);
        parameter4StackPane.getChildren().add(parameter4Gauge);
        measurementTabPane.getTabs().remove(tabCoilTwo);
        injectorChannelComboBox = settingsController.getInjectorsConfigComboBox();
        coilRadioButton = injectorSectionController.getCoilRadioButton();
        attentionLabel.setVisible(false);                                           //for this GUI this label is not used

        measureButton.setOnAction(event -> {
            measureButton.setDisable(true);
            new Thread(()-> measure()).start();
        });
        storeButton.setOnAction(event -> store());
    }

    private void bindingI18N() {
        storeButton.textProperty().bind(i18N.createStringBinding("rlc.store.button"));
        measureButton.textProperty().bind(i18N.createStringBinding("rlc.measure.button"));
    }

    public void setupNull() {
        storeButton.setDisable(true);
        measureButton.setDisable(true);

        parameter1Gauge.setValue(0d);
        parameter2Gauge.setValue(0d);

        parameter1Gauge.setDecimals(1);
        parameter1Gauge.setTitle("");
        parameter1Gauge.setUnit("");
        parameter1Gauge.setMaxValue(500d);

        parameter2Gauge.setDecimals(1);
        parameter2Gauge.setTitle("");
        parameter2Gauge.setUnit("");
        parameter2Gauge.setMaxValue(3d);
    }

    public void setupCoil() {
        storeButton.setDisable(false);
        measureButton.setDisable(false);

        parameter1Gauge.setDecimals(0);
        parameter1Gauge.setTitle("Inductance");
        parameter1Gauge.setUnit("\u03BCH");
        parameter1Gauge.setMaxValue(500d);

        parameter2Gauge.setDecimals(2);
        parameter2Gauge.setTitle("Resistance");
        parameter2Gauge.setUnit("\u03A9");
        parameter2Gauge.setMaxValue(3d);

        tabCoilOne.setText("COIL");
        measurementTabPane.getTabs().remove(tabCoilTwo);
    }

    public void setupPiezo(double capacitanceMaxValue) {
        storeButton.setDisable(false);
        measureButton.setDisable(false);

        parameter1Gauge.setDecimals(1);
        parameter1Gauge.setTitle("Capacitance");
        parameter1Gauge.setUnit("\u03BCF");
        parameter1Gauge.setMaxValue(capacitanceMaxValue);

        parameter2Gauge.setDecimals(0);
        parameter2Gauge.setTitle("Resistance");
        parameter2Gauge.setUnit("k\u03A9");
        parameter2Gauge.setMaxValue(300d);

        measurementTabPane.getTabs().remove(tabCoilTwo);
        tabCoilOne.setText("PIEZO");
    }

    public void setupPiezo(double capacitanceMaxValue, double resistanceMaxValue) {
        storeButton.setDisable(false);
        measureButton.setDisable(false);

        parameter1Gauge.setDecimals(1);
        parameter1Gauge.setTitle("Capacitance");
        parameter1Gauge.setUnit("\u03BCF");
        parameter1Gauge.setMaxValue(capacitanceMaxValue);

        parameter2Gauge.setDecimals(0);
        parameter2Gauge.setTitle("Resistance");
        parameter2Gauge.setUnit("k\u03A9");
        parameter2Gauge.setMaxValue(resistanceMaxValue);

        measurementTabPane.getTabs().remove(tabCoilTwo);
        tabCoilOne.setText("PIEZO");
    }

    public void setupDoubleCoil(){
        setupCoil();
        parameter3Gauge.setDecimals(0);
        parameter3Gauge.setTitle("Inductance");
        parameter3Gauge.setUnit("\u03BCH");
        parameter3Gauge.setMaxValue(10000d);

        parameter4Gauge.setDecimals(2);
        parameter4Gauge.setTitle("Resistance");
        parameter4Gauge.setUnit("\u03A9");
        parameter4Gauge.setMaxValue(3d);

        tabCoilOne.setText("COIL 1");
        measurementTabPane.getTabs().add(tabCoilTwo);
    }

    private static Gauge createGauge() {
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SIMPLE_SECTION)
                .titleColor(Color.YELLOW)
                .title(" ")
                .minValue(0)
                .maxValue(35)
                .valueColor(Color.WHITE)
                .barColor(Color.YELLOW)
                .unitColor(Color.YELLOW)
                .animated(true)
                .decimals(2)
                .maxMeasuredValueVisible(false)
                .minMeasuredValueVisible(false)
                .oldValueVisible(false)
                .animated(false)
                .build();
    }

    private void measure() {
        logger.warn("Measure Button Pressed");
        activeControllers = injectorSectionController.getActiveControllers();
        LedController ledController = singleSelected();
        Double parameter1;
        Double parameter2;
        if (ledController != null) {
            Platform.runLater(() -> {
                progressIndicator.setVisible(true);
                progressIndicatorDoubleCoil.setVisible(true);
            });
            try {
                int injectorModbusChannel = injectorChannelComboBox.getSelectionModel().getSelectedItem() == InjectorChannel.SINGLE_CHANNEL ?
                        1 : ledController.getNumber();
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
                if (coilRadioButton.isSelected()) {
                    parameter1 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Inductance_result);
                    parameter2 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result);
                    Platform.runLater(() -> setCoilData(parameter1, parameter2));
                } else {
                    parameter1 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Capacitance_result_piezo);
                    parameter2 = (Double) ultimaRegisterProvider.read(ModbusMapUltima.Resistance_result_piezo);
                    Platform.runLater(() -> setPiezoData(parameter1, parameter2 > 2000? 2000 : parameter2));
                }
                logger.warn("Parameter 1: {}    Parameter 2: {}", parameter1, parameter2);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> measureButton.setDisable(false));
        }
    }
    //FIXME: next step
    private void store(){
//        Injector injector = currentInjectorObtainer.getInjector();
//        if (null == injector) {
//            return;
//        }
//        if (null != measurementResultsStorage.getMeasurementResults(injector)) {
//            List<MeasurementResult> measurementResults = measurementResultsStorage.getMeasurementResults(injector);
//            setMeasurementResultValue(measurementResults.get(0), String.format("%.2f", parameter1Gauge.getValue()));
//            setMeasurementResultValue(measurementResults.get(1), String.format("%.2f", parameter2Gauge.getValue()));
//        } else {
//            List<MeasurementResult> measurementResults = new ArrayList<>();
//            MeasurementResult measurementResult1 = new MeasurementResult(parameter1Gauge.getTitle(), parameter1Gauge.getUnit());
//            MeasurementResult measurementResult2 = new MeasurementResult(parameter2Gauge.getTitle(), parameter2Gauge.getUnit());
//            setMeasurementResultValue(measurementResult1, String.format("%.2f", parameter1Gauge.getValue()));
//            setMeasurementResultValue(measurementResult2, String.format("%.2f", parameter2Gauge.getValue()));
//            measurementResults.add(measurementResult1);
//            measurementResults.add(measurementResult2);
//
//            measurementResultsStorage.putMeasurementResults(injector, measurementResults);
//        }
//        measurementTableReportWrapper.setMeasureResults();
    }

    private void setMeasurementResultValue(MeasurementResult measurementResult, String value) {
        activeControllers = injectorSectionController.getActiveControllers();
        LedController ledController = singleSelected();
        if (null == ledController) {
            return;
        }
        measurementResult.setParameterValue(ledController.getNumber(), value);
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

    private LedController singleSelected() {
        if (activeControllers.size() != 1) { return null;}
        LedController ledController = activeControllers.get(0);
        if (ledController.getNumber() > 4) { return null;}
        return ledController;
    }
}