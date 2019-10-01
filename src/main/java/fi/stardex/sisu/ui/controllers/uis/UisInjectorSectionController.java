package fi.stardex.sisu.ui.controllers.uis;

import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.updateModels.UisHardwareUpdateModel;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.Inj_Process_Global_Error;
import static fi.stardex.sisu.ui.controllers.common.GUI_TypeController.GUIType.UIS;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.enums.InjectorSubType.*;

public class UisInjectorSectionController {

    @FXML private ToggleButton led1ToggleButton;
    @FXML private ToggleButton led2ToggleButton;
    @FXML private ToggleButton led3ToggleButton;
    @FXML private ToggleButton led4ToggleButton;
    @FXML private ToggleButton led5ToggleButton;
    @FXML private ToggleButton led6ToggleButton;
    @FXML private ToggleButton led7ToggleButton;
    @FXML private ToggleButton led8ToggleButton;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> width2Spinner;
    @FXML private Spinner<Integer> offsetSpinner;
    @FXML private Spinner<Integer> angle1Spinner;
    @FXML private Spinner<Integer> angle2Spinner;
    @FXML private Spinner<Integer> pressureSpinner;
    @FXML private Spinner<Double> currentSpinner;
    @FXML private Spinner<Double> dutySpinner;
    @FXML private TextField typeTextField;
    @FXML private Label offsetLabel;
    @FXML private Label width2Label;
    @FXML private Label angle2Label;
    @FXML private Label widthLabel;
    @FXML private Label angle1Label;
    @FXML private Label pressureLabel;
    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private StackPane topLabelStackPane;
    @FXML private StackPane midLabelStackPane;
    @FXML private StackPane lowLabelStackPane;
    @FXML private StackPane topSpinnerStackPane;
    @FXML private StackPane midSpinnerStackPane;
    @FXML private StackPane lowSpinnerStackPane;
    @FXML private StackPane widthStackPane;
    @FXML private StackPane angle1StackPane;
    @FXML private StackPane led1StackPane;
    @FXML private StackPane led2StackPane;
    @FXML private StackPane led3StackPane;
    @FXML private StackPane led4StackPane;
    @FXML private StackPane led5StackPane;
    @FXML private StackPane led6StackPane;
    @FXML private StackPane led7StackPane;
    @FXML private StackPane led8StackPane;
    @FXML private StackPane bipStackPane;
    @FXML private StackPane delayStackPane;
    @FXML private Button saveBipButton;
    @FXML private Button saveDelayButton;
    @FXML private StackPane lcdStackPane;
    @FXML private ToggleButton regulatorToggleButton;
    @FXML private StackPane rootStackPane;

    private Lcd lcd;
    private Gauge bipGauge;
    private Gauge delayGauge;
    private ObservableList<ToggleButton> ledToggleButtons;
    private ToggleGroup toggleGroup = new ToggleGroup();
    private List<Timeline> timeLinesList;
    private List<KeyFrame> keyFramesList;
    private static final String LED_BLINK_ON = "ledBlink-on";
    private static final String LED_BLINK_OFF = "ledBlink-off";

    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private UisHardwareUpdateModel uisHardwareUpdateModel;
    private GUI_TypeModel gui_typeModel;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private Logger logger = LoggerFactory.getLogger(UisInjectorSectionController.class);

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setUisHardwareUpdateModel(UisHardwareUpdateModel uisHardwareUpdateModel) {
        this.uisHardwareUpdateModel = uisHardwareUpdateModel;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    @PostConstruct
    public void init() {

        bipGauge = GaugeCreator.createBipGauge();
        delayGauge = GaugeCreator.createDelayGauge();
        lcd = GaugeCreator.createLcd("Bar");
        lcdStackPane.getChildren().add(0, lcd);
        bipStackPane.getChildren().add(0, bipGauge);
        delayStackPane.getChildren().add(0, delayGauge);
        hideSlaveControls();
        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> {
            typeTextField.setText(newValue == null ? "" : newValue.getVAP().getInjectorSubType().name());
            configureSlaveControls(newValue == null ? SINGLE_COIL : newValue.getVAP().getInjectorSubType());
        });
        rootStackPane.widthProperty().addListener(new HboxWidthListener(rootStackPane, lcdStackPane));
        setupSpinners();
        setupLedControllers();
        setToggleGroupToLeds(toggleGroup);
        setupTimelines();
        setupListeners();
    }

