package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

public class MainSectionController {

    @FXML
    private Button resetButton;

    @FXML
    private ToggleButton startStopToggleButton;

    private List<String> versions = new LinkedList<>();

    {
        versions.add("CR");
        versions.add("UIS");
    }

    @FXML
    private ComboBox<String> versionComboBox;
    @FXML
    private RadioButton pumpRB;
    @FXML
    private RadioButton injRB;
    @FXML
    private ToggleGroup injectorOrPump;

    private ApplicationConfigHandler applicationConfigHandler;

    private ApplicationAppearanceChanger applicationAppearanceChanger;

    private ModbusRegisterProcessor flowModbusWriter;

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }

    public void setApplicationConfigHandler(ApplicationConfigHandler applicationConfigHandler) {
        this.applicationConfigHandler = applicationConfigHandler;
    }

    public void setApplicationAppearanceChanger(ApplicationAppearanceChanger applicationAppearanceChanger) {
        this.applicationAppearanceChanger = applicationAppearanceChanger;
    }

    @PostConstruct
    private void init() {

        setupVersionComboBox();

        setupStartStopToggleButton();

        setupResetButton();

    }

    private void setupVersionComboBox() {

        versionComboBox.getItems().addAll(versions);

        versionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            switch (newValue) {
                case "UIS":
                    unselectAll();
                    changeToUIS();
                    break;
                case "CR":
                    unselectAll();
                    changeToCR();
                    break;
            }
        });

        injectorOrPump.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (versionComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("CR")) {
                if (injectorOrPump.getSelectedToggle() == injRB) {
                    GUIType.setCurrentType(GUIType.CR_Inj);
                    applicationConfigHandler.put("GUI_Type", "CR_Inj");
                    applicationAppearanceChanger.changeToCRInj();
                } else if (injectorOrPump.getSelectedToggle() == pumpRB) {
                    GUIType.setCurrentType(GUIType.CR_Pump);
                    applicationConfigHandler.put("GUI_Type", "CR_Pump");
                    applicationAppearanceChanger.changeToCRPump();
                }
            }
        });

        switch (GUIType.getByString(applicationConfigHandler.get("GUI_Type"))) {
            case UIS:
                versionComboBox.getSelectionModel().select("UIS");
                break;
            case CR_Inj:
                versionComboBox.getSelectionModel().select("CR");
                break;
            case CR_Pump:
                versionComboBox.getSelectionModel().select("CR");
        }

    }

    private void setupStartStopToggleButton() {

        startStopToggleButton.selectedProperty().addListener((observable, stopped, started) -> {
            if (started) {
                flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
                startStopToggleButton.setText("Stop");
            }
            else {
                flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);
                startStopToggleButton.setText("Start");
            }
        });

    }

    private void setupResetButton() {

        resetButton.setOnAction(event -> flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true));

    }

    private void changeToUIS() {

        GUIType.setCurrentType(GUIType.UIS);
        applicationConfigHandler.put("GUI_Type", "UIS");
        applicationAppearanceChanger.changeToUIS();
        injRB.setSelected(true);
        pumpRB.setDisable(true);

    }

    private void changeToCR() {

        injRB.setSelected(true);
        pumpRB.setDisable(false);

    }

    private void unselectAll() {

        injRB.setSelected(false);
        pumpRB.setSelected(false);

    }

}
