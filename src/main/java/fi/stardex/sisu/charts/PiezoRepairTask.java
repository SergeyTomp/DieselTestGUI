package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.RegisterProvider;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.TimerTask;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.TouchDetection;

public class PiezoRepairTask extends TimerTask {

    private RegisterProvider ultimaRegisterProvider;

    private IntegerProperty touchLevel = new SimpleIntegerProperty();
    public IntegerProperty touchLevelProperty() {
        return touchLevel;
    }

    public PiezoRepairTask(RegisterProvider ultimaRegisterProvider) {
        this.ultimaRegisterProvider = ultimaRegisterProvider;
    }

    @Override
    public void run() {

        ultimaRegisterProvider.read(TouchDetection);

        if (TouchDetection.getLastValue() != null) {
            double lastValue = (double) (TouchDetection.getLastValue());
            touchLevel.setValue(lastValue);
        }
    }
}
