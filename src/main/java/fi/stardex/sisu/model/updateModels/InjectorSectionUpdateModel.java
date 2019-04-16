package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.states.VoltAmpereProfileDialogModel;
import fi.stardex.sisu.ui.updaters.Updater;
import javafx.beans.property.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.BoostIBoardTwo;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.FirstIBoardTwo;
import static fi.stardex.sisu.util.SpinnerDefaults.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToFloat;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.converters.DataConverter.round;

@Module(value = Device.ULTIMA)
public class InjectorSectionUpdateModel implements Updater {

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
    private StringProperty offset = new SimpleStringProperty(String.valueOf(0));
    private BooleanProperty injectorError = new SimpleBooleanProperty();

    private VoltAmpereProfileDialogModel voltAmpereProfileDialogModel;

    private static final float ONE_AMPERE_MULTIPLY = 93.07f;

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
    public StringProperty offsetProperty() {
        return offset;
    }

    public void setVoltAmpereProfileDialogModel(VoltAmpereProfileDialogModel voltAmpereProfileDialogModel) {
        this.voltAmpereProfileDialogModel = voltAmpereProfileDialogModel;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

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

        if (voltAmpereProfileDialogModel.isDoubleCoilProperty().get()) {

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
                offset.setValue(value);
            }
        }
        else{

            first_W2.setValue(Float.toString(0f));
            first_I2.setValue(Float.toString(0f));
            second_I2.setValue(Float.toString(0f));
            boost_I2.setValue(Float.toString(0f));
            width2.setValue(Integer.toString(0));
            offset.setValue(Integer.toString(0));
        }
    }
}
