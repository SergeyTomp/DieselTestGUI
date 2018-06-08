package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.styles.FontColour;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class VoltageController {

    @FXML
    private Label boostI;

    @FXML
    private Label batteryU;

    @FXML
    private Label negativeU1;

    @FXML
    private Label negativeU2;

    private ViewHolder voltAmpereProfileDialog;

    @FXML
    private LineChart<Double, Double> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label width;

    @FXML
    private Label voltage;

    @FXML
    private Label firstWidth;

    @FXML
    private Label firstCurrent;

    @FXML
    private Label secondCurrent;

    @FXML
    private Button pulseSettingsButton;

    private Stage voapStage;

    private AdditionalSectionController additionalSectionController;

    private ObjectProperty<Boolean> isTabVoltageShowing = new SimpleObjectProperty<>();

    public ObjectProperty<Boolean> isTabVoltageShowingProperty() {
        return isTabVoltageShowing;
    }

    private VoltAmpereProfileController voltAmpereProfileController;

    private ObservableList<XYChart.Data<Double, Double>> data1;
    private ObservableList<XYChart.Data<Double, Double>> data2;
    private ObservableList<XYChart.Data<Double, Double>> data3;
    private ObservableList<XYChart.Data<Double, Double>> data4;

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

    public void setVoltAmpereProfileDialog(ViewHolder voltAmpereProfileDialog) {
        this.voltAmpereProfileDialog = voltAmpereProfileDialog;
    }

    public Label getWidth() {
        return width;
    }

    // TODO: добавить чтение регистров в InjectorSectionUpdater и обновление этих labels
    public Label getBoostI() {
        return boostI;
    }

    public Label getBatteryU() {
        return batteryU;
    }

    public Label getNegativeU1() {
        return negativeU1;
    }

    public Label getNegativeU2() {
        return negativeU2;
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

    public void setParentController(AdditionalSectionController additionalSectionController) {
        this.additionalSectionController = additionalSectionController;
    }

    @PostConstruct
    private void init() {

        isTabVoltageShowing.bind(additionalSectionController.getTabVoltage().selectedProperty());

        width.styleProperty().bindBidirectional(FontColour.fontColourPropertyProperty());

        width.styleProperty().addListener((observable, oldValue, newValue) -> width.setStyle(newValue));

        setInitialValuesToLabels();

        setupVoltAmpereProfileDialog();

        configLineChartData();

    }


    private void setInitialValuesToLabels() {
        width.setText("300"); // widthCurrentSignal initial value
        voltage.setText("60"); // boostUSpinner initial value
        firstWidth.setText("500"); // firstWSpinner initial value
        firstCurrent.setText("15.0"); // firstISpinner initial value
        secondCurrent.setText("5.5"); // secondISpinner initial value
        boostI.setText("21.5"); // boostISpinner initial value
        batteryU.setText("20"); // batteryUSpinner initial value
        negativeU1.setText("48"); // negativeU1Spinner initial value
        negativeU2.setText("36"); // negativeU2Spinner initial value
    }

    private void setupVoltAmpereProfileDialog() {

        voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog.getController();

        pulseSettingsButton.setOnMouseClicked(event -> {
            if(voapStage == null) {
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

    public void refreshVoltageLabels(Integer voltageValue, Integer firstWidthValue, Double firstCurrentValue, Double secondCurrentValue) {
        voltage.setText(voltageValue.toString());
        firstWidth.setText(firstWidthValue.toString());
        firstCurrent.setText(firstCurrentValue.toString());
        secondCurrent.setText(secondCurrentValue.toString());
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
}
