package fi.stardex.sisu.ui.controllers.cr.tabs;

import fi.stardex.sisu.model.Step3Model;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.persistence.orm.bosch_info.Parameters;
import fi.stardex.sisu.persistence.repos.cr.BoschRepository;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.BoostUadjustmentState;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

public class Step3Controller {

    @FXML private TextField aheTextField;
    @FXML private Text pauseText;
    @FXML private Spinner<Double> currentSpinner;
    @FXML private Spinner<Integer> timeSpinner;
    @FXML private ProgressBar pauseProgressBar;
    @FXML private ToggleButton startStopButton;

    private Timeline progressTimeLine;
    private Timeline startGapTimeline;
    private PauseProgress pauseProgress;
    private Step3Model step3Model;
    private MainSectionModel mainSectionModel;
    private BoostUadjustmentState boostUadjustmentState;
    private BoschRepository boschRepository;
    private int vapBatteryU;
    private double vapSecondI;
    private float pauseBalance;
    private final float COOLING_PAUSE_MS = 15000;
    private final float FRONT_DELAY_MS = 1000;
    private final float ONE_AMPERE_MULTIPLY = 93.07f;
    private boolean pulseIsActive;
    private boolean step3BatteryU;
    private ModbusRegisterProcessor ultimaModbusWriter;

    public void setStep3Model(Step3Model step3Model) {
        this.step3Model = step3Model;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }
    public void setBoostUadjustmentState(BoostUadjustmentState boostUadjustmentState) {
        this.boostUadjustmentState = boostUadjustmentState;
    }
    public void setBoschRepository(BoschRepository boschRepository) {
        this.boschRepository = boschRepository;
    }

    @PostConstruct
    public void init() {

        setupSpinners();
        setupTimeLines();
        pauseProgress = new PauseProgress(pauseProgressBar, pauseText);
        setupListeners();
    }

    private void setupSpinners() {

        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(10, 14, 13, 0.5));
        timeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(750, 1500, 1000, 250));
    }

    private void setupTimeLines() {
        progressTimeLine = new Timeline();
        progressTimeLine.setCycleCount(Animation.INDEFINITE);
        progressTimeLine.getKeyFrames().add(new KeyFrame(Duration.millis(1000), actionEvent -> tickProgress()));
        pauseText.setVisible(false);

        startGapTimeline = new Timeline();
        startGapTimeline.setCycleCount(1);
        startGapTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(FRONT_DELAY_MS), actionEvent -> ultimaModbusWriter.add(PulseFrontSlump, true)));
    }

    private void setupListeners() {

        startStopButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                disableNodes(true, startStopButton, currentSpinner, timeSpinner);
                start();
            }
            else {
                disableNodes(false, startStopButton, currentSpinner, timeSpinner);
            }
        });

        step3Model.step3PauseProperty().bind(startStopButton.selectedProperty());
        boostUadjustmentState.boostUadjustmentStateProperty().addListener((observableValue, oldValue, newValue) -> startStopButton.setDisable(newValue));
        mainSectionModel.injectorProperty().addListener((observableValue, oldValue, newValue) ->{
            if (newValue != null) {
                boschRepository.findById(newValue.getInjectorCode()).ifPresent(p -> aheTextField.setText(p.getAHE()));
            }
        });
    }

    private void start() {

        vapBatteryU = mainSectionModel.injectorTestProperty().get().getVoltAmpereProfile().getBatteryU();
        vapSecondI = mainSectionModel.injectorTestProperty().get().getVoltAmpereProfile().getSecondI();
        ultimaModbusWriter.add(Battery_U, 31);
        ultimaModbusWriter.add(SecondIBoardOne, currentSpinner.getValue() * ONE_AMPERE_MULTIPLY);
        ultimaModbusWriter.add(CoilHoldingWidth, timeSpinner.getValue());
        ultimaModbusWriter.add(HoldingPulseMode, true);
        startGapTimeline.play();
        pauseProgress.setProgressBar(COOLING_PAUSE_MS);
        pauseText.setVisible(true);
        progressTimeLine.play();
        step3BatteryU = true;
        step3Model.pulseIsActiveProperty().setValue(true);
    }

    private void stop() {
        ultimaModbusWriter.add(HoldingPulseMode, false);
        ultimaModbusWriter.add(CoilHoldingWidth, 0);
        ultimaModbusWriter.add(SecondIBoardOne, vapSecondI  * ONE_AMPERE_MULTIPLY);
    }

    private void tickProgress(){

        pauseBalance = pauseProgress.tick() * 1000;
        if (step3Model.pulseIsActiveProperty().get() && pauseBalance <= COOLING_PAUSE_MS - (FRONT_DELAY_MS + timeSpinner.getValue() + 1000f)) {
            step3Model.pulseIsActiveProperty().setValue(false);
            stop();
        }
        if (step3BatteryU && pauseBalance <= COOLING_PAUSE_MS - 11000f) {
            step3BatteryU = false;
            ultimaModbusWriter.add(Battery_U, vapBatteryU);
        }
        if (pauseBalance <= 0) {
            progressTimeLine.stop();
            pauseText.setVisible(false);
            startStopButton.setSelected(false);
        }
    }

    private void disableNodes(boolean disable, Node... nodes){
        for (Node n : nodes) {
            n.setDisable(disable);
        }
    }

    private class PauseProgress {

        private ProgressBar progressBar;
        private Text text;
        private float initialTime;
        private int timeStep = 1;

        PauseProgress(ProgressBar progressBar, Text text) {
            this.progressBar = progressBar;
            this.text = text;
        }

        void setProgressBar(float initialTime) {

            this.initialTime = initialTime / 1000;
            text.setText(String.valueOf((int)(initialTime / 1000)));
            progressBar.setProgress(initialTime == 0 ? 0 : 1);
        }

        float tick() {

            float time = Float.valueOf(text.getText());

            if (time > 0) {

                text.setText(String.valueOf((int)(time -= timeStep)));
                progressBar.setProgress (time / initialTime);
            }
            return time;
        }
    }

}
