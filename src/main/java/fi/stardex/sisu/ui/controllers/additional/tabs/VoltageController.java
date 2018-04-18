package fi.stardex.sisu.ui.controllers.additional.tabs;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class VoltageController {

    @FXML
    private LineChart lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private TextArea voltAmpereTextArea;

    @FXML
    private Button pulseSettingsButton;
}
