package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
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
    private ConnectionController connectionController;

    @FXML
    private VoltageController voltageController;

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public VoltageController getVoltageController() {
        return voltageController;
    }
}
