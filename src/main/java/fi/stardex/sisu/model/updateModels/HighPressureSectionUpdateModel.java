package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.states.PressureSensorModel;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.property.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.converters.DataConverter.round;
import static fi.stardex.sisu.util.enums.RegActive.CURRENT;
import static fi.stardex.sisu.util.enums.RegActive.DUTY;

@Module(value= Device.ULTIMA)
public class HighPressureSectionUpdateModel implements Updater {

    private PressureSensorModel pressureSensorModel;
    private DoubleProperty current_1 = new SimpleDoubleProperty();
    private DoubleProperty duty_1 = new SimpleDoubleProperty();
    private DoubleProperty current_2 = new SimpleDoubleProperty();
    private DoubleProperty duty_2 = new SimpleDoubleProperty();
    private DoubleProperty current_3 = new SimpleDoubleProperty();
    private DoubleProperty duty_3 = new SimpleDoubleProperty();
    private IntegerProperty lcdPressure = new SimpleIntegerProperty();
    private RegulationModesModel regulationModesModel;

    public DoubleProperty current_1Property() {
        return current_1;
    }

    public DoubleProperty duty_1Property() {
        return duty_1;
    }

    public DoubleProperty current_2Property() {
        return current_2;
    }

    public DoubleProperty duty_2Property() {
        return duty_2;
    }

    public DoubleProperty current_3Property() {
        return current_3;
    }

    public DoubleProperty duty_3Property() {
        return duty_3;
    }

    public IntegerProperty lcdPressureProperty() {
        return lcdPressure;
    }

    public HighPressureSectionUpdateModel(PressureSensorModel pressureSensorModel,
                                          RegulationModesModel regulationModesModel) {
        this.pressureSensorModel = pressureSensorModel;
        this.regulationModesModel = regulationModesModel;
    }

    @Override
    public void update() {
    }

    @Override
    public void run() {

        RegActive activeRegMode1 = regulationModesModel.getRegulatorOneMode().get();
        RegActive activeRegMode2 = regulationModesModel.getRegulatorTwoMode().get();
        RegActive activeRegMode3 = regulationModesModel.getRegulatorThreeMode().get();

        if(PressureReg1_PressFact.getLastValue() != null){
            double pressure = pressureSensorModel.pressureSensorProperty().get() * (Double) PressureReg1_PressFact.getLastValue();
            lcdPressure.setValue(pressure);
        }
        if(PressureReg1_DutyFact.getLastValue() != null && activeRegMode1 != DUTY){
            duty_1.setValue(round((double)PressureReg1_DutyFact.getLastValue()));
        }
        if(PressureReg1_I_Fact.getLastValue() != null && activeRegMode1 != CURRENT){
            current_1.setValue(round((double)PressureReg1_I_Fact.getLastValue()));
        }
        if(PressureReg2_I_Fact.getLastValue() != null && activeRegMode2!= CURRENT){
            current_2.setValue(round((double)PressureReg2_I_Fact.getLastValue()));
        }
        if(PressureReg2_DutyFact.getLastValue() != null &&  activeRegMode2 != DUTY){
            duty_2.setValue(round((double)PressureReg2_DutyFact.getLastValue()));
        }
        if(PressureReg3_I_Fact.getLastValue() != null && activeRegMode3 != CURRENT){
            current_3.setValue(round((double)PressureReg3_I_Fact.getLastValue()));
        }
        if(PressureReg3_DutyFact.getLastValue() != null && activeRegMode3 != DUTY){
            duty_3.setValue(round((double)PressureReg3_DutyFact.getLastValue()));
        }
    }

}
