package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.beans.property.*;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM_4_CH;

@Module(value = {Device.MODBUS_FLOW, Device.MODBUS_STAND})
public class TestBenchSectionUpdateModel implements Updater {

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

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

    public TestBenchSectionUpdateModel(FirmwareVersion<FlowVersions> flowFirmwareVersion) {

        this.flowFirmwareVersion = flowFirmwareVersion;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        boolean isStandFMVersion = (flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH);

        runSyncWriteReadBooleanRegisters(isStandFMVersion ? RotationStandFM : Rotation, sectionStarted);

        runSyncWriteReadBooleanRegisters(isStandFMVersion ? FanTurnOnStandFM : FanTurnOn, fanTurnOn);

        runPressureRegister(isStandFMVersion ? Pressure1StandFM : Pressure1, pressure);

        runTemperatureRegister(isStandFMVersion ? Temperature1StandFM : Temperature1, tankOilTemperature);

        runTemperatureRegister(isStandFMVersion ? Temperature2StandFM : Temperature2, backFlowOilTemperature);

        runTargetRPMRegister(isStandFMVersion ? TargetRPMStandFM : TargetRPM, targetRPM);

        runRotationDirectionRegister(isStandFMVersion ? RotationDirectionStandFM : RotationDirection, rotationDirection);

        runPumpRegisters(isStandFMVersion ? PumpTurnOnStandFM : PumpTurnOn, isStandFMVersion ? PumpAutoModeStandFM : PumpAutoMode, pumpAutoMode, pumpTurnOn);

        runCurrentRPMRegister(isStandFMVersion ? CurrentRPMStandFM : CurrentRPM, currentRPM);

        runTankOilRegister(isStandFMVersion ? TankOilLevelStandFM : TankOilLevel, tankOilLevel);
    }

    private void runSyncWriteReadBooleanRegisters(ModbusMapStand register, BooleanProperty started) {

        Boolean registerLastValue = (Boolean) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (registerLastValue != null){
            started.setValue(registerLastValue);
        }
    }

    private void runPressureRegister(ModbusMapStand register, DoubleProperty pressure) {

        Double registerLastValue = (Double) register.getLastValue();

        if (registerLastValue != null) {
            pressure.setValue(registerLastValue);
        }
    }

    private void runTemperatureRegister(ModbusMapStand register, DoubleProperty oilTemperature){

        Double registerLastValue = (Double) register.getLastValue();

        if (registerLastValue != null) {
            oilTemperature.setValue(registerLastValue);
        }
    }

    private void runTargetRPMRegister(ModbusMapStand register, IntegerProperty targetRPM) {

        Integer targetRPMLastValue = (Integer) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (targetRPMLastValue != null) {
            targetRPM.setValue(targetRPMLastValue);
        }
    }

    private void runRotationDirectionRegister(ModbusMapStand register, BooleanProperty rotationDirection) {

        Boolean rotationDirectionLastValue = (Boolean) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (rotationDirectionLastValue != null) {
            rotationDirection.setValue(rotationDirectionLastValue);
        }
    }

    private void runPumpRegisters(ModbusMapStand pumpTurnOnRegister, ModbusMapStand pumpAutoModeRegister, BooleanProperty pumpAutoMode, BooleanProperty pumpTurnOn) {

        Boolean pumpTurnOnLastValue = (Boolean) pumpTurnOnRegister.getLastValue();
        Boolean pumpAutoModeLastValue = (Boolean) pumpAutoModeRegister.getLastValue();

        if (pumpAutoModeLastValue != null) {
            pumpAutoMode.setValue(pumpAutoModeLastValue);
            if (!pumpAutoModeLastValue) {
                if (pumpTurnOnLastValue != null) {
                    pumpTurnOn.setValue(pumpTurnOnLastValue);
                }
            }
        }
    }

    private void runCurrentRPMRegister(ModbusMapStand register, IntegerProperty currentRPM) {

        Integer currentRPMLastValue = (Integer) register.getLastValue();

        if (currentRPMLastValue != null) {
            currentRPM.setValue(currentRPMLastValue);
        }
    }

    private void runTankOilRegister(ModbusMapStand register, IntegerProperty tankOilLevel) {

        Integer tankOilLevelLastValue = (Integer) register.getLastValue();

        if (tankOilLevelLastValue != null) {
            tankOilLevel.setValue(tankOilLevelLastValue);
        }
    }
}
