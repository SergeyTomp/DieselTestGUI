package fi.stardex.sisu.charts;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.filters.FilterInputChartData;
import fi.stardex.sisu.version.UltimaFirmwareVersion;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import net.wimpi.modbus.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Scope("prototype")
public class DelayChartTask extends ChartTask {
    private static Logger logger = LoggerFactory.getLogger(DelayChartTask.class);

    private static final int STEP_SIZE = 10;

    private static int addingTime;

    private  ObservableList<XYChart.Data<Double, Double>> delayData;

    private  Spinner<Double> sensitivitySpinner;
    private  ComboBox<InjectorChannel> injectorChannelComboBox;
    private  TextField minimumDelayTextField;
    private  TextField maximumDelayTextField;
    private  TextField averageDelayTextField;
    private List<LedController> activeControllers;
    private boolean updateOSC;

    @Autowired
    private ModbusRegisterProcessor ultimaModbusWriter;
    @Autowired
    private DelayCalculator delayCalculator;
    @Autowired
    private SettingsController settingsController;
    @Autowired
    private  InjectorSectionController injectorSectionController;
    @Autowired
    private RegisterProvider ultimaRegisterProvider;
    @Autowired
    private DelayController delayController;

    @PostConstruct
    private void init(){
        delayData = delayController.getDelayData();
        injectorChannelComboBox = settingsController.getInjectorsConfigComboBox();
        sensitivitySpinner = delayController.getSensitivitySpinner();
        minimumDelayTextField = delayController.getMinimumDelay();
        maximumDelayTextField = delayController.getMaximumDelay();
        averageDelayTextField = delayController.getAverageDelay();
    }

