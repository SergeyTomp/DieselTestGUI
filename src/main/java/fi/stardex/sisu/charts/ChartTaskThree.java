package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class ChartTaskThree extends ChartTask {
    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return ModbusMapUltima.Current_graph3;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return ModbusMapUltima.Current_graph3_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return ModbusMapUltima.Current_graph3_update;
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getData() {
        return voltageController.getData3();
    }

    @Override
    protected int getChartNumber() {
        return 3;
    }
}
