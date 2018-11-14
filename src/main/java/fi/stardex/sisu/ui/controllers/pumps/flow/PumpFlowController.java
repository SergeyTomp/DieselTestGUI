package fi.stardex.sisu.ui.controllers.pumps.flow;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class PumpFlowController {

    @FXML private AnchorPane pumpFlowTextArea;
    @FXML private AnchorPane pumpDelivery;
    @FXML private AnchorPane pumpBackflow;

    @FXML private PumpBeakerController pumpDeliveryController;
    @FXML private PumpBeakerController pumpBackflowController;
    @FXML private PumpFlowTextAreaController pumpFlowTextAreaController;

    public PumpBeakerController getPumpDeliveryController() {
        return pumpDeliveryController;
    }

    public PumpBeakerController getPumpBackflowController() {
        return pumpBackflowController;
    }

    public PumpFlowTextAreaController getPumpFlowTextAreaController() {
        return pumpFlowTextAreaController;
    }
}
