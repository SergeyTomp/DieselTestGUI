package fi.stardex.sisu.util.storage;

import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;

public class CurrentVAPStorage {

//    private final FirstIBoardThree firstIBoardThree;
//    private final FirstIBoardFour firstIBoarFoure;
//
//    private final HoldTime2BoardOne holdTime2BoardOne;
//    private final HoldTime2BoardTwo holdTime2BoardTwo;
//    private final HoldTime2BoardThree holdTime2BoardThree;
//    private final HoldTime2BoardFour holdTime2BoardFour;
//
//    private final FirstCurrentBoardOne firstCurrentBoardOne;
//    private final FirstCurrentBoardTwo firstCurrentBoardTwo;
//    private final FirstCurrentBoardThree firstCurrentBoardThree;
//    private final FirstCurrentBoardFour firstCurrentBoardFour;
//
//    private final SecondCurrentBoardOne secondCurrentBoardOne;
//    private final SecondCurrentBoardTwo secondCurrentBoardTwo;
//    private final SecondCurrentBoardThree secondCurrentBoardThree;
//    private final SecondCurrentBoardFour secondCurrentBoardFour;

    private int widthForSend = 1000;
    private int firstWForSend;
    private int batteryUForSend;
    private int boostUForSend;
    private double boostIForSend;
    private double firstIForSend;
    private double secondIForSend;
    private int negativeU1ForSend;
    private int negativeU2ForSend;

    private ModbusRegisterProcessor ultimaModbusWriter;

    public CurrentVAPStorage(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }

    public void calculateAndSet(int firstW, int batteryU, int boostU, double firstI, double boostI,
                                         double secondI, int negativeU1, int negativeU2) {
        checkFirstW(firstW);
        checkFirstIAndBoostI(firstI, boostI);
        checkFirstIAndSecondI(firstI, secondI);
        checkNegatives(negativeU1, negativeU2);
        batteryUForSend = batteryU;
        boostUForSend = boostU;
        sendToUltima();
    }

    private void checkFirstW(int firstW) {
        if (firstW > widthForSend - 30)
            firstWForSend = widthForSend - 30;
        else
            firstWForSend = firstW;
    }

    private void checkFirstIAndBoostI(double firstI, double boostI) {
        if(firstI > boostI) {
            boostIForSend = boostI;
            firstIForSend = boostI - 0.5;
        } else if(firstI == boostI) {
            boostIForSend = boostI + 0.5;
            firstIForSend = firstI;
        } else {
            boostIForSend = boostI;
            firstIForSend = firstI;
        }
    }

    private void checkFirstIAndSecondI(double firstI, double secondI) {
        if(secondI >= firstI)
            secondIForSend = firstI - 0.5;
        else
            secondIForSend = secondI;
    }

    private void checkNegatives(int negativeU1, int negativeU2) {
        if(negativeU1 < negativeU2) {
            negativeU1ForSend = negativeU1;
            negativeU2ForSend = negativeU1 - 5;
        } else {
            negativeU1ForSend = negativeU1;
            negativeU2ForSend = negativeU2;
        }
    }

    private void sendToUltima() {
        sendFirstI();
    }

    private void sendFirstI() {
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardOne, firstIForSend);
        ultimaModbusWriter.add(ModbusMapUltima.FirstIBoardTwo, firstIForSend);
    }
}
