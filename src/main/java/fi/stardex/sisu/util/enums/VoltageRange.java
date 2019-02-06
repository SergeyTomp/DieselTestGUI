package fi.stardex.sisu.util.enums;

import fi.stardex.sisu.registers.ModbusMap;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.Battery_U;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.Boost_U;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.StartOnBatteryUOne;

public enum VoltageRange {

    LOW (14.0, 29.5, 11.0, 0.5, Battery_U, StartOnBatteryUOne, true, -2d),
    HIGH (30.0, 160.0, 30.0, 0.5, Boost_U, StartOnBatteryUOne, false, 0d);

    private double min;
    private double max;
    private double init;
    private double step;
    private ModbusMap voltageRegister;
    private ModbusMap switchRangeRegister;
    private boolean isLowRange;
    private double correction;
    private int upTimeStep = 150;   // в мс
    private int downTimeStep = 400; // в мс
    private double lastValue;

    VoltageRange(double min,
                 double max,
                 double init,
                 double step,
                 ModbusMap voltageRegister,
                 ModbusMap switchRangeRegister,
                 boolean isLowRange,
                 double correction) {
        this.min = min;
        this.max = max;
        this.init = init;
        this.step = step;
        this.voltageRegister = voltageRegister;
        this.switchRangeRegister = switchRangeRegister;
        this.isLowRange = isLowRange;
        this.correction = correction;
    }

    public Double getMin() {
        return min;
    }
    public Double getMax() {
        return max;
    }
    public Double getInit() {
        return init;
    }
    public Double getStep() {
        return step;
    }
    public ModbusMap getVoltageRegister() {
        return voltageRegister;
    }
    public ModbusMap getSwitchRangeRegister() {
        return switchRangeRegister;
    }
    public int getUpTimeStep() {
        return upTimeStep;
    }
    public int getDownTimeStep() {
        return downTimeStep;
    }
    public double getLastValue() {
        return lastValue;
    }
    public boolean isLowRange() {
        return isLowRange;
    }
    public double getCorrection() {
        return correction;
    }

    public void setLastValue(double lastValue) {
        this.lastValue = lastValue;
    }
}
