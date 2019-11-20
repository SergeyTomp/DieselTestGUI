package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisSettingsModel;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.property.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.*;
import static fi.stardex.sisu.util.enums.GUI_type.UIS;
import static fi.stardex.sisu.util.enums.InjectorSubType.*;

@Module(value = Device.ULTIMA)
public class UisHardwareUpdateModel implements Updater {

    private StringProperty battery_U = new SimpleStringProperty(String.valueOf(BATTERY_U_SPINNER_INIT));
    private StringProperty boost_U = new SimpleStringProperty(String.valueOf(BOOST_U_SPINNER_INIT));
    private StringProperty negative_U = new SimpleStringProperty(String.valueOf(NEGATIVE_U_SPINNER_INIT));
    private StringProperty width = new SimpleStringProperty(String.valueOf(WIDTH_CURRENT_SIGNAL_SPINNER_INIT));
    private StringProperty first_W = new SimpleStringProperty(String.valueOf(FIRST_W_SPINNER_INIT));
    private StringProperty first_I = new SimpleStringProperty(String.valueOf(FIRST_I_SPINNER_INIT));
    private StringProperty second_I = new SimpleStringProperty(String.valueOf(SECOND_I_SPINNER_INIT));
    private StringProperty boost_I = new SimpleStringProperty(String.valueOf(BOOST_I_SPINNER_INIT));
    private StringProperty first_W2 = new SimpleStringProperty(String.valueOf(0));
    private StringProperty first_I2 = new SimpleStringProperty(String.valueOf(0));
    private StringProperty second_I2 = new SimpleStringProperty(String.valueOf(0));
    private StringProperty boost_I2 = new SimpleStringProperty(String.valueOf(0));
    private StringProperty width2 = new SimpleStringProperty(String.valueOf(0));
    private StringProperty shift = new SimpleStringProperty(String.valueOf(0));
    private BooleanProperty injectorError = new SimpleBooleanProperty();
    private DoubleProperty current = new SimpleDoubleProperty();
    private DoubleProperty duty = new SimpleDoubleProperty();
    private IntegerProperty lcdPressure = new SimpleIntegerProperty();
    private IntegerProperty maxLcdPressure = new SimpleIntegerProperty();
    private StringProperty bipPWM = new SimpleStringProperty();
    private StringProperty bipWindow = new SimpleStringProperty(String.valueOf(0));

    private MainSectionUisModel mainSectionUisModel;
    private GUI_TypeModel gui_typeModel;
    private RegulationModesModel regulationModesModel;
    private UisSettingsModel uisSettingsModel;
    private ObjectProperty<Model> modelProperty;
    private ObjectProperty<Test> testProperty;
    private static final float ONE_AMPERE_MULTIPLY = 93.07f;
    private InjectorSubType injectorSubType;
    private Model model;
    private InjectorUisTest test;

