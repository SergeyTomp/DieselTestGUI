package fi.stardex.sisu.charts;

import fi.stardex.sisu.model.uis.UisDelayModel;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.util.enums.InjectorChannel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.cr.tabs.DelayController;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.enums.InjectorSubType;
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
    private Spinner<Double> sensitivitySpinner;
    private TextField minimumDelayTextField;
    private TextField maximumDelayTextField;
    private TextField averageDelayTextField;
    private List<ToggleButton> activeLedToggleButtonsList;
    private static final double PULSE_LENGTH_STEP = 32.888;
    private static final int DELAY_SAMPLE_SIZE = 256;
    private final DelayCalculator delayCalculator;
    private final DelayController delayController;
    private final GUI_TypeModel gui_typeModel;
    private final UisDelayModel uisDelayModel;
    private final UisInjectorSectionModel uisInjectorSectionModel;
    private final MainSectionUisModel mainSectionUisModel;
    private int slotNumber;

    @Autowired
    public DelayChartTask(DelayCalculator delayCalculator,
                          DelayController delayController,
                          GUI_TypeModel gui_typeModel,
                          UisDelayModel uisDelayModel,
                          UisInjectorSectionModel uisInjectorSectionModel,
                          MainSectionUisModel mainSectionUisModel) {

        this.delayCalculator = delayCalculator;
        this.delayController = delayController;
        this.gui_typeModel = gui_typeModel;
        this.uisDelayModel = uisDelayModel;
        this.uisInjectorSectionModel = uisInjectorSectionModel;
        this.mainSectionUisModel = mainSectionUisModel;
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
    protected int getFirmwareWidth() {
        return 0;
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getData() {
        return delayController.getDelayData();
    }

    @Override
    protected ObservableList<XYChart.Data<Double, Double>> getChartDataList() {
        return chartTaskDataModel.getDelayChartDataList();
    }

    @Override
    protected int getChartNumber() {
        return 5;
    }

    private boolean isChartActive() {

        boolean isActive;
        switch (gui_typeModel.guiTypeProperty().get()) {

            case CR_Inj:
            case HEUI:

                activeLedToggleButtonsList = injectorControllersState.activeLedToggleButtonsListProperty().get();
                ToggleButton ledController = singleSelected();

                if (ledController == null) {

                    delayController.showAttentionLabel(true);
                    isActive = false;
                } else{
                    delayController.showAttentionLabel(false);
                    delayController.setChannelNumber(getNumber(ledController));
                    slotNumber = injConfigurationModel.injConfigurationProperty().get() == InjectorChannel.SINGLE_CHANNEL ? 1 : getNumber(ledController);
                    isActive = delayController.isTabDelayShowingProperty().get();
                }
                break;

            case UIS:

                isActive = !uisInjectorSectionModel.activeLedToggleButtonsListProperty().get().isEmpty();

                InjectorSubType injectorSubType = mainSectionUisModel.injectorTestProperty().get().getVoltAmpereProfile().getInjectorSubType();
                boolean isDoubleCoil = injectorSubType == InjectorSubType.HPI || injectorSubType == InjectorSubType.F2E_COMMON;
                slotNumber = isDoubleCoil ? 2 : 1;
                break;

            default:isActive = false;
        }
        return isActive;
    }

    @Override
    protected int calculatePointsNumber() {

        int pointsNumber;
        int addingTime;

        switch (gui_typeModel.guiTypeProperty().get()) {

            case CR_Inj:
            case HEUI:

                addingTime = delayController.getAddingTimeValue();
                pointsNumber = (int) ((coilOnePulseParametersModel.widthProperty().get() + addingTime) / PULSE_LENGTH_STEP) > DELAY_SAMPLE_SIZE - 1 ?
                        DELAY_SAMPLE_SIZE - 1 : (int) ((coilOnePulseParametersModel.widthProperty().get() + addingTime) / PULSE_LENGTH_STEP);
                break;
            case UIS:
                /** Calculation of pointsNumber is a bit different for old UIS-GUI version.
                 * 1. Values of PULSE_LENGTH_STEP and DELAY_SAMPLE_SIZE for Ultima version 0x00 (very old products, about 4 pcs sold) differ from those for other versions (4.111, 2047 vs. 32.888, 256 correspondingly.).
                 * 2. Trigger value for TRUE/FALSE definition in pointsNumber formula calculation below is 256 - 1, but not == DELAY_SAMPLE_SIZE - 1 as in CR_Inj !!!)
                 * 3. TRUE condition in pointsNumber formula below corresponds to 2046 hard, again not DELAY_SAMPLE_SIZE - 1 in comparison with CR_Inj !!!
                 * All this stuff should be defined and fixed for CR_Inj and UIS upon the decision to implement support for 0x00 Ultima version.*/
                addingTime = uisDelayModel.addingTimeProperty().get();
                pointsNumber = (int) ((uisInjectorSectionModel.width_1Property().get() + addingTime) / PULSE_LENGTH_STEP) > DELAY_SAMPLE_SIZE - 1 ?
                        DELAY_SAMPLE_SIZE - 1 : (int) ((uisInjectorSectionModel.width_1Property().get() + addingTime) / PULSE_LENGTH_STEP);
                break;
            default:pointsNumber = 0;
        }
        return pointsNumber;
    }

    @Override
    public void run() {

        if (!updateOSC)
            return;

        if (!isChartActive()) {
            return;
        }

        int pointsNumber = calculatePointsNumber();
        int pointsRemainder = pointsNumber % DELAY_SAMPLE_SIZE;

        ArrayList<Integer> resultDataList = new ArrayList<>();
        RegisterProvider ultimaRegisterProvider = ultimaModbusWriter.getRegisterProvider();

        try {

            if (!updateOSC)
                return;

            ultimaModbusWriter.add(getCurrentGraphFrameNum(), slotNumber);
            ultimaModbusWriter.add(getCurrentGraphUpdate(), true);
            waitForUpdate(ultimaRegisterProvider, 300);
            Integer[] data = ultimaRegisterProvider.readBytePacket(getCurrentGraph().getRef(), pointsRemainder);
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
        refreshCharts(resultDataList);
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
        chartTaskDataModel.delayValueProperty().setValue(delayCalculator.getAverageDelay());
        chartTaskDataModel.maxDelayValueProperty().setValue(delayCalculator.getMaximumDelay());
        chartTaskDataModel.minDelayValueProperty().setValue(delayCalculator.getMinimumDelay());
    }

    private int getNumber(ToggleButton ledBeakerController) {
        return Integer.parseInt(ledBeakerController.getText());
    }
}
