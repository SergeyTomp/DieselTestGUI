package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class ChartTaskTwo extends ChartTask {

    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return Current_graph2;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return Current_graph2_frame_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return Current_graph2_update;
    }

    @Override
    protected int getFirmwareWidth() {
        return convertDataToInt(WidthBoardTwo.getLastValue().toString()) + (int) coilTwoPulseParametersModel.shiftProperty().get();
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
