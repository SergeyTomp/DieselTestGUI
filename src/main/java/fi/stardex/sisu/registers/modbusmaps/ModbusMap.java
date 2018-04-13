package fi.stardex.sisu.registers.modbusmaps;

public interface ModbusMap {

    RegisterType getType();

    int getRef();

    int getCount();

    Object getLastValue();

    void setLastValue(Object lastValue);

    boolean isAutoUpdate();

    void setAutoUpdate(boolean autoUpdate);
}
