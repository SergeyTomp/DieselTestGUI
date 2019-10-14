package fi.stardex.sisu.model.uis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

public class UisVoltageTabModel {

    private Button pulseSettingsButton = new Button();
    private ObservableList<XYChart.Data<Double, Double>> data1 = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> data2 = FXCollections.observableArrayList();


    public Button getPulseSettingsButton() {
        return pulseSettingsButton;
    }
    public ObservableList<XYChart.Data<Double, Double>> getData1() {
        return data1;
    }
    public ObservableList<XYChart.Data<Double, Double>> getData2() {
        return data2;
    }
}
