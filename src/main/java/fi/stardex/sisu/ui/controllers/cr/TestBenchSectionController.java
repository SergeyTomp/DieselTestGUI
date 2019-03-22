package fi.stardex.sisu.ui.controllers.cr;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.states.TestBenchSectionPwrState;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.pump.PumpRotation;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM_4_CH;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions.STAND;

public class TestBenchSectionController {

    private Logger logger = LoggerFactory.getLogger(TestBenchSectionController.class);

    @FXML private Label fuelLevelLabel;

    @FXML private StackPane lcdStackPane;

    @FXML private Spinner<Integer> targetRPMSpinner;

    @FXML private ToggleButton leftDirectionRotationToggleButton;

    @FXML private ToggleGroup rotationDirectionToggleGroup;

    @FXML private ToggleButton rightDirectionRotationToggleButton;

    @FXML private ToggleButton testBenchStartToggleButton;

    @FXML private ToggleButton pumpControlToggleButton;

    @FXML private ToggleButton fanControlToggleButton;

    @FXML private ProgressBar tempProgressBar1;

    @FXML private ProgressBar tempProgressBar2;

    @FXML private ProgressBar pressProgressBar1;

    @FXML private ProgressBar tankOil;

    @FXML private Text tankOilText;

    @FXML private Text tempText1;

    @FXML private Text pressText1;

    @FXML private Text tempText2;

    @FXML private Label labelTemp1;

    @FXML private Label labelTemp2;

    @FXML private Label labelPressure1;

    @FXML private Label labelRPM;

    @FXML private HBox rootHbox;

    private I18N i18N;

    private DimasGUIEditionState dimasGUIEditionState;

    private PumpTestModel pumpTestModel;

    private TargetRpmModel targetRpmModel;

    private CurrentRpmModel currentRpmModel;

    private PumpModel pumpModel;

    private GUI_TypeModel gui_typeModel;

    private TestBenchSectionPwrState testBenchSectionPwrState;

    private static final String PUMP_BUTTON_ON = "pump-button-on";

    private static final String PUMP_BUTTON_OFF = "pump-button-off";

    private StatePump pumpState;

    private ModbusRegisterProcessor flowModbusWriter;

    private ModbusRegisterProcessor standModbusWriter;

    private Lcd currentRPMLcd;

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    private FirmwareVersion<StandVersions> standFirmwareVersion;

