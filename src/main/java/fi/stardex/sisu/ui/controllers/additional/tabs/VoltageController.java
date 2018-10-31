package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.additional.TabSectionController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.i18n.I18N;
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
import javafx.scene.control.Spinner;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class VoltageController {

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

    private I18N i18N;

    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";

    private ViewHolder voltAmpereProfileDialog;

    private Stage voapStage;

    private TabSectionController tabSectionController;

    private ObjectProperty<Boolean> isTabVoltageShowing = new SimpleObjectProperty<>();

    private VoltAmpereProfileController voltAmpereProfileController;

    private InjectorSectionController injectorSectionController;

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

    public Label getBoostI() {
        return boostI;
    }

    public Label getBatteryU() {
        return batteryU;
    }

    public Label getNegativeU() {
        return negativeU;
    }

    public Label getVoltage() {
        return voltage;
    }

    public Label getFirstWidth() {
        return firstWidth;
    }

    public Label getFirstCurrent() {
        return firstCurrent;
    }

    public Label getSecondCurrent() {
        return secondCurrent;
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

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
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

        setupYAxisResizable();

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

    private void setupVAPLabels() {

        width.setText(Integer.toString(WIDTH_CURRENT_SIGNAL_SPINNER_INIT)); // widthCurrentSignal initial value
        voltage.setText(Integer.toString(BOOST_U_SPINNER_INIT));            // boostUSpinner initial value
        firstWidth.setText(Integer.toString(FIRST_W_SPINNER_INIT));         // firstWSpinner initial value
        firstCurrent.setText(Double.toString(FIRST_I_SPINNER_INIT));        // firstISpinner initial value
        secondCurrent.setText(Double.toString(SECOND_I_SPINNER_INIT));      // secondISpinner initial value
        boostI.setText(Double.toString(BOOST_I_SPINNER_INIT));              // boostISpinner initial value
        batteryU.setText(Integer.toString(BATTERY_U_SPINNER_INIT));         // batteryUSpinner initial value
        negativeU.setText(Integer.toString(NEGATIVE_U_SPINNER_INIT));       // negativeUSpinner initial value

        width.textProperty().addListener(new LabelListener(width, injectorSectionController.getWidthCurrentSignalSpinner()));
        voltage.textProperty().addListener(new LabelListener(voltage, voltAmpereProfileController.getBoostUSpinner()));
        firstWidth.textProperty().addListener(new LabelListener(firstWidth, voltAmpereProfileController.getFirstWSpinner()));
        firstCurrent.textProperty().addListener(new LabelListener(firstCurrent, voltAmpereProfileController.getFirstISpinner()));
        secondCurrent.textProperty().addListener(new LabelListener(secondCurrent, voltAmpereProfileController.getSecondISpinner()));
        boostI.textProperty().addListener(new LabelListener(boostI, voltAmpereProfileController.getBoostISpinner()));
        batteryU.textProperty().addListener(new LabelListener(batteryU, voltAmpereProfileController.getBatteryUSpinner()));
        negativeU.textProperty().addListener(new LabelListener(negativeU, voltAmpereProfileController.getNegativeUSpinner()));

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

        xAxis.setMinorTickVisible(false);
        yAxis.setLowerBound(-15);
        yAxis.setUpperBound(25);
        yAxis.setTickUnit(5);
        lineChart.setTitle("");
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.getXAxis().setAutoRanging(true);
        lineChart.getYAxis().setAutoRanging(false);
        lineChart.getXAxis().setTickMarkVisible(true);

    }


    private void setupYAxisResizable() {

        injectorSectionController.getPiezoCoilToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == injectorSectionController.getCoilRadioButton())
                yAxis.setUpperBound(25);
            else
                yAxis.setUpperBound(15);

        });

    }

    private class LabelListener implements ChangeListener<String> {

        private Label label;

        private Spinner<? extends Number> spinner;

        LabelListener(Label label, Spinner<? extends Number> spinner) {

            this.label = label;
            this.spinner = spinner;

        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

            Number spinnerValue = spinner.getValue();
            if (spinnerValue instanceof Double) {
                if ((Double) spinnerValue != convertDataToDouble(newValue))
                    setStyle(RED_COLOR_STYLE);
                else
                    setStyle(null);
            } else if (spinnerValue instanceof Integer) {
                if ((Integer) spinnerValue != convertDataToInt(newValue))
                    setStyle(RED_COLOR_STYLE);
                else
                    setStyle(null);
            }

        }

        private void setStyle(String style) {

            spinner.getEditor().setStyle(style);
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
    }
}
