package fi.stardex.sisu.ui.controllers.pumps.main;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class TestModeController {

    @FXML private GridPane testModeGridPane;
    @FXML private RadioButton autoTestRadioButton;
    @FXML private RadioButton testPlanTestRadioButton;

    public RadioButton getAutoTestRadioButton() {
        return autoTestRadioButton;
    }

    public RadioButton getTestPlanTestRadioButton() {
        return testPlanTestRadioButton;
    }

    public GridPane getTestModeGridPane() {
        return testModeGridPane;
    }
}
