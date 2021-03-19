package fi.stardex.sisu.coding.delphi;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DelphiC2ICoder extends DelphiCoder {

    private static Logger logger = LoggerFactory.getLogger(DelphiC2ICoder.class);

    public DelphiC2ICoder(Injector injector,
                          List<Integer> activeLEDs,
                          List<Result> oldCodes,
                          ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        super(oldCodes);
        super.activeLEDs = activeLEDs;
        super.injectorCoefficient = injector.getCoefficient();
        super.mapOfFlowTestResults = mapOfFlowTestResults;
    }

    @Override
    public List<String> buildCode() {

        Map<InjectorTest, List<Double>> temp = getSourceMap(mapOfFlowTestResults);

        activeLEDs.forEach(i -> {
            Map<String,Integer> ledData = new HashMap<>();
            temp.forEach((test,value) -> ledData.put(test.toString(), getCodeValue(test, value.get(i - 1))));
            previousResultList.set(i - 1, makeResultString(ledData));
        });
        return previousResultList;
    }

    private int getCodeValue(InjectorTest injectorTest, double flow) {

        int range = (int) Math.pow(2, getBits(injectorTest.getTestName().toString()) - 1);
        double nominalFlow = injectorTest.getNominalFlow();
        double flowRange = nominalFlow * (injectorTest.getFlowRange() / 100);

        int result;
        int roughDivisor = 2;

        double deviation = nominalFlow - flow;
        result = (int) (range * deviation / flowRange);

        if (result > range) {
            result = range;
        } else if (result < -range) {
            result = -range;
        }
        result = result / roughDivisor + range;
        return result;
    }

    private String makeResultString(Map<String, Integer> data) {

        StringBuilder resultString = new StringBuilder();
        resultString
                .append(completeBinaryWithZeroes(injectorCoefficient, 3))
                .append(completeBinaryWithZeroes(16, 5));

        logger.info("1. resultStr: {}", resultString);

        resultString
                .append(completeBinaryWithZeroes(data.get(TP08), getBits(TP08)))
                .append(completeBinaryWithZeroes(data.get(TP07), getBits(TP07)))
                .append(completeBinaryWithZeroes(data.get(TP06), getBits(TP06)))
                .append(completeBinaryWithZeroes(data.get(TP05), getBits(TP05)))
                .append(completeBinaryWithZeroes(data.get(TP04), getBits(TP04)))
                .append(completeBinaryWithZeroes(data.get(TP03), getBits(TP03)))
                .append(completeBinaryWithZeroes(data.get(TP02), getBits(TP02)))
                .append(completeBinaryWithZeroes(data.get(TP01), getBits(TP01)));

        logger.info("2. resultStr: {}", resultString);

        String temp = resultString.toString().substring(2);
        logger.info("3. temp: {}", temp);

        List<String> hexList = new ArrayList<>();

        for (int j = 0; j < 56; j += 8) {
            hexList.add(intToHex(Integer.valueOf(temp.substring(j, j + 8), 2)));
        }
        logger.info("4. hexList: {}", hexList);

        String hexCode = null;
        int checkSum = 0;

        switch (injectorCoefficient) {

            case 0:
                for (int j = hexList.size() - 1; j > -1; j--){
                    checkSum += Integer.valueOf(hexList.get(j), 16) * (j + 1);
                }
                checkSum = (checkSum % 64) * 4;
                logger.info("5. checkSum: {}", checkSum);
                hexCode = addHexLines(hexList, checkSum);
                break;
            case 1:
                checkSum = (int) (61 - (Long.valueOf("001" + temp.substring(1, 56), 2)) % 61) * 4;
                logger.info("6. checkSum: {}", checkSum);
                hexCode = addHexLines(hexList, checkSum);
                break;
        }
        logger.info("7. Final code: {}", hexCode);
        return hexCode;
    }

    private int getBits(String testName) {

        switch (testName) {
            case TP01:
            case TP03:
            case TP05:
            case TP07:
                return 5;
            case TP02:
            case TP04:
                return 8;
            case TP06:
            case TP08:
                return 7;
            default:
                logger.error("Wrong test for Delphi C2I Coefficient!");
                return 0;
        }
    }

    private static String completeBinaryWithZeroes(int value, int size) {

        StringBuilder result = new StringBuilder(Integer.toBinaryString(value));

        for (int i = result.length(); i < size; i++) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    private static String intToHex(int number) {

        String hex = Integer.toHexString(number);

        if (hex.length() == 1){
            hex = "0" + hex;
        }
        return hex.substring(hex.length() - 2).toUpperCase();
    }

    private static String addHexLines(List<String> hexList, int checkSum) {

        StringBuilder hexCheckSum = new StringBuilder(intToHex(checkSum));

        for (String hexLine : hexList){
            hexCheckSum.append(hexLine);
        }
        return hexCheckSum.toString();
    }
}
