package fi.stardex.sisu.registers;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.registers.modbusmaps.ModbusMap;
import fi.stardex.sisu.registers.modbusmaps.RegisterType;
import fi.stardex.sisu.ui.updaters.Updater;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadCoilsResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RegisterProvider {

    private Logger logger = LoggerFactory.getLogger(RegisterProvider.class);

    private ModbusConnect modbusConnect;

    public RegisterProvider(ModbusConnect modbusConnect) {
        this.modbusConnect = modbusConnect;
    }

    public Object read(ModbusMap register) {
        try {
            Object valueToReturn;
            if (register.getType() == RegisterType.REGISTER) {
                ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) request(RegisterFactory.getRequest(register, false, null));
                if (register.getCount() > 1) {
                    valueToReturn = RequestsUtils.createDouble(
                            (short) response.getRegister(0).getValue(),
                            (short) response.getRegister(1).getValue());
                } else {
                    valueToReturn = response.getRegister(0).getValue();
                }
            } else {
                ReadCoilsResponse response = (ReadCoilsResponse) request(RegisterFactory.getRequest(register, false, null));
                valueToReturn = response.getCoilStatus(0);
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
            request(RegisterFactory.getRequest(register, true, value));
        } catch (ModbusException e) {
            modbusConnect.disconnect2();
            logger.warn(e.getMessage());
            throw e;
        }
    }

    private ModbusResponse request(ModbusRequest request) throws ModbusException {
        ModbusTransaction transaction = modbusConnect.getTransaction(request);
        transaction.execute();
        return transaction.getResponse();
    }

    public boolean isConnected() {
        return modbusConnect.isConnected();
    }
}
