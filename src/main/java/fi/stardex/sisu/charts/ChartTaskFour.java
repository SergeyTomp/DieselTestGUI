package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class ChartTaskFour extends ChartTask {

    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return Current_graph4;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return Current_graph4_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return Current_graph4_update;
    }

    @Override
    protected int getFirmwareWidth() {
        return convertDataToInt(WidthBoardFour.getLastValue().toString());
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
