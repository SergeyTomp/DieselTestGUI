package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class AdditionalSectionController {

    @FXML
    private StackPane connection;

    @FXML
    private GridPane voltage;

    @FXML
    private GridPane delay;

    @FXML
    private ConnectionController connectionController;

    @FXML
    private VoltageController voltageController;

    @FXML
    private DelayController delayController;

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public VoltageController getVoltageController() {
        return voltageController;
    }

    public DelayController getDelayController() { return delayController; }
}
