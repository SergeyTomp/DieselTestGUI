package fi.stardex.sisu.ui.controllers.cr.tabs;

import fi.stardex.sisu.model.updateModels.DiffFlowUpdateModel;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.*;

public class DiffFlowController {

    @FXML private ImageView shiftingInProgressImageView;
    @FXML private Spinner<Integer> shiftingTimeSpinner;
    @FXML private Spinner<Integer> shiftingPeriodSpinner;
    @FXML private Button manualShiftingStartButton;
    @FXML private Button shiftingAutoStartButton;

    private Image darkBulbImage;
    private Image lightBulbImage;
    private ModbusRegisterProcessor flowModbusWriter;
    private DiffFlowUpdateModel diffFlowUpdateModel;
    private static final String LED_BLINK_ON = "quadLedBlink-on";
    private static final String LED_BLINK_OFF = "quadLedBlink-off";

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setDiffFlowUpdateModel(DiffFlowUpdateModel diffFlowUpdateModel) {
        this.diffFlowUpdateModel = diffFlowUpdateModel;
    }

    @PostConstruct
    public void init() {

        darkBulbImage = new Image(getClass().getResourceAsStream("/img/pump_button-off.png"));
        lightBulbImage = new Image(getClass().getResourceAsStream("/img/pump_button-on.png"));
        shiftingInProgressImageView.setImage(darkBulbImage);

        shiftingPeriodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1800, 300, 1));
        shiftingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65000, 10000, 1000));

        manualShiftingStartButton.getStyleClass().clear();
        manualShiftingStartButton.getStyleClass().add(LED_BLINK_OFF);

        manualShiftingStartButton.setOnMousePressed(mouseEvent -> manualShiftingStartButton.getStyleClass().set(0, LED_BLINK_ON));
        manualShiftingStartButton.setOnMouseReleased(mouseEvent -> manualShiftingStartButton.getStyleClass().set(0, LED_BLINK_OFF));

        manualShiftingStartButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                flowModbusWriter.add(ShiftingManualStart, true);
            }
        });

        shiftingAutoStartButton.getStyleClass().clear();
        shiftingAutoStartButton.getStyleClass().add(LED_BLINK_OFF);

        diffFlowUpdateModel.shiftingAutoStartIsOnProperty().addListener((observableValue, oldValue, newValue) ->
                shiftingAutoStartButton.getStyleClass().set(0, newValue ? LED_BLINK_ON : LED_BLINK_OFF));

        shiftingAutoStartButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                flowModbusWriter.add(ShiftingAutoStartIsOn, diffFlowUpdateModel.shiftingAutoStartIsOnProperty().not().get());
            }
        });

        flowModbusWriter.add(ShiftingPeriod, shiftingPeriodSpinner.getValue());
        flowModbusWriter.add(ShiftingDuration, shiftingTimeSpinner.getValue());

        shiftingPeriodSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> flowModbusWriter.add(ShiftingPeriod, newValue));
        shiftingTimeSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> flowModbusWriter.add(ShiftingDuration, newValue));

        diffFlowUpdateModel.shiftingIsInProgressProperty().addListener((observable, oldValue, newValue)
                -> shiftingInProgressImageView.setImage(newValue ? lightBulbImage : darkBulbImage));
    }
}
