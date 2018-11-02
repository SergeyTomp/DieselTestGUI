package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class TestSpeedController {
    @FXML private GridPane timingGridPane;
    @FXML private ProgressBar measuringTimeProgressBar;
    @FXML private ProgressBar adjustingTimeProgressBar;
    @FXML private Label labelAdjustTime;
    @FXML private Label labelMeasureTime;
    @FXML private Text adjustingText;
    @FXML private Text measuringText;
    @FXML private Label timingSpeedLabel;
    @FXML private ComboBox speedComboBox;

    public GridPane getTimingGridPane() {
        return timingGridPane;
    }

    public ProgressBar getMeasuringTimeProgressBar() {
        return measuringTimeProgressBar;
    }

    public ProgressBar getAdjustingTimeProgressBar() {
        return adjustingTimeProgressBar;
    }

    public Label getLabelAdjustTime() {
        return labelAdjustTime;
    }

    public Label getLabelMeasureTime() {
        return labelMeasureTime;
    }

    public Text getAdjustingText() {
        return adjustingText;
    }

    public Text getMeasuringText() {
        return measuringText;
    }

    public Label getTimingSpeedLabel() {
        return timingSpeedLabel;
    }

    public ComboBox getSpeedComboBox() {
        return speedComboBox;
    }
}
