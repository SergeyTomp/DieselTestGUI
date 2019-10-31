package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.*;

@Module(value = Device.MODBUS_FLOW)
public class UisFlowUpdater implements Updater {

    private StringProperty flow = new SimpleStringProperty();
    private StringProperty temperature_1 = new SimpleStringProperty();
    private StringProperty temperature_2 = new SimpleStringProperty();

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;
    public StringProperty flowProperty() {
        return flow;
    }
    public StringProperty temperature_1Property() {
        return temperature_1;
    }
    public StringProperty temperature_2Property() {
        return temperature_2;
    }

    public void setFlowFirmwareVersion(FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        String value;

        if((value = Channel1Level.getLastValue().toString()) != null){
            flow.setValue(value);
        }
        if((value = Channel1Temperature1.getLastValue().toString()) != null){
            temperature_1.setValue(value);
        }
        if((value = Channel2Temperature2.getLastValue().toString()) != null){
            temperature_2.setValue(value);
        }
    }
}
