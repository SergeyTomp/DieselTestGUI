package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpReportModel;
import fi.stardex.sisu.model.PumpTestModeModel;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.dialogs.PrintDialogPanelController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private Stage printStage;
    private ViewHolder printDialogPanel;

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
    public void setPrintDialogPanel(ViewHolder printDialogPanel) {
        this.printDialogPanel = printDialogPanel;
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

                disableNodes(pumpTestModeModel.testModeProperty().get() == AUTO, printButton, storeButton, resetButton);
            }
            else if(pumpTestModeModel.testModeProperty().get() == AUTO){
                disableNodes(false, printButton);
            }
            else{
                disableNodes(false, printButton, storeButton, resetButton);
            }
        });
        printButton.setOnAction(new PrintButtonEventHandler());
    }

    private void disableNodes(boolean disable, Node... nodes) {
        for (Node node : nodes)
            node.setDisable(disable);
    }

    private class PrintButtonEventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {

            if (printStage == null) {
                printStage = new Stage();
                printStage.setTitle("PDF export");
                printStage.setResizable(false);
                printStage.initModality(Modality.APPLICATION_MODAL);
                ((PrintDialogPanelController) printDialogPanel.getController()).setStage(printStage);
            }
            /* Check is necessary due to possibility of dialog window invocation from different controllers.
                In this case we need to avoid repeated set of printDialogPanel as root of another new scene -
                IllegalArgumentException: .... is already set as root of another scene. */
            if (printDialogPanel.getView().getScene() == null) {
                printStage.setScene(new Scene(printDialogPanel.getView()));
            }else{
                printStage.setScene(printDialogPanel.getView().getScene());
            }
            printStage.show();
        }
    }
}
