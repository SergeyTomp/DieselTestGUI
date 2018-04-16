package fi.stardex.sisu.ui.controllers.cr;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class HighPressureSectionController {


    @FXML
    private GridPane gridPaneHBox2;

    @FXML
    private Spinner pressBarReg1;

    @FXML
    private Spinner currentReg1;

    @FXML
    private Spinner dutyCycleReg1;

    @FXML
    private Label labelAmpReg2;

    @FXML
    private Label labelCycleReg2;

    @FXML
    private Spinner currentReg2;

    @FXML
    private Spinner dutyCycleReg2;

    @FXML
    private Label labelReg2;

    @FXML
    private Label labelAmpReg3;

    @FXML
    private Label labelCycleReg3;

    @FXML
    private Spinner currentReg3;

    @FXML
    private Spinner dutyCycleReg3;

    @FXML
    private Label labelReg3;

    @FXML
    private ToggleButton powerButton1;

    @FXML
    private ToggleButton powerButton2;

    @FXML
    private ToggleButton powerButton3;

    @FXML
    private ToggleButton powerSwitch;

    @FXML
    private StackPane stackPaneLCD;
}
