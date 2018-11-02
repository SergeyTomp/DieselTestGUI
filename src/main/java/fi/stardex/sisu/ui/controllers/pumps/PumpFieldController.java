package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PumpFieldController {

    @FXML private TextField pumpTextField;

    public TextField getInjectorTextField() {
        return pumpTextField;
    }
}
