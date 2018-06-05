package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class AdditionalSectionController {

    @FXML
    public Tab tabVoltage;

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
    private ConnectionController connectionController;

    @FXML
    private VoltageController voltageController;

    @FXML
    private DelayController delayController;

    @FXML
    private SettingsController settingsController;

    public Tab getTabVoltage() {
        return tabVoltage;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public ConnectionController getConnectionController() {
        return connectionController;
    }

    public VoltageController getVoltageController() {
        return voltageController;
    }

    public DelayController getDelayController() { return delayController; }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    @PostConstruct
    private void init() {

    }
}
