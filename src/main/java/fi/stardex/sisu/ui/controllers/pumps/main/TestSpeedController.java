package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.model.PumpTimeProgressModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;
import java.util.Optional;

public class TestSpeedController {

    @FXML
    private ProgressBar adjustingTimeProgressBar;
    @FXML
    private ProgressBar measuringTimeProgressBar;
    @FXML
    private Label labelMeasuringTime;
    @FXML
    private Text adjustingText;
    @FXML
    private Text measuringText;
    @FXML
    private ComboBox speedComboBox;

    private PumpTestModel pumpTestModel;

    private PumpTimeProgressModel pumpTimeProgressModel;

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }

    public void setPumpTimeProgressModel(PumpTimeProgressModel pumpTimeProgressModel) {
        this.pumpTimeProgressModel = pumpTimeProgressModel;
    }

    @PostConstruct
    private void init() {

        pumpTestModel.pumpTestProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue != null){

                int initialAdjustingTime = newValue.getAdjustingTime();

                adjustingText.setText(String.valueOf(initialAdjustingTime));

                adjustingTimeProgressBar.setProgress(initialAdjustingTime == 0 ? 0 : 1);

                Optional.ofNullable(newValue.getMeasuringTime()).ifPresentOrElse(initialMeasurementTime -> {

                    showMeasurementTime(true);

                    measuringText.setText(String.valueOf(initialMeasurementTime));

                    measuringTimeProgressBar.setProgress(initialMeasurementTime == 0 ? 0 : 1);

                }, () -> showMeasurementTime(false));
            }
            else{
                adjustingText.setText(String.valueOf(0));

                adjustingTimeProgressBar.setProgress(0);

                showMeasurementTime(false);
            }
        });

        pumpTimeProgressModel.adjustingTimeProperty().addListener((observable, oldValue, newValue) -> {


        });

        pumpTimeProgressModel.measurementTimeProperty().addListener((observable, oldValue, newValue) -> {


        });

    }

    private void showMeasurementTime(boolean show) {

        labelMeasuringTime.setVisible(show);
        measuringTimeProgressBar.setVisible(show);
        measuringText.setVisible(show);

    }

}
