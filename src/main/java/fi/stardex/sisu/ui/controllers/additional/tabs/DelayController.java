package fi.stardex.sisu.ui.controllers.additional.tabs;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class DelayController {

    @FXML
    private LineChart delayChart;

    @FXML
    private NumberAxis delayXAxis;

    @FXML
    private NumberAxis delayYAxis;

    @FXML
    private TextField minimumDelay;

    @FXML
    private TextField maximumDelay;

    @FXML
    private TextField averageDelay;

    @FXML
    private Spinner sensitivitySpinner;

    @FXML
    private Button resetDelayButton;

    @FXML
    private TextField addingTime;
}
