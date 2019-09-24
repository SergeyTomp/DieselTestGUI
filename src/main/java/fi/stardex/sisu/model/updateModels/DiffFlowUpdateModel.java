package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.updaters.Updater;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.ShiftingAutoStartIsOn;
import static fi.stardex.sisu.registers.flow.ModbusMapFlow.ShiftingIsInProgress;

@Module(value= Device.MODBUS_FLOW)
public class DiffFlowUpdateModel implements Updater {

    private BooleanProperty shiftingIsInProgress = new SimpleBooleanProperty(false);
    private BooleanProperty shiftingAutoStartIsOn = new SimpleBooleanProperty(false);

    public BooleanProperty shiftingIsInProgressProperty() {
        return shiftingIsInProgress;
    }
    public BooleanProperty shiftingAutoStartIsOnProperty() {
        return shiftingAutoStartIsOn;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if (ShiftingIsInProgress.getLastValue() != null) {
            shiftingIsInProgress.setValue((boolean) ShiftingIsInProgress.getLastValue());
        }
        if (ShiftingAutoStartIsOn.getLastValue() != null) {
            shiftingAutoStartIsOn.setValue((boolean) ShiftingAutoStartIsOn.getLastValue());
        }

    }
}
