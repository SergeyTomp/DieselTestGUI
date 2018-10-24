package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class InjectorSectionController {

    @FXML private Spinner<Integer> widthCurrentSignalSpinner;

    @FXML private Spinner<Double> freqCurrentSignalSpinner;

    @FXML private RadioButton piezoRadioButton;

    @FXML private ToggleGroup piezoCoilToggleGroup;

    @FXML private RadioButton coilRadioButton;

    @FXML private RadioButton piezoDelphiRadioButton;

    @FXML private ToggleButton injectorSectionStartToggleButton;

    @FXML private Label widthLabel;

    @FXML private Label freqLabel;

    @FXML private Label statusBoostULabelText;

    @FXML private StackPane led1StackPane;

    @FXML private StackPane led2StackPane;

    @FXML private StackPane led3StackPane;

    @FXML private StackPane led4StackPane;

    @FXML private AnchorPane led1AnchorPane;

    @FXML private AnchorPane led2AnchorPane;

    @FXML private AnchorPane led3AnchorPane;

    @FXML private AnchorPane led4AnchorPane;

    @FXML private ToggleButton led1ToggleButton;

    @FXML private ToggleButton led2ToggleButton;

    @FXML private ToggleButton led3ToggleButton;

    @FXML private ToggleButton led4ToggleButton;

    private static final double LED_GAP = 10;

    private static final String LED_BLINK_ON = "ledBlink-on";

    private static final String LED_BLINK_OFF = "ledBlink-off";

    private I18N i18N;

    private Enabler enabler;

    private Logger logger = LoggerFactory.getLogger(InjectorSectionController.class);

    private TimerTasksManager timerTasksManager;

    private ComboBox<InjectorChannel> injectorsConfigComboBox;

    private SingleSelectionModel<InjectorChannel> injectorsConfigComboBoxSelectionModel;

    private DelayController delayController;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private ObservableList<ToggleButton> ledToggleButtons;

    private List<ToggleButton> activeLedToggleButtonsList = new ArrayList<>();

    private List<Integer> arrayNumbersOfActiveLedToggleButtons = new ArrayList<>();

    private ToggleGroup toggleGroup = new ToggleGroup();

    private LedParametersChangeListener ledParametersChangeListener;

    private List<Timeline> timeLinesList;
    private List<KeyFrame> keyFramesList;

    public Spinner<Integer> getWidthCurrentSignalSpinner() {
        return widthCurrentSignalSpinner;
    }

    public Spinner<Double> getFreqCurrentSignalSpinner() {
        return freqCurrentSignalSpinner;
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

    public ToggleButton getLed1ToggleButton() {
        return led1ToggleButton;
    }

    public ToggleButton getLed2ToggleButton() {
        return led2ToggleButton;
    }

    public ToggleButton getLed3ToggleButton() {
        return led3ToggleButton;
    }

    public ToggleButton getLed4ToggleButton() {
        return led4ToggleButton;
    }

    public ToggleButton getInjectorSectionStartToggleButton() {
        return injectorSectionStartToggleButton;
    }

    public List<Integer> getArrayNumbersOfActiveLedToggleButtons()
    {
        return arrayNumbersOfActiveLedToggleButtons;
    }

    public void setInjectorsConfigComboBox(ComboBox<InjectorChannel> injectorsConfigComboBox) {
        this.injectorsConfigComboBox = injectorsConfigComboBox;
    }

    public void setEnabler(Enabler enabler) {
        this.enabler = enabler;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setTimerTasksManager(TimerTasksManager timerTasksManager) {
        this.timerTasksManager = timerTasksManager;
    }

    public void setDelayController(DelayController delayController) {
        this.delayController = delayController;
    }

    public synchronized List<ToggleButton> getActiveLedToggleButtonsList() {
        activeLedToggleButtonsList.clear();
        for (ToggleButton s : ledToggleButtons) {
            if (s.isSelected()) activeLedToggleButtonsList.add(s);
        }
        activeLedToggleButtonsList.sort(Comparator.comparingInt(InjectorSectionController.this::getNumber));
        return activeLedToggleButtonsList;
    }

    public void fillArrayNumbersOfActiveLedToggleButtons() {
        arrayNumbersOfActiveLedToggleButtons.clear();
        getActiveLedToggleButtonsList().forEach(e -> arrayNumbersOfActiveLedToggleButtons.add(getNumber(e)));
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init() {

        injectorsConfigComboBoxSelectionModel = injectorsConfigComboBox.getSelectionModel();

        bindingI18N();

        setupLedControllers();

        setupInjectorConfigComboBox();

        setupSpinners();

        ledParametersChangeListener = new LedParametersChangeListener();

        new PowerButtonChangeListener();

        led1StackPane.widthProperty().addListener(new StackPaneWidthListener(led1AnchorPane, led1StackPane.heightProperty()));
        led2StackPane.widthProperty().addListener(new StackPaneWidthListener(led2AnchorPane, led2StackPane.heightProperty()));
        led3StackPane.widthProperty().addListener(new StackPaneWidthListener(led3AnchorPane, led3StackPane.heightProperty()));
        led4StackPane.widthProperty().addListener(new StackPaneWidthListener(led4AnchorPane, led4StackPane.heightProperty()));

        timeLinesList = new ArrayList<>();
        keyFramesList = new ArrayList<>();

        for(int i = 0; i < 4; i++){

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

    private void bindingI18N() {
        piezoRadioButton.textProperty().bind(i18N.createStringBinding("injSection.radio.piezo"));
        coilRadioButton.textProperty().bind(i18N.createStringBinding("injSection.radio.coil"));
        piezoDelphiRadioButton.textProperty().bind(i18N.createStringBinding("injSection.radio.piezoDelphi"));
        widthLabel.textProperty().bind(i18N.createStringBinding("injSection.label.width"));
        freqLabel.textProperty().bind(i18N.createStringBinding("injSection.label.freq"));
    }

    private void setupLedControllers() {

        setNumber(1, led1ToggleButton);
        setNumber(2, led2ToggleButton);
        setNumber(3, led3ToggleButton);
        setNumber(4, led4ToggleButton);

        setBlinkingStatus(led1ToggleButton, false);
        setBlinkingStatus(led2ToggleButton, false);
        setBlinkingStatus(led3ToggleButton, false);
        setBlinkingStatus(led4ToggleButton, false);

        ledToggleButtons = FXCollections.observableArrayList(new LinkedList<>());

        ledToggleButtons.add(led1ToggleButton);
        ledToggleButtons.add(led2ToggleButton);
        ledToggleButtons.add(led3ToggleButton);
        ledToggleButtons.add(led4ToggleButton);

    }

    private void setupInjectorConfigComboBox() {

        setToggleGroupToLeds(injectorsConfigComboBoxSelectionModel.getSelectedItem() == InjectorChannel.SINGLE_CHANNEL ? toggleGroup : null);

        injectorsConfigComboBoxSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setToggleGroupToLeds(newValue == InjectorChannel.SINGLE_CHANNEL ? toggleGroup : null));
    }

    private void setupSpinners() {

        widthCurrentSignalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(WIDTH_CURRENT_SIGNAL_SPINNER_MIN,
                WIDTH_CURRENT_SIGNAL_SPINNER_MAX,
                WIDTH_CURRENT_SIGNAL_SPINNER_INIT,
                WIDTH_CURRENT_SIGNAL_SPINNER_STEP));

        freqCurrentSignalSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(FREQ_CURRENT_SIGNAL_SPINNER_MIN,
                FREQ_CURRENT_SIGNAL_SPINNER_MAX,
                FREQ_CURRENT_SIGNAL_SPINNER_INIT,
                FREQ_CURRENT_SIGNAL_SPINNER_STEP));

        SpinnerManager.setupSpinner(freqCurrentSignalSpinner,
                FREQ_CURRENT_SIGNAL_SPINNER_INIT,
                FREQ_CURRENT_SIGNAL_SPINNER_FAKE,
                new CustomTooltip(),
                new SpinnerValueObtainer(FREQ_CURRENT_SIGNAL_SPINNER_INIT));

    }

    private void setToggleGroupToLeds(ToggleGroup toggleGroup) {
        ledToggleButtons.forEach(s -> s.setToggleGroup(toggleGroup));
    }

    private class LedParametersChangeListener implements ChangeListener<Object> {

        private ReadOnlyObjectProperty<Toggle> injectorTypeProperty;

        private ReadOnlyObjectProperty<InjectorChannel> injectorChannelProperty;

        private List<ModbusMapUltima> slotNumbersList = getSlotNumbersList();

        private List<ModbusMapUltima> slotPulsesList = getSlotPulsesList();

        LedParametersChangeListener() {

            injectorTypeProperty = piezoCoilToggleGroup.selectedToggleProperty();
            injectorChannelProperty = injectorsConfigComboBoxSelectionModel.selectedItemProperty();
            freqCurrentSignalSpinner.valueProperty().addListener(this);
            injectorTypeProperty.addListener(this);
            ledToggleButtons.forEach(s -> s.selectedProperty().addListener(this));

        }

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {

            switchOffAll();

            if (newValue instanceof Double) {
                if (isValid((Double) newValue))
                    sendLedRegisters();
            } else if (newValue instanceof Toggle) {
                if (newValue == piezoDelphiRadioButton) {
                    enabler.disableNode(true, led2ToggleButton, led3ToggleButton, led4ToggleButton);
                    injectorsConfigComboBoxSelectionModel.select(InjectorChannel.SINGLE_CHANNEL);
                    led1ToggleButton.setSelected(true);
                } else {
                    enabler.disableNode(false, led2ToggleButton, led3ToggleButton, led4ToggleButton);
                    sendLedRegisters();
                }
            } else if (newValue instanceof Boolean) {
                if (isValid((Boolean) newValue))
                    sendLedRegisters();
            } else
                throw new RuntimeException("Wrong listener type!");

        }

        private boolean isValid(Double newValue) {
            return (newValue <= 50 && newValue >= 0.5) && newValue != FREQ_CURRENT_SIGNAL_SPINNER_FAKE;
        }

        private boolean isValid(Boolean newValue) {
            return (newValue && (injectorChannelProperty.get() == InjectorChannel.SINGLE_CHANNEL || injectorTypeProperty.get() == piezoDelphiRadioButton))
                    || (injectorChannelProperty.get() == InjectorChannel.MULTI_CHANNEL);
        }

        private void sendLedRegisters() {

            writeInjectorTypeRegister();

            List<ToggleButton> activeControllers = getActiveLedToggleButtonsList();

            Iterator<ToggleButton> activeControllersIterator = activeControllers.iterator();

            int activeLeds = activeControllers.size();

            delayController.showAttentionLabel(activeLeds > 1);

            double frequency = freqCurrentSignalSpinner.getValue();

            ultimaModbusWriter.add(GImpulsesPeriod, 1000 / frequency);

            if (activeLeds == 0)
                return;

            int step = (int) Math.round(1000 / (frequency * activeLeds));

            int impulseTime = 0;

            while (activeControllersIterator.hasNext()) {

                int selectedChannel = getNumber(activeControllersIterator.next());
                int injectorChannel = injectorChannelProperty.get() == InjectorChannel.SINGLE_CHANNEL ? 1 : selectedChannel;
                ultimaModbusWriter.add(slotNumbersList.get(selectedChannel - 1), injectorChannel);
                ultimaModbusWriter.add(slotPulsesList.get(selectedChannel - 1), impulseTime);
                impulseTime += step;

            }

        }

        private void switchOffAll() {

            slotNumbersList.forEach((s) -> ultimaModbusWriter.add(s, 255));
            slotPulsesList.forEach((s) -> ultimaModbusWriter.add(s, 65535));

        }

        private void writeInjectorTypeRegister() {

            if (Objects.equals(coilRadioButton, injectorTypeProperty.get()))
                ultimaModbusWriter.add(Injector_type, 0);
            else if (Objects.equals(piezoRadioButton, injectorTypeProperty.get()))
                ultimaModbusWriter.add(Injector_type, 1);
            else if (Objects.equals(piezoDelphiRadioButton, injectorTypeProperty.get()))
                ultimaModbusWriter.add(Injector_type, 2);
            else
                throw new AssertionError("Coil or piezo buttons have not been changeFlow.");

        }

    }

    private class PowerButtonChangeListener implements ChangeListener<Boolean> {

        PowerButtonChangeListener() {
            injectorSectionStartToggleButton.selectedProperty().addListener(this);
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

            if (newValue) {
                fillArrayNumbersOfActiveLedToggleButtons();
                ledToggleButtons.forEach(l -> {if(l.isSelected()){ledBlinkStart(l);}});
                ultimaModbusWriter.add(Injectors_Running_En, true);
                ledParametersChangeListener.sendLedRegisters();
                // FIXME: throws NPE if there is no connection
                // при коннекте должен строиться хотя бы нулевой график
                timerTasksManager.start();
                enabler.disableVAP(true);

            } else {
                ledToggleButtons.forEach(l -> {
                    if(l.isSelected()){
                        ledBlinkStop(l);
                    }
                });
                ultimaModbusWriter.add(Injectors_Running_En, false);
                ledParametersChangeListener.switchOffAll();
                timerTasksManager.stop();
                enabler.disableVAP(false);

            }

        }

    }

    private class StackPaneWidthListener implements ChangeListener<Number>{

        final AnchorPane ledAnchorPane;
        final ReadOnlyDoubleProperty heightProperty;

        public StackPaneWidthListener(AnchorPane ledAnchorPane, ReadOnlyDoubleProperty heightProperty){
            this.ledAnchorPane = ledAnchorPane;
            this.heightProperty = heightProperty;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double height = heightProperty.getValue();
            if (newValue.doubleValue() > 75) {
                if(height > newValue.doubleValue()){
                    ledAnchorPane.setPrefWidth(newValue.doubleValue() * 0.7 - LED_GAP);
                    ledAnchorPane.setPrefHeight(newValue.doubleValue() * 0.7 - LED_GAP);
                }
                else {
                    ledAnchorPane.setPrefWidth(height * 0.8 - LED_GAP);
                    ledAnchorPane.setPrefHeight(height * 0.8 - LED_GAP);
                }

            } else {
                ledAnchorPane.setPrefWidth(80);
                ledAnchorPane.setPrefHeight(80);
            }
        }
    }

    private int getNumber(ToggleButton ledToggleButton) {
        return Integer.parseInt(ledToggleButton.getText());
    }

    private void setNumber(int number, ToggleButton ledToggleButton) {
        ledToggleButton.setText(String.valueOf(number));
    }

    private void ledBlinkStart(ToggleButton ledToggleButton) {
        if (ledToggleButton.isSelected()) {
            setBlinkingStatus(ledToggleButton, true);
            timeLinePlay(ledToggleButton);
        }
    }

    private void ledBlinkStop(ToggleButton ledToggleButton) {
//        if (!piezoDelphiMode) {
            if (ledToggleButton.isSelected()) {
                setBlinkingStatus(ledToggleButton, false);
                ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
                ledToggleButton.setDisable(false);
                timeLineStop(ledToggleButton);
            } else {
                setBlinkingStatus(ledToggleButton, false);
                ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
                ledToggleButton.setDisable(false);
                timeLineStop(ledToggleButton);
            }
//        }
    }

    private void setBlinkingStatus(ToggleButton ledBeakerController, boolean blinking){
        ledBeakerController.setUserData(blinking);
    }

    private boolean getBlinkingStatus(ToggleButton ledBeakerController){
        return (boolean)ledBeakerController.getUserData();
    }

    private KeyFrame makeKeyFrame(ToggleButton ledToggleButton){
        return new KeyFrame(Duration.millis(500), event -> {
            if (InjectorSectionController.this.getBlinkingStatus(ledToggleButton)) {
                ledToggleButton.getStyleClass().set(2, LED_BLINK_OFF);
                InjectorSectionController.this.setBlinkingStatus(ledToggleButton, false);
            } else {
                ledToggleButton.getStyleClass().set(2, LED_BLINK_ON);
                InjectorSectionController.this.setBlinkingStatus(ledToggleButton, true);
            }
        });
    }

    private void timeLinePlay(ToggleButton ledToggleButton){
        timeLinesList.get(getNumber(ledToggleButton) - 1).play();
    }

    private void timeLineStop(ToggleButton ledToggleButton){
        timeLinesList.get(getNumber(ledToggleButton) - 1).stop();
    }

    private void getLoggingInjectorSelection(Boolean value, ToggleButton ledController) {
        String s = String.format("LedBeaker %s selected: %s", ledController.getText(), value);
        logger.info(s);
    }
}
