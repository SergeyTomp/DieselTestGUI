package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

public class InjectorSectionController {

    private Logger logger = LoggerFactory.getLogger(InjectorSectionController.class);

    private TimerTasksManager timerTasksManager;

    @FXML
    private Spinner<Integer> widthCurrentSignal;

    @FXML
    private Spinner<Double> freqCurrentSignal;

    @FXML
    private RadioButton piezoRadioButton;

    @FXML
    private ToggleGroup piezoCoilToggleGroup;

    @FXML
    private RadioButton coilRadioButton;

    @FXML
    private RadioButton piezoDelphiRadioButton;

    @FXML
    private ToggleButton powerSwitch;

    @FXML
    private Label statusBoostULabelText;

    @FXML
    private ProgressBar switcherProgressBar;

    @FXML
    private LedController ledBeaker1Controller;

    @FXML
    private LedController ledBeaker2Controller;

    @FXML
    private LedController ledBeaker3Controller;

    @FXML
    private LedController ledBeaker4Controller;

    private SettingsController settingsController;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private ObservableList<LedController> ledControllers;

    private List<LedController> activeControllers = new ArrayList<>();

    private List<Integer> arrayNumbersOfActiveControllers = new ArrayList<>();

    private ToggleGroup toggleGroup = new ToggleGroup();

    private LedParametersChangeListener ledParametersChangeListener;

    public Spinner<Integer> getWidthCurrentSignal() {
        return widthCurrentSignal;
    }

    public Spinner<Double> getFreqCurrentSignal() {
        return freqCurrentSignal;
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

    public ToggleButton getPowerSwitch() {
        return powerSwitch;
    }

    public void setSettingsController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void setTimerTasksManager(TimerTasksManager timerTasksManager) {
        this.timerTasksManager = timerTasksManager;
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

    @PostConstruct
    private void init() {

        ledBeaker1Controller.setNumber(1);
        ledBeaker2Controller.setNumber(2);
        ledBeaker3Controller.setNumber(3);
        ledBeaker4Controller.setNumber(4);

        ledControllers = FXCollections.observableArrayList(new LinkedList<>());

        ledControllers.add(ledBeaker1Controller);
        ledControllers.add(ledBeaker2Controller);
        ledControllers.add(ledBeaker3Controller);
        ledControllers.add(ledBeaker4Controller);

        setupInjectorConfigComboBox();

        widthCurrentSignal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(120, 15500, 300, 10));

        freqCurrentSignal.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 50, 16.67, 0.01));

        SpinnerManager.setupSpinner(freqCurrentSignal, 16.67, 16.671, new CustomTooltip(), new SpinnerValueObtainer(16.67));

        ledParametersChangeListener = new LedParametersChangeListener();