    private void configureSlaveControls(InjectorSubType injectorSubType) {

        hideSlaveControls();
        switch (injectorSubType) {
            case SINGLE_COIL:
            case SINGLE_PIEZO:
            case MECHANIC:
                break;
            case DOUBLE_COIL:
                offsetLabel.setVisible(true);
                offsetSpinner.setVisible(true);
                break;
            case DOUBLE_SIGNAL:
            case HPI:
                width2Label.setVisible(true);
                angle2Label.setVisible(true);
                width2Spinner.setVisible(true);
                angle2Spinner.setVisible(true);
                break;
            case F2E:
                pressureLabel.setVisible(true);
                dutyLabel.setVisible(true);
                currentLabel.setVisible(true);
                pressureSpinner.setVisible(true);
                currentSpinner.setVisible(true);
                dutySpinner.setVisible(true);
                regulatorToggleButton.setDisable(false);
                lcd.setOpacity(1);
                break;
        }
    }

    private void hideSlaveControls() {

        pressureLabel.setVisible(false);
        dutyLabel.setVisible(false);
        currentLabel.setVisible(false);
        width2Label.setVisible(false);
        angle2Label.setVisible(false);
        offsetLabel.setVisible(false);
        width2Spinner.setVisible(false);
        angle2Spinner.setVisible(false);
        offsetSpinner.setVisible(false);
        pressureSpinner.setVisible(false);
        currentSpinner.setVisible(false);
        dutySpinner.setVisible(false);
        regulatorToggleButton.setDisable(true);
        lcd.setOpacity(0.4);
    }

