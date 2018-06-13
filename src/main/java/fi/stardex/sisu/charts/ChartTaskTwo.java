package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartTaskTwo extends ChartTask {
    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return ModbusMapUltima.Current_graph2;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return ModbusMapUltima.Current_graph2_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return ModbusMapUltima.Current_graph2_update;
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getData() {
        return voltageController.getData2();
    }

    @Override
    protected int getChartNumber() {
        return 2;
    }
}
