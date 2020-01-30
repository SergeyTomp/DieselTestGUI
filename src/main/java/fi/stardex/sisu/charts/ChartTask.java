package fi.stardex.sisu.charts;

import fi.stardex.sisu.model.ChartTaskDataModel;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.cr.*;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.uis.UisTabSectionModel;
import fi.stardex.sisu.model.uis.UisVapModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.InjectorControllersState;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.states.VoltAmpereProfileDialogModel;
import fi.stardex.sisu.ui.controllers.cr.tabs.VoltageController;
import fi.stardex.sisu.util.enums.InjectorChannel;
import fi.stardex.sisu.util.enums.InjectorSubType;
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

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.SecondSignalInterval;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.WidthBoardTwo;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.enums.InjectorSubType.*;
import static fi.stardex.sisu.util.enums.InjectorType.*;
import static fi.stardex.sisu.version.UltimaFirmwareVersion.UltimaVersions;
import static fi.stardex.sisu.version.UltimaFirmwareVersion.UltimaVersions.WITHOUT_F;

@Component
public abstract class ChartTask extends TimerTask {

    private final Logger logger = LoggerFactory.getLogger(ChartTask.class);

    private static final double X_VALUE_OFFSET = 0.95;

    private static final double CURRENT_COEF = 93.07;

    protected boolean updateOSC;

    private int firmwareWidth;

    private InjectorType injectorType;

    @Autowired
    protected ModbusRegisterProcessor ultimaModbusWriter;

    @Autowired
    protected VoltageController voltageController;

    @Autowired
    protected UisTabSectionModel uisTabSectionModel;

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
    protected InjectorControllersState injectorControllersState;

    @Autowired
    private InjectorSectionPwrState injectorSectionPwrState;

    @Autowired
    private GUI_TypeModel gui_typeModel;

    @Autowired
    private MainSectionUisModel mainSectionUisModel;

    @Autowired
    private UisVapModel uisVapModel;

    @Autowired
    protected CoilOnePulseParametersModel coilOnePulseParametersModel;

    @Autowired
    protected ChartTaskDataModel chartTaskDataModel;

    @Autowired
    protected UisInjectorSectionModel uisInjectorSectionModel;

    private final int DERIVATIVE_OFFSET = 10; // Yi+10 - Yi   use for search DERIVATIVE.
    private final double STEP_BIP = -5.0; //add to Bip Line if bip doesn't search
    private final int MAX_REPEAT = 20; // max repeat loop for search Bip signal

    protected abstract ModbusMapUltima getCurrentGraphFrameNum();

    protected abstract ModbusMapUltima getCurrentGraphUpdate();

    protected abstract int getFirmwareWidth();

    protected abstract ObservableList<XYChart.Data<Double, Double>> getData();

    protected abstract ObservableList<XYChart.Data<Double, Double>> getChartDataList();

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