    private void setupSpinners() {

        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(WIDTH_CURRENT_SIGNAL_SPINNER_MIN,
                WIDTH_CURRENT_SIGNAL_SPINNER_MAX,
                WIDTH_CURRENT_SIGNAL_SPINNER_INIT,
                WIDTH_CURRENT_SIGNAL_SPINNER_STEP));
        width2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(WIDTH_CURRENT_SIGNAL_SPINNER_MIN,
                WIDTH_CURRENT_SIGNAL_SPINNER_MAX,
                WIDTH_CURRENT_SIGNAL_SPINNER_INIT,
                WIDTH_CURRENT_SIGNAL_SPINNER_STEP));
        offsetSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(OFFSET_SPINNER_MIN,
                OFFSET_SPINNER_MAX,
                OFFSET_SPINNER_INIT,
                OFFSET_SPINNER_STEP));
        angle1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                ANGLE_OFFSET_SPINNER_MIN,
                ANGLE_OFFSET_SPINNER_MAX,
                ANGLE_OFFSET_SPINNER_INIT,
                ANGLE_OFFSET_SPINNER_STEP));
        angle2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                ANGLE_OFFSET_SPINNER_MIN,
                ANGLE_OFFSET_SPINNER_MAX,
                ANGLE_OFFSET_SPINNER_INIT,
                ANGLE_OFFSET_SPINNER_STEP));
        pressureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                PRESS_REG_1_SPINNER_MIN,
                2500,
                PRESS_REG_1_SPINNER_INIT,
                PRESS_REG_1_SPINNER_STEP));
        currentSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(CURRENT_REG_1_SPINNER_MIN,
                CURRENT_REG_1_SPINNER_MAX,
                CURRENT_REG_1_SPINNER_INIT,
                CURRENT_REG_1_SPINNER_STEP));
        dutySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(DUTY_CYCLE_REG_1_SPINNER_MIN,
                DUTY_CYCLE_REG_1_SPINNER_MAX,
                DUTY_CYCLE_REG_1_SPINNER_INIT,
                DUTY_CYCLE_REG_1_SPINNER_STEP));

        SpinnerManager.setupDoubleSpinner(currentSpinner);
        SpinnerManager.setupDoubleSpinner(dutySpinner);
        SpinnerManager.setupIntegerSpinner(width2Spinner);
        SpinnerManager.setupIntegerSpinner(offsetSpinner);
        SpinnerManager.setupIntegerSpinner(angle1Spinner);
        SpinnerManager.setupIntegerSpinner(angle2Spinner);
        SpinnerManager.setupIntegerSpinner(pressureSpinner);
    }

    private void setupLedControllers() {

        setNumber(1, led1ToggleButton);
        setNumber(2, led2ToggleButton);
        setNumber(3, led3ToggleButton);
        setNumber(4, led4ToggleButton);
        setNumber(5, led5ToggleButton);
        setNumber(6, led6ToggleButton);
        setNumber(7, led7ToggleButton);
        setNumber(8, led8ToggleButton);

        setNumber(1, uisInjectorSectionModel.getLedBeaker1ToggleButton());
        setNumber(2, uisInjectorSectionModel.getLedBeaker2ToggleButton());
        setNumber(3, uisInjectorSectionModel.getLedBeaker3ToggleButton());
        setNumber(4, uisInjectorSectionModel.getLedBeaker4ToggleButton());
        setNumber(5, uisInjectorSectionModel.getLedBeaker5ToggleButton());
        setNumber(6, uisInjectorSectionModel.getLedBeaker6ToggleButton());
        setNumber(7, uisInjectorSectionModel.getLedBeaker7ToggleButton());
        setNumber(8, uisInjectorSectionModel.getLedBeaker8ToggleButton());

        setBlinkingStatus(led1ToggleButton, false);
        setBlinkingStatus(led2ToggleButton, false);
        setBlinkingStatus(led3ToggleButton, false);
        setBlinkingStatus(led4ToggleButton, false);
        setBlinkingStatus(led5ToggleButton, false);
        setBlinkingStatus(led6ToggleButton, false);
        setBlinkingStatus(led7ToggleButton, false);
        setBlinkingStatus(led8ToggleButton, false);

        ledToggleButtons = FXCollections.observableArrayList(new LinkedList<>());

        ledToggleButtons.add(led1ToggleButton);
        ledToggleButtons.add(led2ToggleButton);
        ledToggleButtons.add(led3ToggleButton);
        ledToggleButtons.add(led4ToggleButton);
        ledToggleButtons.add(led5ToggleButton);
        ledToggleButtons.add(led6ToggleButton);
        ledToggleButtons.add(led7ToggleButton);
        ledToggleButtons.add(led8ToggleButton);
    }

    private void setupListeners() {

        uisInjectorSectionModel.getLedBeaker1ToggleButton().selectedProperty().bind(led1ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker2ToggleButton().selectedProperty().bind(led2ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker3ToggleButton().selectedProperty().bind(led3ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker4ToggleButton().selectedProperty().bind(led4ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker5ToggleButton().selectedProperty().bind(led5ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker6ToggleButton().selectedProperty().bind(led6ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker7ToggleButton().selectedProperty().bind(led7ToggleButton.selectedProperty());
        uisInjectorSectionModel.getLedBeaker8ToggleButton().selectedProperty().bind(led8ToggleButton.selectedProperty());
        uisInjectorSectionModel.getPowerButton().selectedProperty().bind(regulatorToggleButton.selectedProperty());
        uisInjectorSectionModel.width_1Property().bind(widthSpinner.valueProperty());
        uisInjectorSectionModel.width_2Property().bind(width2Spinner.valueProperty());
        uisInjectorSectionModel.shiftProperty().bind(offsetSpinner.valueProperty());
        uisInjectorSectionModel.angle_1Property().bind(angle1Spinner.valueProperty());
        uisInjectorSectionModel.angle_2Property().bind(angle2Spinner.valueProperty());

        uisHardwareUpdateModel.injectorErrorProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                ultimaModbusWriter.add(Inj_Process_Global_Error, false);
            }
        });

        widthSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.toString().equals(uisHardwareUpdateModel.widthProperty().get())) {
                widthSpinner.getEditor().setStyle(null);
            }
        });
        width2Spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.toString().equals(uisHardwareUpdateModel.width2Property().get())) {
                width2Spinner.getEditor().setStyle(null);
            }
        });

        mainSectionUisModel.injectorTestProperty().addListener((observable, oldValue, newValue) -> {

            /** Additional check of GUI type is done to prevent ClassCactExeption when Test is casted to InjectorUisTest.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf InjectorUisTest} but it is slower */
            if (gui_typeModel.guiTypeProperty().get() != UIS) {return;}

            if (newValue == null) {

                widthSpinner.getValueFactory().setValue(WIDTH_CURRENT_SIGNAL_SPINNER_INIT);
                angle1Spinner.getValueFactory().setValue(ANGLE_OFFSET_SPINNER_INIT);
                return;
            }

            Integer totalPulseTime1 = newValue.getTotalPulseTime1();
            Integer totalPulseTime2 = newValue.getTotalPulseTime2();
            Integer angle_1 = ((InjectorUisTest) newValue).getAngle_1();
            Integer angle_2 = ((InjectorUisTest) newValue).getAngle_2();
            Integer shift = newValue.getShift();
            Integer settedPressure = newValue.getSettedPressure();

            widthSpinner.getValueFactory().setValue(totalPulseTime1);
            angle1Spinner.getValueFactory().setValue(angle_1);

            InjectorSubType injectorSubType = newValue.getVoltAmpereProfile().getInjectorSubType();

            if (injectorSubType == DOUBLE_COIL || injectorSubType == HPI || injectorSubType == DOUBLE_SIGNAL) {
                width2Spinner.getValueFactory().setValue(totalPulseTime2 != null ? totalPulseTime2 : totalPulseTime1 - shift);
                if (injectorSubType != DOUBLE_COIL) {
                    angle2Spinner.getValueFactory().setValue(angle_2);
                } else {
                    offsetSpinner.getValueFactory().setValue(shift);
                }
            } else if (injectorSubType == F2E) {
                pressureSpinner.getValueFactory().setValue(settedPressure);
            }
        });
    }

    private void setNumber(int number, ToggleButton ledToggleButton) {
        ledToggleButton.setText(String.valueOf(number));
    }

    private int getNumber(ToggleButton ledToggleButton) {
        return Integer.parseInt(ledToggleButton.getText());
    }

    private void ledBlinkStart(ToggleButton ledToggleButton) {
        if (ledToggleButton.isSelected()) {
            setBlinkingStatus(ledToggleButton, true);
            timeLinePlay(ledToggleButton);
        }
    }

    private void setBlinkingStatus(ToggleButton ledBeakerController, boolean blinking){
        ledBeakerController.setUserData(blinking);
    }

    private boolean getBlinkingStatus(ToggleButton ledBeakerController){
        return (boolean)ledBeakerController.getUserData();
    }

    private void setToggleGroupToLeds(ToggleGroup toggleGroup) {
        ledToggleButtons.forEach(s -> s.setToggleGroup(toggleGroup));
    }

    private void setupTimelines() {

        timeLinesList = new ArrayList<>();
        keyFramesList = new ArrayList<>();

        for(int i = 0; i < 8; i++){

            ToggleButton ledToggleButton = ledToggleButtons.get(i);

            timeLinesList.add(new Timeline());
            keyFramesList.add(makeKeyFrame(ledToggleButton));

            Timeline timeLine = timeLinesList.get(i);
            KeyFrame keyFrame = keyFramesList.get(i);

            timeLine.getKeyFrames().add(keyFrame);
            timeLine.setCycleCount(Animation.INDEFINITE);
            ledToggleButton.getStyleClass().add(2, LED_BLINK_OFF);
            ledToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                getLoggingInjectorSelection(newValue, ledToggleButton);
                if (newValue) {
                    ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
                } else {
                    ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
                }
            });
        }
    }

    private KeyFrame makeKeyFrame(ToggleButton ledToggleButton){
        return new KeyFrame(Duration.millis(500), event -> {
            if (UisInjectorSectionController.this.getBlinkingStatus(ledToggleButton)) {
                ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
                UisInjectorSectionController.this.setBlinkingStatus(ledToggleButton, false);
            } else {
                ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
                UisInjectorSectionController.this.setBlinkingStatus(ledToggleButton, true);
            }
        });
    }

    private void timeLinePlay(ToggleButton ledToggleButton){
        timeLinesList.get(getNumber(ledToggleButton) - 1).play();
    }

    private void timeLineStop(ToggleButton ledToggleButton){
        timeLinesList.get(getNumber(ledToggleButton) - 1).stop();
    }

    private class HboxWidthListener implements ChangeListener<Number> {

        private final StackPane rootStackPane;
        private final StackPane stackPaneLCD;

        HboxWidthListener(StackPane rootStackPane, StackPane stackPaneLCD) {
            this.rootStackPane = rootStackPane;
            this.stackPaneLCD = stackPaneLCD;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double tempWidth = rootStackPane.getWidth() / 7.416;
            if (tempWidth < 192) {
                if (stackPaneLCD.getWidth() > 150) {
                    stackPaneLCD.setPrefWidth(tempWidth);

                } else {
                    stackPaneLCD.setPrefWidth(140);
                }
            } else {
                stackPaneLCD.setPrefWidth(192);
            }
        }
    }

    private void getLoggingInjectorSelection(Boolean value, ToggleButton ledController) {
        String s = String.format("LedBeaker %s selected: %s", ledController.getText(), value);
        logger.info(s);
    }

    private class LedParametersChangeListener implements ChangeListener<Object> {

        public LedParametersChangeListener() {

            ledToggleButtons.forEach(s -> s.selectedProperty().addListener(this));
        }

        @Override
        public void changed(ObservableValue<?> observableValue, Object oldValue, Object newValue) {

            if (newValue instanceof Toggle) {

            }

        }
    }
}
