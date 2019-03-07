package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.model.PiezoRepairModel;
import fi.stardex.sisu.model.updateModels.PiezoRepairUpdateModel;
import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.enums.VoltageRange;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.enums.VoltageRange.HIGH;
import static fi.stardex.sisu.util.enums.VoltageRange.LOW;

public class PiezoRepairController {

    @FXML private ImageView bulbImage;
    @FXML private Spinner<Double> currentSpinner;
    @FXML private Label output;
    @FXML private Label outputValue;
    @FXML private Label volt;
    @FXML private ToggleGroup toggleGroup;
    @FXML private RadioButton lowVoltageButton;
    @FXML private RadioButton highVoltageButton;
    @FXML private Spinner<Double> voltageSpinner;
    @FXML private ToggleButton startStopButton;
    @FXML private ProgressBar voltageAdjustment;
    @FXML private Text adjustingText;

    private Image darkBulb;
    private Image lightBulb;

    private ObservableList<String> startStopButtonStyleClass;
    private PiezoRepairModel piezoRepairModel;
    private PiezoRepairUpdateModel piezoRepairUpdateModel;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private Timeline pulseStartTimeLine;
    private Timeline progressTimeLine;
    private VoltageProgressBar adjustingTime;
    private boolean isSwitchRange;
    private boolean incorrectInput;
    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