    @Override
    protected ModbusMapUltima getCurrentGraph() {
        return ModbusMapUltima.Delay_graph;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphFrameNum() {
        return ModbusMapUltima.Delay_graph_channel_num;
    }

    @Override
    protected ModbusMapUltima getCurrentGraphUpdate() {
        return ModbusMapUltima.Delay_graph_update;
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getData() {
        return delayController.getDelayData();
    }

    @Override
    protected int getChartNumber() {
        return 5;
    }

    public void setUpdateOSC(boolean updateOSC) {
        this.updateOSC = updateOSC;
    }
    public static void setAddingTime(int newTime) {
        addingTime = newTime;
    }
    @Override
    public void run() {
        updateOSC = delayController.isTabDelayShowingProperty().get();
        activeControllers = injectorSectionController.getActiveControllers();
        LedController ledController = singleSelected();
        if(ledController == null){
            delayController.showAttentionLabel(true);
            return;
        }
        delayController.showAttentionLabel(false);
        double pulseLengthStep = UltimaFirmwareVersion.getUltimaFirmwareVersion().getPulseLengthStep();
        int delaySampleSize = UltimaFirmwareVersion.getUltimaFirmwareVersion().getDelaySampleSize();

        int n = (int) ((injectorSectionController.getWidthCurrentSignal().getValue() + addingTime) / pulseLengthStep) > delaySampleSize - 1 ?
                delaySampleSize - 1 : (int) ((injectorSectionController.getWidthCurrentSignal().getValue() + addingTime) / pulseLengthStep);

        int remainder = n % delaySampleSize;

        int injectorModbusChannel = injectorChannelComboBox.getSelectionModel().getSelectedItem() == InjectorChannel.SINGLE_CHANNEL ?
                1 : ledController.getNumber();

        ArrayList<Integer> resultDataList = new ArrayList<>();
        try {
            if (!updateOSC) {
                return;
            }
            ultimaModbusWriter.add(getCurrentGraphFrameNum(), injectorModbusChannel);
            ultimaModbusWriter.add(getCurrentGraphUpdate(), true);
            boolean ready;
            do {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    logger.error("Interrupted 1.", e);
                }
                try {
                    if (!updateOSC) {
                        return;
                    }
                    ready = (boolean) ultimaRegisterProvider.read(getCurrentGraphUpdate());
                } catch (ClassCastException e) {
                    logger.error("Cast Exception: ", e);
                    return;
                }
            } while (ready);
            Integer[] data = ultimaRegisterProvider.readBytePacket(getCurrentGraph().getRef(), remainder);
            addModbusData(resultDataList, data);
        } catch (ModbusException e) {
            logger.error("Cannot obtain delay graphic", e);
            return;
        }catch (ClassCastException e) {
            logger.error("Cast Exception: ", e);
            return;
        }
        if (!updateOSC) {
            return;
        }
        Platform.runLater(() -> addData(resultDataList, delayData));
    }

    private LedController singleSelected() {
        if (activeControllers.size() != 1) { return null;}
        LedController ledController = activeControllers.get(0);
        if (ledController.getNumber() > 4) { return null;}
        return ledController;
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

        if (UltimaFirmwareVersion.getUltimaFirmwareVersion() != UltimaFirmwareVersion.MULTI_CHANNEL_FIRMWARE_WO_FILTER)
            return FilterInputChartData.medianFilter(doubleData, STEP_SIZE);
        else
            return doubleData;
    }
    private void addData(ArrayList<Integer> resultDataList, ObservableList<XYChart.Data<Double, Double>> chartData) {
        double[] resultData = getFilteredResultData(resultDataList);
        addDataToChart(resultData, chartData);
    }

    private void addDataToChart(double[] data, ObservableList<XYChart.Data<Double, Double>> chartData) {
        chartData.clear();
        double pulseLengthStep = UltimaFirmwareVersion.getUltimaFirmwareVersion().getPulseLengthStep();
        double xValue = 0;
        List<XYChart.Data<Double, Double>> pointsList = new ArrayList<>();
        for (Double aData : data) {
            pointsList.add(new XYChart.Data<>(xValue, aData / 4095 * 3.3));
            xValue += pulseLengthStep;
        }
        chartData.addAll(pointsList);
        setDelay(pointsList);
    }

    private void setDelay(final List<XYChart.Data<Double, Double>> resultDataList) {

        List<XYChart.Data<Double, Double>> testPoints = new ArrayList<>();
        Iterator<XYChart.Data<Double, Double>> iterator = resultDataList.iterator();
        Double point = null;
        double spinner = sensitivitySpinner.getValue();
        while (iterator.hasNext()) {
            XYChart.Data<Double, Double> nextPoint = iterator.next();
            testPoints.add(nextPoint);
            if (testPoints.size() >= 3) {
                double head = testPoints.get(testPoints.size() - 3).getYValue();
                double middle = testPoints.get(testPoints.size() - 2).getYValue();
                double last = testPoints.get(testPoints.size() - 1).getYValue();
                if (spinner > head && spinner < middle && middle < last) {
                    point = calculatePoint(testPoints.get(testPoints.size() - 2), testPoints.get(testPoints.size() - 3));
                    break;
                }
            }
        }
        if (point == null) {
            logger.debug("Point not found");
        } else {
            delayCalculator.addDelayValue(point);
            Platform.runLater(this::setDelayValues);
        }
    }

    private double calculatePoint(XYChart.Data<Double, Double> point1, XYChart.Data<Double, Double> point2) {
        double spinner = sensitivitySpinner.getValue();
        Double t1 = point1.getXValue();
        Double t2 = point2.getXValue();
        Double v1 = point1.getYValue();
        Double v2 = point2.getYValue();
        return  (t1*spinner - t2*spinner + t2*v1 - t1*v2) / (v1 - v2);
    }
    private void setDelayValues() {
        minimumDelayTextField.setText(String.format("%.0f", delayCalculator.getMinimumDelay()));
        maximumDelayTextField.setText(String.format("%.0f", delayCalculator.getMaximumDelay()));
        averageDelayTextField.setText(String.format("%.0f", delayCalculator.getAverageDelay()));

    }
}
