package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class AdditionalSectionController {

    @FXML
    private StackPane connection;
    @FXML
    private ConnectionController connectionController;

    public ConnectionController getConnectionController() {
        return connectionController;
    }
}
