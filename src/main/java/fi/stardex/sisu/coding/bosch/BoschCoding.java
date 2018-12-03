package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.model.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.ISADetectionController.ISAResult;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class BoschCoding {

    private static final Logger logger = LoggerFactory.getLogger(BoschCoding.class);

    private static final String MASK = "ABCDEFGHIKLMNOPRSTUVWXYZ12345678";

    private static final String NO_CODING = "NO_CODING";

    private static final Map<String, Integer> CODE_FIELD_SIZE = new HashMap<>() {{
        put("Emission Point", 7);
        put("Idle", 5);
        put("Maximum Load", 7);
        put("Pre-injection", 5);
        put("Pre-injection 2", 5);
        put("Check Sum", 4);
    }};

    public static List<String> calculate(ObservableMap<InjectorTest, FlowResult> mapOfFlowTestResults) {

        Map<InjectorTest, List<Double>> temp = new HashMap<>();

        for (Map.Entry<InjectorTest, FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            FlowResult flowTestResult = entry.getValue();

            temp.put(entry.getKey(), Arrays.asList(
                    flowTestResult.getFlow1_double(),
                    flowTestResult.getFlow2_double(),
                    flowTestResult.getFlow3_double(),
                    flowTestResult.getFlow4_double()));

        }

        Map<String, List<Integer>> preparedMap = convert(temp);

        return getCodeResult(preparedMap);

    }

    private static Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert) {

        Map<String, List<Integer>> convertedMap = new HashMap<>();

        for (Map.Entry<InjectorTest, List<Double>> entry : mapToConvert.entrySet()) {

            List<Integer> convertedList = entry.getValue().stream().map(value -> convertToInt(value, entry.getKey().getNominalFlow())).collect(Collectors.toList());

            convertedMap.put(entry.getKey().toString(), convertedList);

        }

        logger.debug("1. Converted to int: {}", convertedMap);

        List<Integer> checkSum = addCheckSum(convertedMap);

        convertNegativeValues(convertedMap);

        convertedMap.put("Check Sum", checkSum);

        logger.debug("3. Converted map: {}", convertedMap);

        return convertedMap;

    }

    private static int convertToInt(double value, Double nominalFlow) {

        if (value == -99d)
            return -99;
        else {
            value = nominalFlow - value;
            int coefficient = getInjector().getCoefficient();
            switch (coefficient) {
                case 0:
                    return (int) (value / 0.1);
                case 1:
                    return (int) (value / 0.15);
                case 2:
                    return (int) (value / 0.25);
                default:
                    logger.debug("Invalid bosch coding coefficient: " + coefficient + ", used 0 instead");
                    return (int) (value / 0.1);
            }
        }

    }

    private static List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap) {

        List<Integer> resultCheckSum = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            int value = 0;

            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

                value += entry.getValue().stream().filter(element -> element != -99).reduce(Integer::sum).orElse(0);

            }

            if (value != -99) {
                value += getInjector().getCoefficient();
                value = (value & 15) + ((value & 240) >> 4) + 1 & 15;
            }
            resultCheckSum.add(value);
        }

        logger.debug("2. Check sum list: {}", resultCheckSum);

        return resultCheckSum;

    }

    private static void convertNegativeValues(Map<String, List<Integer>> convertedMap) {

        for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

            for (int i = 0; i < entry.getValue().size(); i++) {

                int value = entry.getValue().get(i);

                if (value != -99) {
                    value = value < 0 ? value + (int) Math.pow(2d, CODE_FIELD_SIZE.get(entry.getKey())) : value;
                    entry.getValue().set(i, value);
                }
            }
        }
    }

    private static List<String> getCodeResult(Map<String, List<Integer>> preparedMap) {

        List<String> codeResult = new ArrayList<>();

        CodeTypes codeTypes = CodeTypes.getCodeType(getInjector().getCodetype());

        for (int i = 0; i < 4; i++) {

            StringBuilder resultString = new StringBuilder();

            Integer checkSum = preparedMap.get("Check Sum").get(i);

            Integer emissionPointValue = preparedMap.get("Emission Point").get(i);
            resultString.append(emissionPointValue != -99 ? completeBinaryWithZeroes(emissionPointValue, CODE_FIELD_SIZE.get("Emission Point")) : "");

            Integer idleValue = preparedMap.get("Idle").get(i);
            resultString.append(idleValue != -99 ? completeBinaryWithZeroes(idleValue, CODE_FIELD_SIZE.get("Idle")) : "");

            Integer maximumLoadValue = preparedMap.get("Maximum Load").get(i);
            resultString.append(maximumLoadValue != -99 ? completeBinaryWithZeroes(maximumLoadValue, CODE_FIELD_SIZE.get("Maximum Load")) : "");

            Integer preInjectionValue = preparedMap.get("Pre-injection").get(i);
            resultString.append(preInjectionValue != -99 ? completeBinaryWithZeroes(preInjectionValue, CODE_FIELD_SIZE.get("Pre-injection")) : "");

            switch (codeTypes) {

                case ONE:
                case FIVE:
                    if (checkSum != -99)
                        resultString
                                .append(completeBinaryWithZeroes(checkSum, CODE_FIELD_SIZE.get("Check Sum")))
                                .append(completeBinaryWithZeroes(getInjector().getCoefficient(), 2));
                    break;
                case THREE:
                    if (checkSum != -99)
                        resultString
                                .append(completeBinaryWithZeroes(checkSum, CODE_FIELD_SIZE.get("Check Sum")))
                                .append(completeBinaryWithZeroes(getInjector().getCoefficient(), 2))
                                .append("00");
                    break;
                case TWO:
                    if (preparedMap.containsKey("Pre-injection 2")) {
                        Integer preInjection2 = preparedMap.get("Pre-injection 2").get(i);
                        if (checkSum != -99)
                            resultString
                                    .append(completeBinaryWithZeroes(checkSum, CODE_FIELD_SIZE.get("Check Sum")))
                                    .append(completeBinaryWithZeroes(getInjector().getCoefficient(), 2));
                        if (preInjection2 != -99)
                            resultString
                                    .append(completeBinaryWithZeroes(preInjection2, CODE_FIELD_SIZE.get("Pre-injection 2")));
                    }
                    break;
                case FOUR:
                    if (preparedMap.containsKey("Pre-injection 2")) {
                        Integer preInjection2 = preparedMap.get("Pre-injection 2").get(i);
                        if (preInjection2 != -99)
                            resultString
                                    .append(completeBinaryWithZeroes(preInjection2, CODE_FIELD_SIZE.get("Pre-injection 2")))
                                    .append(completeBinaryWithZeroes(getInjector().getCoefficient(), 2))
                                    .append("0").append(completeBinaryWithZeroes(checkSum, CODE_FIELD_SIZE.get("Check Sum")));
                    }
                    break;

            }

            codeResult.add(resultString.toString());

            logger.debug("4. result string {}: {}", i, resultString.toString());

        }

        return getCode(codeResult, codeTypes);

    }

    private static String completeBinaryWithZeroes(final int decimal, final int size) {

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

    private static List<String> getCode(List<String> codeResult, CodeTypes codeTypes) {

        List<String> resultList = new ArrayList<>();

        switch (codeTypes) {
            case ONE: case TWO:
                codeResult.forEach(result -> resultList.add(result.contains(NO_CODING) ? "Coding impossible" : prepareStringResult(result, codeTypes, false)));
                break;
            case THREE: case FOUR:
                codeResult.forEach(result -> resultList.add(result.contains(NO_CODING) ? "Coding impossible" : prepareStringResult(result, codeTypes, true)));
                break;
            case FIVE:
                List<ISAResult> isaResult = ISADetectionController.getIsaResult();
                for (int i = 0; i < codeResult.size(); i++) {
                    if (codeResult.get(i).contains(NO_CODING))
                        resultList.add("Coding impossible");
                    else {
                        switch (isaResult.get(i).getIsaResultState()) {
                            case INVALID:
                                resultList.add("Injector is out of parameters. ISA detection impossible.");
                                break;
                            case VALID:
                                resultList.add(prepareStringResult(codeResult.get(i), codeTypes, false) + isaResult.get(i).getIsa_char());
                                break;
                            case OFF:
                                resultList.add("");
                                break;
                        }
                    }
                }
                break;
        }

        logger.debug("5. result: {}", resultList);

        return resultList;

    }

    private static String prepareStringResult(String binaryResult, CodeTypes codeType, boolean isHEX) {

        StringBuilder result = new StringBuilder();

        if (!binaryResult.isEmpty() && (codeType.getSize() == binaryResult.length())) {

            for (int j = 0; j < codeType.getSize(); j += codeType.getStep()) {

                String binaryLine = binaryResult.substring(j, j + codeType.getStep());

                int decimal = Integer.parseInt(binaryLine, 2);

                if (isHEX)
                    result.append(Integer.toString(decimal, 16));
                else
                    result.append(MASK.charAt(decimal));

            }

        }

        if (isHEX)
            return result.toString().toUpperCase();
        else
            return result.toString();

    }

}

enum CodeTypes {

    ONE(1, 5, 30),
    TWO(2, 5, 35),
    THREE(3, 4, 32),
    FOUR(4, 4, 36),
    FIVE(5, 5, 30);

    private final Integer codeType;
    private final Integer step;
    private final Integer size;

    CodeTypes(Integer codeType, Integer step, Integer size) {

        this.codeType = codeType;
        this.step = step;
        this.size = size;

    }

    public static CodeTypes getCodeType(Integer codeType) {

        for (CodeTypes codeTypes : values()) {
            if (codeTypes.codeType.equals(codeType))
                return codeTypes;
        }

        return ONE;

    }

    public Integer getStep() {
        return step;
    }

    public Integer getSize() {
        return size;
    }

}
