package fi.stardex.sisu.registers;

import fi.stardex.sisu.connect.ModbusConnect;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.procimg.InputRegister;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class RegisterProvider {

    private Logger logger = LoggerFactory.getLogger(RegisterProvider.class);

    private ModbusConnect modbusConnect;

    public RegisterProvider(ModbusConnect modbusConnect) {
        this.modbusConnect = modbusConnect;
    }

    @PostConstruct
    private void init() {
        setupFirmwareVersionListener();
    }

    protected abstract void setupFirmwareVersionListener();

    public synchronized Object read(ModbusMap register) {

        try {

            Object valueToReturn = null;

            RegisterType type = register.getType();

            switch (type) {

                case REGISTER_HOLDING:
                    ReadMultipleRegistersResponse readMultipleRegistersResponse =
                            (ReadMultipleRegistersResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                    if (register.getCount() > 1) {
                        valueToReturn = createDouble(
                                (short) readMultipleRegistersResponse.getRegister(0).getValue(),
                                (short) readMultipleRegistersResponse.getRegister(1).getValue());
                    } else
                        valueToReturn = readMultipleRegistersResponse.getRegister(0).getValue();
                    break;
                case REGISTER_INPUT:
                    ReadInputRegistersResponse readInputRegistersResponse =
                            (ReadInputRegistersResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                    if (register.getCount() > 1) {
                        valueToReturn = createDouble(
                                (short) readInputRegistersResponse.getRegister(0).getValue(),
                                (short) readInputRegistersResponse.getRegister(1).getValue());
                    } else {
                        valueToReturn = readInputRegistersResponse.getRegister(0).getValue();
                    }
                    break;
                case DISCRETE_COIL:
                    ReadCoilsResponse readCoilsResponse =
                            (ReadCoilsResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                    valueToReturn = readCoilsResponse.getCoilStatus(0);
                    break;
                case DISCRETE_INPUT:
                    ReadInputDiscretesResponse readInputDiscretesResponse =
                            (ReadInputDiscretesResponse) subscribeRequest(RegisterFactory.getRequest(register, false, null));
                    valueToReturn = readInputDiscretesResponse.getDiscreteStatus(0);
                    break;
                case REGISTER_INPUT_CHART:
                    valueToReturn = readBytePacket(register);
                    break;

            }

            register.setLastValue(valueToReturn);
            return valueToReturn;

        } catch (ModbusException e) {

            logger.warn(e.getMessage());
            modbusConnect.disconnect2();
            return register.getLastValue();

        } catch (ClassCastException e) {

            logger.error("Cast Exception: ");
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

        int chartDataSize;
        if (size <= currRef)
            chartDataSize = size;
        else
            chartDataSize = size - currRef;

        Integer[] chartData = new Integer[chartDataSize];
        int stepSize = 125;
        int stepCount = chartDataSize / stepSize;
        int remainder = chartDataSize % stepSize;

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

    private static Double createDouble(short first, short second) {

        byte[] firstWord = new byte[2];
        byte[] secondWord = new byte[2];
        firstWord[0] = (byte) ((first >> 8) & 0xff);
        firstWord[1] = (byte) (first & 0xff);
        secondWord[0] = (byte) ((second >> 8) & 0xff);
        secondWord[1] = (byte) (second & 0xff);
        byte[] resultWord = new byte[firstWord.length + secondWord.length];
        System.arraycopy(firstWord, 0, resultWord, 0, firstWord.length);
        System.arraycopy(secondWord, 0, resultWord, firstWord.length, secondWord.length);
        return (double) ByteBuffer.wrap(resultWord).getFloat();

    }

    private static abstract class RegisterFactory {

        static ModbusRequest getRequest(ModbusMap reg, boolean isWriting, Object value) {

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
                // old variant of 2-word register creation for writing to ModBus
                // all holding registers at hardware side are defined as float, so values of all number types are converted to float
//                float floatValue;

//                if (value instanceof Integer) {
//
//                    floatValue = ((Integer) value).floatValue();
//                    byteData = ModbusUtil.floatToRegisters(floatValue);
//
//                } else{
//                    byteData = ModbusUtil.floatToRegisters(((Number) value).floatValue());
//                }

//                short firstWord = (short) (((byteData[0] & 0xFF) << 8) | (byteData[1] & 0xFF));
//                short secondWord = (short) (((byteData[2] & 0xFF) << 8) | (byteData[3] & 0xFF));
//                Register firstReg = new SimpleRegister(firstWord);
//                Register secondReg = new SimpleRegister(secondWord);
//                request = new WriteMultipleRegistersRequest(reg.getRef(), new Register[]{firstReg, secondReg});

                Register[] registers = new Register[reg.getCount()];
                /**Attention!!! Code for String-type is very specific.
                 * Works only if every symbol represents hex-digit (A-Fa-f0-9) and string-length == 4*(register words count).*/
                if (value instanceof String) {

                    String trimmed = ((String) value).trim();
                    if (trimmed.length() != reg.getCount() * 4) { throw new IllegalArgumentException("Incorrect code string length!"); }

                    Pattern p = Pattern.compile("[A-Fa-f0-9]*");
                    if (!p.matcher(trimmed).matches()) { throw new IllegalArgumentException("Incorrect code string characters!"); }

                    byteData = new byte[reg.getCount() * 2];

                    List<String> parts = new ArrayList<>();
                    int length = trimmed.length();
                    for (int i = 0; i < length; i += 2) {
                        parts.add(trimmed.substring(i, Math.min(length, i + 2)));
                    }
                    for (int i = 0; i < byteData.length; i++){
                        byteData[i] = (byte)(Integer.parseInt(parts.get(i),16));
                    }
                }
                else { // all holding registers are defined as float, so values of all number types are converted to float
                    byteData = ModbusUtil.floatToRegisters(((Number) value).floatValue());
                }

                for(int i = 0; i < byteData.length; i+=2) {
                    short word = (short) (((byteData[i] & 0xFF) << 8) | (byteData[i+1] & 0xFF));
                    registers[Math.min(byteData.length, i/2)] = new SimpleRegister(word);
                }
                request = new WriteMultipleRegistersRequest(reg.getRef(), registers);

            } else {

                Register register;
                if (value instanceof Double) {
                    double doubleValue = Double.parseDouble(value.toString());
                    register = new SimpleRegister((int) Math.round(doubleValue));
                } else
                    register = new SimpleRegister((int) value);

                request = new WriteMultipleRegistersRequest(reg.getRef(), new Register[]{register});

            }

            return request;

        }

    }

}
