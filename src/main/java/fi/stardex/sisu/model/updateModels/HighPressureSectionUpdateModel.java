package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.model.PressureSensorModel;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.converters.DataConverter.round;
import static fi.stardex.sisu.util.enums.RegActive.CURRENT;
import static fi.stardex.sisu.util.enums.RegActive.DUTY;

@Module(value= Device.ULTIMA)
public class HighPressureSectionUpdateModel implements Updater {

    private PressureSensorModel pressureSensorModel;
    private DoubleProperty current_1Property = new SimpleDoubleProperty();
    private DoubleProperty duty_1Property = new SimpleDoubleProperty();
    private DoubleProperty current_2Property = new SimpleDoubleProperty();
    private DoubleProperty duty_2Property = new SimpleDoubleProperty();
    private DoubleProperty current_3Property = new SimpleDoubleProperty();
    private DoubleProperty duty_3Property = new SimpleDoubleProperty();
    private IntegerProperty lcdPressureProperty = new SimpleIntegerProperty();
    private RegulationModesModel regulationModesModel;
    private DoubleProperty gauge_1Property = new SimpleDoubleProperty();
    private DoubleProperty gauge_2Property = new SimpleDoubleProperty();
    private DoubleProperty gauge_3Property = new SimpleDoubleProperty();

    public DoubleProperty current_1Property() {
        return current_1Property;
    }
    public DoubleProperty duty_1Property() {
        return duty_1Property;
    }
    public DoubleProperty current_2Property() {
        return current_2Property;
    }
    public DoubleProperty duty_2Property() {
        return duty_2Property;
    }
    public DoubleProperty current_3Property() {
        return current_3Property;
    }
    public DoubleProperty duty_3Property() {
        return duty_3Property;
    }
    public IntegerProperty lcdPressureProperty() {
        return lcdPressureProperty;
    }

    public DoubleProperty gauge_1PropertyProperty() {
        return gauge_1Property;
    }
    public DoubleProperty gauge_2PropertyProperty() {
        return gauge_2Property;
    }
    public DoubleProperty gauge_3PropertyProperty() {
        return gauge_3Property;
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

        RegActive activeRegMode1 = regulationModesModel.regulatorOneModeProperty().get();
        RegActive activeRegMode2 = regulationModesModel.regulatorTwoModeProperty().get();
        RegActive activeRegMode3 = regulationModesModel.regulatorThreeModeProperty().get();

        if(PressureReg1_PressFact.getLastValue() != null){
            double pressure = pressureSensorModel.pressureSensorProperty().get() * (Double) PressureReg1_PressFact.getLastValue();
            lcdPressureProperty.setValue(pressure);
        }
        if(PressureReg1_DutyFact.getLastValue() != null && activeRegMode1 != DUTY){
            duty_1Property.setValue(round((double)PressureReg1_DutyFact.getLastValue()));
        }

        if(PressureReg1_I_Fact.getLastValue() != null && activeRegMode1 != CURRENT){
            current_1Property.setValue(round((double)PressureReg1_I_Fact.getLastValue()));
        }
        gauge_1Property.setValue(round((double)PressureReg1_I_Fact.getLastValue()));

        if(PressureReg2_I_Fact.getLastValue() != null && activeRegMode2!= CURRENT){
            current_2Property.setValue(round((double)PressureReg2_I_Fact.getLastValue()));
        }
        gauge_2Property.setValue(round((double)PressureReg2_I_Fact.getLastValue()));

        if(PressureReg2_DutyFact.getLastValue() != null &&  activeRegMode2 != DUTY){
            duty_2Property.setValue(round((double)PressureReg2_DutyFact.getLastValue()));
        }
        if(PressureReg3_I_Fact.getLastValue() != null && activeRegMode3 != CURRENT){
            current_3Property.setValue(round((double)PressureReg3_I_Fact.getLastValue()));
        }
        gauge_3Property.setValue(round((double)PressureReg3_I_Fact.getLastValue()));

        if(PressureReg3_DutyFact.getLastValue() != null && activeRegMode3 != DUTY){
            duty_3Property.setValue(round((double)PressureReg3_DutyFact.getLastValue()));
        }
    }

}