    public void setPiezoRepairModel(PiezoRepairModel piezoRepairModel) {
        this.piezoRepairModel = piezoRepairModel;
    }
    public void setPiezoRepairUpdateModel(PiezoRepairUpdateModel piezoRepairUpdateModel) {
        this.piezoRepairUpdateModel = piezoRepairUpdateModel;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    @PostConstruct
    public void init() {

        startStopButtonStyleClass = startStopButton.getStyleClass();
        setupSpinners();
        setupListeners();
        initToggleGroup();
        setupTimeLines();
        adjustingTime = new VoltageProgressBar(voltageAdjustment, adjustingText, outputValue);
        output.setText("Output ");
        outputValue.setText(String.valueOf(LOW.getMin()));
        volt.setText(" V");
        darkBulb = new Image(getClass().getResourceAsStream("/img/pump_button-off.png"));
        lightBulb = new Image(getClass().getResourceAsStream("/img/pump_button-on.png"));
        bulbImage.setImage(darkBulb);
    }

    private void setupTimeLines(){

        pulseStartTimeLine = new Timeline(new KeyFrame(Duration.millis(500), event ->
                ultimaModbusWriter.add(PulseFrontSlump, true)));

        progressTimeLine = new Timeline();
        progressTimeLine.setCycleCount(Animation.INDEFINITE);
        adjustingText.setVisible(false);    // возможно adjustingText потом совсем убрать
        voltageAdjustment.setVisible(true);
        voltageAdjustment.setProgress(1);
    }

    private void setupSpinners() {

        setSpinnerRangeConstants(LOW);
        SpinnerManager.setupDoubleSpinner(voltageSpinner);
        LOW.setLastValue(LOW.getMin());
        HIGH.setLastValue(HIGH.getMin());
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 20, 0, 0.1));
        SpinnerManager.setupDoubleSpinner(currentSpinner);
    }

    private void initToggleGroup(){

        highVoltageButton.setToggleGroup(toggleGroup);
        lowVoltageButton.setToggleGroup(toggleGroup);
        piezoRepairModel.voltageRangeObjectProperty().setValue(LOW);
        lowVoltageButton.setSelected(true);
    }

    private void setSpinnerRangeConstants(VoltageRange range) {

        voltageSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                range.getMin(),
                range.getMax(),
                range.getInit(),
                range.getStep()));
    }

    private void setupListeners() {

        startStopButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

            startStopButtonStyleClass.clear();
            VoltageRange range = piezoRepairModel.voltageRangeObjectProperty().get();

            if (newValue) {
                startStopButtonStyleClass.add("stopButtonLight");
                disableNodes(true, lowVoltageButton, highVoltageButton, voltageSpinner, currentSpinner);
                front(range);
            }
            else {
                startStopButtonStyleClass.add("startButton");
                disableNodes(false, lowVoltageButton, highVoltageButton, voltageSpinner, currentSpinner);
                slump(range);
            }
            piezoRepairModel.startMeasureProperty().setValue(newValue);
        });

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            isSwitchRange = true;

            if (newValue == lowVoltageButton){
                switchRange(LOW);
            }
            else {
                switchRange(HIGH);
            }
            isSwitchRange = false;
        });

        voltageSpinner.valueProperty().addListener((observable, oldValue, newValue) ->{

            if (newValue != null) {
                if (isSwitchRange) {
                    return;
                }

                if (incorrectInput) {
                    incorrectInput = false;
                    return;
                }

                double max = piezoRepairModel.voltageRangeObjectProperty().get().getMax();
                double min = piezoRepairModel.voltageRangeObjectProperty().get().getMin();

                if (newValue > max || newValue < min || oldValue > max || oldValue < min) {
                    incorrectInput = true;
                    voltageSpinner.getValueFactory().setValue(piezoRepairModel.voltageRangeObjectProperty().get().getLastValue());
                    return;
                }

                progressTimeLine.stop();

                double delta = newValue - oldValue;
                double voltageStep = piezoRepairModel.voltageRangeObjectProperty().get().getStep();
                double upTimeStep = piezoRepairModel.voltageRangeObjectProperty().get().getUpTimeStep() * voltageStep;
                double downTimeStep = piezoRepairModel.voltageRangeObjectProperty().get().getDownTimeStep() * voltageStep;
                double timeStep = delta >= 0 ? upTimeStep : downTimeStep;
                double timeLineDuration = (Math.abs(delta) * timeStep / voltageStep);

                adjustingTime.setTimeStep((int)timeStep);
                adjustingTime.setProgress((int)timeLineDuration);
                adjustingTime.setInitialVoltage(oldValue);
                adjustingTime.setVoltageStep(piezoRepairModel.voltageRangeObjectProperty().get().getStep() * delta / Math.abs(delta));

                piezoRepairModel.voltageValueProperty().setValue(newValue);
                piezoRepairModel.voltageRangeObjectProperty().get().setLastValue(newValue);
                disableNodes(true, startStopButton, voltageSpinner, lowVoltageButton, highVoltageButton);

                sendVoltage();

                progressTimeLine.getKeyFrames().clear();
                progressTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(timeStep), event ->
                        tickProgress()));
                progressTimeLine.play();
            }
            else {
                incorrectInput = true;
                voltageSpinner.getValueFactory().setValue(piezoRepairModel.voltageRangeObjectProperty().get().getLastValue());
            }
        });

        currentSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                piezoRepairModel.currentValueProperty().setValue(newValue));

        piezoRepairUpdateModel.touchLevelProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null && newValue.intValue() > 50) {
                bulbImage.setImage(lightBulb);
            }else {
                bulbImage.setImage(darkBulb);
            }
        });
    }

    private void switchRange(VoltageRange range) {

        setSpinnerRangeConstants(range);

        double lastValue = range.getLastValue();

        piezoRepairModel.voltageRangeObjectProperty().setValue(range);
        voltageSpinner.getValueFactory().setValue(lastValue);

        if(range == HIGH){
            ultimaModbusWriter.add(range.getSwitchRangeRegister(), range.isLowRange());
        }

        sendVoltage();

        outputValue.setText(String.valueOf(lastValue));
    }

    private void sendVoltage() {

        ModbusMap voltageRegister = piezoRepairModel.voltageRangeObjectProperty().get().getVoltageRegister();
        float correction = (float)piezoRepairModel.voltageRangeObjectProperty().get().getCorrection();
        float value = voltageSpinner.getValue().floatValue();

        ultimaModbusWriter.add(voltageRegister, value + correction);
    }


    private void front(VoltageRange range){

        ultimaModbusWriter.add(HoldingPulseMode, true);
        ultimaModbusWriter.add(CurrentLimit, (int)(currentSpinner.getValue() * ONE_AMPERE_MULTIPLY));
        ultimaModbusWriter.add(BoardNumber, 1);
        if (range == LOW) {
            ultimaModbusWriter.add(StartOnBatteryUOne, true);
        }
        pulseStartTimeLine.play();
    }

    private void slump(VoltageRange range) {

        ultimaModbusWriter.add(HoldingPulseMode, false);
        if (range == LOW) {
            ultimaModbusWriter.add(StartOnBatteryUOne, false);
        }
        pulseStartTimeLine.stop();
    }

    private void disableNodes(boolean disable, Node ... nodes){
        for (Node n : nodes) {
            n.setDisable(disable);
        }
    }

    private void tickProgress(){

        if(adjustingTime.tick() <= 0){
            progressTimeLine.stop();
//            adjustingText.setVisible(false);      // возможно adjustingText потом совсем убрать
//            voltageAdjustment.setVisible(false);  // возможно voltageAdjustment потом совсем убрать
            disableNodes(false, startStopButton, voltageSpinner, lowVoltageButton, highVoltageButton);
        }
    }

    private class VoltageProgressBar {

        private ProgressBar progressBar;
        private Text text;
        private Label output;
        private int initialTime;
        private int timeStep;
        private double initialVoltage;
        private double voltageStep;

        VoltageProgressBar(ProgressBar progressBar, Text text, Label output) {
            this.progressBar = progressBar;
            this.text = text;
            this.output = output;
        }

        public void setTimeStep(int timeStep) {
            this.timeStep = timeStep;
        }
        public void setInitialVoltage(double initialVoltage) {
            this.initialVoltage = initialVoltage;
        }
        public void setVoltageStep(double voltageStep) {
            this.voltageStep = voltageStep;
        }

        private void setProgress(int time) {
            this.initialTime = time;
            text.setText(String.valueOf(initialTime));
            progressBar.setProgress(initialTime == 0 ? 1 : 0);
//            text.setVisible(true);            // возможно text потом совсем убрать
//            progressBar.setVisible(true);     // возможно progressBar потом совсем убрать
        }

        public int tick() {

            int time = Integer.valueOf(text.getText());

            if (time > 0) {

                text.setText(String.valueOf(time -= timeStep));
                progressBar.setProgress (1 - (float)time / (float) initialTime);
                output.setText(String.valueOf(initialVoltage += voltageStep));

            }
            return time;
        }
    }
}