    public StringProperty widthProperty() {
        return width;
    }
    public StringProperty boost_UProperty() {
        return boost_U;
    }
    public StringProperty first_WProperty() {
        return first_W;
    }
    public StringProperty first_IProperty() {
        return first_I;
    }
    public StringProperty second_IProperty() {
        return second_I;
    }
    public StringProperty boost_IProperty() {
        return boost_I;
    }
    public StringProperty battery_UProperty() {
        return battery_U;
    }
    public StringProperty negative_UProperty() {
        return negative_U;
    }
    public BooleanProperty injectorErrorProperty() {
        return injectorError;
    }
    public StringProperty first_W2Property() {
        return first_W2;
    }
    public StringProperty first_I2Property() {
        return first_I2;
    }
    public StringProperty second_I2Property() {
        return second_I2;
    }
    public StringProperty boost_I2Property() {
        return boost_I2;
    }
    public StringProperty width2Property() {
        return width2;
    }
    public StringProperty shiftProperty() {
        return shift;
    }
    public DoubleProperty currentProperty() {
        return current;
    }
    public DoubleProperty dutyProperty() {
        return duty;
    }
    public IntegerProperty lcdPressureProperty() {
        return lcdPressure;
    }
    public IntegerProperty maxLcdPressureProperty() {
        return maxLcdPressure;
    }
    public StringProperty bipPWMProperty() {
        return bipPWM;
    }
    public StringProperty bipWindowProperty() {
        return bipWindow;
    }

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
        this.modelProperty = mainSectionUisModel.modelProperty();
        this.testProperty = mainSectionUisModel.injectorTestProperty();
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setRegulationModesModel(RegulationModesModel regulationModesModel) {
        this.regulationModesModel = regulationModesModel;
    }
    public void setUisSettingsModel(UisSettingsModel uisSettingsModel) {
        this.uisSettingsModel = uisSettingsModel;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        RegActive activeRegulatingMode = regulationModesModel.regulatorOneModeProperty().get();

        if (gui_typeModel.guiTypeProperty().get() != UIS) { return; }

        String value;
        float convertedValue;

        if ((value = WidthBoardOne.getLastValue().toString()) != null){
            width.setValue(value);
        }
        if ((value = Boost_U.getLastValue().toString()) != null){
            boost_U.setValue(Integer.toString(convertDataToInt(value)));
        }
        if ((value = FirstWBoardOne.getLastValue().toString()) != null){
            first_W.setValue(value);
        }
        if ((value = FirstIBoardOne.getLastValue().toString()) != null) {
            convertedValue = round(convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
            first_I.setValue(Float.toString(convertedValue));
        }
        if ((value = SecondIBoardOne.getLastValue().toString()) != null) {
            convertedValue = round(convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
            second_I.setValue(Float.toString(convertedValue));
        }
        if ((value = BoostIBoardOne.getLastValue().toString()) != null) {
            convertedValue = round(convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
            boost_I.setValue(Float.toString(convertedValue));
        }
        if ((value = Battery_U.getLastValue().toString()) != null){
            battery_U.setValue(Integer.toString(convertDataToInt(value)));
        }
        if ((value = Negative_U.getLastValue().toString()) != null){
            negative_U.setValue(Integer.toString(convertDataToInt(value)));
        }
        if ((value = Inj_Process_Global_Error.getLastValue().toString()) != null) {
            injectorError.setValue(Boolean.parseBoolean(value));
        }
//        if ((value = Injectors_Running_En.getLastValue().toString()) != null) {
//            System.err.println("Injectors_Running_En  " + value);
//        }

        model = modelProperty.get();
        test = (InjectorUisTest) testProperty.get();
        boolean isBipTest = (test != null && isBipTest(test));
        if (model != null) {

            injectorSubType = model.getVAP().getInjectorSubType();

            if (injectorSubType == DOUBLE_COIL || injectorSubType == HPI) {

                if ((value = FirstWBoardTwo.getLastValue().toString()) != null){
                    first_W2.setValue(value);
                }
                if ((value = FirstIBoardTwo.getLastValue().toString()) != null) {
                    convertedValue = round(convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
                    first_I2.setValue(Float.toString(convertedValue));
                }
                if ((value = SecondIBoardTwo.getLastValue().toString()) != null) {
                    convertedValue = round(convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
                    second_I2.setValue(Float.toString(convertedValue));
                }
                if ((value = BoostIBoardTwo.getLastValue().toString()) != null) {
                    convertedValue = round(convertDataToFloat(value) / ONE_AMPERE_MULTIPLY);
                    boost_I2.setValue(Float.toString(convertedValue));
                }
                if ((value = WidthBoardTwo.getLastValue().toString()) != null) {
                    width2.setValue(value);
                }
                if ((value = SecondCoilShiftTime.getLastValue().toString()) != null) {
                    shift.setValue(value);
                }
            }
            else {

                first_W2.setValue(Float.toString(0f));
                first_I2.setValue(Float.toString(0f));
                second_I2.setValue(Float.toString(0f));
                boost_I2.setValue(Float.toString(0f));
                width2.setValue(Integer.toString(0));
                shift.setValue(Integer.toString(0));
            }
            if (injectorSubType == F2E) {

                if(PressureReg1_PressFact.getLastValue() != null){
                    double pressure = uisSettingsModel.pressureSensorProperty().get() * (Double) PressureReg1_PressFact.getLastValue();
                    lcdPressure.setValue(pressure);
                }
                if ((PressureReg1_I_Fact.getLastValue()) != null && activeRegulatingMode != RegActive.CURRENT) {
                    current.setValue(round((double)PressureReg1_I_Fact.getLastValue()));
                }
                if ((PressureReg1_DutyFact.getLastValue()) != null && activeRegulatingMode != RegActive.DUTY) {
                    duty.setValue(round((double)PressureReg1_DutyFact.getLastValue()));
                }
            }

            if (injectorSubType == MECHANIC) {

                int pressureSensor = uisSettingsModel.pressureSensorProperty().get();
                if(PressureReg1_PressFact.getLastValue() != null){
                    double pressure = pressureSensor * (Double) PressureReg1_PressFact.getLastValue();
                    lcdPressure.setValue(pressure);
                }
                if(MaxPressureRegistered.getLastValue() != null){
                    double pressure = pressureSensor * (Double) MaxPressureRegistered.getLastValue();
                    maxLcdPressure.setValue(pressure);
                }
            }
        }

        if (isBipTest) {

            if ((value = BipModeInterval_1.getLastValue().toString()) != null) {
                bipWindow.set(value);
            }
            if ((value = BipModeDuty_1.getLastValue().toString()) != null) {
                bipPWM.set(value);
            }
        } else {

            bipPWM.setValue(Integer.toString(0));
            bipWindow.setValue(Integer.toString(0));
        }

    }

    private boolean isBipTest(Test test) {
        return ((InjectorUisTest)test).getVoltAmpereProfile().getBipPWM() != null
                && ((InjectorUisTest)test).getVoltAmpereProfile().getBipWindow() != null;
    }
}
