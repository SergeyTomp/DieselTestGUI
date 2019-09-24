package fi.stardex.sisu.ui.controllers.common;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.TabSectionModel;
import fi.stardex.sisu.model.cr.InjectorTestModel;
import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.model.pump.PumpTestModel;
import fi.stardex.sisu.model.TestBenchSectionModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.updateModels.TachometerUltimaUpdateModel;
import fi.stardex.sisu.model.updateModels.TestBenchSectionUpdateModel;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.enums.pump.PumpRotation;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.application.Platform;
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

    private PumpModel pumpModel;

    private InjectorTestModel injectorTestModel;

    private TestBenchSectionUpdateModel testBenchSectionUpdateModel;

    private TachometerUltimaUpdateModel tachometerUltimaUpdateModel;

    private TestBenchSectionModel testBenchSectionModel;

    private MainSectionUisModel mainSectionUisModel;

    private TabSectionModel tabSectionModel;

    private static final String PUMP_BUTTON_ON = "pump-button-on";

    private static final String PUMP_BUTTON_OFF = "pump-button-off";

    private StatePump pumpState;

    private ModbusRegisterProcessor flowModbusWriter;

    private ModbusRegisterProcessor standModbusWriter;

    private Lcd currentRPMLcd;

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    private FirmwareVersion<StandVersions> standFirmwareVersion;

    private ModbusConnect flowModbusConnect;

    private ModbusConnect standModbusConnect;

    private ChangeListener<Boolean> pumpTurnOnListener;

    public Spinner<Integer> getTargetRPMSpinner() {
        return targetRPMSpinner;
    }
    public Lcd getCurrentRPMLcd() {
        return currentRPMLcd;
    }
    public ToggleButton getTestBenchStartToggleButton() {
        return testBenchStartToggleButton;
    }
    private StatePump getPumpState() {
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
    private void setPumpState(StatePump pumpState) {
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

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }


    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    public void setTestBenchSectionUpdateModel(TestBenchSectionUpdateModel testBenchSectionUpdateModel) {
        this.testBenchSectionUpdateModel = testBenchSectionUpdateModel;
    }

    public void setTachometerUltimaUpdateModel(TachometerUltimaUpdateModel tachometerUltimaUpdateModel) {
        this.tachometerUltimaUpdateModel = tachometerUltimaUpdateModel;
    }

    public void setTestBenchSectionModel(TestBenchSectionModel testBenchSectionModel) {
        this.testBenchSectionModel = testBenchSectionModel;
    }

    public void setFlowModbusConnect(ModbusConnect flowModbusConnect) {
        this.flowModbusConnect = flowModbusConnect;
    }

    public void setStandModbusConnect(ModbusConnect standModbusConnect) {
        this.standModbusConnect = standModbusConnect;
    }

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }

    public void setTabSectionModel(TabSectionModel tabSectionModel) {
        this.tabSectionModel = tabSectionModel;
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

        setupUpdatersListeners();

        setupConnectionListeners();

        setupTabSectionListeners();
    }

    private void bindingI18N() {
        labelTemp1.textProperty().bind(i18N.createStringBinding("bench.label.temp1"));
        labelTemp2.textProperty().bind(i18N.createStringBinding("bench.label.temp2"));
        labelPressure1.textProperty().bind(i18N.createStringBinding("bench.label.pressure1"));
        labelRPM.textProperty().bind(i18N.createStringBinding("bench.label.rpm"));
        fuelLevelLabel.textProperty().bind(i18N.createStringBinding("bench.label.fuelLevel"));
    }

    private void setupTabSectionListeners() {

        tabSectionModel.step3TabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                testBenchStartToggleButton.setSelected(false);
            }
            testBenchStartToggleButton.setDisable(newValue || (flowFirmwareVersion.getVersions() != STAND_FM
                    && flowFirmwareVersion.getVersions() != STAND_FM_4_CH
                    && standFirmwareVersion.versionProperty().get() != STAND));
        });

        tabSectionModel.piezoTabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                testBenchStartToggleButton.setSelected(false);
            }
            testBenchStartToggleButton.setDisable(newValue || (flowFirmwareVersion.getVersions() != STAND_FM
                    && flowFirmwareVersion.getVersions() != STAND_FM_4_CH
                    && standFirmwareVersion.versionProperty().get() != STAND));
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

        injectorTestModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {

                targetRPMSpinner.getValueFactory().setValue(newValue.getMotorSpeed());
            }
            else{
                targetRPMSpinner.getValueFactory().setValue(0);
            }
        });

        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

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
            else{
                testBenchStartToggleButton.setDisable(true);
                resetNodes();
            }

        });

    }

    private void setupStandFirmwareVersionListener() {

        standFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == STAND || flowFirmwareVersion.versionProperty().get() == STAND_FM || flowFirmwareVersion.versionProperty().get() == STAND_FM_4_CH)
                testBenchStartToggleButton.setDisable(false);
            else{
                testBenchStartToggleButton.setDisable(true);
                resetNodes();
            }

        });

    }

    private void resetNodes() {

        tempProgressBar1.setProgress(0d);
        tempProgressBar1.getStyleClass().clear();
        tempProgressBar1.getStyleClass().add("progress-bar");
        tempText1.setText("0");
        tempProgressBar2.setProgress(0d);
        tempProgressBar2.getStyleClass().clear();
        tempProgressBar2.getStyleClass().add("progress-bar");
        tempText2.setText("0");
        pressProgressBar1.setProgress(0d);
        pressProgressBar1.getStyleClass().clear();
        pressProgressBar1.getStyleClass().add("progress-bar");
        pressText1.setText("0");
        tankOil.setProgress(0);
        tankOil.getStyleClass().clear();
        tankOil.getStyleClass().add("oil-bar");
        tankOilText.setText("");
    }

    private void setupLCD() {

        currentRPMLcd = GaugeCreator.createLcd("rpm");

        currentRPMLcd.setMaxValue(5000.0);

        currentRPMLcd.setValue(0);

        lcdStackPane.getChildren().add(currentRPMLcd);

        testBenchSectionModel.currentRPMProperty().bind(currentRPMLcd.valueProperty());
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
            testBenchSectionModel.targetRPMProperty().setValue(newValue);
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

        testBenchStartToggleButton.selectedProperty().bindBidirectional(testBenchSectionModel.isPowerButtonOnProperty());
        testBenchSectionModel.isPowerButtonDisabledProperty().bind(testBenchStartToggleButton.disableProperty());

        if (flowFirmwareVersion.getVersions() == STAND_FM || flowFirmwareVersion.getVersions() == STAND_FM_4_CH || standFirmwareVersion.versionProperty().get() == STAND)
            testBenchStartToggleButton.setDisable(false);
        else
            testBenchStartToggleButton.setDisable(true);

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

        dimasGuiEditionProperty.addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                rightDirectionRotationToggleButton.setSelected(true);}
        });

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

    private void setupUpdatersListeners() {

        pumpTurnOnListener = (observableValue, oldValue, newValue) -> {
            setPumpState(newValue ? StatePump.ON : StatePump.OFF);
            changePumpButton();
        };

        testBenchSectionUpdateModel.pumpTurnOnProperty().addListener(pumpTurnOnListener);

        testBenchSectionUpdateModel.sectionStartedProperty().addListener((observableValue, oldValue, newValue) ->
                testBenchStartToggleButton.setSelected(newValue));

        testBenchSectionUpdateModel.fanTurnOnProperty().addListener((observableValue, oldValue, newValue) ->
                fanControlToggleButton.setSelected(newValue));

        testBenchSectionUpdateModel.rotationDirectionProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) rightDirectionRotationToggleButton.setSelected(true);
            else leftDirectionRotationToggleButton.setSelected(true);
        });

        testBenchSectionUpdateModel.pumpAutoModeProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                testBenchSectionUpdateModel.pumpTurnOnProperty().removeListener(pumpTurnOnListener);
                setPumpState(testBenchStartToggleButton.isSelected() ? StatePump.AUTO_ON : StatePump.AUTO_OFF);
                changePumpButton();
            } else {
                testBenchSectionUpdateModel.pumpTurnOnProperty().addListener(pumpTurnOnListener);
            }
        });

        testBenchSectionUpdateModel.targetRPMProperty().addListener((observableValue, oldValue, newValue) ->
                targetRPMSpinner.getValueFactory().setValue(newValue.intValue()));

        testBenchSectionUpdateModel.currentRPMProperty().addListener((observableValue, oldValue, newValue) ->
                currentRPMLcd.setValue(newValue.intValue()));

        testBenchSectionUpdateModel.tankOilLevelProperty().addListener((observableValue, oldValue, newValue) -> {
            int level = newValue.intValue();
            if (level > 10)
                changeTankOil("green-oil-bar", "NORMAL");
            else if (level > 1)
                changeTankOil("yellow-oil-bar", "LOW");
            else
                changeTankOil("red-oil-bar", "VERY\nLOW");

            tankOil.setProgress(newValue.intValue() / 110.0);
        });

        testBenchSectionUpdateModel.pressureProperty().addListener((observableValue, oldValue, newValue) ->
                setPressureProgress(3, 5, pressProgressBar1, pressText1, newValue.doubleValue()));

        testBenchSectionUpdateModel.backFlowOilTemperatureProperty().addListener((observableValue, oldValue, newValue) ->
                setTemperatureProgress(37, 41, tempProgressBar2, tempText2, newValue.doubleValue()));

        testBenchSectionUpdateModel.tankOilTemperatureProperty().addListener((observableValue, oldValue, newValue) ->
                setTemperatureProgress(37, 41, tempProgressBar1, tempText1, newValue.doubleValue()));

        tachometerUltimaUpdateModel.currentRPMProperty().addListener((observableValue, oldValue, newValue) ->
                currentRPMLcd.setValue(newValue.intValue()));
    }

    private void setupConnectionListeners(){

        flowModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue && ((flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH))) { initRotationDirection(); }});

        standModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {if(newValue) initRotationDirection();});
    }

    private void initRotationDirection() {

        rightDirectionRotationToggleButton.setSelected(true);
        //forced focus request below is done to avoid phantom selection of left arrow simultaneously with right one despite of the fact that both buttons belong to toggle group
        //phantom effect takes place while this method invocation upon first connection established only
        //in fact left arrow is not selected but lightened nevertheless, hardware register value corresponds with right arrow
        //reason is not clear, forced focus request is made just to avoid disorienting visual bug
        Platform.runLater(() -> rightDirectionRotationToggleButton.requestFocus());
    }

    private void changePumpButton() {
        StatePump currentStatePump = getPumpState();
        pumpControlToggleButton.getStyleClass().set(1, currentStatePump.getStyle());
        pumpControlToggleButton.setText(currentStatePump.getText());
    }

    private void changeTankOil(String style, String text) {

        tankOil.getStyleClass().clear();
        tankOil.getStyleClass().add(style);
        tankOilText.setText(text);
    }

    private void setPressureProgress(int left, int right, ProgressBar progressBar, Text text, double pressureValue) {

        if (pressureValue <= left) {
            progressBarStyle(progressBar, "green-progress-bar");
        }
        if (pressureValue > left && pressureValue < right) {
            progressBarStyle(progressBar, "orange-progress-bar");
        }
        if (pressureValue >= right) {
            progressBarStyle(progressBar, "red-progress-bar");
        }
        fillProgressBar(pressureValue, text, progressBar);
    }

    private void setTemperatureProgress(int left, int right, ProgressBar progressBar, Text text, double temperatureValue){

        if (temperatureValue <= left || temperatureValue >= right) {
            progressBarStyle(progressBar, "red-progress-bar");
        }
        else {
            progressBarStyle(progressBar, "green-progress-bar");
        }
        fillProgressBar(temperatureValue, text, progressBar);
    }
    private void progressBarStyle(ProgressBar progressBar, String style) {

        progressBar.getStyleClass().clear();
        progressBar.getStyleClass().add("progress-bar");
        progressBar.getStyleClass().add(style);
    }

    private void fillProgressBar(double value, Text text, ProgressBar progressBar){
        text.setText(String.format("%.1f", value));
        progressBar.setProgress(value < 1 ? 1.0 : value);
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
