package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import net.wimpi.modbus.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

public class ChartTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(ChartTask.class);

    private VoltageController voltageController;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private InjectorSectionController injectorSectionController;

    private static final double X_VALUE_OFFSET = 0.95;
    private static final double CURRENT_COEF = 93.07;
    private static final int STEP_SIZE = 10;

    private int offset = 0;

    public ChartTask(VoltageController voltageController, ModbusRegisterProcessor ultimaModbusWriter, InjectorSectionController injectorSectionController) {
        this.voltageController = voltageController;
        this.ultimaModbusWriter = ultimaModbusWriter;
        this.injectorSectionController = injectorSectionController;
    }

    private void addModbusData(ArrayList<Integer> resultDataList, Integer[] data) {
        resultDataList.addAll(Arrays.asList(data));
    }

    private double[] getFilteredResultData(ArrayList<Integer> resultDataList) {
        double[] doubleData = new double[resultDataList.size()];
        for (int i = 0; i < doubleData.length; i++) {
            if (resultDataList.get(i) == null)
                break;

            doubleData[i] = resultDataList.get(i).doubleValue();
        }
        return doubleData;
    }

    private void addDataToChart(double[] data, ObservableList<XYChart.Data<Double, Double>> chartData) {
        chartData.clear();
        double xValue = 0;
        List<XYChart.Data<Double, Double>> pointsList = new ArrayList<>();
        for (double aData : data) {
            pointsList.add(new XYChart.Data<>(xValue, aData / CURRENT_COEF));
            xValue += X_VALUE_OFFSET;
        }
        chartData.addAll(pointsList);
    }

    private void addData(ArrayList<Integer> resultDataList, ObservableList<XYChart.Data<Double, Double>> chartData) {
        double[] resultData = getFilteredResultData(resultDataList);
        addDataToChart(resultData, chartData);
    }

    @Override
    public void run() {

        int n;
        offset = 200;
        // TODO: hardcoded n
        //n = (int) ((injectorSectionController.getWidthCurrentSignal().getValue() + offset) / X_VALUE_OFFSET);
        n = 526;
        int div = n / 2047;
        int remainder = n % 2047;
        int part = 1;
        if (!injectorSectionController.isUpdateOSC())
            return;
        ArrayList<Integer> resultDataList = new ArrayList<>();
        try {
            for (int i = 0; i < div; i++) {
                if (!injectorSectionController.isUpdateOSC())
                    return;
                ultimaModbusWriter.add(ModbusMapUltima.Current_graph1_frame_num, part);
                ultimaModbusWriter.add(ModbusMapUltima.Current_graph1_update, true);
                boolean ready;
                do {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted 1.", e);
                    }
                    try {
                        if (!injectorSectionController.isUpdateOSC())
                            return;
                        ready = (boolean) ultimaModbusWriter.getRegisterProvider().read(ModbusMapUltima.Current_graph1_update);
                    } catch (ClassCastException e) {
                        logger.error("Cast Error: ", e);
                        return;
                    }
                } while (ready);
                Integer[] data = (Integer[]) ultimaModbusWriter.getRegisterProvider().read(ModbusMapUltima.Current_graph1);
                addModbusData(resultDataList, data);
                part++;
            }
            if (!injectorSectionController.isUpdateOSC())
                return;
            ultimaModbusWriter.add(ModbusMapUltima.Current_graph1_frame_num, part);
            ultimaModbusWriter.add(ModbusMapUltima.Current_graph1_update, true);
            boolean ready;
            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error("Interrupted 1.", e);
                }
                try {
                    if (!injectorSectionController.isUpdateOSC())
                        return;
                    ready = (boolean) ultimaModbusWriter.getRegisterProvider().read(ModbusMapUltima.Current_graph1_update);
                } catch (ClassCastException e) {
                    logger.error("Cast Exception: ", e);
                    return;
                }
            } while (ready);
            Integer[] data = ultimaModbusWriter.getRegisterProvider().readBytePacket(0, remainder);
            addModbusData(resultDataList, data);
        } catch (ModbusException e) {
            logger.error("Cannot obtain graphic 2", e);
            return;
        }
        Platform.runLater(() -> addData(resultDataList, voltageController.getData1()));
    }
}
