package fi.stardex.sisu.ui.controllers.cr.windows;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.measurement.CrTestManager;
import fi.stardex.sisu.model.cr.InjectorTestModel;
import fi.stardex.sisu.model.cr.PressureRegulatorOneModel;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.persistence.orm.ISADetection;
import fi.stardex.sisu.persistence.repos.ISADetectionRepository;
import fi.stardex.sisu.states.InjectorControllersState;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class ISADetectionController {

    private static final int DEFAULT_PRESSURE_TIME = 20;
    @FXML
    private Label testVoltage;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button stopISAButton;

    private Parent isaParent;

    private Parent rootParent;

    private Stage isaStage;

    private InjectorSectionController injectorSectionController;

    private Timeline pressureTimeline;

    private Timeline gapTimeline;

    private Timeline adjustingTimeline;

    private Timeline measurementTimeline;

    private CrTestManager crTestManager;

    private ToggleButton mainSectionStartToggleButton;

    private boolean led1Active;

    private boolean led2Active;

    private boolean led3Active;

    private boolean led4Active;

    private int shiftVoltage;

    private String mask;

    private ISADetectionRepository isaDetectionRepository;

    private Integer[] voltageArray;

    private Spinner<Integer> boostUSpinner;

    private Button voltAmpereProfileApplyButton;

    private Button resetButton;

    private Lcd pressureLcd;

    private int currentIndex = ISADetection.ISA_CHARS_NUMBER - 1;

    private int currentPressureTime = DEFAULT_PRESSURE_TIME;

    private TextField delivery1TextField;

    private TextField delivery2TextField;

    private TextField delivery3TextField;

    private TextField delivery4TextField;

    private ISAState isaState;

    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;

    private PressureRegulatorOneModel pressureRegulatorOneModel;

    private InjectorTestModel injectorTestModel;

    private InjectorControllersState injectorControllersState;

    private double ISA_trigger;

    private Alert alert;
    private StringProperty yesButton = new SimpleStringProperty();
    private I18N i18N;
    private Text txt = new Text();

    public static List<ISAResult> getIsaResult() {
        return ISA_RESULT;
    }

    public void setISAParent(Parent isaParent) {
        this.isaParent = isaParent;
    }

    public void setRootParent(Parent rootParent) {
        this.rootParent = rootParent;
    }

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
    }

    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }

    public void setPressureRegulatorOneModel(PressureRegulatorOneModel pressureRegulatorOneModel) {
        this.pressureRegulatorOneModel = pressureRegulatorOneModel;
    }

    public void setCrTestManager(CrTestManager crTestManager) {
        this.crTestManager = crTestManager;
    }

    public void setMainSectionStartToggleButton(ToggleButton mainSectionStartToggleButton) {
        this.mainSectionStartToggleButton = mainSectionStartToggleButton;
    }

    public void setISADetectionRepository(ISADetectionRepository isaDetectionRepository) {
        this.isaDetectionRepository = isaDetectionRepository;
    }

    public void setBoostUSpinner(Spinner<Integer> boostUSpinner) {
        this.boostUSpinner = boostUSpinner;
    }

    public void setResetButton(Button resetButton) {
        this.resetButton = resetButton;
    }

    public void setDelivery1TextField(TextField delivery1TextField) {
        this.delivery1TextField = delivery1TextField;
    }

    public void setDelivery2TextField(TextField delivery2TextField) {
        this.delivery2TextField = delivery2TextField;
    }

    public void setDelivery3TextField(TextField delivery3TextField) {
        this.delivery3TextField = delivery3TextField;
    }

    public void setDelivery4TextField(TextField delivery4TextField) {
        this.delivery4TextField = delivery4TextField;
    }

    public void setVoltAmpereProfileApplyButton(Button voltAmpereProfileApplyButton) {
        this.voltAmpereProfileApplyButton = voltAmpereProfileApplyButton;
    }

    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    public void setInjectorControllersState(InjectorControllersState injectorControllersState) {
        this.injectorControllersState = injectorControllersState;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public enum ISAResultState {
        VALID, INVALID, OFF
    }

    public static class ISAResult {

        private ISAResultState isaResultState;

        private Character isa_char;

        ISAResult(ISAResultState isaResultState) {
            this.isaResultState = isaResultState;
            isa_char = null;
        }

        public ISAResultState getIsaResultState() {
            return isaResultState;
        }

        public Character getIsa_char() {
            return isa_char;
        }

        void setIsaResultState(ISAResultState isaResultState) {
            this.isaResultState = isaResultState;
        }

        void setIsa_char(Character isa_char) {
            this.isa_char = isa_char;
        }

        @Override
        public String toString() {
            return "ISAResult{" +
                    "isaResultState=" + isaResultState +
                    ", isa_char=" + isa_char +
                    '}';
        }
    }

    private enum ISAState {
        RUNNING,
        NO_ACTIVE_LEDS,
        PRESSURE_NOT_DIALED,
        FINISHED
    }

    private static final List<ISAResult> ISA_RESULT = new ArrayList<>();

    static {

        resetISAResult();
    }

    @PostConstruct
    private void init() {

        this
                .setupPressureTimeline()
                .setupGapTimeline()
                .setupAdjustingTimeline()
                .setupMeasurementTimeline()
                .setupStopISAButtonActionListener();

        yesButton.bind(i18N.createStringBinding("dialog.customer.close"));
    }

    private ISADetectionController setupPressureTimeline() {

        pressureTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> preparePressure()));
        pressureTimeline.setCycleCount(Animation.INDEFINITE);
        return this;
    }

    private ISADetectionController setupGapTimeline() {

        gapTimeline = new Timeline(new KeyFrame(Duration.seconds(1)));

        gapTimeline.setOnFinished(event -> {
            injectorSectionController.getInjectorSectionStartToggleButton().setSelected(true);
            adjustingTimeline.play();
        });
        return this;
    }

    private ISADetectionController setupAdjustingTimeline() {

        adjustingTimeline = new Timeline(new KeyFrame(Duration.seconds(1)));
        adjustingTimeline.setCycleCount(10);
        adjustingTimeline.setOnFinished(event -> startMeasurement());

        return this;
    }

    private ISADetectionController setupMeasurementTimeline() {

        measurementTimeline = new Timeline(new KeyFrame(Duration.seconds(1)));
        measurementTimeline.setCycleCount(30);

        measurementTimeline.setOnFinished(event -> checkFlow());

        return this;
    }

    private void setupStopISAButtonActionListener() {

        stopISAButton.setOnAction((event) -> {

            switch (isaState) {

                case RUNNING:
                case NO_ACTIVE_LEDS:
                case PRESSURE_NOT_DIALED:
                    isaStage.close();
                    crTestManager.setCodingComplete(false);
                    mainSectionStartToggleButton.setSelected(false);
                    break;
                case FINISHED:
                    isaStage.close();
                    crTestManager.runNextTest();
                    break;

            }
            currentIndex = ISADetection.ISA_CHARS_NUMBER - 1;
        });
    }

    public void work() {

        initISAStage();

        resetISAResult();

        Double nominalFlow = injectorTestModel.injectorTestProperty().get().getNominalFlow();
        Double flowRange = injectorTestModel.injectorTestProperty().get().getFlowRange();
        if (nominalFlow == null) {
            nominalFlow = 0.0;
        }
        if (flowRange == null) {
            flowRange = 0.0;
        }
        ISA_trigger = nominalFlow * (1 + (flowRange / 100));

        if (injectorControllersState.activeLedToggleButtonsListProperty().get().isEmpty())
            changeISAState(ISAState.NO_ACTIVE_LEDS);
        else {
            changeISAState(ISAState.RUNNING);

            led1Active = injectorControllersState.getLedBeaker1ToggleButton().isSelected();
            led2Active = injectorControllersState.getLedBeaker2ToggleButton().isSelected();
            led3Active = injectorControllersState.getLedBeaker3ToggleButton().isSelected();
            led4Active = injectorControllersState.getLedBeaker4ToggleButton().isSelected();
            
            if (led1Active)
                ISA_RESULT.get(0).setIsaResultState(ISAResultState.INVALID);
            if (led2Active)
                ISA_RESULT.get(1).setIsaResultState(ISAResultState.INVALID);
            if (led3Active)
                ISA_RESULT.get(2).setIsaResultState(ISAResultState.INVALID);
            if (led4Active)
                ISA_RESULT.get(3).setIsaResultState(ISAResultState.INVALID);

            ISADetection isaDetection = isaDetectionRepository.findById(getInjector().getInjectorCode())
                    .orElseThrow(() -> new RuntimeException("Invalid ISA Detection code!"));
            voltageArray = isaDetection.getCharArray();
            shiftVoltage = isaDetection.getShiftVoltage();
            mask = isaDetection.getMaskType().getMask();
            nextVoltage(currentIndex);
        }
    }

    private void initISAStage() {

        if (isaStage == null) {

            isaStage = new Stage(StageStyle.UNDECORATED);
            isaStage.setScene(new Scene(isaParent));
            isaStage.initModality(Modality.WINDOW_MODAL);
            isaStage.initOwner(rootParent.getScene().getWindow());
        }
        isaStage.show();
    }

    private void nextVoltage(int index) {

        injectorSectionController.getInjectorSectionStartToggleButton().setSelected(false);

        if (currentIndex == -1) {
            finish();
            return;
        }

        int voltage = voltageArray[index];

        if (voltage == 0) {
            nextVoltage(--currentIndex);
            return;
        }

        testVoltage.setText("Test " + Integer.toString(voltage + shiftVoltage) + " V");
        boostUSpinner.getValueFactory().setValue(voltage + shiftVoltage);
        voltAmpereProfileApplyButton.fire();
        pressureTimeline.play();
    }

    private void preparePressure() {

        if (isPressureReady()) {
            pressureTimeline.stop();
            currentPressureTime = DEFAULT_PRESSURE_TIME;
            gapTimeline.play();
        }

        if (--currentPressureTime == 0) {
            changeISAState(ISAState.PRESSURE_NOT_DIALED);
            pressureTimeline.stop();
            currentPressureTime = DEFAULT_PRESSURE_TIME;
        }
    }

    private void finish() {

        changeISAState(ISAState.FINISHED);
        boostUSpinner.getValueFactory().setValue(getInjector().getVoltAmpereProfile().getBoostU());
        voltAmpereProfileApplyButton.fire();
        currentIndex = ISADetection.ISA_CHARS_NUMBER - 1;
//        Platform.runLater(this::showLetter);
        stopISAButton.fire();
    }

    private boolean isPressureReady() {

        int pressSpinnerValue = pressureRegulatorOneModel.pressureRegOneProperty().get();
        int realPressValue = highPressureSectionUpdateModel.lcdPressureProperty().get();

        return Math.abs((pressSpinnerValue - realPressValue) / pressSpinnerValue) < 0.2;
    }

    private void startMeasurement() {

        resetButton.fire();
        measurementTimeline.play();
    }

    private void checkFlow() {

        if (led1Active && convertDataToDouble(delivery1TextField.getText()) <= ISA_trigger) {
            setISA(ISA_RESULT.get(0));
            led1Active = false;
        }
        if (led2Active && convertDataToDouble(delivery2TextField.getText()) <= ISA_trigger) {
            led2Active = false;
            setISA(ISA_RESULT.get(1));
        }
        if (led3Active && convertDataToDouble(delivery3TextField.getText()) <= ISA_trigger) {
            led3Active = false;
            setISA(ISA_RESULT.get(2));
        }
        if (led4Active && convertDataToDouble(delivery4TextField.getText()) <= ISA_trigger) {
            led4Active = false;
            setISA(ISA_RESULT.get(3));
        }

        if (led1Active || led2Active || led3Active || led4Active)
            nextVoltage(--currentIndex);
        else
            finish();
    }

    private void setISA(ISAResult isaResult) {

        isaResult.setIsaResultState(ISAResultState.VALID);
        isaResult.setIsa_char(mask.charAt(currentIndex));
    }

    private void changeISAState(ISAState isaState) {

        this.isaState = isaState;

        switch (isaState) {

            case RUNNING:
            case FINISHED:
                errorLabel.setText("");
                progressIndicator.setVisible(true);
                break;
            case NO_ACTIVE_LEDS:
                errorLabel.setText("No one led is active. Test cannot be performed.");
                progressIndicator.setVisible(false);
                break;
            case PRESSURE_NOT_DIALED:
                errorLabel.setText("Pressure is not dialed. Coding is not possible.");
                progressIndicator.setVisible(false);
                break;
        }
    }

    private static void resetISAResult() {

        ISA_RESULT.clear();

        ISA_RESULT.add(new ISAResult(ISAResultState.OFF));
        ISA_RESULT.add(new ISAResult(ISAResultState.OFF));
        ISA_RESULT.add(new ISAResult(ISAResultState.OFF));
        ISA_RESULT.add(new ISAResult(ISAResultState.OFF));
    }

    private void showLetter() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.initModality(Modality.NONE);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setPrefHeight(Region.USE_COMPUTED_SIZE);
            alert.getDialogPane().setMaxWidth(185);
        }
        ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());

        String one = ISA_RESULT.get(0).getIsa_char() != null ? String.valueOf(ISA_RESULT.get(0).getIsa_char()) : "";
        String two = ISA_RESULT.get(1).getIsa_char() != null ? String.valueOf(ISA_RESULT.get(1).getIsa_char()) : "";
        String three = ISA_RESULT.get(2).getIsa_char() != null ? String.valueOf(ISA_RESULT.get(2).getIsa_char()) : "";
        String four = ISA_RESULT.get(3).getIsa_char() != null ? String.valueOf(ISA_RESULT.get(3).getIsa_char()) : "";

        alert.setContentText("ISA Chars:" + "\n"
                + (!one.isEmpty() ? ("1 - " + one + "\n") : "")
                + (!two.isEmpty() ? ("2 - " + two + "\n") : "")
                + (!three.isEmpty() ? ("3 - " + three + "\n") : "")
                + (!four.isEmpty() ? ("4 - " + four) : ""));
        alert.show();
    }
}
