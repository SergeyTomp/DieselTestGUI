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

    public DelphiC2ICoder(Injector injector, List<Integer> activeLEDs, List<Result> oldCodes) {
        super(oldCodes);
        super.activeLEDs = activeLEDs;
        super.injectorCoefficient = injector.getCoefficient();
    }

    @Override
    public List<String> buildCode(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {

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
                .append(completeBinaryWithZeroes(data.get("Test Point 08"), getBits("Test Point 08")))
                .append(completeBinaryWithZeroes(data.get("Test Point 07"), getBits("Test Point 07")))
                .append(completeBinaryWithZeroes(data.get("Test Point 06"), getBits("Test Point 06")))
                .append(completeBinaryWithZeroes(data.get("Test Point 05"), getBits("Test Point 05")))
                .append(completeBinaryWithZeroes(data.get("Test Point 04"), getBits("Test Point 04")))
                .append(completeBinaryWithZeroes(data.get("Test Point 03"), getBits("Test Point 03")))
                .append(completeBinaryWithZeroes(data.get("Test Point 02"), getBits("Test Point 02")))
                .append(completeBinaryWithZeroes(data.get("Test Point 01"), getBits("Test Point 01")));

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
            case "Test Point 01":
            case "Test Point 03":
            case "Test Point 05":
            case "Test Point 07":
                return 5;
            case "Test Point 02":
            case "Test Point 04":
                return 8;
            case "Test Point 06":
            case "Test Point 08":
                return 7;
            default:
                logger.error("Wrong test for Delphi C2I Coefficient!");
                return 0;
        }
    }

    private Map<InjectorTest, List<Double>> getSourceMap(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        Map<InjectorTest, List<Double>> temp = new HashMap<>();

        for (Map.Entry<InjectorTest, FlowReportModel.FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            if (entry.getKey().getTestName().isTestPoint()) {

                FlowReportModel.FlowResult flowTestResult = entry.getValue();
                Double nominalFlow = entry.getKey().getNominalFlow();

                temp.put(entry.getKey(), Arrays.asList(
                        flowTestResult.getDoubleValue_1() - nominalFlow,
                        flowTestResult.getDoubleValue_2() - nominalFlow,
                        flowTestResult.getDoubleValue_3() - nominalFlow,
                        flowTestResult.getDoubleValue_4() - nominalFlow));
            }
        }
        return temp;
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
