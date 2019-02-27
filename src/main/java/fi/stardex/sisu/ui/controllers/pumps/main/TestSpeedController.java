package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestModeModel;
import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.model.PumpTestSpeedModel;
import fi.stardex.sisu.model.PumpTimeProgressModel;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.TestSpeed;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static fi.stardex.sisu.util.enums.TestSpeed.*;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;

public class TestSpeedController {

    @FXML private GridPane panelGridPane;
    @FXML private Label timerSpeedLabel;
    @FXML private ProgressBar adjustingTimeProgressBar;
    @FXML private ProgressBar measuringTimeProgressBar;
    @FXML private Label labelMeasuringTime;
    @FXML private Label labelAjustingTime;
    @FXML private Text adjustingText;
    @FXML private Text measuringText;
    @FXML private ComboBox<TestSpeed> speedComboBox;

    private int initialAdjustingTime;
    private int initialMeasurementTime;
    private int originalAdjustingTime;
    private int originalMeasurementTime;

    private PumpTestModel pumpTestModel;
    private PumpTimeProgressModel pumpTimeProgressModel;
    private I18N i18N;
    private PumpTestSpeedModel pumpTestSpeedModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private PumpTestModeModel pumpTestModeModel;

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpTimeProgressModel(PumpTimeProgressModel pumpTimeProgressModel) {
        this.pumpTimeProgressModel = pumpTimeProgressModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpTestSpeedModel(PumpTestSpeedModel pumpTestSpeedModel) {
        this.pumpTestSpeedModel = pumpTestSpeedModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }

    @PostConstruct
    private void init() {

        speedComboBox.getItems().setAll(NORM, PRECISE, FAST);
        setupListeners();
        speedComboBox.getSelectionModel().selectFirst();
        bindingI18N();
    }

    private void setupListeners() {

        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {
            speedComboBox.setDisable(newValue);
            if (!newValue) {

                pumpTimeProgressModel.adjustingTimeProperty().setValue(initialAdjustingTime);
                pumpTimeProgressModel.measurementTimeProperty().setValue(initialMeasurementTime);
            }

        });
        pumpTestModeModel.testModeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == TESTPLAN) {
                panelGridPane.setVisible(false);
            }else{
                panelGridPane.setVisible(true);
            }

        });

        speedComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            pumpTestSpeedModel.testSpeedProperty().setValue(newValue);
            initialAdjustingTime = (int)(originalAdjustingTime * newValue.getMultiplier());
            initialMeasurementTime = (int)(originalMeasurementTime * newValue.getMultiplier());
            pumpTimeProgressModel.adjustingTimeProperty().setValue(initialAdjustingTime);
            pumpTimeProgressModel.measurementTimeProperty().setValue(initialMeasurementTime);

        });

        pumpTestModel.pumpTestProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue != null){

                double multiplier = pumpTestSpeedModel.testSpeedProperty().get().getMultiplier();

                originalAdjustingTime = newValue.getAdjustingTime();

                initialAdjustingTime = (int)(originalAdjustingTime * multiplier);

                adjustingTimeProgressBar.setProgress(originalAdjustingTime == 0 ? 0 : 1);

                pumpTimeProgressModel.adjustingTimeProperty().setValue(initialAdjustingTime);

                Optional.ofNullable(newValue.getMeasuringTime()).ifPresentOrElse(initialTime -> {

                    showMeasurementTime(true);

                    originalMeasurementTime = initialTime;

                    initialMeasurementTime = (int)(initialTime * multiplier);

                    measuringTimeProgressBar.setProgress(originalMeasurementTime == 0 ? 0 : 1);

                    pumpTimeProgressModel.measurementTimeProperty().setValue(initialMeasurementTime);

                }, () -> showMeasurementTime(false));
            }
            else{

                adjustingTimeProgressBar.setProgress(0);

                showMeasurementTime(false);
            }
        });
        pumpTimeProgressModel.adjustingTimeProperty().addListener((observable, oldValue, newValue) -> {

            adjustingTimeProgressBar.setProgress(newValue.floatValue() / initialAdjustingTime);
            adjustingText.setText(String.valueOf(newValue));
        });

        pumpTimeProgressModel.measurementTimeProperty().addListener((observable, oldValue, newValue) -> {

            measuringTimeProgressBar.setProgress(newValue.floatValue() / initialMeasurementTime);
            measuringText.setText(String.valueOf(newValue));
        });
    }

    private void showMeasurementTime(boolean show) {

        labelMeasuringTime.setVisible(show);
        measuringTimeProgressBar.setVisible(show);
        measuringText.setVisible(show);
        pumpTimeProgressModel.measurementTimeEnabledProperty().setValue(show);

    }

    private void bindingI18N(){

        labelAjustingTime.textProperty().bind(i18N.createStringBinding("main.adjusting.label"));
        labelMeasuringTime.textProperty().bind(i18N.createStringBinding("main.measuring.label"));
        timerSpeedLabel.textProperty().bind(i18N.createStringBinding("main.timingSpeed.label"));
    }
}
