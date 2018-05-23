package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.charts.ChartTask;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
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
import java.util.LinkedList;
import java.util.List;

public class InjectorSectionController {

    //TODO: delete after test
    @Autowired
    private TimerTasksManager timerTasksManager;

    @Autowired
    private ChartTask chartTask;

    @Autowired
    private VoltageController voltageController;

    @Autowired
    private ModbusRegisterProcessor ultimaModbusWriter;

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

        ledControllers = FXCollections.observableArrayList(new LinkedList<>());

        ledControllers.add(ledBeaker1Controller);
        ledControllers.add(ledBeaker2Controller);
        ledControllers.add(ledBeaker3Controller);
        ledControllers.add(ledBeaker4Controller);

        LedParametersChanger ledParametersChanger = new LedParametersChanger(freqCurrentSignal.valueProperty(),
                piezoCoilToggleGroup.selectedToggleProperty(), new SimpleListProperty<>(ledControllers));

        setupFrequencySpinner();

        setupWidthSpinner();

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

        LedParametersChanger(ReadOnlyObjectProperty<Double> doubleObjectProperty, ReadOnlyObjectProperty<Toggle> toggleReadOnlyObjectProperty, ListProperty<LedController> ledProperty) {
            frequencyProperty.bind(doubleObjectProperty);
            injectorTypeProperty.bind(toggleReadOnlyObjectProperty);
            ledControllersProperty.bind(ledProperty);
            frequencyProperty.addListener(this);
            injectorTypeProperty.addListener(this);
            ledControllersProperty.get().forEach(s -> s.getLedBeaker().selectedProperty().addListener(this));
        }

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
            if (newValue instanceof Double) {
                if ((Double) newValue <= 50 && (Double) newValue >= 0.5)
                    //FIXME: при инициализации сразу отправляется значение frequency
                    System.err.println(newValue);
            } else if (newValue instanceof Toggle)
                System.err.println(newValue);
            else if (newValue instanceof Boolean)
                System.err.println(newValue);
            else throw new RuntimeException("Wrong listener type!");
        }
    }
}
