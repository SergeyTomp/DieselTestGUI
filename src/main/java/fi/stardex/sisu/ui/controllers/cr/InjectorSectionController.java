package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.ChartTask;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.ws.Action;
import java.io.IOException;

public class InjectorSectionController {

    @Autowired
    private TimerTasksManager timerTasksManager;

    @Autowired
    private ChartTask chartTask;

    @Autowired
    private VoltageController voltageController;

    @FXML
    private Spinner widthCurrentSignal;

    @FXML
    private Spinner freqCurrentSignal;

    @FXML
    private RadioButton piezoRadioButton;

    @FXML
    private ToggleGroup piezoCoilToggleGroup;

    @FXML
    private RadioButton coilRadioButton;

    @FXML
    private RadioButton piezoDelphiRadioButton;

    @FXML
    private GridPane gridLedBeaker;

    @FXML
    private ToggleButton powerSwitch;

    @FXML
    private Label statusBoostULabelText;

    @FXML
    private Label statusBoostULabel;

    @FXML
    private StackPane stackPaneLed1;

    @FXML
    private StackPane stackPaneLed2;

    @FXML
    private StackPane stackPaneLed3;

    @FXML
    private StackPane stackPaneLed4;

    @FXML
    private StackPane stackPaneLed5;

    @FXML
    private StackPane stackPaneLed6;

    @FXML
    private ProgressBar switcherProgressBar;

    private boolean updateOSC;

    public boolean isUpdateOSC() {
        return updateOSC;
    }

    public void setUpdateOSC(boolean updateOSC) {
        this.updateOSC = updateOSC;
    }

    @PostConstruct
    private void init() {
        powerSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                timerTasksManager.start(chartTask);
            else {
                timerTasksManager.stop();
                voltageController.getData1().clear();
            }
        });
    }
}
