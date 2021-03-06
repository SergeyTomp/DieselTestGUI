package fi.stardex.sisu.coding.siemens;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SiemensCoding {

    private static final Logger logger = LoggerFactory.getLogger(SiemensCoding.class);

    private static final String MASK = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";
    private static final String NO_CODING = "NO_CODING";
    private static Map<InjectorTest, List<Double>> mapOfResults = new HashMap<>();

    private static final String MAXIMUM_LOAD = "Maximum Load";
    private static final String PART_LOAD = "Part Load";
    private static final String IDLE = "Idle";
    private static final String PRE_INJECTION = "Pre-injection";
    private static final String CHECK_SUM = "Check Sum";

    private static final Map<String, Integer> TEST_COEFF = new HashMap<>();
    static{ TEST_COEFF.put(CHECK_SUM, 5); }

    public static List<String> calculate(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults, Injector injector) {

        switch (injector.getCodetype()) {

            case 2:
                TEST_COEFF.put(MAXIMUM_LOAD, 1);
                TEST_COEFF.put(PRE_INJECTION, 2);
                TEST_COEFF.put(PART_LOAD, 3);
                TEST_COEFF.put(IDLE, 4);
                break;
            case 1:
            default:
                TEST_COEFF.put(MAXIMUM_LOAD, 1);
                TEST_COEFF.put(PART_LOAD, 2);
                TEST_COEFF.put(IDLE, 3);
                TEST_COEFF.put(PRE_INJECTION, 4);
        }


        Map<InjectorTest, List<Double>> temp = new HashMap<>();

        for (Map.Entry<InjectorTest, FlowReportModel.FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            FlowReportModel.FlowResult flowTestResult = entry.getValue();

            temp.put(entry.getKey(), Arrays.asList(
                    flowTestResult.getDoubleValue_1(),
                    flowTestResult.getDoubleValue_2(),
                    flowTestResult.getDoubleValue_3(),
                    flowTestResult.getDoubleValue_4()));

        }

        mapOfResults.clear();
        mapOfResults.putAll(temp);

        Map<String, List<Integer>> preparedMap = convert(temp);
        return getCodeResult(preparedMap);
    }

    private static Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert) {

        Map<String, List<Integer>> convertedMap = new HashMap<>();

        for (Map.Entry<InjectorTest, List<Double>> entry : mapToConvert.entrySet()) {

            Double flowRange = entry.getKey().getFlowRange();

            List<Integer> convertedList = entry.getValue().stream().map(value -> convertToInt(value, entry.getKey().getNominalFlow(), flowRange)).collect(Collectors.toList());

            convertedMap.put(entry.getKey().toString(), convertedList);

        }

        logger.debug("1. Converted to int: {}", convertedMap);

        shiftValues(convertedMap);

        logger.debug("2. Shifted by 15: {}", convertedMap);

        List<Integer> checkSum = addCheckSum(convertedMap);

        convertedMap.put(CHECK_SUM, checkSum);

        logger.debug("4. Check Sum added: {}", convertedMap);

        return convertedMap;

    }

    private static int convertToInt(double value, Double nominalFlow, Double flowRange) {

        if (value == -99d)
            return -99;
        else {
            /** Deviation from central point calculation with result devided by 2 and finally limited within 50% of flowRange in case of range breakage */
            double delta = (nominalFlow - value) / 2;
            double deltaLimitAbs = (nominalFlow * (flowRange * 0.5 / 100));
            double flowRangeAbs = nominalFlow * (flowRange / 100);
            value = ((value >= (nominalFlow - flowRangeAbs)) && (value <= (nominalFlow + flowRangeAbs))) ? - delta : delta < 0 ? deltaLimitAbs : - deltaLimitAbs;
            value = (value / flowRangeAbs) * 15;

            return (int) Math.round(value);
        }
    }

    private static List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap) {

        List<Integer> resultCheckSum = new ArrayList<>();
        Map<String, Integer> testFlows = new HashMap<>();

        for (int i = 0; i < 4; i++) {

            int value;
            testFlows.clear();

            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

                testFlows.put(entry.getKey(), entry.getValue().get(i));
            }

            value = testFlows.entrySet()
                    .stream()
                    .filter(e -> e.getValue() != -99)
                    .peek(e -> e.setValue(e.getValue() * TEST_COEFF.get(e.getKey())))
                    .map(Map.Entry::getValue)
                    .reduce(Integer::sum).orElse(-1);

            resultCheckSum.add(value);
        }

        logger.debug("3. Check sum list: {}", resultCheckSum);
        return resultCheckSum;
    }

    private static void shiftValues(Map<String, List<Integer>> convertedMap) {

        for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

            for (int i = 0; i < entry.getValue().size(); i++) {

                int value = entry.getValue().get(i);

                if (value != -99) {
                    value = value + 15;
                    entry.getValue().set(i, value);
                }
            }
        }
    }

    private static List<String> getCodeResult(Map<String, List<Integer>> preparedMap){

        List<String> codeResult = new ArrayList<>();

        TreeMap<String, List<Integer>> sortedMap = new TreeMap<>(Comparator.comparingInt(TEST_COEFF::get));
        sortedMap.putAll(preparedMap);
        sortedMap.remove(CHECK_SUM);
        StringBuilder codeString = new StringBuilder();

        for (int i = 0; i < 4; i++) {

            codeString.append(getCheckSumCode(preparedMap.get(CHECK_SUM).get(i)));

            for (Map.Entry<String, List<Integer>> entry : sortedMap.entrySet()) {

                Integer flowValue = entry.getValue().get(i);
                codeString.append(flowValue != -99 ? getResultSymbol(flowValue) : "");
            }
            codeString.append(!allTestPassed(i) ? " *" : "");
            codeResult.add(codeString.toString());
            codeString.setLength(0);
        }
        return getCode(codeResult);
    }

    private static boolean allTestPassed(int ledNumber) {

        double nominalFlow;
        double flowRange;
        double flow;

        for (Map.Entry<InjectorTest, List<Double>> entry : mapOfResults.entrySet()) {

            nominalFlow = entry.getKey().getNominalFlow();
            flowRange = entry.getKey().getFlowRange();
            flow = entry.getValue().get(ledNumber);

            if (!(flow <= nominalFlow + flowRange && flow >= nominalFlow - flowRange)) {
                return false;
            }
        }
        return true;
    }

    private static String completeBinaryWithZeroes(final int decimal) {

        int size = 10;

        if (decimal < 0 || Integer.toBinaryString(decimal).length() > size)
            return NO_CODING;

        logger.debug("decimal: {}, size: {}", decimal, size);

        String binary = Integer.toBinaryString(decimal);

        logger.debug("binary: {}", binary);

        StringBuilder result = new StringBuilder(binary);

        for (int j = result.length(); j < size; j++) {

            result.insert(0, "0");

        }

        logger.debug("completed binary: {}", result.toString());

        return result.toString();
    }

    private static String getCheckSumCode(Integer checkSum) {

        if (checkSum < 0) {
            return "";
        }

        StringBuilder temp = new StringBuilder();

        String binaryString = completeBinaryWithZeroes(checkSum);
        int binaryValue = Integer.parseInt(binaryString, 2);
        binaryValue = ((binaryValue & 0b1111110000) << 1) | (binaryValue & 0b0000001111);
        char firstSymbol = getResultSymbol((binaryValue & 0b1111100000) >> 5);
        char secondSymbol = getResultSymbol(binaryValue & 0b0000011111);

        return temp.append(firstSymbol).append(secondSymbol).toString();
    }

    private static char getResultSymbol(Integer value) {

        return MASK.charAt(value);
    }

    private static List<String> getCode(List<String> codeResult) {

        List<String> resultList = new ArrayList<>();
        codeResult.forEach(result -> resultList.add((result.length() <= 7 && result.length() > 2 && result.contains("*"))? "Coding impossible" : result.length() <= 2 ? "" : result));
        return resultList;
    }
}
