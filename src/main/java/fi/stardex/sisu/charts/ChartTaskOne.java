package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartTaskOne extends ChartTask {
    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return ModbusMapUltima.Current_graph1;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return ModbusMapUltima.Current_graph1_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return ModbusMapUltima.Current_graph1_update;
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getData() {
        return voltageController.getData1();
    }

    @Override
    protected int getChartNumber() {
        return 1;
    }
}
