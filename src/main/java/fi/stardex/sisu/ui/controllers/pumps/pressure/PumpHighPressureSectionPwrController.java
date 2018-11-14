package fi.stardex.sisu.ui.controllers.pumps.pressure;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

public class PumpHighPressureSectionPwrController {
    @FXML private StackPane pwrButtonStackPane;
    @FXML private ToggleButton pwrButtonToggleButton;

    public StackPane getPwrButtonStackPane() {
        return pwrButtonStackPane;
    }

    public ToggleButton getPwrButtonToggleButton() {
        return pwrButtonToggleButton;
    }
}
