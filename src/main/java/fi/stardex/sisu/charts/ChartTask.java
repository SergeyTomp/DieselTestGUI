package fi.stardex.sisu.charts;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.model.CoilTwoPulseParametersModel;
import fi.stardex.sisu.model.InjConfigurationModel;
import fi.stardex.sisu.model.InjectorModel;
import fi.stardex.sisu.model.InjectorTypeModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.VoltAmpereProfileDialogModel;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.util.filters.FilterInputChartData;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import net.wimpi.modbus.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

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
    protected VoltageController voltageController;

    @Autowired
    protected InjectorSectionController injectorSectionController;

    @Autowired
    protected FirmwareVersion<UltimaVersions> ultimaFirmwareVersion;

    @Autowired
    protected InjConfigurationModel injConfigurationModel;

    protected abstract ModbusMapUltima getCurrentGraph();

    @Autowired
    private VoltAmpereProfileDialogModel voltAmpereProfileDialogModel;

    @Autowired
    protected CoilTwoPulseParametersModel coilTwoPulseParametersModel;

    @Autowired
    private InjectorTypeModel injectorTypeModel;

    @Autowired
    private InjectorModel injectorModel;

    protected abstract ModbusMapUltima getCurrentGraphFrameNum();

    protected abstract ModbusMapUltima getCurrentGraphUpdate();

    protected abstract int getFirmwareWidth();

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

        if (injectorTypeModel.injectorTypeProperty().get() == InjectorType.PIEZO_DELPHI) {

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
        if (injectorTypeModel.injectorTypeProperty().get() == InjectorType.PIEZO) {

            double xOffset = 0;

            for (Double aData : data) {
                pointsList.add(new XYChart.Data<>(xValue, -aData / CURRENT_COEF));
                xValue += X_VALUE_OFFSET;
                xOffset += X_VALUE_OFFSET;
                if (xOffset > 200)
                    break;
            }

        } else if (injectorTypeModel.injectorTypeProperty().get() == InjectorType.PIEZO_DELPHI) {

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

        if (injectorSectionController.getArrayNumbersOfActiveLedToggleButtons().size() == 0)
            return;

        if(injConfigurationModel.injConfigurationProperty().get() == InjectorChannel.SINGLE_CHANNEL){
            int number = getChartNumber();
            if (number == 2 | number == 3 | number == 4)
                return;

        } else {

//            List<Integer> activeLedControllerNumbers = injectorSectionController.fillArrayNumbersOfActiveLedToggleButtons();
//            if (!activeLedControllerNumbers.contains(getChartNumber()))
//                return;

            if (!injectorSectionController.getArrayNumbersOfActiveLedToggleButtons().contains(getChartNumber())) {

                if (!voltAmpereProfileDialogModel.isDoubleCoilProperty().get()) {

                    return;
                } else if (getChartNumber() == 3 || getChartNumber() == 4) {
                    return;
                }
            }
        }

        int n;

        firmwareWidth = getFirmwareWidth();

        InjectorType injectorType = injectorTypeModel.injectorTypeProperty().get();

        int offset;

        if (injectorType == InjectorType.COIL) {

            offset = 200;
            n = (int) ((firmwareWidth + offset) / X_VALUE_OFFSET);

        } else if (injectorType == InjectorType.PIEZO)

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

            logger.error("Cast Exception: ChartTask " + getChartNumber());
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
