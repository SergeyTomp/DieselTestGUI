package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;


public class AdditionalSectionController {
    @FXML
    private Tab tabDelay;

    @FXML
    private Tab tabVoltage;

    @FXML
    private Tab tabRLC;

    @FXML
    private TabPane tabPane;

    @FXML
    private StackPane connection;

    @FXML
    private StackPane voltage;

    @FXML
    private GridPane delay;

    @FXML
    private AnchorPane flow;

    @FXML
    private GridPane coding;

    @FXML
    private GridPane settings;

    @FXML
    private GridPane rlc;

    @FXML
    private ConnectionController connectionController;

    @FXML
    private VoltageController voltageController;

    @FXML
    private DelayController delayController;

    @FXML
    private SettingsController settingsController;

    @FXML
    private FlowController flowController;

    @FXML
    private RLCController rlcController;

    @FXML
    private Spinner<Double> sensitivitySpinner;




    public RLCController getRlCController() {
        return rlcController;
    }
    public FlowController getFlowController() {
        return flowController;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public VoltageController getVoltageController() {
        return voltageController;
    }

    public DelayController getDelayController() {
        return delayController;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public Tab getTabVoltage() {
        return tabVoltage;
    }

    public Tab getTabDelay() {
        return tabDelay;
    }
}