        switch (gui_typeModel.guiTypeProperty().get()) {

            case CR_Inj:
            case HEUI:

                if (injectorType == PIEZO_DELPHI) {

                    List<XYChart.Data<Double, Double>> piezoDelphiNegativePoints = new ArrayList<>();

                    for (double aData : data) {
                        pointsList.add(new XYChart.Data<>(xValue, aData / CURRENT_COEF));
                        if (xValue >= firmwareWidth){
                            piezoDelphiNegativePoints.add(new XYChart.Data<>(xValue, -aData / CURRENT_COEF));
                        }
                        xValue += X_VALUE_OFFSET;
                    }

                    int i = 0;
                    for (XYChart.Data<Double, Double> point : piezoDelphiNegativePoints) {
                        pointsList.get(i).setYValue(point.getYValue());
                        i++;
                    }

                } else {

                    for (double aData : data) {
                        pointsList.add(new XYChart.Data<>(xValue, aData / CURRENT_COEF));
                        xValue += X_VALUE_OFFSET;
                    }

                    if (injectorType == PIEZO) {

                        double xOffset = 0;
                        for (Double aData : data) {
                            pointsList.add(new XYChart.Data<>(xValue, -aData / CURRENT_COEF));
                            xValue += X_VALUE_OFFSET;
                            xOffset += X_VALUE_OFFSET;
                            if (xOffset > 200)
                                break;
                        }
                    }
                }
                break;

            case UIS:

                for (Double aData : data) {
                    pointsList.add(new XYChart.Data<>(xValue, aData / CURRENT_COEF));
                    xValue += X_VALUE_OFFSET;
                }

                if (injectorType == PIEZO) {

                    double xOffset = 0;
                    for (Double aData : data) {
                        pointsList.add(new XYChart.Data<>(xValue, -aData / CURRENT_COEF));
                        xValue += X_VALUE_OFFSET;
                        xOffset += X_VALUE_OFFSET;
                        if (xOffset > 200) {
                            break;
                        }
                    }
                }

                if (isBipTest(mainSectionUisModel.injectorTestProperty().get())) {

                    int reduction = (int)(uisVapModel.bipWindowProperty().get() * 0.05); // BipWindow - 0.05 ; 5% cut of array both sides
                    int timeStart = uisVapModel.firstWProperty().get() - reduction;
                    int timeEnd = timeStart + uisVapModel.bipWindowProperty().get() - reduction;
                    double bipSignalValue = getBipSignalValue(data, timeStart, timeEnd);
                    chartTaskDataModel.bipSignalValueProperty().setValue(bipSignalValue);
                }
                break;
        }
        chartData.addAll(pointsList);
    }

    protected void addData(ArrayList<Integer> resultDataList, ObservableList<XYChart.Data<Double, Double>> chartData) {

        double[] resultData = getFilteredResultData(resultDataList);
        addDataToChart(resultData, chartData);
    }

    private boolean isChartActive() {

        switch (gui_typeModel.guiTypeProperty().get()) {

            case CR_Inj:
            case HEUI:

                if (injectorControllersState.getArrayNumbersOfActiveLedToggleButtons().size() == 0
                        || !voltageController.isTabVoltageShowingProperty().get()){ return false;}

                if (injConfigurationModel.injConfigurationProperty().get() == InjectorChannel.SINGLE_CHANNEL) {

                    int number = getChartNumber();
                    return number != 2 && number != 3 && number != 4;
                } else {

                    if (!injectorControllersState.getArrayNumbersOfActiveLedToggleButtons().contains(getChartNumber())) {

                        return isDoubleCoil() && (getChartNumber() != 3 && getChartNumber() != 4);
                    }
                }
                return true;
            case UIS:

                if (uisInjectorSectionModel.activeLedToggleButtonsListProperty().get().isEmpty()) { return false; }
                int number = getChartNumber();
                return isDoubleCoil() ? number != 3 && number != 4 : number != 2 && number != 3 && number != 4;
            default:
                return false;
        }
    }

    protected int calculatePointsNumber() {

        int pointsNumber;
        int offset;

        switch (gui_typeModel.guiTypeProperty().get()) {

            case CR_Inj:
            case HEUI:

                if (injectorType == COIL) {
                    offset = 200;
                    pointsNumber = (int) ((firmwareWidth + offset) / X_VALUE_OFFSET);

                } else if (injectorType == PIEZO)
                    pointsNumber = (int) ((firmwareWidth) / X_VALUE_OFFSET);
                else {
                    offset = firmwareWidth < 500 ? firmwareWidth : 500;
                    pointsNumber = (int) ((firmwareWidth + offset) / X_VALUE_OFFSET);
                }
                return pointsNumber;

            case UIS:

                InjectorSubType injectorSubType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorSubType();
                int width_2;

                if (injectorType == COIL) {

                    if (injectorSubType == DOUBLE_SIGNAL) {

                        width_2 = convertDataToInt(SecondSignalInterval.getLastValue().toString());
                        pointsNumber = (int)((width_2 + 1000) / X_VALUE_OFFSET);

                    } else if (injectorSubType == HPI) {

                        width_2 = convertDataToInt(WidthBoardTwo.getLastValue().toString());
                        pointsNumber = firmwareWidth > width_2 ? (int) ((firmwareWidth + 1000) / X_VALUE_OFFSET) : (int) ((width_2 + 1000) / X_VALUE_OFFSET);

                    } else {

                        pointsNumber = (int) ((firmwareWidth + 1000) / X_VALUE_OFFSET);
                    }
                } else {

                    pointsNumber = (int) (firmwareWidth / X_VALUE_OFFSET);
                }
                return pointsNumber;

            default:return (int) (firmwareWidth / X_VALUE_OFFSET);
        }
    }

    private InjectorType checkInjectorType() {

        switch (gui_typeModel.guiTypeProperty().get()) {
            case CR_Inj:
            case HEUI:
                return injectorTypeModel.injectorTypeProperty().get();
            case UIS:
                return mainSectionUisModel.modelProperty().get().getVAP().getInjectorType();
            default:return COIL;
        }
    }

    private boolean isDoubleCoil() {

        switch (gui_typeModel.guiTypeProperty().get()) {
            case CR_Inj:
            case HEUI:
                return voltAmpereProfileDialogModel.isDoubleCoilProperty().get();
            case UIS:
                InjectorSubType injectorSubType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorSubType();
                return injectorSubType == DOUBLE_COIL || injectorSubType == HPI;
            default:return false;
        }
    }

    private boolean isBipTest(Test test) {
        return ((InjectorUisTest)test).getVoltAmpereProfile().getBipPWM() != null
                && ((InjectorUisTest)test).getVoltAmpereProfile().getBipWindow() != null;
    }

    @Override
    public void run() {

        if (!updateOSC)
            return;

        if (!isChartActive()) {
            return;
        }

        firmwareWidth = getFirmwareWidth();
        injectorType = checkInjectorType();
        int pointsNumber = calculatePointsNumber();

        int framesNumber = pointsNumber / 2047;
        int pointsRemainder = pointsNumber % 2047;
        int part = 1;

        if (!updateOSC)
            return;

        ArrayList<Integer> resultDataList = new ArrayList<>();
        RegisterProvider ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();

        try {

            for (int i = 0; i < framesNumber; i++) {
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
            Integer[] data = ultimaRegisterProvider.readBytePacket(getCurrentGraph().getRef(), pointsRemainder);
            addModbusData(resultDataList, data);

        } catch (ModbusException e) {

            logger.error("Cannot obtain graphic 2", e);
            return;

        } catch (ClassCastException e) {

            logger.error("Cast Exception: ChartTask " + getChartNumber());
            return;

        }

        if (!updateOSC)
            return;

        refreshCharts(resultDataList);
    }

    protected void refreshCharts(ArrayList<Integer> resultDataList) {

        switch (gui_typeModel.guiTypeProperty().get()) {
            case CR_Inj:
            case HEUI:
                Platform.runLater(() -> {
                    if(injectorSectionPwrState.powerButtonProperty().get()){
                        ChartTask.this.addData(resultDataList, ChartTask.this.getData());
                    }
                });
                break;
            case UIS:
                Platform.runLater(() -> {
                    if(uisInjectorSectionModel.injectorButtonProperty().get()){
                        ChartTask.this.addData(resultDataList, ChartTask.this.getChartDataList());
                    }
                });
                break;
                default:break;
        }
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

    protected double getBipSignalValue(double data[], int timeStart, int timeEnd) {
        double bipSignal = 0.0;
        if (data.length >= (timeEnd / X_VALUE_OFFSET)) {
            double derivative; // Derivative
            int indexStart = (int) (timeStart / X_VALUE_OFFSET);
            int indexEnd = (int) (timeEnd / X_VALUE_OFFSET);
            data = smooth(data, 20); // set filter Simple Moving Average code from https://habrahabr.ru/post/134375/
            for (double lineMax = 0.0; (bipSignal < 1.0) && (lineMax >= STEP_BIP * MAX_REPEAT); lineMax += STEP_BIP) { //lineMax is line when bip signal is coming
                for (int i = indexStart; i < indexEnd - DERIVATIVE_OFFSET; i++) {
                    derivative = data[i + DERIVATIVE_OFFSET] - data[i];
                    if (derivative >= lineMax) {
                        bipSignal = i + 1;
                        break;
                    }
                }
            }
            bipSignal = bipSignal * X_VALUE_OFFSET;
        } else {
            logger.warn("{Data Array is so small} " + data.length);
        }
        return bipSignal;
    }

    // Filter - see https://habrahabr.ru/post/134375/
    private double[] smooth(double[] input, Integer window) {
        Integer n = input.length;
        double[] output = new double[input.length];
        Integer i, j, z, k1, k2, hw;
        Double tmp;
        if (window % 2 == 0) window++;
        hw = (window - 1) / 2;
        output[0] = input[0];

        for (i = 1; i < n; i++) {
            tmp = 0.0;
            if (i < hw) {
                k1 = 0;
                k2 = 2 * i;
                z = k2 + 1;
            } else if ((i + hw) > (n - 1)) {
                k1 = i - n + i + 1;
                k2 = n - 1;
                z = k2 - k1 + 1;
            } else {
                k1 = i - hw;
                k2 = i + hw;
                z = window;
            }
            for (j = k1; j <= k2; j++) {
                tmp = tmp + input[j];
            }
            output[i] = tmp / z;
        }
        return output;
    }
}
