package fi.stardex.sisu.registers;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.util.enums.Controls;
import fi.stardex.sisu.util.enums.ControlsService;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.Set;

import static fi.stardex.sisu.registers.StandControlsService.StandControls.*;
import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;

public class StandControlsService implements ControlsService {

    private FirmwareVersion<StandVersions> standFirmwareVersion;
    private FirmwareVersion<FlowVersions> flowFirmwareVersion;
    private GUI_TypeModel gui_typeModel;
    private Set<? extends Controls> controlSet;
    private ObjectProperty<Object> controlsChangeProperty = new SimpleObjectProperty<>();

    public void setStandFirmwareVersion(FirmwareVersion<StandVersions> standFirmwareVersion) {
        this.standFirmwareVersion = standFirmwareVersion;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    @PostConstruct
    public void init() {

        flowFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> setControls());
        standFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> setControls());
        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> setControls());
    }

    public enum StandControls implements Controls {

        VERSION, PUMP_ON, PUMP_AUTO, FAN_ON, PRESSURE, OIL, TEMP_1, TEMP_2, MAIN_TARGET_RPM, FACT_RPM, DRIVE_DIRECTION, MAIN_DRIVE_ON, SLAVE_DRIVE_ON, SLAVE_TARGET_RPM;

        private ModbusMapStand register;

        @Override
        public void setRegister(ModbusMap register) {
            this.register = (ModbusMapStand)register;
        }

        @Override
        public ModbusMapStand getRegister() {
            return register;
        }
    }

    @Override
    public ObjectProperty<Object> controlsChangeProperty() {
        return controlsChangeProperty;
    }

    @Override
    public Set<? extends Controls> getControls() {

        return controlSet;
    }

    private void setControls() {

        switch (flowFirmwareVersion.getVersions()) {

            case STAND_FM:
            case STAND_FM_4_CH:
                setStandFmControls();
                break;
            default:
                setStandControls();
                break;
        }
        controlSet = EnumSet.allOf(StandControls.class);
        controlsChangeProperty.setValue(new Object());
    }

    private void setStandControls() {

        VERSION.setRegister(FirmwareVersion);
        PUMP_ON.setRegister(PumpTurnOn);
        PUMP_AUTO.setRegister(PumpAutoMode);
        FAN_ON.setRegister(FanTurnOn);
        PRESSURE.setRegister(Pressure1);
        OIL.setRegister(TankOilLevel);
        TEMP_1.setRegister(Temperature1);
        TEMP_2.setRegister(Temperature2);
        SLAVE_DRIVE_ON.setRegister(Rotation);
        SLAVE_TARGET_RPM.setRegister(TargetRPM);

        switch (standFirmwareVersion.getVersions()) {

            case STAND:
            case UNKNOWN:
            case NO_VERSION:

                setStandDriveControls();
                break;
            case STAND_FORTE:

                switch (gui_typeModel.guiTypeProperty().get()) {

                    case CR_Inj:
                    case HEUI:
                        setStandDriveControls();
                        break;
                    case CR_Pump:
                    case UIS:
                        setForteDriveControls();
                }
            break;
        }
    }

    private void setStandDriveControls() {

        MAIN_TARGET_RPM.setRegister(TargetRPM);
        FACT_RPM.setRegister(CurrentRPM);
        DRIVE_DIRECTION.setRegister(RotationDirection);
        MAIN_DRIVE_ON.setRegister(Rotation);
    }

    private void setForteDriveControls() {

        MAIN_TARGET_RPM.setRegister(TargetRPM_Forte);
        FACT_RPM.setRegister(CurrentRPM_Forte);
        DRIVE_DIRECTION.setRegister(RotationDirection_Forte);
        MAIN_DRIVE_ON.setRegister(Rotation_Forte);
    }

    private void setStandFmControls() {

        VERSION.setRegister(FirmwareVersion);
        PUMP_ON.setRegister(PumpTurnOnStandFM);
        PUMP_AUTO.setRegister(PumpAutoModeStandFM);
        FAN_ON.setRegister(FanTurnOnStandFM);
        PRESSURE.setRegister(Pressure1StandFM);
        OIL.setRegister(TankOilLevelStandFM);
        TEMP_1.setRegister(Temperature1StandFM);
        TEMP_2.setRegister(Temperature2StandFM);
        MAIN_TARGET_RPM.setRegister(TargetRPMStandFM);
        SLAVE_TARGET_RPM.setRegister(TargetRPM);
        MAIN_DRIVE_ON.setRegister(RotationStandFM);
        SLAVE_DRIVE_ON.setRegister(Rotation);
        FACT_RPM.setRegister(CurrentRPMStandFM);
        DRIVE_DIRECTION.setRegister(RotationDirectionStandFM);
    }
}

