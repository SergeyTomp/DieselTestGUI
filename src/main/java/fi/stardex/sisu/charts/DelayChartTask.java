package fi.stardex.sisu.charts;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.util.DelayCalculator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import net.wimpi.modbus.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Scope("prototype")
public class DelayChartTask extends ChartTask {

    private Logger logger = LoggerFactory.getLogger(DelayChartTask.class);

    private ObservableList<XYChart.Data<Double, Double>> delayData;

    private Spinner<Double> sensitivitySpinner;

    private TextField minimumDelayTextField;

    private TextField maximumDelayTextField;

    private TextField averageDelayTextField;

    private List<ToggleButton> activeLedToggleButtonsList;

    private boolean updateOSC;

    private static final double PULSE_LENGTH_STEP = 32.888;

    private static final int DELAY_SAMPLE_SIZE = 256;

    private final DelayCalculator delayCalculator;

    private final DelayController delayController;

    @Autowired
    public DelayChartTask(DelayCalculator delayCalculator, DelayController delayController) {

        this.delayCalculator = delayCalculator;
        this.delayController = delayController;
        delayData = delayController.getDelayData();
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

    @Override
    public void run() {

        updateOSC = delayController.isTabDelayShowingProperty().get();

        activeLedToggleButtonsList = injectorSectionController.getActiveLedToggleButtonsList();

        ToggleButton ledController = singleSelected();

        if (ledController == null) {
            delayController.showAttentionLabel(true);
            return;
        } else
            delayController.showAttentionLabel(false);
            delayController.setChannelNumber(getNumber(ledController));

        int addingTime = delayController.getAddingTimeValue();

        int n = (int) ((injectorSectionController.getWidthCurrentSignalSpinner().getValue() + addingTime) / PULSE_LENGTH_STEP) > DELAY_SAMPLE_SIZE - 1 ?
                DELAY_SAMPLE_SIZE - 1 : (int) ((injectorSectionController.getWidthCurrentSignalSpinner().getValue() + addingTime) / PULSE_LENGTH_STEP);

        int remainder = n % DELAY_SAMPLE_SIZE;

        int injectorModbusChannel = injConfigurationState.injConfigurationStateProperty().get() == InjectorChannel.SINGLE_CHANNEL ? 1 : getNumber(ledController);

        ArrayList<Integer> resultDataList = new ArrayList<>();

        RegisterProvider ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();

        try {

            if (!updateOSC) {
                return;
            }
            ultimaModbusWriter.add(getCurrentGraphFrameNum(), injectorModbusChannel);
            ultimaModbusWriter.add(getCurrentGraphUpdate(), true);
            waitForUpdate(ultimaRegisterProvider, 300);
            Integer[] data = ultimaRegisterProvider.readBytePacket(getCurrentGraph().getRef(), remainder);
            addModbusData(resultDataList, data);

        } catch (ModbusException e) {

            logger.error("Cannot obtain delay graphic", e);
            return;

        } catch (ClassCastException e) {

            logger.error("Cast Exception: ", e);
            return;

        }

        if (!updateOSC) {
            return;
        }

        Platform.runLater(() -> addData(resultDataList, delayData));

    }

    private ToggleButton singleSelected() {
        return (activeLedToggleButtonsList.size() != 1) ? null : activeLedToggleButtonsList.get(0);
    }

    @Override
    protected void addDataToChart(double[] data, ObservableList<XYChart.Data<Double, Double>> chartData) {

        chartData.clear();

        double xValue = 0;

        List<XYChart.Data<Double, Double>> pointsList = new ArrayList<>();

        for (Double aData : data) {
            pointsList.add(new XYChart.Data<>(xValue, aData / 4095 * 3.3));
            xValue += PULSE_LENGTH_STEP;
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

        if (point == null)
            logger.debug("Point not found");
        else {
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

        return (t1 * spinner - t2 * spinner + t2 * v1 - t1 * v2) / (v1 - v2);

    }

    private void setDelayValues() {

        minimumDelayTextField.setText(String.format("%.0f", delayCalculator.getMinimumDelay()));
        maximumDelayTextField.setText(String.format("%.0f", delayCalculator.getMaximumDelay()));
        averageDelayTextField.setText(String.format("%.0f", delayCalculator.getAverageDelay()));
    }

    private int getNumber(ToggleButton ledBeakerController) {
        return Integer.parseInt(ledBeakerController.getText());
    }
}
