package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpReportModel;
import fi.stardex.sisu.model.PumpTestModeModel;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PumpsStartButtonState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;

public class StoreResetPrintController {

    @FXML private Button printButton;
    @FXML private Button storeButton;
    @FXML private Button resetButton;
    @FXML private HBox storePrintResetHBox;

    private ModbusRegisterProcessor flowModbusWriter;
    private PumpReportModel pumpReportModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private PumpTestModeModel pumpTestModeModel;


    public Button getPrintButton() {
        return printButton;
    }
    public Button getStoreButton() {
        return storeButton;
    }
    public Button getResetButton() {
        return resetButton;
    }
    public HBox getStorePrintResetHBox() {
        return storePrintResetHBox;
    }

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }

    @PostConstruct
    public void init(){

        resetButton.setOnAction(event -> flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true));
        storeButton.setOnAction(event -> pumpReportModel.storeResult());
        disableNodes(true, storeButton, resetButton);
        pumpTestModeModel.testModeProperty().addListener((observableValue, oldValue, newValue) -> {

            disableNodes(false, printButton, storeButton, resetButton);
            if (newValue == AUTO) {
                disableNodes(true, storeButton, resetButton);
            }
        });
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {

                disableNodes(true, printButton, storeButton, resetButton);
            }
            else if(pumpTestModeModel.testModeProperty().get() == AUTO){
                disableNodes(false, printButton);
            }
            else{
                disableNodes(false, printButton, storeButton, resetButton);
            }
        });
    }

    private void disableNodes(boolean disable, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);
    }
}
