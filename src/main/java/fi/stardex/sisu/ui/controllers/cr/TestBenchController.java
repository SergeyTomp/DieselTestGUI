package fi.stardex.sisu.ui.controllers.cr;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TestBenchController {

    @FXML
    private HBox testBench;

    @FXML
    private ToggleButton leftDirectionToggleButton;

    @FXML
    private ToggleGroup rotationDirectionToggleGroup;

    @FXML
    private ToggleButton rightDirectionToggleButton;

    @FXML
    private Spinner spinnerRPM;

    @FXML
    private ProgressBar pressProgressBar1;

    @FXML
    private ProgressBar tempProgressBar2;

    @FXML
    private ProgressBar tempProgressBar1;

    @FXML
    private Text textTempBar1;

    @FXML
    private Text textTempBar2;

    @FXML
    private Text textPressBar1;

    @FXML
    private GridPane gridPaneHBox1;

    @FXML
    private ToggleButton buttonPumpControl;

    @FXML
    private ToggleButton buttonFanControl;

    @FXML
    private ToggleButton deviceStateButton;

    @FXML
    private StackPane stackPaneLCD;

    @FXML
    private ProgressBar oilTank;

    @FXML
    private Text textOilTank;

    @FXML
    private StackPane rootStackPane;

    @FXML
    private Label pressure1Label;

}
