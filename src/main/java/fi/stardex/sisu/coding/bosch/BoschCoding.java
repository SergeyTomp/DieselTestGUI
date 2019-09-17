package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.model.cr.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController;
import fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResult;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.bosch.BitFields.*;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.*;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState.INVALID;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState.OFF;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class BoschCoding {

    private static final Logger logger = LoggerFactory.getLogger(BoschCoding.class);

    private static final String MASK = "ABCDEFGHIKLMNOPRSTUVWXYZ12345678";
    private static final String NO_CODING = "NO_CODING";
    private final static int CHANNELS_QTY = 4;
    private static int injectorCoefficient;
    private static double valueDivider;
    private static int injectorIntCodeType;
    private static CodeTypes injectorCodeType;
    private static Random randomFlow = new Random();

    public static List<String> calculate(ObservableMap<InjectorTest, FlowResult> mapOfFlowTestResults) {

        Map<InjectorTest, List<Double>> temp = new HashMap<>();
        Injector injector = getInjector();
        injectorCodeType = CodeTypes.getCodeType(injector.getCodetype());
        injectorCoefficient = injector.getCoefficient();
        valueDivider = injectorCodeType.divider(injectorCoefficient);
        injectorIntCodeType = injectorCodeType.getCodeType();

        for (Map.Entry<InjectorTest, FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            FlowResult flowTestResult = entry.getValue();

            temp.put(entry.getKey(), Arrays.asList(
                    flowTestResult.getFlow1_double(),
                    flowTestResult.getFlow2_double(),
                    flowTestResult.getFlow3_double(),
                    flowTestResult.getFlow4_double()));

        }

        /**In case of real tests activation for codeType == 0 do not forget to remove this fake results generation */
        if (injectorCodeType == CodeTypes.ZERO) {

            if (temp.keySet().stream().noneMatch(k -> k.getTestName().toString().equals(IDLE.toString()))) {

                InjectorTest idleTest = new InjectorTest(new TestName(IDLE.toString()), 1d, 100d);
                temp.put(idleTest, Arrays.asList(1d, 1d, 1d, 1d));
            }

            if (temp.keySet().stream().noneMatch(k -> k.getTestName().toString().equals(PRE_INJ_2.toString()))) {

                InjectorTest preInjection2Test = new InjectorTest(new TestName(PRE_INJ_2.toString()), 1d, 100d);
                temp.put(preInjection2Test, Arrays.asList(1d, 1d, 1d, 1d));
            }

            switch (injectorCoefficient) {
                case 0:

                    InjectorTest reserveTest = new InjectorTest(new TestName(RESERVE.toString()), 1d, 100d);
                    InjectorTest restTest = new InjectorTest(new TestName(REST.toString()), 1d, 100d);
                    temp.put(reserveTest, Arrays.asList(1d, 1d, 1d, 1d));
                    temp.put(restTest, Arrays.asList(1d, 1d, 1d, 1d));
                    break;
                default:

                    InjectorTest preInjection3Test = new InjectorTest(new TestName(PRE_INJ_3.toString()), 0d, 100d);
                    InjectorTest kemTest = new InjectorTest(new TestName(KEM.toString()), 0d, 100d);

                    temp.put(preInjection3Test, getRandomFlowsList());
                    temp.put(kemTest, getRandomFlowsList());
            }
        }

        Map<String, List<Integer>> preparedMap = convert(temp);

        return getCodeResult(preparedMap);

    }

    private static Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert) {

        Set<Map.Entry<InjectorTest, List<Double>>> filteredSet = mapToConvert.entrySet()
                .stream()
                .filter(es -> injectorCodeType.getTestsList().contains(es.getKey().getTestName().toString()))
                .collect(Collectors.toSet());

        Map<String, List<Integer>> convertedMap = new HashMap<>();

        for (Map.Entry<InjectorTest, List<Double>> entry : filteredSet) {

//            double flowRange = entry.getKey().getFlowRange();
            double nominalFlow = entry.getKey().getNominalFlow();

            int fieldLength = getFieldLength(entry.getKey().getTestName().toString());

            List<Integer> convertedList = entry.getValue().stream().map(value -> convertToInt(value, nominalFlow, fieldLength)).collect(Collectors.toList());

            convertedMap.put(entry.getKey().toString(), convertedList);

        }

        logger.debug("1. Converted to int: {}", convertedMap);

        List<Integer> checkSum = addCheckSum(convertedMap);

        convertNegativeValues(convertedMap);

        convertedMap.put("Check Sum", checkSum);

        logger.debug("3. Converted map: {}", convertedMap);

        return convertedMap;

    }

    private static int convertToInt(double value, Double nominalFlow, int fieldLength) {

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
            int sign = delta < 0 ? -1 : 1;
            int valueToReturn = (int) (Math.round((delta / valueDivider) / 2));
            valueToReturn = Math.abs(valueToReturn) > Math.pow(2d, fieldLength - 2) ? (sign * (int)Math.pow(2d, fieldLength - 2)) : valueToReturn;
            return valueToReturn;
        }
    }

    private static List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap) {

        List<Integer> resultCheckSum = new ArrayList<>();
        List <Integer> injectorFlows = new ArrayList<>();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            int value = 0;
            int isaCharIndex = 32;
            injectorFlows.clear();

            /** Corrected variant of summing - now we get flows sum individually for very channel*/
            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

                injectorFlows.add(entry.getValue().get(i));
            }
            value += injectorFlows.stream().filter(element -> element != -99).reduce(Integer::sum).orElse(0);

            if (value != -99) {
                value += injectorCoefficient;
                if (injectorCodeType == CodeTypes.FIVE) {
                    ISAResultState isaResultState = getIsaResult().get(i).getIsaResultState();
                    if (isaResultState != INVALID && isaResultState != OFF) {
                        System.err.println(getIsaResult().get(i).getIsa_char());
                        isaCharIndex = MASK.indexOf(getIsaResult().get(i).getIsa_char());
                    }
                    value += isaCharIndex;
                }
                value = (value & 15) + ((value & 240) >> 4) + 1 & 15;
            }
            resultCheckSum.add(value);
        }

        logger.debug("2. Check sum list: {}", resultCheckSum);

        return resultCheckSum;
    }

    private static void convertNegativeValues(Map<String, List<Integer>> convertedMap) {

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

    private static int getFieldLength(String testName){

        return Arrays
                .stream(BitFields.values())
                .filter(v -> v.toString().equals(testName))
                .findFirst()
                .orElseThrow()
                .fieldLength(injectorIntCodeType, injectorCoefficient);
    }

    private static List<String> getCodeResult(Map<String, List<Integer>> preparedMap) {

        List<String> codeResult = new ArrayList<>();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            StringBuilder resultString = new StringBuilder();

            int checkSum = preparedMap.get(CHECK_SUM.toString()).get(i);

            int emissionPointValue = preparedMap.get(EMISSION_POINT.toString()).get(i);
            resultString.append(emissionPointValue != -99 ? completeBinaryWithZeroes(emissionPointValue, EMISSION_POINT.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

            int idleValue = preparedMap.get(IDLE.toString()).get(i);
            resultString.append(idleValue != -99 ? completeBinaryWithZeroes(idleValue, IDLE.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

            int maximumLoadValue = preparedMap.get(MAX_LOAD.toString()).get(i);
            resultString.append(maximumLoadValue != -99 ? completeBinaryWithZeroes(maximumLoadValue, MAX_LOAD.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

            int preInjectionValue = preparedMap.get(PRE_INJ.toString()).get(i);
            resultString.append(preInjectionValue != -99 ? completeBinaryWithZeroes(preInjectionValue, PRE_INJ.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

            switch (injectorCodeType) {

                case ONE:
                case FIVE:
                    if (checkSum != -99)
                        resultString
                                .append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                .append(completeBinaryWithZeroes(injectorCoefficient, 2));
                    break;
                case THREE:
                    if (checkSum != -99)
                        resultString
                                .append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                .append(completeBinaryWithZeroes(injectorCoefficient, 2))
                                .append("00");
                    break;
                case TWO:
                    if (preparedMap.containsKey(PRE_INJ_2.toString())) {
                        int preInjection2 = preparedMap.get(PRE_INJ_2.toString()).get(i);
                        if (checkSum != -99)
                            resultString
                                    .append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                    .append(completeBinaryWithZeroes(injectorCoefficient, 2));
                        if (preInjection2 != -99)
                            resultString
                                    .append(completeBinaryWithZeroes(preInjection2, PRE_INJ_2.fieldLength(injectorIntCodeType, injectorCoefficient)));
                    }
                    break;
                case FOUR:
                    if (preparedMap.containsKey(PRE_INJ_2.toString())) {
                        int preInjection2 = preparedMap.get(PRE_INJ_2.toString()).get(i);
                        if (preInjection2 != -99)
                            resultString
                                    .append(completeBinaryWithZeroes(preInjection2, PRE_INJ_2.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                    .append(completeBinaryWithZeroes(injectorCoefficient, 2))
                                    .append("0").append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)));
                    }
                    break;
                case ZERO:
                    int preInjection2 = preparedMap.get(PRE_INJ_2.toString()).get(i);

                    if (checkSum != -99)
                        resultString
                                .append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                .append(completeBinaryWithZeroes(injectorCoefficient, 2));

                    if (preInjection2 != -99)
                        resultString.append(completeBinaryWithZeroes(preInjection2, PRE_INJ_2.fieldLength(injectorIntCodeType, injectorCoefficient)));

                    switch (injectorCoefficient) {
                        case 0:
                            int reserve = preparedMap.get(RESERVE.toString()).get(i);
                            int rest = preparedMap.get(REST.toString()).get(i);
                            resultString
                                    .append(completeBinaryWithZeroes(reserve, RESERVE.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                    .append(completeBinaryWithZeroes(rest, REST.fieldLength(injectorIntCodeType, injectorCoefficient)));
                            break;
                        default:
                            int kem = preparedMap.get(KEM.toString()).get(i);
                            int preInjection3 = preparedMap.get(PRE_INJ_3.toString()).get(i);
                            resultString
                                    .append(completeBinaryWithZeroes(kem, KEM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                    .append(completeBinaryWithZeroes(preInjection3, PRE_INJ_3.fieldLength(injectorIntCodeType, injectorCoefficient)));
                    }
                    break;
            }

            codeResult.add(resultString.toString());

            logger.debug("4. result string {}: {}", i, resultString.toString());

        }

        return getCode(codeResult);

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

    private static List<String> getCode(List<String> codeResult) {

        List<String> resultList = new ArrayList<>();

        switch (injectorCodeType) {

            case ONE:
            case TWO:
            case ZERO:
                codeResult.forEach(result -> resultList.add(result.contains(NO_CODING) ? "Coding impossible" : prepareStringResult(result, injectorCodeType, false)));
                break;
            case THREE:
            case FOUR:
                codeResult.forEach(result -> resultList.add(result.contains(NO_CODING) ? "Coding impossible" : prepareStringResult(result, injectorCodeType, true)));
                break;
            case FIVE:
                List<ISAResult> isaResult = getIsaResult();
                for (int i = 0; i < codeResult.size(); i++) {
                    if (codeResult.get(i).contains(NO_CODING))
                        resultList.add("Coding impossible");
                    else {
                        switch (isaResult.get(i).getIsaResultState()) {
                            case INVALID:
                                resultList.add("Injector is out of parameters. ISA detection impossible.");
                                break;
                            case VALID:
                                resultList.add(prepareStringResult(codeResult.get(i), injectorCodeType, false) + isaResult.get(i).getIsa_char());
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

        int codeSize = codeType.codeSize(injectorCoefficient);
        int codeStep = CodeTypes.codeStep(codeSize);
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

    /** Generate random flows for fake tests for codeType == 0, values should provide -1, 0, 1 in deviation from central point calculation in convertToInt(...) */
    private static List<Double> getRandomFlowsList() {

        List<Double> randomFlows = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            randomFlows.add((randomFlow.nextInt(3) - 1) * valueDivider * 2);
        }
        return randomFlows;
    }
}

enum CodeTypes {

    //As a reminder:
    //CodeType(int codeType, int step, int size)
    //ZERO(0, 5, 45)
    //ONE(1, 5, 30)
    //TWO(2, 5, 35)
    //THREE(3, 4, 32)
    //FOUR(4, 4, 36)
    //FIVE(5, 5, 30)
    ZERO(0) {
        @Override final double divider(int k_coeff) { return k_coeff == 0 ? 0.1 : 0.25; }
        @Override final int codeSize(int k_coeff) {
            int codeType = getCodeType();
            int length = commonPart(k_coeff) + PRE_INJ_2.fieldLength(codeType, k_coeff);
            if (k_coeff == 0) {
                length = length + RESERVE.fieldLength(codeType, k_coeff) + REST.fieldLength(codeType, k_coeff);
            }
            else length = length + PRE_INJ_3.fieldLength(codeType, k_coeff) + KEM.fieldLength(codeType, k_coeff);
            return length;
        }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(),
                    IDLE.toString(),
                    MAX_LOAD.toString(),
                    PRE_INJ.toString(),
                    PRE_INJ_2.toString(),
                    KEM.toString(),
                    PRE_INJ_3.toString(),
                    RESERVE.toString(),
                    REST.toString());}
    },
    ONE(1) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    },
    TWO(2) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff) + PRE_INJ_2.fieldLength(getCodeType(), k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString(), PRE_INJ_2.toString());}
    },
    THREE(3) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff) + 2; }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    },
    FOUR(4) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff) + PRE_INJ_2.fieldLength(getCodeType(), k_coeff) + 1; }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString(), PRE_INJ_2.toString());}
    },
    FIVE(5) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    };

    private final int codeType;

    CodeTypes(int codeType) {

        this.codeType = codeType;
    }

    public static CodeTypes getCodeType(int codeType) {

        for (CodeTypes codeTypes : values()) {
            if (codeTypes.codeType == codeType)
                return codeTypes;
        }
        return ONE;
    }

    abstract double divider(int k_coeff);

    private static int commonPart(int k_coeff) {
        return
                EMISSION_POINT.fieldLength(0, k_coeff)
                + IDLE.fieldLength(0, k_coeff)
                + MAX_LOAD.fieldLength(0, k_coeff)
                + PRE_INJ.fieldLength(0, k_coeff)
                + CHECK_SUM.fieldLength(0, k_coeff)
                + 2;
    }

    abstract int codeSize(int k_coeff);

    abstract List<String> getTestsList();

    public static int codeStep(int codeSize) {
        return codeSize % 5 == 0 ? 5 : 4;
    }

    public final int getCodeType() {
        return codeType;
    }

    public final double getDivider(int k_coeff) {
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

enum BitFields{

    EMISSION_POINT ("Emission Point") {final int fieldLength(int codeType, int k){return 7;}},
    IDLE ("Idle"){int fieldLength(final int codeType, int k){return 5;}},
    MAX_LOAD ("Maximum Load"){final int fieldLength(int codeType, int k){return 7;}},
    PRE_INJ ("Pre-injection") {final int fieldLength(int codeType, int k){return 5;}},
    PRE_INJ_2 ("Pre-injection 2") {final int fieldLength(int codeType, int k){return codeType != 0 ? 5 : k != 0 ? 4 : 6;}},
    KEM ("Kem") {int fieldLength(final int codeType, int k){return 7;}},
    PRE_INJ_3 ("Pre-injection 3") {final int fieldLength(int codeType, int k){return 4;}},
    RESERVE ("Reserve"){final int fieldLength(int codeType, int k){return 7;}},
    REST ("Rest"){final int fieldLength(int codeType, int k){return 2;}},
    CHECK_SUM ("Check Sum") {final int fieldLength(int codeType, int k){return 4;}};

    private final String testName;

    BitFields(String testName) {
        this.testName = testName;
    }

    abstract int fieldLength(int codeType, int k);

    @Override
    public String toString() {
        return testName;
    }
}

