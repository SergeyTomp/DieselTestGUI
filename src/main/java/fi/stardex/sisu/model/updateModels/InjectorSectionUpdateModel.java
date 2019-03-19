package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.ui.updaters.Updater;
import javafx.beans.property.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToFloat;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.converters.DataConverter.round;

@Module(value = Device.ULTIMA)
public class InjectorSectionUpdateModel implements Updater {

    private StringProperty width = new SimpleStringProperty();
    private StringProperty boost_U = new SimpleStringProperty();
    private StringProperty first_W = new SimpleStringProperty();
    private StringProperty first_I = new SimpleStringProperty();
    private StringProperty second_I = new SimpleStringProperty();
    private StringProperty boost_I = new SimpleStringProperty();
    private StringProperty battery_U = new SimpleStringProperty();
    private StringProperty negative_U = new SimpleStringProperty();
    private BooleanProperty injectorError = new SimpleBooleanProperty();

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
    }
}
