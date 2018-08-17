package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

public class ChartTaskOne extends ChartTask {

    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return Current_graph1;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return Current_graph1_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return Current_graph1_update;
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