        new PowerButtonChangeListener();

    }

    private void setupInjectorConfigComboBox() {

        setToggleGroupToLeds(settingsController.getInjectorsConfigComboBox().getSelectionModel().getSelectedItem() == InjectorChannel.SINGLE_CHANNEL ? toggleGroup : null);

        settingsController.getInjectorsConfigComboBox().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setToggleGroupToLeds(newValue == InjectorChannel.SINGLE_CHANNEL ? toggleGroup : null));
    }

    private void setToggleGroupToLeds(ToggleGroup toggleGroup) {
        ledControllers.forEach(s -> s.getLedBeaker().setToggleGroup(toggleGroup));
    }

    private class LedParametersChangeListener implements ChangeListener<Object> {

        private ReadOnlyObjectProperty<Toggle> injectorTypeProperty;

        private ReadOnlyObjectProperty<InjectorChannel> injectorChannelProperty;

        private List<ModbusMapUltima> slotNumbersList = getSlotNumbersList();

        private List<ModbusMapUltima> slotPulsesList = getSlotPulsesList();

        private static final int OFF_COMMAND_NUMBER = 255;

        private static final int OFF_COMMAND_NUMBER_PULSE_TIME = 65535;

        LedParametersChangeListener() {
            injectorTypeProperty = piezoCoilToggleGroup.selectedToggleProperty();
            injectorChannelProperty = settingsController.getInjectorsConfigComboBox().getSelectionModel().selectedItemProperty();
            freqCurrentSignal.valueProperty().addListener(this);
            injectorTypeProperty.addListener(this);
            ledControllers.forEach(s -> s.getLedBeaker().selectedProperty().addListener(this));
        }

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
            switchOffAll();
            if (newValue instanceof Double) {
                if (((Double) newValue <= 50 && (Double) newValue >= 0.5) && (Double) newValue != 16.671) {
                    sendLedRegisters();
                }
            } else if (newValue instanceof Toggle) {
                if (newValue == piezoDelphiRadioButton) {
                    disableLedsExceptFirst(true);
                    settingsController.getInjectorsConfigComboBox().getSelectionModel().select(InjectorChannel.SINGLE_CHANNEL);
                    ledBeaker1Controller.getLedBeaker().setSelected(true);
                } else {
                    disableLedsExceptFirst(false);
                    sendLedRegisters();
                }
            } else if (newValue instanceof Boolean) {
                if ((((Boolean) newValue) && (injectorChannelProperty.get() == InjectorChannel.SINGLE_CHANNEL || injectorTypeProperty.get() == piezoDelphiRadioButton))
                        || (injectorChannelProperty.get() == InjectorChannel.MULTI_CHANNEL))
                    sendLedRegisters();
            } else
                throw new RuntimeException("Wrong listener type!");
        }

        private void sendLedRegisters() {

            writeInjectorTypeRegister();

            List<LedController> activeControllers = getActiveControllers();
            Iterator<LedController> activeControllersIterator = activeControllers.iterator();
            int activeLeds = activeControllers.size();
            double frequency = freqCurrentSignal.getValue();
            ultimaModbusWriter.add(GImpulsesPeriod, 1000 / frequency);
            if (activeLeds == 0) {
                return;
            }
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
            slotNumbersList.forEach((s) -> ultimaModbusWriter.add(s, OFF_COMMAND_NUMBER));
            slotPulsesList.forEach((s) -> ultimaModbusWriter.add(s, OFF_COMMAND_NUMBER_PULSE_TIME));
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

        private void disableLedsExceptFirst(boolean disable) {
            ledControllers.get(1).setDisable(disable);
            ledControllers.get(2).setDisable(disable);
            ledControllers.get(3).setDisable(disable);
        }
    }

    private class PowerButtonChangeListener implements ChangeListener<Boolean> {

        PowerButtonChangeListener() {
            powerSwitch.selectedProperty().addListener(this);
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                ultimaModbusWriter.add(Injectors_Running_En, true);
                ledParametersChangeListener.sendLedRegisters();
                // FIXME: throws NPE if there is no connection
                // при коннекте должен строиться хотя бы нулевой график
                timerTasksManager.start();
                disableInjectorSectionLedsAndToggleGroup(true);
            } else {
                ultimaModbusWriter.add(Injectors_Running_En, false);
                ledParametersChangeListener.switchOffAll();
                timerTasksManager.stop();
                disableInjectorSectionLedsAndToggleGroup(false);
            }
        }

        private void disableInjectorSectionLedsAndToggleGroup(boolean disabled) {
            coilRadioButton.setDisable(disabled);
            piezoRadioButton.setDisable(disabled);
            piezoDelphiRadioButton.setDisable(disabled);
            ledBeaker1Controller.getLedBeaker().setDisable(disabled);
            ledBeaker2Controller.getLedBeaker().setDisable(disabled);
            ledBeaker3Controller.getLedBeaker().setDisable(disabled);
            ledBeaker4Controller.getLedBeaker().setDisable(disabled);
        }
    }
}
