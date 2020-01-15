package fi.stardex.sisu.model.updateModels;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.updaters.Updater;
import javafx.beans.property.*;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.ShiftingIsInProgress;

@Module(value= Device.MODBUS_FLOW)
public class DifferentialFmUpdateModel implements Updater {

    private BooleanProperty calibrationIsInProgress = new SimpleBooleanProperty(false);
    private BooleanProperty calibrationPermitted = new SimpleBooleanProperty(false);
    private IntegerProperty calibrationPeriod = new SimpleIntegerProperty();
    private LongProperty calibrationTime = new SimpleLongProperty();
    private LongProperty lastCalibrationStart = new SimpleLongProperty();
    private IntegerProperty restCalibrationTime = new SimpleIntegerProperty();
    private FloatProperty progressProperty = new SimpleFloatProperty();
    private BooleanProperty showProgressProperty = new SimpleBooleanProperty();

    public BooleanProperty calibrationIsInProgressProperty() {
        return calibrationIsInProgress;
    }
    public IntegerProperty calibrationPeriodProperty() {
        return calibrationPeriod;
    }
    public LongProperty calibrationTimeProperty() {
        return calibrationTime;
    }
    public LongProperty lastCalibrationStartProperty() {
        return lastCalibrationStart;
    }
    public BooleanProperty calibrationPermittedProperty() {
        return calibrationPermitted;
    }
    public IntegerProperty restCalibrationTimeProperty() {
        return restCalibrationTime;
    }
    public FloatProperty progressProperty() {
        return progressProperty;
    }
    public BooleanProperty showProgressProperty() {
        return showProgressProperty;
    }

    @Override
    public void update() {

        //Temporarily passed time is corrected by 29% due to hardware timer under clocking feature
        double passedTime = getPassedTime() / 1.29;

        if (!calibrationPermitted.get() && passedTime >= calibrationPeriod.get() * 1000) {
            calibrationPermitted.setValue(true);
        }

        if (calibrationIsInProgress.get()) {
            restCalibrationTime.setValue(calibrationTime.get() - passedTime / 1000);
            progressProperty.setValue(1 - restCalibrationTime.floatValue() / calibrationTime.get());
        } else {
            restCalibrationTime.setValue(0);
            progressProperty.setValue(1);
        }
    }

    @Override
    public void run() {

        if (ShiftingIsInProgress.getLastValue() != null) {
            calibrationIsInProgress.setValue((boolean) ShiftingIsInProgress.getLastValue());
        }

        update();
    }

    private long getPassedTime() {
        return System.currentTimeMillis() - lastCalibrationStart.get();
    }
}
