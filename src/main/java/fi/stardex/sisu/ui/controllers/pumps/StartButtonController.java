package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class StartButtonController {
    @FXML private ToggleButton startToggleButton;
    @FXML private HBox startHBox;

    public ToggleButton getStartToggleButton() {
        return startToggleButton;
    }

    public HBox getStartHBox() {
        return startHBox;
    }
}
