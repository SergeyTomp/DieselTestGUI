package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartTaskFour extends ChartTask {
    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return ModbusMapUltima.Current_graph4;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return ModbusMapUltima.Current_graph4_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return ModbusMapUltima.Current_graph4_update;
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getData() {
        return voltageController.getData4();
    }

    @Override
    protected int getChartNumber() {
        return 4;
    }
}
