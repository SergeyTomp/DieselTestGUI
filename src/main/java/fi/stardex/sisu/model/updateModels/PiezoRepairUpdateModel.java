package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.updaters.Updater;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.TouchDetection;

@Module(value= Device.ULTIMA)
public class PiezoRepairUpdateModel implements Updater {

    private IntegerProperty touchLevel = new SimpleIntegerProperty();
    public IntegerProperty touchLevelProperty() {
        return touchLevel;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if (TouchDetection.getLastValue() != null) {
            double lastValue = (double) (TouchDetection.getLastValue());
            touchLevel.setValue(lastValue);
        }
    }
}
