package fi.stardex.sisu.registers;

public interface ModbusMap {

    RegisterType getType();

    int getRef();

    int getCount();

    Object getLastValue();

    void setLastValue(Object lastValue);

    boolean isAutoUpdate();

    boolean isIgnoreFirstRead();

    void setFirstRead(boolean firstRead);

    boolean isFirstRead();
}
