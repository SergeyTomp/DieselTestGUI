package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.enums.ControlsService;
import javafx.beans.property.*;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.StandControlsService.StandControls.*;

@Module(value = {Device.MODBUS_FLOW, Device.MODBUS_STAND})
public class TestBenchSectionUpdateModel implements Updater {

    private BooleanProperty sectionStarted = new SimpleBooleanProperty();
    private BooleanProperty fanTurnOn = new SimpleBooleanProperty();
    private BooleanProperty rotationDirection = new SimpleBooleanProperty();
    private BooleanProperty pumpAutoMode = new SimpleBooleanProperty();
    private BooleanProperty pumpTurnOn = new SimpleBooleanProperty();
    private IntegerProperty targetRPM = new SimpleIntegerProperty();
    private IntegerProperty currentRPM = new SimpleIntegerProperty();
    private IntegerProperty tankOilLevel = new SimpleIntegerProperty();
    private DoubleProperty pressure = new SimpleDoubleProperty(4d);
    private DoubleProperty backFlowOilTemperature = new SimpleDoubleProperty();
    private DoubleProperty tankOilTemperature = new SimpleDoubleProperty();
    
    private boolean isPaused;
    private ControlsService controlsService;

    private ModbusMapStand pumpON;
    private ModbusMapStand pumpAuto;
    private ModbusMapStand fanON;
    private ModbusMapStand pressureValue;
    private ModbusMapStand oilValue;
    private ModbusMapStand temp_1Value;
    private ModbusMapStand temp_2Value;
    private ModbusMapStand targetRpmValue;
    private ModbusMapStand factRpmValue;
    private ModbusMapStand direction;
    private ModbusMapStand driveON;

    public BooleanProperty sectionStartedProperty() {
        return sectionStarted;
    }
    public BooleanProperty fanTurnOnProperty() {
        return fanTurnOn;
    }
    public BooleanProperty rotationDirectionProperty() {
        return rotationDirection;
    }
    public BooleanProperty pumpAutoModeProperty() {
        return pumpAutoMode;
    }
    public BooleanProperty pumpTurnOnProperty() {
        return pumpTurnOn;
    }
    public IntegerProperty targetRPMProperty() {
        return targetRPM;
    }
    public IntegerProperty currentRPMProperty() {
        return currentRPM;
    }
    public IntegerProperty tankOilLevelProperty() {
        return tankOilLevel;
    }
    public DoubleProperty pressureProperty() {
        return pressure;
    }
    public DoubleProperty backFlowOilTemperatureProperty() {
        return backFlowOilTemperature;
    }
    public DoubleProperty tankOilTemperatureProperty() {
        return tankOilTemperature;
    }

    public void setControlsService(ControlsService controlsService) {
        this.controlsService = controlsService;
    }

    @PostConstruct
    public void init() {

        controlsService.controlsChangeProperty().addListener((observableValue, oldValue, newValue) -> {

                isPaused = true;
                updateRegisters();
                isPaused = false;
        });
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if (isPaused) {
            return;
        }

        runBooleanRegister(driveON, sectionStarted);
        runBooleanRegister(fanON, fanTurnOn);
        runBooleanRegister(direction, rotationDirection);
        runDoubleRegister(pressureValue, pressure);
        runDoubleRegister(temp_1Value, tankOilTemperature);
        runDoubleRegister(temp_2Value, backFlowOilTemperature);
        runIntegerRegister(targetRpmValue, targetRPM);
        runIntegerRegister(factRpmValue, currentRPM);
        runIntegerRegister(oilValue, tankOilLevel);
        runPumpButtonRegisters(pumpAuto, pumpAutoMode, pumpON, pumpTurnOn);
    }


    private void runBooleanRegister(ModbusMapStand register, BooleanProperty valueProperty) {

        if (checkIgnoreFirstRead(register)) {
            return;
        }

        Boolean lastValue = (Boolean) register.getLastValue();

        if (lastValue != null) {
            valueProperty.setValue(lastValue);
        }

    }

    private void runIntegerRegister(ModbusMapStand register, IntegerProperty valueProperty) {

        if (checkIgnoreFirstRead(register)) {
            return;
        }

        Integer lastValue = (Integer) register.getLastValue();

        if (lastValue != null) {
            valueProperty.setValue(lastValue);
        }
    }

    private void runDoubleRegister(ModbusMapStand register, DoubleProperty valueProperty) {

        if (checkIgnoreFirstRead(register)) {
            return;
        }

        Double lastValue = (Double) register.getLastValue();

        if (lastValue != null) {
            valueProperty.setValue(lastValue);
        }
    }

    private void runPumpButtonRegisters(ModbusMapStand pumpAutoModeRegister, BooleanProperty pumpAutoMode, ModbusMapStand pumpTurnOnRegister, BooleanProperty pumpTurnOn) {

        if (checkIgnoreFirstRead(pumpAutoModeRegister, pumpTurnOnRegister)) {
            return;
        }

        Boolean pumpAutoModeLastValue = (Boolean) pumpAutoModeRegister.getLastValue();
        Boolean pumpTurnOnLastValue = (Boolean) pumpTurnOnRegister.getLastValue();

        if (pumpAutoModeLastValue != null) {
            pumpAutoMode.setValue(pumpAutoModeLastValue);
            if (!pumpAutoModeLastValue) {
                if (pumpTurnOnLastValue != null) {
                    pumpTurnOn.setValue(pumpTurnOnLastValue);
                }
            }
        }
    }

    private boolean checkIgnoreFirstRead(ModbusMapStand ... registers) {

        for (ModbusMapStand reg: registers) {
            if (reg.isFirstRead()) {
                reg.setFirstRead(false);
                return true;
            }
        }
        return false;
    }

    private void updateRegisters() {

        pumpON = PUMP_ON.getRegister();
        pumpAuto = PUMP_AUTO.getRegister();
        fanON = FAN_ON.getRegister();
        pressureValue = PRESSURE.getRegister();
        oilValue = OIL.getRegister();
        temp_1Value = TEMP_1.getRegister();
        temp_2Value = TEMP_1.getRegister();
        targetRpmValue = MAIN_TARGET_RPM.getRegister();
        factRpmValue = FACT_RPM.getRegister();
        direction = DRIVE_DIRECTION.getRegister();
        driveON = MAIN_DRIVE_ON.getRegister();
    }
}
