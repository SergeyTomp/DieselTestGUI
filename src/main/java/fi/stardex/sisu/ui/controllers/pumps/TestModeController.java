package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;

public class TestModeController {

    @FXML private RadioButton autoTestRadioButton;
    @FXML private RadioButton testPlanTestRadioButton;
    @FXML private GridPane testModeGridPane;

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
