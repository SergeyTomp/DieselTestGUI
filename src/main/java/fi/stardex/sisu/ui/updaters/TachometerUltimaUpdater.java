package fi.stardex.sisu.ui.updaters;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.beans.property.BooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;

@Module(value = Device.ULTIMA)
public class TachometerUltimaUpdater implements Updater {

    private Logger logger = LoggerFactory.getLogger(TachometerUltimaUpdater.class);

    private Lcd currentRPMLcd;

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    private BooleanProperty standModbusConnectProperty;

    public TachometerUltimaUpdater(TestBenchSectionController testBenchSectionController,
                                   FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                   BooleanProperty standModbusConnectProperty) {

        currentRPMLcd = testBenchSectionController.getCurrentRPMLcd();
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

            if (currentRPMLastValue != null)
                currentRPMLcd.setValue(currentRPMLastValue);

        }

    }
}
