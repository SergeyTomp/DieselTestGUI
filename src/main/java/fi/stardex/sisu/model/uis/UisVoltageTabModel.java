package fi.stardex.sisu.model.uis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

public class UisVoltageTabModel {

    private Button pulseSettingsButton = new Button();
    private ObservableList<XYChart.Data<Double, Double>> data1 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data2 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data3 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data4 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data5 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data6 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data7 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data8 = FXCollections.observableArrayList();


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
    public ObservableList<XYChart.Data<Double, Double>> getData5() {
        return data5;
    }
    public ObservableList<XYChart.Data<Double, Double>> getData6() {
        return data6;
    }
    public ObservableList<XYChart.Data<Double, Double>> getData7() {
        return data7;
    }
    public ObservableList<XYChart.Data<Double, Double>> getData8() {
        return data8;
    }
}
