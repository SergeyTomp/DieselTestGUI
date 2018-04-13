package fi.stardex.sisu.ui.controllers.cr;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TestBenchController {

    @FXML
    public ToggleButton leftDirectionToggleButton;

    @FXML
    public ToggleGroup rotationDirectionToggleGroup;

    @FXML
    public ToggleButton rightDirectionToggleButton;

    @FXML
    public Spinner spinnerRPM;

    @FXML
    public ProgressBar pressProgressBar1;

    @FXML
    public ProgressBar tempProgressBar2;

    @FXML
    public ProgressBar tempProgressBar1;

    @FXML
    public Text textTempBar1;

    @FXML
    public Text textTempBar2;

    @FXML
    public Text textPressBar1;

    @FXML
    public GridPane gridPaneHBox1;

    @FXML
    public ToggleButton buttonPumpControl;

    @FXML
    public ToggleButton buttonFanControl;

    @FXML
    public ToggleButton deviceStateButton;

    @FXML
    public StackPane stackPaneLCD;

    @FXML
    public ProgressBar oilTank;

    @FXML
    public Text textOilTank;

    @FXML
    public StackPane rootStackPane;

    @FXML
    public Label pressure1Label;
}
