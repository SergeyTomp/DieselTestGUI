package fi.stardex.sisu.registers;

import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;

public abstract class RegisterFactory {

    public static ModbusRequest getRequest(ModbusMap reg, boolean isWriting, Object value) {
        ModbusRequest request;
        switch (reg.getType()) {
            case REGISTER_HOLDING:
                request = isWriting ? createWriteRegisters(reg, value) : new ReadMultipleRegistersRequest(reg.getRef(), reg.getCount());
                break;
            case REGISTER_INPUT:
                request = new ReadInputRegistersRequest(reg.getRef(), reg.getCount());
                break;
            case DISCRETE_COIL:
                request = isWriting ? new WriteCoilRequest(reg.getRef(), (boolean) value) : new ReadCoilsRequest(reg.getRef(), reg.getCount());
                break;
            case DISCRETE_INPUT:
                request = new ReadInputDiscretesRequest(reg.getRef(), reg.getCount());
                break;
            default:
                throw new IllegalArgumentException("Incorrect program logic we have only two register types");
        }
        return request;
    }

    private static ModbusRequest createWriteRegisters(ModbusMap reg, Object value) {
        WriteMultipleRegistersRequest request;
        if (reg.getCount() > 1) {
            byte[] byteData;
            Float floatValue;
            if(value instanceof Integer) {
                floatValue = ((Integer) value).floatValue();
                byteData = ModbusUtil.floatToRegisters(floatValue);
            } else
                byteData = ModbusUtil.floatToRegisters((float) value);
            short firstWord = (short) (((byteData[0] & 0xFF) << 8) | (byteData[1] & 0xFF));
            short secondWord = (short) (((byteData[2] & 0xFF) << 8) | (byteData[3] & 0xFF));
            Register firstReg = new SimpleRegister(firstWord);
            Register secondReg = new SimpleRegister(secondWord);
            request = new WriteMultipleRegistersRequest(reg.getRef(), new Register[]{firstReg, secondReg});
        } else {
            Register register;
            if (value instanceof Double) {
                Double doubleValue = Double.parseDouble(value.toString());
                register = new SimpleRegister((int) Math.round(doubleValue));
            } else {
                register = new SimpleRegister((int) value);
            }
            request = new WriteMultipleRegistersRequest(reg.getRef(), new Register[]{register});
        }
        return request;
    }
}
