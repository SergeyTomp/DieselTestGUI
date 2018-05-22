package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.ChartTask;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class InjectorSectionController {

    //TODO: delete after test
    @Autowired
    private TimerTasksManager timerTasksManager;

    @Autowired
    private ChartTask chartTask;

    @Autowired
    private VoltageController voltageController;

    @Autowired
    private ModbusRegisterProcessor ultimaModbusWriter;

    @FXML
    private Spinner widthCurrentSignal;

    @FXML
    private Spinner<Double> freqCurrentSignal;

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

    @FXML
    private LedController ledBeaker1Controller;

    @FXML
    private LedController ledBeaker2Controller;

    @FXML
    private LedController ledBeaker3Controller;

    @FXML
    private LedController ledBeaker4Controller;

    @FXML
    private AnchorPane ledBeaker1;

    @FXML
    private AnchorPane ledBeaker2;

    @FXML
    private AnchorPane ledBeaker3;

    @FXML
    private AnchorPane ledBeaker4;

    private boolean updateOSC;

    public LedController getLedBeaker1Controller() {
        return ledBeaker1Controller;
    }

    public LedController getLedBeaker2Controller() {
        return ledBeaker2Controller;
    }

    public LedController getLedBeaker3Controller() {
        return ledBeaker3Controller;
    }

    public LedController getLedBeaker4Controller() {
        return ledBeaker4Controller;
    }

    public Spinner<Double> getFreqCurrentSignal() {
        return freqCurrentSignal;
    }

    public ToggleButton getPowerSwitch() {
        return powerSwitch;
    }

    public boolean isUpdateOSC() {
        return updateOSC;
    }

    public void setUpdateOSC(boolean updateOSC) {
        this.updateOSC = updateOSC;
    }

    public ToggleGroup getPiezoCoilToggleGroup() {
        return piezoCoilToggleGroup;
    }

    public RadioButton getPiezoRadioButton() {
        return piezoRadioButton;
    }

    public RadioButton getCoilRadioButton() {
        return coilRadioButton;
    }

    public RadioButton getPiezoDelphiRadioButton() {
        return piezoDelphiRadioButton;
    }

    @PostConstruct
    private void init() {

        //TODO: delete after test
        ultimaModbusWriter.add(ModbusMapUltima.Ftime1, 0);
        ultimaModbusWriter.add(ModbusMapUltima.GImpulsesPeriod, 60);
        ultimaModbusWriter.add(ModbusMapUltima.FInjectorNumber1, 1);

        powerSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ultimaModbusWriter.add(ModbusMapUltima.Injectors_Running_En, true);
                timerTasksManager.start(chartTask);
            }
            else {
                ultimaModbusWriter.add(ModbusMapUltima.Injectors_Running_En, false);
                ultimaModbusWriter.add(ModbusMapUltima.FInjectorNumber1, 0xff);
                ultimaModbusWriter.add(ModbusMapUltima.Ftime1, 0);
                timerTasksManager.stop();
                voltageController.getData1().clear();
            }
        });

        setupFrequencySpinner();



    }

    private void setupFrequencySpinner() {
        freqCurrentSignal.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 50, 16.67, 0.01));
    }
}
