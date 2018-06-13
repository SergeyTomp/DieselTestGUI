package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class FlowController {

    @FXML
    private AnchorPane beakerFlowDelivery1;

    @FXML
    private AnchorPane beakerFlowDelivery2;

    @FXML
    private AnchorPane beakerFlowDelivery3;

    @FXML
    private AnchorPane beakerFlowDelivery4;

    @FXML
    private BeakerController beakerFlowDelivery1Controller;

    @FXML
    private BeakerController beakerFlowDelivery2Controller;

    @FXML
    private BeakerController beakerFlowDelivery3Controller;

    @FXML
    private BeakerController beakerFlowDelivery4Controller;

    public BeakerController getBeakerFlowDelivery1Controller() {
        return beakerFlowDelivery1Controller;
    }

    public BeakerController getBeakerFlowDelivery2Controller() {
        return beakerFlowDelivery2Controller;
    }

    public BeakerController getBeakerFlowDelivery3Controller() {
        return beakerFlowDelivery3Controller;
    }

    public BeakerController getBeakerFlowDelivery4Controller() {
        return beakerFlowDelivery4Controller;
    }
}
