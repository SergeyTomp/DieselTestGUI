package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.ChartTask;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.injectors.InjectorChannel;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.*;

public class InjectorSectionController {

    //TODO: delete after test
    @Autowired
    private TimerTasksManager timerTasksManager;

    @Autowired
    private ChartTask chartTask;

    @Autowired
    private VoltageController voltageController;

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
    private GridPane gridLedBeaker;

    @FXML
    private ToggleButton powerSwitch;

    @FXML
    private Label statusBoostULabelText;

    @FXML
    private Label statusBoostULabel;

    @FXML
    private StackPane stackPaneLed1;

    @FXML
    private StackPane stackPaneLed2;

    @FXML
    private StackPane stackPaneLed3;

    @FXML
    private StackPane stackPaneLed4;

    @FXML
    private StackPane stackPaneLed5;

    @FXML
    private StackPane stackPaneLed6;

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

    @FXML
    private AnchorPane ledBeaker1;

    @FXML
    private AnchorPane ledBeaker2;

    @FXML
    private AnchorPane ledBeaker3;

    @FXML
    private AnchorPane ledBeaker4;

    private boolean updateOSC;

    private SettingsController settingsController;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private ObservableList<LedController> ledControllers;

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

    public Spinner<Double> getFreqCurrentSignal() {
        return freqCurrentSignal;
    }

    public Spinner<Integer> getWidthCurrentSignal() {
        return widthCurrentSignal;
    }

    public ToggleButton getPowerSwitch() {
        return powerSwitch;
    }

    public boolean isUpdateOSC() {
        return updateOSC;
    }

