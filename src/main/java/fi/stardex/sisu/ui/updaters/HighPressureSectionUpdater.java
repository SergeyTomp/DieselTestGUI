package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.enums.RegActive.*;

@Module(value=Device.ULTIMA)
public class HighPressureSectionUpdater implements Updater {

    private HighPressureSectionController highPressureSectionController;
    private DataConverter dataConverter;
    private ObjectProperty<Integer> pressMultiplierProperty = new SimpleObjectProperty<>();


    public HighPressureSectionUpdater(HighPressureSectionController highPressureSectionController,
                                      AdditionalSectionController additionalSectionController,
                                      DataConverter dataConverter) {
        this.highPressureSectionController = highPressureSectionController;
        this.dataConverter = dataConverter;
        pressMultiplierProperty.bind(additionalSectionController.getSettingsController().pressMultiplierPropertyProperty());
    }

    @Override
    public void update() {
    }

    @Override
    public void run() {

        RegActive reg1paramActive = highPressureSectionController.getReg1paramActive();
        RegActive reg2paramActive = highPressureSectionController.getReg2paramActive();
        RegActive reg3paramActive = highPressureSectionController.getReg3paramActive();

        if(PressureReg1_PressFact.getLastValue() != null){
            Double pressure = (pressMultiplierProperty.get() * (Double) PressureReg1_PressFact.getLastValue());
//            if(reg1paramActive != PRESSURE){
//                highPressureSectionController.getPressReg1Spinner().getValueFactory().setValue(pressure.intValue());
//            }
            highPressureSectionController.getPressureLcd().setValue(pressure);
        }
        if(PressureReg1_DutyFact.getLastValue() != null && reg1paramActive != DUTY){
            highPressureSectionController.getDutyCycleReg1Spinner().getValueFactory().setValue(dataConverter.round((double)PressureReg1_DutyFact.getLastValue()));
        }
        if(PressureReg1_I_Fact.getLastValue() != null && reg1paramActive != CURRENT){
            highPressureSectionController.getCurrentReg1Spinner().getValueFactory().setValue(dataConverter.round((double)PressureReg1_I_Fact.getLastValue()));
        }
        if(PressureReg2_I_Fact.getLastValue() != null && reg2paramActive != CURRENT){
            highPressureSectionController.getCurrentReg2Spinner().getValueFactory().setValue(dataConverter.round((double)PressureReg2_I_Fact.getLastValue()));
        }
        if(PressureReg2_DutyFact.getLastValue() != null &&  reg2paramActive != DUTY){
            highPressureSectionController.getDutyCycleReg2Spinner().getValueFactory().setValue(dataConverter.round((double)PressureReg2_DutyFact.getLastValue()));
        }
        if(PressureReg3_I_Fact.getLastValue() != null && reg3paramActive != CURRENT){
            highPressureSectionController.getCurrentReg3Spinner().getValueFactory().setValue(dataConverter.round((double)PressureReg3_I_Fact.getLastValue()));
        }
        if(PressureReg3_DutyFact.getLastValue() != null && reg3paramActive != DUTY){
            highPressureSectionController.getDutyCycleReg3Spinner().getValueFactory().setValue(dataConverter.round((double)PressureReg3_DutyFact.getLastValue()));
        }
    }
}
