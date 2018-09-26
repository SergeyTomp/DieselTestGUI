package fi.stardex.sisu.charts;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.filters.FilterInputChartData;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Toggle;
import net.wimpi.modbus.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.version.UltimaFirmwareVersion.UltimaVersions;
import static fi.stardex.sisu.version.UltimaFirmwareVersion.UltimaVersions.WITHOUT_F;

@Component
public abstract class ChartTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(ChartTask.class);

    private static final double X_VALUE_OFFSET = 0.95;

    private static final double CURRENT_COEF = 93.07;

    private boolean updateOSC;

    private int firmwareWidth;

    @Autowired
    protected ModbusRegisterProcessor ultimaModbusWriter;

    @Autowired
    protected SettingsController settingsController;

    @Autowired
    protected VoltageController voltageController;

    @Autowired
    protected InjectorSectionController injectorSectionController;

    @Autowired
    protected FirmwareVersion<UltimaVersions> ultimaFirmwareVersion;

    protected abstract ModbusMapUltima getCurrentGraph();

    protected abstract ModbusMapUltima getCurrentGraphFrameNum();

    protected abstract ModbusMapUltima getCurrentGraphUpdate();

    protected abstract ObservableList<XYChart.Data<Double, Double>> getData();

    protected abstract int getChartNumber();

    void setUpdateOSC(boolean updateOSC) {
        this.updateOSC = updateOSC;
    }

    protected void addModbusData(ArrayList<Integer> resultDataList, Integer[] data) {

        resultDataList.addAll(Arrays.asList(data));

    }

    protected double[] getFilteredResultData(ArrayList<Integer> resultDataList) {

        double[] doubleData = new double[resultDataList.size()];

        for (int i = 0; i < doubleData.length; i++) {
            if (resultDataList.get(i) == null)
                break;
            doubleData[i] = resultDataList.get(i).doubleValue();
        }

        if (ultimaFirmwareVersion.getVersions() != WITHOUT_F)
            return FilterInputChartData.medianFilter(doubleData);
        else
            return doubleData;

    }

    protected void addDataToChart(double[] data, ObservableList<XYChart.Data<Double, Double>> chartData) {

        chartData.clear();

        double xValue = 0;

        List<XYChart.Data<Double, Double>> pointsList = new ArrayList<>();

        List<XYChart.Data<Double, Double>> piezoDelphiNegativePoints = new ArrayList<>();

        if (injectorSectionController.getPiezoDelphiRadioButton().isSelected()) {

            for (double aData : data) {
                pointsList.add(new XYChart.Data<>(xValue, aData / CURRENT_COEF));
                if (xValue >= firmwareWidth)
                    piezoDelphiNegativePoints.add(new XYChart.Data<>(xValue, -aData / CURRENT_COEF));

                xValue += X_VALUE_OFFSET;
            }

        } else {

            for (double aData : data) {
                pointsList.add(new XYChart.Data<>(xValue, aData / CURRENT_COEF));
                xValue += X_VALUE_OFFSET;
            }

        }
        if (injectorSectionController.getPiezoRadioButton().isSelected()) {

            double xOffset = 0;

            for (Double aData : data) {
                pointsList.add(new XYChart.Data<>(xValue, -aData / CURRENT_COEF));
                xValue += X_VALUE_OFFSET;
                xOffset += X_VALUE_OFFSET;
                if (xOffset > 200)
                    break;
            }

        } else if (injectorSectionController.getPiezoDelphiRadioButton().isSelected()) {

            int i = 0;

            for (XYChart.Data<Double, Double> point : piezoDelphiNegativePoints) {
                pointsList.get(i).setYValue(point.getYValue());
                i++;
            }

        }

        chartData.addAll(pointsList);

    }

    protected void addData(ArrayList<Integer> resultDataList, ObservableList<XYChart.Data<Double, Double>> chartData) {

        double[] resultData = getFilteredResultData(resultDataList);
        addDataToChart(resultData, chartData);


    }

    @Override
    public void run() {

        updateOSC = voltageController.isTabVoltageShowingProperty().get();

        if (injectorSectionController.getActiveControllers().size() == 0)
            return;

        if (settingsController.getInjectorsConfigComboBox().getSelectionModel().getSelectedItem() == InjectorChannel.SINGLE_CHANNEL) {

            int number = getChartNumber();
            if (number == 2 | number == 3 | number == 4)
                return;

        } else {

//            List<Integer> activeLedControllerNumbers = injectorSectionController.fillArrayNumbersOfActiveControllers();
//            if (!activeLedControllerNumbers.contains(getChartNumber()))
//                return;
            if(!injectorSectionController.getArrayNumbersOfActiveControllers().contains(getChartNumber())) return;
        }

        int n;

        firmwareWidth = convertDataToInt(ModbusMapUltima.WidthBoardOne.getLastValue().toString());

        Toggle selectedToggle = injectorSectionController.getPiezoCoilToggleGroup().getSelectedToggle();

        int offset;

        if (selectedToggle == injectorSectionController.getCoilRadioButton()) {

            offset = 200;
            n = (int) ((firmwareWidth + offset) / X_VALUE_OFFSET);

        } else if (selectedToggle == injectorSectionController.getPiezoRadioButton())

            n = (int) ((firmwareWidth) / X_VALUE_OFFSET);

        else {

            offset = firmwareWidth < 500 ? firmwareWidth : 500;
            n = (int) ((firmwareWidth + offset) / X_VALUE_OFFSET);

        }

        int div = n / 2047;

        int remainder = n % 2047;

        int part = 1;

        if (!updateOSC)
            return;

        ArrayList<Integer> resultDataList = new ArrayList<>();

        RegisterProvider ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();

        try {

            for (int i = 0; i < div; i++) {
                if (!updateOSC)
                    return;
                ultimaModbusWriter.add(getCurrentGraphFrameNum(), part);
                ultimaModbusWriter.add(getCurrentGraphUpdate(), true);
                waitForUpdate(ultimaRegisterProvider, 500);
                Integer[] data = (Integer[]) ultimaRegisterProvider.read(getCurrentGraph());
                addModbusData(resultDataList, data);
                part++;
            }

            if (!updateOSC)
                return;

            ultimaModbusWriter.add(getCurrentGraphFrameNum(), part);
            ultimaModbusWriter.add(getCurrentGraphUpdate(), true);
            waitForUpdate(ultimaRegisterProvider, 500);
            Integer[] data = ultimaRegisterProvider.readBytePacket(getCurrentGraph().getRef(), remainder);
            addModbusData(resultDataList, data);

        } catch (ModbusException e) {

            logger.error("Cannot obtain graphic 2", e);
            return;

        } catch (ClassCastException e) {

            logger.error("Cast Exception: ");
            return;

        }

        Platform.runLater(() -> {
            if(injectorSectionController.getInjectorSectionStartToggleButton().isSelected()){
                ChartTask.this.addData(resultDataList, ChartTask.this.getData());
            }
        });


    }

    protected void waitForUpdate(RegisterProvider ultimaRegisterProvider, int sleepTime) {

        boolean ready;

        do {

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.error("Interrupted 1.", e);
            }

            try {

                if (!updateOSC)
                    return;
                ready = (boolean) ultimaRegisterProvider.read(getCurrentGraphUpdate());

            } catch (ClassCastException e) {

                logger.error("Cast Error: ", e);
                return;

            }

        } while (ready);

    }

}