    public void setUpdateOSC(boolean updateOSC) {
        this.updateOSC = updateOSC;
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

    public ObservableList<LedController> getLedControllers() {
        return ledControllers;
    }

    public List<LedController> activeControllers() {
        List<LedController> result = new ArrayList<>();
        for (LedController s : ledControllers) {
            if (s.isSelected()) result.add(s);
        }
        result.sort(Comparator.comparingInt(LedController::getNumber));
        return result;
    }

    public void setSettingsController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    @PostConstruct
    private void init() {

        //TODO: delete after test
//        ultimaModbusWriter.add(ModbusMapUltima.Ftime1, 0);
//        ultimaModbusWriter.add(ModbusMapUltima.GImpulsesPeriod, 60);
//        ultimaModbusWriter.add(ModbusMapUltima.FInjectorNumber1, 1);

        powerSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ultimaModbusWriter.add(ModbusMapUltima.Injectors_Running_En, true);
                timerTasksManager.start(chartTask);
            } else {
                ultimaModbusWriter.add(ModbusMapUltima.Injectors_Running_En, false);
                ultimaModbusWriter.add(ModbusMapUltima.FInjectorNumber1, 0xff);
                ultimaModbusWriter.add(ModbusMapUltima.Ftime1, 0);
                timerTasksManager.stop();
                voltageController.getData1().clear();
            }
        });

        ledBeaker1Controller.setNumber(1);
        ledBeaker2Controller.setNumber(2);
        ledBeaker3Controller.setNumber(3);
        ledBeaker4Controller.setNumber(4);

        ledControllers = FXCollections.observableArrayList(new LinkedList<>());

        ledControllers.add(ledBeaker1Controller);
        ledControllers.add(ledBeaker2Controller);
        ledControllers.add(ledBeaker3Controller);
        ledControllers.add(ledBeaker4Controller);

        setupFrequencySpinner();

        setupWidthSpinner();

        new LedParametersChanger(freqCurrentSignal.valueProperty(),
                piezoCoilToggleGroup.selectedToggleProperty(), new SimpleListProperty<>(ledControllers));

    }

    private void setupWidthSpinner() {
        widthCurrentSignal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3000, 300, 10));
    }

    private void setupFrequencySpinner() {

        freqCurrentSignal.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 50, 16.67, 0.01));

        StringConverter<Double> converter = freqCurrentSignal.getValueFactory().getConverter();

        freqCurrentSignal.getValueFactory().setConverter(new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return converter.toString(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return converter.fromString(string);
                } catch (RuntimeException ex) {
                    freqCurrentSignal.getValueFactory().setValue(0.49);
                    return freqCurrentSignal.getValue();
                }
            }
        });

        freqCurrentSignal.addEventHandler(MouseEvent.MOUSE_EXITED, event -> freqCurrentSignal.getParent().requestFocus());

        freqCurrentSignal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                freqCurrentSignal.increment(0);
        });

    }

    private class LedParametersChanger implements ChangeListener<Object> {

        private DoubleProperty frequencyProperty = new SimpleDoubleProperty();

        private ObjectProperty<Toggle> injectorTypeProperty = new SimpleObjectProperty<>();

        private ListProperty<LedController> ledControllersProperty = new SimpleListProperty<>();

        private ObjectProperty<InjectorChannel> injectorChannelProperty = new SimpleObjectProperty<>();

        private List<ModbusMapUltima> slotNumbersList = ModbusMapUltima.getSlotNumbersList();

        private List<ModbusMapUltima> slotPulsesList = ModbusMapUltima.getSlotPulsesList();

        private static final int OFF_COMMAND_NUMBER = 255;

        LedParametersChanger(ReadOnlyObjectProperty<Double> doubleObjectProperty,
                             ReadOnlyObjectProperty<Toggle> toggleReadOnlyObjectProperty,
                             ListProperty<LedController> ledProperty) {
            frequencyProperty.bind(doubleObjectProperty);
            injectorTypeProperty.bind(toggleReadOnlyObjectProperty);
            ledControllersProperty.bind(ledProperty);
            injectorChannelProperty.bind(settingsController.getComboInjectorConfig().getSelectionModel().selectedItemProperty());
            frequencyProperty.addListener(this);
            injectorTypeProperty.addListener(this);
            ledControllersProperty.get().forEach(s -> s.getLedBeaker().selectedProperty().addListener(this));
        }

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
            if (newValue instanceof Double) {
                if ((Double) newValue <= 50 && (Double) newValue >= 0.5)
                    sendLedRegisters();
            } else if (newValue instanceof Toggle)
                sendLedRegisters();
            else if (newValue instanceof Boolean) {
                if ((injectorChannelProperty.get() == InjectorChannel.SINGLE_CHANNEL && (Boolean) newValue)
                        || (injectorChannelProperty.get() == InjectorChannel.MULTI_CHANNEL))
                    sendLedRegisters();
            } else
                throw new RuntimeException("Wrong listener type!");
        }

        private void sendLedRegisters() {
            switchOffAll();

            writeInjectorTypeRegister();

            List<LedController> activeControllers = activeControllers();
            System.err.println("Active controllers: " + activeControllers);
            Iterator<LedController> activeControllersIterator = activeControllers.iterator();
            int activeLeds = activeControllers.size();
            System.err.println("activeLeds: " + activeLeds);
            double frequency = freqCurrentSignal.getValue();
            System.err.println("frequency: " + frequency);
            ultimaModbusWriter.add(ModbusMapUltima.GImpulsesPeriod, 1000 / frequency);
            if (activeLeds == 0) {
                return;
            }
            int step = (int) Math.round(1000 / (frequency * activeLeds));
            System.err.println("step: " + step);
            int impulseTime = 0;
            while (activeControllersIterator.hasNext()) {
                System.err.println("looping");
                int selectedChannel = activeControllersIterator.next().getNumber();
                int injectorChannel = injectorChannelProperty.get() == InjectorChannel.SINGLE_CHANNEL ? 1 : selectedChannel;
                ultimaModbusWriter.add(slotNumbersList.get(selectedChannel - 1), injectorChannel);
                ultimaModbusWriter.add(slotPulsesList.get(selectedChannel - 1), impulseTime);
                impulseTime += step;
            }
        }

        private void switchOffAll() {
            slotNumbersList.forEach((s) -> ultimaModbusWriter.add(s, OFF_COMMAND_NUMBER));
            slotPulsesList.forEach((s) -> ultimaModbusWriter.add(s, 0));
        }

        private void writeInjectorTypeRegister() {
            if (Objects.equals(coilRadioButton, injectorTypeProperty.get()))
                ultimaModbusWriter.add(ModbusMapUltima.Injector_type, 0);
            else if (Objects.equals(piezoRadioButton, injectorTypeProperty.get()))
                ultimaModbusWriter.add(ModbusMapUltima.Injector_type, 1);
            else if (Objects.equals(piezoDelphiRadioButton, injectorTypeProperty.get()))
                ultimaModbusWriter.add(ModbusMapUltima.Injector_type, 2);
            else
                throw new AssertionError("Coil or piezo buttons has not been set.");
        }
    }
}
