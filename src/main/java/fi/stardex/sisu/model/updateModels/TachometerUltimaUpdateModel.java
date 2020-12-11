package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.registers.ultima.ModbusMapUltima;
import fi.stardex.sisu.ui.updaters.Updater;
import fi.stardex.sisu.util.enums.GUI_type;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;

@Module(value = Device.ULTIMA)
public class TachometerUltimaUpdateModel implements Updater {

    private Logger logger = LoggerFactory.getLogger(TachometerUltimaUpdateModel.class);

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    private BooleanProperty standModbusConnectProperty;
    private IntegerProperty currentRPM = new SimpleIntegerProperty();

    @Autowired
    private GUI_TypeModel gui_typeModel;

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

        /**в комментарии необходимая доработка для продолжения отслеживания оборотов от датчика Ultima для UIS
         * даже при подключенных Stand или StandFM.
         * При подключении этой доработки необходимо раскомментировать в TestBenchSectionUpdateModel, также в run(),
         * условие для отключения отслеживания датчика оборотов Stand или StandFM при выборе UIS, там есть аналогичный комментарий*/
        if (flowFirmwareVersion.getVersions() != FlowVersions.STAND_FM
                && flowFirmwareVersion.getVersions() != FlowVersions.STAND_FM_4_CH
                && !standModbusConnectProperty.get()
//                || gui_typeModel.guiTypeProperty().get() == GUI_type.UIS
        ) {

            Integer currentRPMLastValue = (Integer) ModbusMapUltima.Tachometer.getLastValue();

            if (currentRPMLastValue != null) {

                currentRPM.setValue(currentRPMLastValue);
            }
        }
    }
}
