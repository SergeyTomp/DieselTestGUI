package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;

@Module(value = Device.ULTIMA)
public class TachometerUltimaUpdateModel implements Updater {

    private Logger logger = LoggerFactory.getLogger(TachometerUltimaUpdateModel.class);

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    private BooleanProperty standModbusConnectProperty;
    private IntegerProperty currentRPM = new SimpleIntegerProperty();

    public IntegerProperty currentRPMProperty() {
        return currentRPM;
    }

    public TachometerUltimaUpdateModel(FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                       BooleanProperty standModbusConnectProperty) {

        this.flowFirmwareVersion = flowFirmwareVersion;
        this.standModbusConnectProperty = standModbusConnectProperty;

    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if (flowFirmwareVersion.getVersions() != FlowVersions.STAND_FM
                && flowFirmwareVersion.getVersions() != FlowVersions.STAND_FM_4_CH
                && !standModbusConnectProperty.get()) {

            Integer currentRPMLastValue = (Integer) ModbusMapUltima.Tachometer.getLastValue();

            if (currentRPMLastValue != null) {

                currentRPM.setValue(currentRPMLastValue);
            }
        }
    }
}
