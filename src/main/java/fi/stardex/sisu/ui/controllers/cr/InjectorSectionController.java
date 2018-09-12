package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.DelayController;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
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

    @FXML private Label labelWidth;

    @FXML private Label labelFreq;

    @FXML private Label statusBoostULabelText;

    @FXML private StackPane stackPaneLed1;

    @FXML private StackPane stackPaneLed2;

    @FXML private StackPane stackPaneLed3;

    @FXML private StackPane stackPaneLed4;

    @FXML private LedController ledBeaker1Controller;

    @FXML private LedController ledBeaker2Controller;

    @FXML private LedController ledBeaker3Controller;

    @FXML private LedController ledBeaker4Controller;

    private static final double LED_GAP = 10;

    private I18N i18N;

    private Enabler enabler;

    private Logger logger = LoggerFactory.getLogger(InjectorSectionController.class);

    private TimerTasksManager timerTasksManager;

    private ComboBox<InjectorChannel> injectorsConfigComboBox;

    private SingleSelectionModel<InjectorChannel> injectorsConfigComboBoxSelectionModel;

    private DelayController delayController;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private ObservableList<LedController> ledControllers;

    private List<LedController> activeControllers = new ArrayList<>();

    private List<Integer> arrayNumbersOfActiveControllers = new ArrayList<>();

    private ToggleGroup toggleGroup = new ToggleGroup();

    private LedParametersChangeListener ledParametersChangeListener;

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

    public ToggleButton getInjectorSectionStartToggleButton() {
        return injectorSectionStartToggleButton;
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

    public synchronized List<LedController> getActiveControllers() {
        activeControllers.clear();
        for (LedController s : ledControllers) {
            if (s.isSelected()) activeControllers.add(s);
        }
        activeControllers.sort(Comparator.comparingInt(LedController::getNumber));
        return activeControllers;
    }

    public List<Integer> getArrayNumbersOfActiveControllers() {
        arrayNumbersOfActiveControllers.clear();
        getActiveControllers().forEach(e -> arrayNumbersOfActiveControllers.add(e.getNumber()));
        return arrayNumbersOfActiveControllers;
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

        stackPaneLed1.widthProperty().addListener(new StackPaneWidthListener(ledBeaker1Controller.getAnchorPaneLed(), stackPaneLed1.heightProperty()));
        stackPaneLed2.widthProperty().addListener(new StackPaneWidthListener(ledBeaker2Controller.getAnchorPaneLed(), stackPaneLed2.heightProperty()));
        stackPaneLed3.widthProperty().addListener(new StackPaneWidthListener(ledBeaker3Controller.getAnchorPaneLed(), stackPaneLed3.heightProperty()));
        stackPaneLed4.widthProperty().addListener(new StackPaneWidthListener(ledBeaker4Controller.getAnchorPaneLed(), stackPaneLed4.heightProperty()));


    }

    private void bindingI18N() {
        piezoRadioButton.textProperty().bind(i18N.createStringBinding("injSection.radio.piezo"));
        coilRadioButton.textProperty().bind(i18N.createStringBinding("injSection.radio.coil"));
        piezoDelphiRadioButton.textProperty().bind(i18N.createStringBinding("injSection.radio.piezoDelphi"));
        labelWidth.textProperty().bind(i18N.createStringBinding("injSection.label.width"));
        labelFreq.textProperty().bind(i18N.createStringBinding("injSection.label.freq"));
    }

    private void setupLedControllers() {

        ledBeaker1Controller.setNumber(1);
        ledBeaker2Controller.setNumber(2);
        ledBeaker3Controller.setNumber(3);
        ledBeaker4Controller.setNumber(4);

        ledControllers = FXCollections.observableArrayList(new LinkedList<>());

        ledControllers.add(ledBeaker1Controller);
        ledControllers.add(ledBeaker2Controller);
        ledControllers.add(ledBeaker3Controller);
        ledControllers.add(ledBeaker4Controller);

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
        ledControllers.forEach(s -> s.getLedBeaker().setToggleGroup(toggleGroup));
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
            ledControllers.forEach(s -> s.getLedBeaker().selectedProperty().addListener(this));

        }

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {

            switchOffAll();

            if (newValue instanceof Double) {
                if (isValid((Double) newValue))
                    sendLedRegisters();
            } else if (newValue instanceof Toggle) {
                if (newValue == piezoDelphiRadioButton) {
                    enabler.disableAllLedsExceptFirst(true);
                    injectorsConfigComboBoxSelectionModel.select(InjectorChannel.SINGLE_CHANNEL);
                    ledBeaker1Controller.getLedBeaker().setSelected(true);
                } else {
                    enabler.disableAllLedsExceptFirst(false);
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

            List<LedController> activeControllers = getActiveControllers();

            Iterator<LedController> activeControllersIterator = activeControllers.iterator();

            int activeLeds = activeControllers.size();

            delayController.showAttentionLabel(activeLeds > 1);

            double frequency = freqCurrentSignalSpinner.getValue();

            ultimaModbusWriter.add(GImpulsesPeriod, 1000 / frequency);

            if (activeLeds == 0)
                return;

            int step = (int) Math.round(1000 / (frequency * activeLeds));

            int impulseTime = 0;

            while (activeControllersIterator.hasNext()) {

                int selectedChannel = activeControllersIterator.next().getNumber();
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

                ultimaModbusWriter.add(Injectors_Running_En, true);
                ledParametersChangeListener.sendLedRegisters();
                // FIXME: throws NPE if there is no connection
                // при коннекте должен строиться хотя бы нулевой график
                timerTasksManager.start();
                enabler.disableVAP(true);

            } else {

                ultimaModbusWriter.add(Injectors_Running_En, false);
                ledParametersChangeListener.switchOffAll();
                timerTasksManager.stop();
                enabler.disableVAP(false);

            }

        }

    }

    private class StackPaneWidthListener implements ChangeListener<Number>{

        final AnchorPane ledBeaker;
        final ReadOnlyDoubleProperty heightProperty;

        public StackPaneWidthListener(AnchorPane ledBeaker, ReadOnlyDoubleProperty heightProperty){
            this.ledBeaker = ledBeaker;
            this.heightProperty = heightProperty;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double height = heightProperty.getValue();
            if (newValue.doubleValue() > 75) {
                if(height > newValue.doubleValue()){
                    ledBeaker.setPrefWidth(newValue.doubleValue() * 0.7 - LED_GAP);
                    ledBeaker.setPrefHeight(newValue.doubleValue() * 0.7 - LED_GAP);
                }
                else {
                    ledBeaker.setPrefWidth(height * 0.8 - LED_GAP);
                    ledBeaker.setPrefHeight(height * 0.8 - LED_GAP);
                }

            } else {
                ledBeaker.setPrefWidth(80);
                ledBeaker.setPrefHeight(80);
            }
        }
    }
}
