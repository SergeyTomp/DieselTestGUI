package fi.stardex.sisu.registers;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.registers.modbusmaps.ModbusMap;
import fi.stardex.sisu.registers.modbusmaps.RegisterType;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.procimg.InputRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RegisterProvider {

    private Logger logger = LoggerFactory.getLogger(RegisterProvider.class);

    private ModbusConnect modbusConnect;

    public RegisterProvider(ModbusConnect modbusConnect) {
        this.modbusConnect = modbusConnect;
    }

    public Object read(ModbusMap register) {
        try {
            Object valueToReturn = null;
            if (register.getType() == RegisterType.REGISTER_HOLDING) {
                ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                if (register.getCount() > 1) {
                    valueToReturn = RequestsUtils.createDouble(
                            (short) response.getRegister(0).getValue(),
                            (short) response.getRegister(1).getValue());
                } else {
                    valueToReturn = response.getRegister(0).getValue();
                }
            } else if (register.getType() == RegisterType.REGISTER_INPUT) {
                ReadInputRegistersResponse response = (ReadInputRegistersResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                if (register.getCount() > 1) {
                    valueToReturn = RequestsUtils.createDouble(
                            (short) response.getRegister(0).getValue(),
                            (short) response.getRegister(1).getValue());
                } else {
                    valueToReturn = response.getRegister(0).getValue();
                }
            } else if (register.getType() == RegisterType.DISCRETE_COIL) {
                ReadCoilsResponse response = (ReadCoilsResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                valueToReturn = response.getCoilStatus(0);
            } else if (register.getType() == RegisterType.DISCRETE_INPUT) {
                ReadInputDiscretesResponse response = (ReadInputDiscretesResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                valueToReturn = response.getDiscreteStatus(0);
            } else if(register.getType() == RegisterType.REGISTER_INPUT_CHART) {
                valueToReturn = readBytePacket(register);
            }
            register.setLastValue(valueToReturn);
            return valueToReturn;
        } catch (ModbusException e) {
            logger.warn(e.getMessage());
            modbusConnect.disconnect2();
            return register.getLastValue();
        }
    }

    public void write(ModbusMap register, Object value) throws ModbusException {
        try {
            subscribeRequest(RegisterFactory.getRequest(register, true, value));
        } catch (ModbusException e) {
            modbusConnect.disconnect2();
            logger.warn(e.getMessage());
            throw e;
        }
    }

    private ModbusResponse subscribeRequest(ModbusRequest request) throws ModbusException {
        ModbusTransaction transaction = modbusConnect.getTransaction(request);
        transaction.execute();
        return transaction.getResponse();
    }

    public boolean isConnected() {
        return modbusConnect.isConnected();
    }

    private Integer[] readBytePacket(ModbusMap register) throws ModbusException {
        int currRef = register.getRef();
        int size = register.getCount();
        return readBytePacket(currRef, size);
    }

    public Integer[] readBytePacket(int currRef, int size) throws ModbusException {

        Integer[] chartData = new Integer[size];
        //TODO why IOOBEE if stepsize = 150;
        int stepSize = 100;
        int stepCount = size / stepSize;
        int remainder = size % stepSize;

        int currPoint = 0;
        for (int i = 0; i < stepCount; i++) {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(currRef, stepSize);
            currRef += stepSize;
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) subscribeRequest(request);
            for (InputRegister inputRegister : response.getRegisters()) {
                chartData[currPoint++] = inputRegister.getValue();
            }
        }

        if (remainder > 0) {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(currRef, remainder);
            ReadInputRegistersResponse response = (ReadInputRegistersResponse) subscribeRequest(request);
            for (InputRegister inputRegister : response.getRegisters()) {
                chartData[currPoint++] = inputRegister.getValue();
            }
        }
        return chartData;
    }
}