    public Spinner<Integer> getTargetRPMSpinner() {
        return targetRPMSpinner;
    }
    public ToggleButton getLeftDirectionRotationToggleButton() {
        return leftDirectionRotationToggleButton;
    }
    public ToggleButton getRightDirectionRotationToggleButton() {
        return rightDirectionRotationToggleButton;
    }
    public ToggleButton getPumpControlToggleButton() {
        return pumpControlToggleButton;
    }
    public ToggleButton getTestBenchStartToggleButton() {
        return testBenchStartToggleButton;
    }
    public ToggleButton getFanControlToggleButton() {
        return fanControlToggleButton;
    }
    public Lcd getCurrentRPMLcd() {
        return currentRPMLcd;
    }
    public ProgressBar getTempProgressBar1() {
        return tempProgressBar1;
    }
    public ProgressBar getTempProgressBar2() {
        return tempProgressBar2;
    }
    public ProgressBar getPressProgressBar1() {
        return pressProgressBar1;
    }
    public Text getTempText1() {
        return tempText1;
    }
    public Text getPressText1() {
        return pressText1;
    }
    public Text getTempText2() {
        return tempText2;
    }
    public ProgressBar getTankOil() {
        return tankOil;
    }
    public Text getTankOilText() {
        return tankOilText;
    }
    public StatePump getPumpState() {
        return pumpState;
    }
    public boolean isOn() {
        return pumpState == StatePump.ON;
    }
    private boolean isAuto() {
        return pumpState == StatePump.AUTO_ON || pumpState == StatePump.AUTO_OFF;
    }

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setStandModbusWriter(ModbusRegisterProcessor standModbusWriter) {
        this.standModbusWriter = standModbusWriter;
    }
    public void setPumpState(StatePump pumpState) {
        this.pumpState = pumpState;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setDimasGUIEditionState(DimasGUIEditionState dimasGUIEditionState) {
        this.dimasGUIEditionState = dimasGUIEditionState;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }
    public void setStandFirmwareVersion(FirmwareVersion<StandVersions> standFirmwareVersion) {
        this.standFirmwareVersion = standFirmwareVersion;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setTargetRpmModel(TargetRpmModel targetRpmModel) {
        this.targetRpmModel = targetRpmModel;
    }
    public void setTestBenchSectionPwrState(TestBenchSectionPwrState testBenchSectionPwrState) {
        this.testBenchSectionPwrState = testBenchSectionPwrState;
    }
    public void setCurrentRpmModel(CurrentRpmModel currentRpmModel) {
        this.currentRpmModel = currentRpmModel;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    public enum StatePump {

        ON("PUMP\n ON", true, PUMP_BUTTON_ON),
        OFF("PUMP\n OFF ", false, PUMP_BUTTON_OFF),
        AUTO_ON("PUMP\nAUTO", true, PUMP_BUTTON_ON),
        AUTO_OFF("PUMP\nAUTO", false, PUMP_BUTTON_OFF);

        private String text;
        private Boolean isActive;
        private String style;

        StatePump(String text, Boolean isActive, String style) {
            this.text = text;
            this.isActive = isActive;
            this.style = style;
        }

        public String getText() {
            return text;
        }

        public String getStyle() {
            return style;
        }

        public static boolean isAuto(StatePump statePump) {
            return statePump == AUTO_ON || statePump == AUTO_OFF;
        }

    }

    @PostConstruct
    private void init() {

        bindingI18N();

        setupFlowFirmwareVersionListener();

        setupStandFirmwareVersionListener();

        setupLCD();

        setupRotationDirectionToggleButton();

        setupTargetRPMSpinner();

        setupTestBenchStartToggleButton();

        setupPumpControlToggleButton();

        setupFanControlToggleButton();

        rootHbox.widthProperty().addListener(new HboxWidthListener(rootHbox, lcdStackPane));

        setupDimasGuiBinding();

        setupTestListener();

        setupGuiTypeListener();

    }

    private void bindingI18N() {
        labelTemp1.textProperty().bind(i18N.createStringBinding("bench.label.temp1"));
        labelTemp2.textProperty().bind(i18N.createStringBinding("bench.label.temp2"));
        labelPressure1.textProperty().bind(i18N.createStringBinding("bench.label.pressure1"));
        labelRPM.textProperty().bind(i18N.createStringBinding("bench.label.rpm"));
        fuelLevelLabel.textProperty().bind(i18N.createStringBinding("bench.label.fuelLevel"));
    }

    private void setupGuiTypeListener() {

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            leftDirectionRotationToggleButton.setSelected(false);
            rightDirectionRotationToggleButton.setSelected(false);
        });
    }

    private void setupTestListener() {
        pumpTestModel.pumpTestProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {

                targetRPMSpinner.getValueFactory().setValue(newValue.getMotorSpeed());
            }
            else{
                targetRPMSpinner.getValueFactory().setValue(0);
            }
        });
    }

    private void setupFlowFirmwareVersionListener() {

        flowFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == STAND_FM || newValue == STAND_FM_4_CH || standFirmwareVersion.versionProperty().get() == STAND)
                testBenchStartToggleButton.setDisable(false);
            else
                testBenchStartToggleButton.setDisable(true);

        });

    }

    private void setupStandFirmwareVersionListener() {

        standFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == STAND || flowFirmwareVersion.versionProperty().get() == STAND_FM || flowFirmwareVersion.versionProperty().get() == STAND_FM_4_CH)
                testBenchStartToggleButton.setDisable(false);
            else
                testBenchStartToggleButton.setDisable(true);

        });

    }

    private void setupLCD() {

        currentRPMLcd = GaugeCreator.createLcd("rpm");

        currentRPMLcd.setMaxValue(5000.0);

        currentRPMLcd.setValue(0);

        lcdStackPane.getChildren().add(currentRPMLcd);

        currentRpmModel.currentRPMProperty().bind(currentRPMLcd.valueProperty());
    }

    private void setupRotationDirectionToggleButton() {

        leftDirectionRotationToggleButton.setUserData(false);

        rightDirectionRotationToggleButton.setUserData(true);

        rotationDirectionToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))
                    flowModbusWriter.add(RotationDirectionStandFM, newValue.getUserData());
                else
                    standModbusWriter.add(RotationDirection, newValue.getUserData());
            }

        });
        pumpModel.pumpProperty().addListener((observableValue, oldPump, newPump) -> {
            if (newPump != null) {

                if (newPump.getPumpRotation() == PumpRotation.RIGHT) {
                    rightDirectionRotationToggleButton.setSelected(true);
                }else {
                    leftDirectionRotationToggleButton.setSelected(true);
                }
            }
        });

    }

    private void setupTargetRPMSpinner() {

        targetRPMSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3000, 0, 50));

        SpinnerManager.setupIntegerSpinner(targetRPMSpinner);

        targetRPMSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {

            if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH)){

                flowModbusWriter.add(TargetRPMStandFM, newValue);
            }
            else{

                standModbusWriter.add(TargetRPM, newValue);
            }
            targetRpmModel.targetRPMProperty().setValue(newValue);
        });

    }

    private void setupTestBenchStartToggleButton() {

        testBenchStartToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))
                flowModbusWriter.add(RotationStandFM, newValue);
            else
                standModbusWriter.add(Rotation, newValue);

            if (StatePump.isAuto(pumpState))
                setPumpAuto(newValue);

        });

        testBenchStartToggleButton.selectedProperty().bindBidirectional(testBenchSectionPwrState.isPowerButtonOnProperty());
        testBenchSectionPwrState.isPowerButtonDisabledProperty().bind(testBenchStartToggleButton.disableProperty());

    }

    private void setupPumpControlToggleButton() {

        pumpState = StatePump.OFF;

        pumpControlToggleButton.getStyleClass().add(1, "pump-button");

        pumpControlToggleButton.getStyleClass().add(1, PUMP_BUTTON_ON);

        pumpControlToggleButton.getStyleClass().add(1, PUMP_BUTTON_OFF);

        pumpControlToggleButton.getStyleClass().set(1, PUMP_BUTTON_OFF);

        pumpControlToggleButton.setText(pumpState.getText());

        pumpControlToggleButton.setOnMouseClicked(this::mouseHandler);

    }

    private void setupFanControlToggleButton() {

        fanControlToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))
                flowModbusWriter.add(FanTurnOnStandFM, newValue);
            else
                standModbusWriter.add(FanTurnOn, newValue);

        });

    }

    private void mouseHandler(MouseEvent mouseEvent) {

        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                logger.warn("Double clicked");
                if (isAuto()) {
                    setPumpState(TestBenchSectionController.StatePump.OFF);
                    if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))
                        flowModbusWriter.add(PumpAutoModeStandFM, false);
                    else
                        standModbusWriter.add(PumpAutoMode, false);
                } else {
                    setPumpState(testBenchStartToggleButton.isSelected() ?
                            TestBenchSectionController.StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
                    if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))
                        flowModbusWriter.add(PumpAutoModeStandFM, true);
                    else
                        standModbusWriter.add(PumpAutoMode, true);
                }
            } else if (mouseEvent.getClickCount() == 1) {
                if (pumpState == TestBenchSectionController.StatePump.OFF)
                    setPumpState(TestBenchSectionController.StatePump.ON);
                else if (pumpState == TestBenchSectionController.StatePump.ON)
                    setPumpState(TestBenchSectionController.StatePump.OFF);
            }
            pumpStateChange();
        }

    }

    private void setupDimasGuiBinding() {

        BooleanProperty dimasGuiEditionProperty = dimasGUIEditionState.isDimasGuiEditionProperty();

        leftDirectionRotationToggleButton.visibleProperty().bind(dimasGuiEditionProperty.not());
        rightDirectionRotationToggleButton.visibleProperty().bind(dimasGuiEditionProperty.not());
        pressText1.visibleProperty().bind(dimasGuiEditionProperty.not());
        labelPressure1.visibleProperty().bind(dimasGuiEditionProperty.not());
        pressProgressBar1.visibleProperty().bind(dimasGuiEditionProperty.not());

        dimasGuiEditionProperty.addListener((observableValue, oldValue, newValue) -> rightDirectionRotationToggleButton.setSelected(newValue));

    }

    private void pumpStateChange() {

        pumpControlToggleButton.getStyleClass().set(1, pumpState.getStyle());

        pumpControlToggleButton.setText(pumpState.getText());

        if ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))
            flowModbusWriter.add(PumpTurnOnStandFM, pumpState.isActive);
        else
            standModbusWriter.add(PumpTurnOn, pumpState.isActive);

    }

    private void setPumpAuto(boolean deviceStateOn) {

        setPumpState(deviceStateOn ? StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);

        pumpStateChange();

    }

    private class HboxWidthListener implements ChangeListener<Number> {

        private final HBox rootHbox;
        private final StackPane stackPaneLCD;

        public HboxWidthListener(HBox rootHbox, StackPane stackPaneLCD) {
            this.rootHbox = rootHbox;
            this.stackPaneLCD = stackPaneLCD;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double tempWidth = rootHbox.getWidth() / 7.416;
            if (tempWidth < 192) {
                if (stackPaneLCD.getWidth() > 150) {
                    stackPaneLCD.setPrefWidth(tempWidth);

                } else {
                    stackPaneLCD.setPrefWidth(135);
                }
            } else {
                stackPaneLCD.setPrefWidth(192);
            }
        }
    }

}
