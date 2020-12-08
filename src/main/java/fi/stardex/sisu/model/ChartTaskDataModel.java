package fi.stardex.sisu.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartTaskDataModel {

    private ObservableList<XYChart.Data<Double, Double>> chartOneDataList = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> chartTwoDataList = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> chartThreeDataList = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> chartFourDataList = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> delayChartDataList = FXCollections.observableArrayList();
    private DoubleProperty bipSignalValue = new SimpleDoubleProperty();
    private DoubleProperty delayValue = new SimpleDoubleProperty();
    private DoubleProperty minDelayValue = new SimpleDoubleProperty();
    private DoubleProperty maxDelayValue = new SimpleDoubleProperty();


    public ObservableList<XYChart.Data<Double, Double>> getChartOneDataList() {
        return chartOneDataList;
    }
    public ObservableList<XYChart.Data<Double, Double>> getChartTwoDataList() {
        return chartTwoDataList;
    }
    public ObservableList<XYChart.Data<Double, Double>> getChartThreeDataList() {
        return chartThreeDataList;
    }
    public ObservableList<XYChart.Data<Double, Double>> getChartFourDataList() {
        return chartFourDataList;
    }
    public ObservableList<XYChart.Data<Double, Double>> getDelayChartDataList() {
        return delayChartDataList;
    }
    public DoubleProperty bipSignalValueProperty() {
        return bipSignalValue;
    }
    public DoubleProperty delayValueProperty() {
        return delayValue;
    }
    public DoubleProperty minDelayValueProperty() {
        return minDelayValue;
    }
    public DoubleProperty maxDelayValueProperty() {
        return maxDelayValue;
    }
}
