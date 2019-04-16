package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.InjectorSectionUpdateModel;
import fi.stardex.sisu.states.VoltAmpereProfileDialogModel;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.additional.TabSectionController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class VoltageController {

    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
    @FXML private Label firstW2Label;
    @FXML private Label firstI2Label;
    @FXML private Label secondI2Label;
    @FXML private Label boostI2Label;
    @FXML private Label labelVoltage;
    @FXML private Label labelVoltageUOM;
    @FXML private Label labelFirstWidth;
    @FXML private Label labelFirstWidthUOM;
    @FXML private Label labelCurrent1;
    @FXML private Label labelCurrent1UOM;
    @FXML private Label labelCurrent2;
    @FXML private Label labelCurrent2UOM;
    @FXML private Label labelWidth;
    @FXML private Label labelWidthUOM;
    @FXML private Label labelBoostI;
    @FXML private Label labelBoostIUOM;
    @FXML private Label labelBatteryU;
    @FXML private Label labelBatteryUUOM;
    @FXML private Label labelNegativeU;
    @FXML private Label labelNegativeUUOM;
    @FXML private Label boostI;
    @FXML private Label batteryU;
    @FXML private Label negativeU;
    @FXML private LineChart<Double, Double> lineChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Label width;
    @FXML private Label voltage;
    @FXML private Label firstWidth;
    @FXML private Label firstCurrent;
    @FXML private Label secondCurrent;
    @FXML private Button pulseSettingsButton;

    private List<XYChart.Data<Double, Double>> emptyPointsList = new ArrayList<>(Collections.singletonList(new XYChart.Data<>(0d,0d)));

    private I18N i18N;

    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";

    private ViewHolder voltAmpereProfileDialog;

    private Stage voapStage;

    private TabSectionController tabSectionController;

    private ObjectProperty<Boolean> isTabVoltageShowing = new SimpleObjectProperty<>();

    private VoltAmpereProfileController voltAmpereProfileController;

    private InjectorTypeModel injectorTypeModel;

    private CoilOnePulseParametersModel coilOnePulseParametersModel;

    private CoilTwoPulseParametersModel coilTwoPulseParametersModel;

    private InjectorSectionUpdateModel injectorSectionUpdateModel;

    private VoltAmpereProfileDialogModel voltAmpereProfileDialogModel;

    private InjectorModel injectorModel;

    private InjectorTestModel injectorTestModel;

    private ObservableList<XYChart.Data<Double, Double>> data1;

    private ObservableList<XYChart.Data<Double, Double>> data2;

    private ObservableList<XYChart.Data<Double, Double>> data3;

    private ObservableList<XYChart.Data<Double, Double>> data4;

    public Button getPulseSettingsButton() {
        return pulseSettingsButton;
    }

    public ObservableList<XYChart.Data<Double, Double>> getData1() {
        return data1;
    }

    public ObservableList<XYChart.Data<Double, Double>> getData2() {
        return data2;
    }

    public ObservableList<XYChart.Data<Double, Double>> getData3() {
        return data3;
    }

    public ObservableList<XYChart.Data<Double, Double>> getData4() {
        return data4;
    }

    public Label getWidth() {
        return width;
    }

    public Label getVoltage() {
        return voltage;
    }

    public ObjectProperty<Boolean> isTabVoltageShowingProperty() {
        return isTabVoltageShowing;
    }

    public void setVoltAmpereProfileDialog(ViewHolder voltAmpereProfileDialog) {
        this.voltAmpereProfileDialog = voltAmpereProfileDialog;
    }

    public void setParentController(TabSectionController tabSectionController) {
        this.tabSectionController = tabSectionController;
    }

    public void setInjectorTypeModel(InjectorTypeModel injectorTypeModel) {
        this.injectorTypeModel = injectorTypeModel;
    }

    public void setCoilOnePulseParametersModel(CoilOnePulseParametersModel coilOnePulseParametersModel) {
        this.coilOnePulseParametersModel = coilOnePulseParametersModel;
    }

    public void setCoilTwoPulseParametersModel(CoilTwoPulseParametersModel coilTwoPulseParametersModel) {
        this.coilTwoPulseParametersModel = coilTwoPulseParametersModel;
    }

    public void setInjectorSectionUpdateModel(InjectorSectionUpdateModel injectorSectionUpdateModel) {
        this.injectorSectionUpdateModel = injectorSectionUpdateModel;
    }

    public void setVoltAmpereProfileDialogModel(VoltAmpereProfileDialogModel voltAmpereProfileDialogModel) {
        this.voltAmpereProfileDialogModel = voltAmpereProfileDialogModel;
    }

    public void setInjectorModel(InjectorModel injectorModel) {
        this.injectorModel = injectorModel;
    }

    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init() {

        isTabVoltageShowing.bind(tabSectionController.getTabVoltage().selectedProperty());

        setupVoltAmpereProfileDialog();

        setupVAPLabels();

        configLineChartData();

        setupXYAxisResizable();

        setupModelsListeners();

        bindingI18N();

    }

    private void setupVoltAmpereProfileDialog() {

        voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog.getController();

        pulseSettingsButton.setOnMouseClicked(event -> {

            if (voapStage == null) {
                voapStage = new Stage();
                voapStage.setTitle("Settings");
                voapStage.setScene(new Scene(voltAmpereProfileDialog.getView()));
                voapStage.setResizable(false);
                voapStage.initModality(Modality.APPLICATION_MODAL);
                voapStage.initStyle(StageStyle.UTILITY);
                voapStage.setOnCloseRequest(ev -> voltAmpereProfileController.getCancelButton().fire());
            }
            voltAmpereProfileController.setStage(voapStage);
            voltAmpereProfileController.saveValues();
            voapStage.show();

        });

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

        injectorSectionUpdateModel.boost_UProperty().addListener((observableValue, oldValue, newValue) -> voltage.setText(newValue));
        injectorSectionUpdateModel.first_WProperty().addListener((observableValue, oldValue, newValue) -> firstWidth.setText(newValue));
        injectorSectionUpdateModel.first_IProperty().addListener((observableValue, oldValue, newValue) -> firstCurrent.setText(newValue));
        injectorSectionUpdateModel.second_IProperty().addListener((observableValue, oldValue, newValue) -> secondCurrent.setText(newValue));
        injectorSectionUpdateModel.boost_IProperty().addListener((observableValue, oldValue, newValue) -> boostI.setText(newValue));
        injectorSectionUpdateModel.battery_UProperty().addListener((observableValue, oldValue, newValue) -> batteryU.setText(newValue));
        injectorSectionUpdateModel.negative_UProperty().addListener((observableValue, oldValue, newValue) -> negativeU.setText(newValue));

        injectorSectionUpdateModel.first_I2Property().addListener((observableValue, oldValue, newValue) -> firstI2Label.setText(newValue));
        injectorSectionUpdateModel.second_I2Property().addListener((observableValue, oldValue, newValue) -> secondI2Label.setText(newValue));
        injectorSectionUpdateModel.boost_I2Property().addListener((observableValue, oldValue, newValue) -> boostI2Label.setText(newValue));
        injectorSectionUpdateModel.first_W2Property().addListener((observableValue, oldValue, newValue) -> firstW2Label.setText(newValue));
    }

    private void setupLabelListeners() {


        width.textProperty().addListener((observableValue, oldValue, newValue) -> {

            if (convertDataToDouble(newValue) != coilOnePulseParametersModel.widthProperty().get()) {
                width.setStyle(RED_COLOR_STYLE);
            }
        });


//        width.textProperty().addListener(new VoltageLabelListener(width, coilOnePulseParametersModel.widthProperty().get()));
        voltage.textProperty().addListener(new VoltageLabelListener(voltage, voltAmpereProfileDialogModel.boostUProperty()));
        firstWidth.textProperty().addListener(new VoltageLabelListener(firstWidth, voltAmpereProfileDialogModel.firstWProperty()));
        firstCurrent.textProperty().addListener(new VoltageLabelListener(firstCurrent, voltAmpereProfileDialogModel.firstIProperty()));
        secondCurrent.textProperty().addListener(new VoltageLabelListener(secondCurrent, voltAmpereProfileDialogModel.secondIProperty()));
        boostI.textProperty().addListener(new VoltageLabelListener(boostI, voltAmpereProfileDialogModel.boostIProperty()));
        batteryU.textProperty().addListener(new VoltageLabelListener(batteryU, voltAmpereProfileDialogModel.batteryUProperty()));
        negativeU.textProperty().addListener(new VoltageLabelListener(negativeU, voltAmpereProfileDialogModel.negativeUProperty()));
        firstW2Label.textProperty().addListener(new VoltageLabelListener(firstW2Label, voltAmpereProfileDialogModel.firstW2Property()));
        firstI2Label.textProperty().addListener(new VoltageLabelListener(firstI2Label, voltAmpereProfileDialogModel.firstI2Property()));
        secondI2Label.textProperty().addListener(new VoltageLabelListener(secondI2Label, voltAmpereProfileDialogModel.secondI2Property()));
        boostI2Label.textProperty().addListener(new VoltageLabelListener(boostI2Label, voltAmpereProfileDialogModel.boostI2Property()));

        voltAmpereProfileDialogModel.boostUProperty().addListener(new VapModelListener(voltage));
        voltAmpereProfileDialogModel.firstWProperty().addListener(new VapModelListener(firstWidth));
        voltAmpereProfileDialogModel.firstIProperty().addListener(new VapModelListener(firstCurrent));
        voltAmpereProfileDialogModel.secondIProperty().addListener(new VapModelListener(secondCurrent));
        voltAmpereProfileDialogModel.boostIProperty().addListener(new VapModelListener(boostI));
        voltAmpereProfileDialogModel.batteryUProperty().addListener(new VapModelListener(batteryU));
        voltAmpereProfileDialogModel.negativeUProperty().addListener(new VapModelListener(negativeU));
        voltAmpereProfileDialogModel.firstW2Property().addListener(new VapModelListener(firstW2Label));
        voltAmpereProfileDialogModel.firstI2Property().addListener(new VapModelListener(firstI2Label));
        voltAmpereProfileDialogModel.secondI2Property().addListener(new VapModelListener(secondI2Label));
        voltAmpereProfileDialogModel.boostI2Property().addListener(new VapModelListener(boostI2Label));
    }

    private void setupModelsListeners() {

        injectorModel.injectorProperty().addListener((observableValue, oldValue, newValue) -> clearCharts());
        injectorTestModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> clearCharts());
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
        data1 = FXCollections.observableArrayList();
        data2 = FXCollections.observableArrayList();
        data3 = FXCollections.observableArrayList();
        data4 = FXCollections.observableArrayList();
        series1.setData(data1);
        series2.setData(data2);
        series3.setData(data3);
        series4.setData(data4);
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        lineChart.getData().add(series4);

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

        injectorTypeModel.injectorTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == InjectorType.COIL) {
                yAxis.setUpperBound(25);
            }else{
                yAxis.setUpperBound(15);
            }
        });

        injectorTestModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> correctChartXaxis());

        coilOnePulseParametersModel.widthProperty().addListener((observableValue, oldValue, newValue) ->{

            if (injectorTestModel.isTestIsChanging()) { return; }
            correctChartXaxis();
        });

        coilTwoPulseParametersModel.width_2Property().addListener((observableValue, oldValue, newValue) ->{
            if (injectorTestModel.isTestIsChanging()) { return; }
            correctChartXaxis();
        });

        coilTwoPulseParametersModel.shiftProperty().addListener((observableValue, oldValue, newValue) -> {
            if (injectorTestModel.isTestIsChanging()) { return; }
            correctChartXaxis();
        });
    }

    private void correctChartXaxis() {

        double w1 = coilOnePulseParametersModel.widthProperty().get();
        double w2 = coilTwoPulseParametersModel.width_2Property().get() + coilTwoPulseParametersModel.shiftProperty().get();
        xAxis.setUpperBound(w1 == 0 ? 0 : w1 > w2 ? w1 * 1.2 : w2 * 1.2);
    }

    private class VapModelListener implements ChangeListener<Number>{

        private Label valueLabel;

        public VapModelListener(Label valueLabel) {
            this.valueLabel = valueLabel;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

            if (newValue instanceof Double) {

                setStyle(newValue.doubleValue() == convertDataToDouble(valueLabel.getText()));
            } else if (newValue instanceof Integer) {

                setStyle(newValue.intValue() == convertDataToInt(valueLabel.getText()));
            }
        }
        private void setStyle(boolean equal) {

            if(equal) valueLabel.setStyle(null);
            else valueLabel.setStyle(RED_COLOR_STYLE);
        }
    }


    private class VoltageLabelListener implements ChangeListener<String>{

        private Label label;

        private DoubleProperty doubleValue;

        private IntegerProperty intValue;

        public VoltageLabelListener(Label label, DoubleProperty value) {
            this.label = label;
            this.doubleValue = value;
        }

        public VoltageLabelListener(Label label, IntegerProperty value) {
            this.label = label;
            this.intValue = value;
        }

        @Override
        public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {

            if (doubleValue != null) {

                if (convertDataToDouble(newValue) != doubleValue.get()) {
                    setStyle(RED_COLOR_STYLE);
                }else{
                    setStyle(null);
                }

            } else if (intValue != null) {

                if (convertDataToInt(newValue) != intValue.get()) {
                    setStyle(RED_COLOR_STYLE);
                }else{
                    setStyle(null);
                }
            }
        }

        private void setStyle(String style) {

            label.setStyle(style);
        }
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
