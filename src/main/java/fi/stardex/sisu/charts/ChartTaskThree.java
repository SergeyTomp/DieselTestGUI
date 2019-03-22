package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class ChartTaskThree extends ChartTask {

    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return Current_graph3;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return Current_graph3_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return Current_graph3_update;
    }

    @Override
    protected int getFirmwareWidth() {
        return convertDataToInt(WidthBoardThree.getLastValue().toString());
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
