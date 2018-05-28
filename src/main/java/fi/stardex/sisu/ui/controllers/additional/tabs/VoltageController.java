package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.ui.ViewHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;

public class VoltageController {

    private ViewHolder voltAmpereProfileDialog;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private Label voltage;
    @FXML
    private Spinner<Double> voltageHold;
    @FXML
    private Label firstWidth;
    @FXML
    private Label firstCurrent;
    @FXML
    private Label secondCurrent;
    @FXML
    private Spinner<Double> boostCurrent;
    @FXML
    private Spinner<Double> firstNegative;
    @FXML
    private Spinner<Double> secondNegative;

    @FXML
    private Label labelVoltage;
    @FXML
    private Label labelFirstWidth;
    @FXML
    private Label labelCurrent1;
    @FXML
    private Label labelCurrent2;
    @FXML
    private Label labelCurrentBoost;
    @FXML
    private Label labelVoltageHold;
    @FXML
    private Label labelVoltageFirst;
    @FXML
    private Label labelVoltageSecond;

    @FXML
    private Label labelUnitsBoostU;
    @FXML
    private Label labelUnitsFirstW;
    @FXML
    private Label labelUnitsFirstI;
    @FXML
    private Label labelUnitsSecondI;
    @FXML
    private Label labelUnitsBoostI;
    @FXML
    private Label labelUnitsBatteryU;
    @FXML
    private Label labelUnitsNegativeU1;
    @FXML
    private Label labelUnitsNegativeU2;
    @FXML
    private CheckBox checkBoxBoostDisable;

    @FXML
    private Button pulseSettingsButton;

    private Stage voapStage;

    private ObservableList<XYChart.Data<Double, Double>> data1;
    private ObservableList<XYChart.Data<Double, Double>> data2;
    private ObservableList<XYChart.Data<Double, Double>> data3;
    private ObservableList<XYChart.Data<Double, Double>> data4;
    private ObservableList<XYChart.Data<Double, Double>> data5;
    private ObservableList<XYChart.Data<Double, Double>> data6;

    public ObservableList<XYChart.Data<Double, Double>> getData1() {
        return data1;
    }

    public void setVoltAmpereProfileDialog(ViewHolder voltAmpereProfileDialog) {
        this.voltAmpereProfileDialog = voltAmpereProfileDialog;

    }

    @PostConstruct
    private void init() {
        pulseSettingsButton.setOnMouseClicked(event -> {
            if(voapStage == null) {
                voapStage = new Stage();
                voapStage.setTitle("Settings");
                voapStage.setScene(new Scene(voltAmpereProfileDialog.getView()));
                voapStage.setResizable(false);
                voapStage.initModality(Modality.APPLICATION_MODAL);
            }
            voapStage.show();
        });

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
        data1 = FXCollections.observableArrayList();
        data2 = FXCollections.observableArrayList();
        data3 = FXCollections.observableArrayList();
        data4 = FXCollections.observableArrayList();
        data5 = FXCollections.observableArrayList();
        data6 = FXCollections.observableArrayList();
        series1.setData(data1);
        series2.setData(data2);
        series3.setData(data3);
        series4.setData(data4);
        series5.setData(data5);
        series6.setData(data6);
        lineChart.getData().add(series1);
        lineChart.getData().add(series2);
        lineChart.getData().add(series3);
        lineChart.getData().add(series4);
        lineChart.getData().add(series5);
        lineChart.getData().add(series6);

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
