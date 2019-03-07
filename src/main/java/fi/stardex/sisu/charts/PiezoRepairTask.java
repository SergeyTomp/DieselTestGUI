package fi.stardex.sisu.charts;

import fi.stardex.sisu.registers.RegisterProvider;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TimerTask;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.TouchDetection;

public class PiezoRepairTask extends TimerTask {

    @Autowired
    private RegisterProvider ultimaRegisterProvider;

    private IntegerProperty touchLevel = new SimpleIntegerProperty();
    public IntegerProperty touchLevelProperty() {
        return touchLevel;
    }

    @Override
    public void run() {

        ultimaRegisterProvider.read(TouchDetection);

        if (TouchDetection.getLastValue() != null) {
            double lastValue = (double) (TouchDetection.getLastValue());
            touchLevel.setValue(lastValue);
            System.err.println(TouchDetection.getLastValue());
        }
    }
}
