package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.*;
import fi.stardex.sisu.model.updateModels.UisHardwareUpdateModel;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.listeners.VapModelListener;
import fi.stardex.sisu.util.listeners.VoltageLabelListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class UisVoltageController {

    @FXML private LineChart<Double, Double> lineChart;
    @FXML private NumberAxis yAxis;
    @FXML private NumberAxis xAxis;
    @FXML private Label labelVoltage;
    @FXML private Label labelFirstWidth;
    @FXML private Label labelCurrent1;
    @FXML private Label labelCurrent2;
    @FXML private Label voltage;
    @FXML private Label firstWidth;
    @FXML private Label firstCurrent;
    @FXML private Label secondCurrent;
    @FXML private Label labelWidth;
    @FXML private Label width;
    @FXML private Label labelBoostI;
    @FXML private Label labelBatteryU;
    @FXML private Label labelNegativeU;
    @FXML private Label boostI;
    @FXML private Label batteryU;
    @FXML private Label negativeU;
    @FXML private Label labelWidthUOM;
    @FXML private Label labelVoltageUOM;
    @FXML private Label labelFirstWidthUOM;
    @FXML private Label labelCurrent1UOM;
    @FXML private Label labelCurrent2UOM;
    @FXML private Label labelBoostIUOM;
    @FXML private Label labelBatteryUUOM;
    @FXML private Label labelNegativeUUOM;
    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
    @FXML private Label firstW2Label;
    @FXML private Label firstI2Label;
    @FXML private Label secondI2Label;
    @FXML private Label boostI2Label;
    @FXML private Button pulseSettingsButton;

    private List<XYChart.Data<Double, Double>> emptyPointsList = new ArrayList<>(Collections.singletonList(new XYChart.Data<>(0d,0d)));
    private I18N i18N;
    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";
    private UisInjectorSectionModel uisInjectorSectionModel;
    private UisTabSectionModel uisTabSectionModel;
    private MainSectionUisModel mainSectionUisModel;
    private UisVoltageTabModel uisVoltageTabModel;
    private UisHardwareUpdateModel uisHardwareUpdateModel;
    private UisVapModel uisVapModel;
    private ObservableList<XYChart.Data<Double, Double>> data1;
    private ObservableList<XYChart.Data<Double, Double>> data2;
    private ObservableList<XYChart.Data<Double, Double>> data3;
    private ObservableList<XYChart.Data<Double, Double>> data4;
    private ObservableList<XYChart.Data<Double, Double>> data5;
    private ObservableList<XYChart.Data<Double, Double>> data6;
    private ObservableList<XYChart.Data<Double, Double>> data7;
    private ObservableList<XYChart.Data<Double, Double>> data8;

    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setUisTabSectionModel(UisTabSectionModel uisTabSectionModel) {
        this.uisTabSectionModel = uisTabSectionModel;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisVoltageTabModel(UisVoltageTabModel uisVoltageTabModel) {
        this.uisVoltageTabModel = uisVoltageTabModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setUisHardwareUpdateModel(UisHardwareUpdateModel uisHardwareUpdateModel) {
        this.uisHardwareUpdateModel = uisHardwareUpdateModel;
    }

    public void setUisVapModel(UisVapModel uisVapModel) {
        this.uisVapModel = uisVapModel;
    }

    @PostConstruct
    public void init() {

        setupVoltAmpereProfileDialog();
        setupVAPLabels();
        configLineChartData();
        setupXYAxisResizable();
        setupModelsListeners();
        bindingI18N();
    }

    private void setupVoltAmpereProfileDialog() {

        pulseSettingsButton.setOnMouseClicked(event -> uisVoltageTabModel.getPulseSettingsButton().fire());
    }

    private void clearCharts() {

        lineChart.getData().forEach(s -> {

            s.getData().clear();
            s.getData().addAll(emptyPointsList);
        });
    }

    private void setupVAPLabels() {

        width.setText(Integer.toString(WIDTH_CURRENT_SIGNAL_SPINNER_INIT)); // widthCurrentSignal initial value
        voltage.setText(Integer.toString(BOOST_U_SPINNER_INIT));            // boostUSpinner initial value
        firstWidth.setText(Integer.toString(FIRST_W_SPINNER_INIT));         // firstWSpinner initial value
        firstCurrent.setText(Double.toString(FIRST_I_SPINNER_INIT));        // firstISpinner initial value
        secondCurrent.setText(Double.toString(SECOND_I_SPINNER_INIT));      // secondISpinner initial value
        boostI.setText(Double.toString(BOOST_I_SPINNER_INIT));              // boostISpinner initial value
        batteryU.setText(Integer.toString(BATTERY_U_SPINNER_INIT));         // batteryUSpinner initial value
        negativeU.setText(Integer.toString(NEGATIVE_U_SPINNER_INIT));       // negativeUSpinner initial value

        firstW2Label.setText(Integer.toString(0));                        // firstW2Spinner initial value
        firstI2Label.setText(Double.toString(0));                         // firstI2Spinner initial value
        secondI2Label.setText(Double.toString(0));                        // secondI2Spinner initial value
        boostI2Label.setText(Double.toString(0));                         // boostI2Spinner initial value

        setupLabelListeners();
        setupUpdaterListeners();
    }

    private void setupUpdaterListeners() {

        uisHardwareUpdateModel.boost_UProperty().addListener((observableValue, oldValue, newValue) -> voltage.setText(newValue));
        uisHardwareUpdateModel.first_WProperty().addListener((observableValue, oldValue, newValue) -> firstWidth.setText(newValue));
        uisHardwareUpdateModel.first_IProperty().addListener((observableValue, oldValue, newValue) -> firstCurrent.setText(newValue));
        uisHardwareUpdateModel.second_IProperty().addListener((observableValue, oldValue, newValue) -> secondCurrent.setText(newValue));
        uisHardwareUpdateModel.boost_IProperty().addListener((observableValue, oldValue, newValue) -> boostI.setText(newValue));
        uisHardwareUpdateModel.battery_UProperty().addListener((observableValue, oldValue, newValue) -> batteryU.setText(newValue));
        uisHardwareUpdateModel.negative_UProperty().addListener((observableValue, oldValue, newValue) -> negativeU.setText(newValue));

        uisHardwareUpdateModel.first_I2Property().addListener((observableValue, oldValue, newValue) -> firstI2Label.setText(newValue));
        uisHardwareUpdateModel.second_I2Property().addListener((observableValue, oldValue, newValue) -> secondI2Label.setText(newValue));
        uisHardwareUpdateModel.boost_I2Property().addListener((observableValue, oldValue, newValue) -> boostI2Label.setText(newValue));
        uisHardwareUpdateModel.first_W2Property().addListener((observableValue, oldValue, newValue) -> firstW2Label.setText(newValue));
    }

    private void setupLabelListeners() {

//
        width.textProperty().addListener((observableValue, oldValue, newValue) -> {

            if (convertDataToDouble(newValue) != uisInjectorSectionModel.width_1Property().get()) {
                width.setStyle(RED_COLOR_STYLE);
            }
        });
//
//
////        width.textProperty().addListener(new VoltageLabelListener(width, uisInjectorSectionModel.widthProperty().get()));
        voltage.textProperty().addListener(new VoltageLabelListener(voltage, uisVapModel.boostUProperty()));
        firstWidth.textProperty().addListener(new VoltageLabelListener(firstWidth, uisVapModel.firstWProperty()));
        firstCurrent.textProperty().addListener(new VoltageLabelListener(firstCurrent, uisVapModel.firstIProperty()));
        secondCurrent.textProperty().addListener(new VoltageLabelListener(secondCurrent, uisVapModel.secondIProperty()));
        boostI.textProperty().addListener(new VoltageLabelListener(boostI, uisVapModel.boostIProperty()));
        batteryU.textProperty().addListener(new VoltageLabelListener(batteryU, uisVapModel.batteryUProperty()));
        negativeU.textProperty().addListener(new VoltageLabelListener(negativeU, uisVapModel.negativeUProperty()));
        firstW2Label.textProperty().addListener(new VoltageLabelListener(firstW2Label, uisVapModel.firstW2Property()));
        firstI2Label.textProperty().addListener(new VoltageLabelListener(firstI2Label, uisVapModel.firstI2Property()));
        secondI2Label.textProperty().addListener(new VoltageLabelListener(secondI2Label, uisVapModel.secondI2Property()));
        boostI2Label.textProperty().addListener(new VoltageLabelListener(boostI2Label, uisVapModel.boostI2Property()));
//
        uisVapModel.boostUProperty().addListener(new VapModelListener(voltage));
        uisVapModel.firstWProperty().addListener(new VapModelListener(firstWidth));
        uisVapModel.firstIProperty().addListener(new VapModelListener(firstCurrent));
        uisVapModel.secondIProperty().addListener(new VapModelListener(secondCurrent));
        uisVapModel.boostIProperty().addListener(new VapModelListener(boostI));
        uisVapModel.batteryUProperty().addListener(new VapModelListener(batteryU));
        uisVapModel.negativeUProperty().addListener(new VapModelListener(negativeU));
        uisVapModel.firstW2Property().addListener(new VapModelListener(firstW2Label));
        uisVapModel.firstI2Property().addListener(new VapModelListener(firstI2Label));
        uisVapModel.secondI2Property().addListener(new VapModelListener(secondI2Label));
        uisVapModel.boostI2Property().addListener(new VapModelListener(boostI2Label));
    }

    private void setupModelsListeners() {

        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> clearCharts());
        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> clearCharts());
        pulseSettingsButton.disableProperty().bindBidirectional(uisInjectorSectionModel.powerButtonProperty());
    }

    private void configLineChartData() {

        XYChart.Series<Double, Double> series1 = new XYChart.Series<>();
        series1.setName("");
        XYChart.Series<Double, Double> series2 = new XYChart.Series<>();
        series2.setName("");
        XYChart.Series<Double, Double> series3 = new XYChart.Series<>();
        series3.setName("");
        XYChart.Series<Double, Double> series4 = new XYChart.Series<>();
        series4.setName("");
        XYChart.Series<Double, Double> series5 = new XYChart.Series<>();
        series5.setName("");
        XYChart.Series<Double, Double> series6 = new XYChart.Series<>();
        series6.setName("");
        XYChart.Series<Double, Double> series7 = new XYChart.Series<>();
        series7.setName("");
        XYChart.Series<Double, Double> series8 = new XYChart.Series<>();
        series8.setName("");

        data1 = FXCollections.observableArrayList();
        data2 = FXCollections.observableArrayList();
        data3 = FXCollections.observableArrayList();
        data4 = FXCollections.observableArrayList();
        data5 = FXCollections.observableArrayList();
        data6 = FXCollections.observableArrayList();
        data7 = FXCollections.observableArrayList();
        data8 = FXCollections.observableArrayList();

        series1.setData(data1);
        series2.setData(data2);
        series3.setData(data3);
        series4.setData(data4);
        series5.setData(data5);
        series6.setData(data6);
        series7.setData(data7);
        series8.setData(data8);

        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        lineChart.getData().add(series4);
        lineChart.getData().add(series5);
        lineChart.getData().add(series6);
        lineChart.getData().add(series7);
        lineChart.getData().add(series8);

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(1200);
        xAxis.setTickUnit(100);
        xAxis.setMinorTickVisible(false);
        yAxis.setLowerBound(-15);
        yAxis.setUpperBound(25);
        yAxis.setTickUnit(5);
        lineChart.setTitle("");
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getYAxis().setAutoRanging(false);
        lineChart.getXAxis().setTickMarkVisible(true);
    }

    private void setupXYAxisResizable() {

        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null && newValue.getVAP().getInjectorType() == InjectorType.COIL) {
                yAxis.setUpperBound(25);
            }else{
                yAxis.setUpperBound(15);
            }
        });

        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> correctChartXaxis());

        uisInjectorSectionModel.width_1Property().addListener((observableValue, oldValue, newValue) ->{

            if (mainSectionUisModel.isTestIsChanging()) { return; }
            correctChartXaxis();
        });

        uisInjectorSectionModel.width_2Property().addListener((observableValue, oldValue, newValue) ->{
            if (mainSectionUisModel.isTestIsChanging()) { return; }
            correctChartXaxis();
        });

        uisInjectorSectionModel.shiftProperty().addListener((observableValue, oldValue, newValue) -> {
            if (mainSectionUisModel.isTestIsChanging()) { return; }
            correctChartXaxis();
        });
    }

    private void correctChartXaxis() {

        double w1 = uisInjectorSectionModel.width_1Property().get();
        double w2 = uisInjectorSectionModel.width_2Property().get() + uisInjectorSectionModel.shiftProperty().get();
        xAxis.setUpperBound(w1 == 0 ? 0 : w1 > w2 ? w1 * 1.2 : w2 * 1.2);
    }

    private void bindingI18N() {
        labelVoltage.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltage"));
        labelFirstWidth.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        labelCurrent1.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        labelCurrent2.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        labelWidth.textProperty().bind(i18N.createStringBinding("h4.voltage.label.width"));
        labelBoostI.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        labelBatteryU.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltageHold"));
        labelNegativeU.textProperty().bind(i18N.createStringBinding("h4.voltage.label.firstNegativeVoltage"));
        pulseSettingsButton.textProperty().bind(i18N.createStringBinding("h4.voltage.button"));
        xAxis.labelProperty().bind(i18N.createStringBinding("h4.voltage.chars.time"));
        yAxis.labelProperty().bind(i18N.createStringBinding("h4.voltage.chars.amp"));
        labelVoltageUOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.volt"));
        labelFirstWidthUOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.us"));
        labelCurrent1UOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.ampere"));
        labelCurrent2UOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.ampere"));
        labelWidthUOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.us"));
        labelBoostIUOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.ampere"));
        labelBatteryUUOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.volt"));
        labelNegativeUUOM.textProperty().bind(i18N.createStringBinding("h4.voltage.label.volt"));
        coil1Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil1"));
        coil2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil2"));
    }
}
