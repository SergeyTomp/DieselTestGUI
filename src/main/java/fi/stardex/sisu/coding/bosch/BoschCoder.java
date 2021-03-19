package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.coding.Coder;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BoschCoder implements Coder {

    final Logger logger = LoggerFactory.getLogger(BoschCoder.class);

    final String NO_CODING = "NO_CODING";
    final String MASK = "ABCDEFGHIKLMNOPRSTUVWXYZ12345678";
    final int CHANNELS_QTY = 4;
    private final Random randomFlow = new Random();
    double valueDivider;
    List<String> baseTestsList;
    List<String> previousResultList;
    private List<Result> oldCodes;
    int codeSize;
    int codeStep;
    boolean isHEX;
    int injectorCoefficient;
    Map<CodeField, Integer> fieldLengthMap;
    List<Integer> activeLEDs;
    ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults;

    BoschCoder( List<Result> oldCodes) {
        this.oldCodes = oldCodes;
        fieldLengthMap  = new HashMap<>();
        makePreviousResultsList();
    }

    protected abstract List<String> getCodeResult(Map<String, List<Integer>> preparedMap);
//    protected Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert);
//    protected abstract List<String> getCode(List<String> codeResult);
//    protected abstract List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap);

    Map<InjectorTest, List<Double>> getSourceMap(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        Map<InjectorTest, List<Double>> temp = new HashMap<>();

        for (Map.Entry<InjectorTest, FlowReportModel.FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            FlowReportModel.FlowResult flowTestResult = entry.getValue();

            temp.put(entry.getKey(), Arrays.asList(
                    flowTestResult.getDoubleValue_1(),
                    flowTestResult.getDoubleValue_2(),
                    flowTestResult.getDoubleValue_3(),
                    flowTestResult.getDoubleValue_4()));
        }
        return temp;
    }

    protected List<String> getCode(List<String> codeResult) {

        List<String> resultList = new ArrayList<>(previousResultList);
        for (int i = 0; i < codeResult.size(); i++) {
            if (activeLEDs.contains(i + 1)) {
                resultList.set(i, codeResult.get(i).equals(NO_CODING) ? "Coding impossible" : prepareStringResult(codeResult.get(i)));
            }
        }
        logger.info("6. results list: {}", resultList);
        return resultList;
    }

    protected List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap) {

        List<Integer> resultCheckSum = new ArrayList<>();
        List <Integer> injectorFlows = new ArrayList<>();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            int value = 0;
            injectorFlows.clear();

            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {
                injectorFlows.add(entry.getValue().get(i));
            }
            value += injectorFlows.stream().filter(element -> element != -99).reduce(Integer::sum).orElse(0);
            if (value != -99) {
                value += injectorCoefficient;
            }
            value = (value & 15) + ((value & 240) >> 4) + 1 & 15;
            resultCheckSum.add(value);
        }
        logger.info("2. Check sum list: {}", resultCheckSum);
        return resultCheckSum;
    }

    protected Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert) {

        Set<Map.Entry<InjectorTest, List<Double>>> filteredSet = mapToConvert.entrySet()
                .stream()
                .filter(es -> baseTestsList.contains(es.getKey().getTestName().toString()))
                .collect(Collectors.toSet());

        Map<String, List<Integer>> convertedMap = new HashMap<>();

        for (Map.Entry<InjectorTest, List<Double>> entry : filteredSet) {

            double nominalFlow = entry.getKey().getNominalFlow();
            Double codingRange = entry.getKey().getCodingRange() == null ? 0 : entry.getKey().getCodingRange();

            int fieldLength = getFieldLength(entry.getKey().getTestName().toString());
            List<Integer> convertedList = entry.getValue().stream()
                    .map(value -> convertToInt(value, nominalFlow, fieldLength, codingRange))
                    .collect(Collectors.toList());
            convertedMap.put(entry.getKey().toString(), convertedList);
        }
        logger.info("1. Converted to int: {}", convertedMap);
        return convertedMap;
    }

    String prepareStringResult(String binaryResult){

        logger.info("5. final binary string: {}", binaryResult);

        StringBuilder result = new StringBuilder();

        if (!binaryResult.isEmpty() && (codeSize == binaryResult.length())) {

            for (int j = 0; j < codeSize; j += codeStep) {

                String binaryLine = binaryResult.substring(j, j + codeStep);
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

    private void makePreviousResultsList() {

        previousResultList = new LinkedList<>();
        if (oldCodes.isEmpty()) {
            previousResultList.add("");
            previousResultList.add("");
            previousResultList.add("");
            previousResultList.add("");
        } else {
            previousResultList.add(oldCodes.get(0) != null ? oldCodes.get(0).getSubColumn1() : "" );
            previousResultList.add(oldCodes.get(1) != null ? oldCodes.get(1).getSubColumn1() : "" );
            previousResultList.add(oldCodes.get(2) != null ? oldCodes.get(2).getSubColumn1() : "" );
            previousResultList.add(oldCodes.get(3) != null ? oldCodes.get(3).getSubColumn1() : "" );
        }
    }

    int convertToInt(double value, Double nominalFlow, int fieldLength, double codingRange) {

        if (value == -99d)
            return -99;
        else {
            double delta = nominalFlow - value;
            /**  Deviation from central point calculation with limitation  within 49 % of flowRange*/
//            double deltaLimitAbs = (nominalFlow * (flowRange * 0.49 / 100));
//            value = ((value >= (nominalFlow - deltaLimitAbs)) && (value <= (nominalFlow + deltaLimitAbs))) ? delta : delta < 0 ? deltaLimitAbs : - deltaLimitAbs;

            /** Deviation from central point calculation without limitation*/
//            value = nominalFlow - value;

            /** Limitation of deviation value to corresponding field bit size*/
//            int valueToReturn = (int) (Math.round(value / valueDivider));
//            int minValue = - (int) (Math.pow(2d, fieldLength));
//            int maxValue = (int) (Math.pow(2d, fieldLength) - 1);
//            valueToReturn = valueToReturn >= minValue && valueToReturn <= maxValue ? valueToReturn : valueToReturn < 0 ? minValue : maxValue;

//            return (int) (value / valueDivider);

            /** New calculation of deviation from central point value with limitation to 50%*/
//            int sign = delta < 0 ? -1 : 1;
//            int valueToReturn = (int) (Math.round((delta / valueDivider) / 2));
//            valueToReturn = Math.abs(valueToReturn) > Math.pow(2d, fieldLength - 2) ? (sign * (int)Math.pow(2d, fieldLength - 2)) : valueToReturn;
//            return valueToReturn;

            /** New modified calculation of deviation from central point value:
             *  limitation to 50%
             *  additional limitation to coding_range (special field in injector-test DB-table)
             */
            int sign = delta < 0 ? -1 : 1;
            double valueToReturn = Math.abs(delta / 2);

            if (codingRange != 0 && valueToReturn > codingRange) {
                valueToReturn =  codingRange;
            }

            valueToReturn =  valueToReturn / valueDivider;

            if (valueToReturn > Math.pow(2d, fieldLength - 2)) {
                valueToReturn = Math.pow(2d, fieldLength - 2);
            }

            return (int)(Math.abs(valueToReturn) * sign);
        }
    }

    void convertNegativeValues(Map<String, List<Integer>> convertedMap) {

        int fieldLength;
        for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

            for (int i = 0; i < entry.getValue().size(); i++) {
                int value = entry.getValue().get(i);

                if (value != -99) {
                    fieldLength = getFieldLength(entry.getKey());
                    value = value < 0 ? value + (int) Math.pow(2d, fieldLength) : value;
                    entry.getValue().set(i, value);
                }
            }
        }
    }

    int getFieldLength(String testName){

        CodeField field = CodeField.getField(testName);
        return fieldLengthMap.get(field);
    }

    String completeBinaryWithZeroes(final int decimal, final int size) {

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

    List<Double> getRandomFlowsList() {

        List<Double> randomFlows = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            randomFlows.add((randomFlow.nextInt(3) - 1) * valueDivider * 2);
        }
        return randomFlows;
    }

    void addLostTests(Map<InjectorTest, List<Double>> testsMap, List<String> toExclude) {

        List<String> baseTestNames = baseTestsList.stream().filter(name -> !toExclude.contains(name)).collect(Collectors.toList());
        List<String> factTestNames = testsMap.keySet().stream().map(t -> t.getTestName().toString()).collect(Collectors.toList());
        baseTestNames.stream()
                .filter(name -> !factTestNames.contains(name))
                .forEach(name -> testsMap.put(new InjectorTest(new TestName(name), 0d, 100d), getRandomFlowsList()));
    }

    double getDivider(int k_coeff) {
        switch (k_coeff) {
            case 0:
                return 0.1;
            case 1:
                return 0.15;
            case 2:
                return 0.25;
            case 3:
                return 0.5;
            default:
                return 0.1;
        }
    }
}
