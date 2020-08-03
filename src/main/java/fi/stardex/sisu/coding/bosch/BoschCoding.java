package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.model.cr.FlowReportModel.FlowResult;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResult;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.bosch.BitFields.*;
import static fi.stardex.sisu.coding.bosch.CodeTypes.*;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState.INVALID;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState.OFF;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.getIsaResult;
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
    private static List<String> previousResultList;
    private static List<Integer> activeLEDs;

    public static List<String> calculate(ObservableMap<InjectorTest, FlowResult> mapOfFlowTestResults, List<Result> codes, List<Integer> activeChannels) {

        Map<InjectorTest, List<Double>> temp = new HashMap<>();
        Injector injector = getInjector();
        injectorCodeType = getCodeType(injector.getCodetype());
        injectorCoefficient = injector.getCoefficient();
        valueDivider = injectorCodeType.divider(injectorCoefficient);
        injectorIntCodeType = injectorCodeType.getCodeType();
        activeLEDs = activeChannels;

            previousResultList = new LinkedList<>();
            if (codes.isEmpty()) {
                previousResultList.add("");
                previousResultList.add("");
                previousResultList.add("");
                previousResultList.add("");
            } else {
                previousResultList.add(codes.get(0) != null ? codes.get(0).getSubColumn1() : "" );
                previousResultList.add(codes.get(1) != null ? codes.get(1).getSubColumn1() : "" );
                previousResultList.add(codes.get(2) != null ? codes.get(2).getSubColumn1() : "" );
                previousResultList.add(codes.get(3) != null ? codes.get(3).getSubColumn1() : "" );
            }

        for (Map.Entry<InjectorTest, FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            FlowResult flowTestResult = entry.getValue();

            temp.put(entry.getKey(), Arrays.asList(
                    flowTestResult.getDoubleValue_1(),
                    flowTestResult.getDoubleValue_2(),
                    flowTestResult.getDoubleValue_3(),
                    flowTestResult.getDoubleValue_4()));
        }

        if (injectorCodeType == IMA11_1 || injectorCodeType == IMA11_2 || injectorCodeType == IMA11_3 || injectorCodeType == IMA11_4) {
            /** First step: we have to define if one of Test Point 01 or Pre-Injection tests are present to prevent fake substitution creation of another one.
             * In case neither Test Point 01 nor Pre-Injection has been found we agree Test Point 01 fake substitution creation is the best. */
            String toExclude = temp.keySet()
                    .stream().map(k -> k.getTestName().toString())
                    .filter(k -> k.equals(TP1.toString()) || k.equals(PRE_INJ.toString()))
                    .findFirst().filter(k -> !k.equals(TP1.toString()))
                    .map(k -> TP1.toString())
                    .orElse(PRE_INJ.toString());
            addLostTests(temp, List.of(toExclude));

            /** Second step: depending on injectorCodeType we have to provide zero deviations of results from nominal values for tests defined in the technical task.
             * This is necessary for:
             * 1. correct checkSum calculation - those tests should not bring in any value into checkSum calculation.
             * 2. universal and approach to code parts concatenation in getCodeResult() for all IMA-types
             * 3. simplification of future changes - no necessity to make changes in getCodeResult() for IMA-types but only here below */
            for (Map.Entry<InjectorTest, List<Double>> entrySet : temp.entrySet()) {

                Double nominal = entrySet.getKey().getNominalFlow();
                String testName = entrySet.getKey().getTestName().toString();

                switch (injectorCodeType) {
                    case IMA11_1:
                        if(testName.equals(TP1.toString()) || testName.equals(TP7.toString()) || testName.equals(PRE_INJ.toString()) || testName.equals(TP5.toString())){
                            temp.put(entrySet.getKey(), List.of(nominal, nominal, nominal, nominal));
                        }
                        break;
                    case IMA11_2:
                        if(testName.equals(TP7.toString()) || testName.equals(TP5.toString())){
                            temp.put(entrySet.getKey(), List.of(nominal, nominal, nominal, nominal));
                        }
                        break;
                    case IMA11_3:
                        if(testName.equals(TP1.toString()) || testName.equals(PRE_INJ.toString()) || testName.equals(TP5.toString())){
                            temp.put(entrySet.getKey(), List.of(nominal, nominal, nominal, nominal));
                        }
                        break;
                    case IMA11_4:
                        if(testName.equals(TP5.toString())){
                            temp.put(entrySet.getKey(), List.of(nominal, nominal, nominal, nominal));
                        }
                        break;
                }
            }
        }
        else if (injectorCodeType != ZERO) { addLostTests(temp, Collections.emptyList()); }
        /**In case of real tests activation for codeType == 0 do not forget to remove this fake results generation */
        else {

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

        boolean isType1Coeff2andPreInjection;
        for (Map.Entry<InjectorTest, List<Double>> entry : filteredSet) {

            isType1Coeff2andPreInjection = entry.getKey().getTestName().toString().equals(PRE_INJ.toString())
                    && injectorCodeType == ONE
                    && injectorCoefficient == 2;

            double nominalFlow = isType1Coeff2andPreInjection ? 0d : entry.getKey().getNominalFlow();

            if (isType1Coeff2andPreInjection) {
                entry.setValue(entry.getValue().stream()
                        .map(v -> { if (v != -99) { v = 0d; }return v; })
                        .collect(Collectors.toList()));
            }

            int fieldLength = getFieldLength(entry.getKey().getTestName().toString());

            List<Integer> convertedList = entry.getValue().stream().map(value -> convertToInt(value, nominalFlow, fieldLength)).collect(Collectors.toList());

            convertedMap.put(entry.getKey().toString(), convertedList);

        }

        logger.info("1. Converted to int: {}", convertedMap);

        List<Integer> checkSum = addCheckSum(convertedMap);

        convertNegativeValues(convertedMap);

        convertedMap.put("Check Sum", checkSum);

        logger.info("3. Converted map: {}", convertedMap);

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

            /** Corrected variant of summing - now we get flows sum individually for every channel*/
            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

                injectorFlows.add(entry.getValue().get(i));
            }
            value += injectorFlows.stream().filter(element -> element != -99).reduce(Integer::sum).orElse(0);

            if (value != -99) {
                value += injectorCoefficient;

                ISAResultState isaResultState = getIsaResult().get(i).getIsaResultState();
                if (isaResultState != INVALID && isaResultState != OFF && (injectorCodeType == FIVE || injectorCodeType == SIX)) {

                        isaCharIndex = MASK.indexOf(getIsaResult().get(i).getIsa_char());
                        value += isaCharIndex;
                }

                value = (value & 15) + ((value & 240) >> 4) + 1 & 15;
            }
            resultCheckSum.add(value);
        }

        logger.info("2. Check sum list: {}", resultCheckSum);

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

            int testPoint1Value = -99;
            int testPoint5Value = -99;
            int testPoint6Value = -99;
            int testPoint7Value = -99;

            if (injectorCodeType == IMA11_1 || injectorCodeType == IMA11_2 || injectorCodeType == IMA11_3 || injectorCodeType == IMA11_4) {

                int testPoint4Value = preparedMap.get(TP4.toString()).get(i);
                resultString.append(testPoint4Value != -99 ? completeBinaryWithZeroes(testPoint4Value, TP4.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

                resultString.append(completeBinaryWithZeroes(0, ADD_00000.fieldLength(injectorIntCodeType, injectorCoefficient)));

                int testPoint2value = preparedMap.get(TP2.toString()).get(i);
                resultString.append(testPoint2value != -99 ? completeBinaryWithZeroes(testPoint2value, TP2.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

                testPoint1Value = preparedMap.get(TP1.toString()) != null ? preparedMap.get(TP1.toString()).get(i) : preparedMap.get(PRE_INJ.toString()).get(i);
                testPoint5Value = preparedMap.get(TP5.toString()).get(i);
                testPoint6Value = preparedMap.get(TP6.toString()).get(i);
                testPoint7Value = preparedMap.get(TP7.toString()).get(i);
            }else{

                int emissionPointValue = preparedMap.get(EMISSION_POINT.toString()).get(i);
                resultString.append(emissionPointValue != -99 ? completeBinaryWithZeroes(emissionPointValue, EMISSION_POINT.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

                int idleValue = preparedMap.get(IDLE.toString()).get(i);
                resultString.append(idleValue != -99 ? completeBinaryWithZeroes(idleValue, IDLE.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

                int maximumLoadValue = preparedMap.get(MAX_LOAD.toString()).get(i);
                resultString.append(maximumLoadValue != -99 ? completeBinaryWithZeroes(maximumLoadValue, MAX_LOAD.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");

                int preInjectionValue = preparedMap.get(PRE_INJ.toString()).get(i);
                resultString.append(preInjectionValue != -99 ? completeBinaryWithZeroes(preInjectionValue, PRE_INJ.fieldLength(injectorIntCodeType, injectorCoefficient)) : "");
            }

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
                case SIX:
                    if (checkSum != -99)
                        resultString
                                .append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                .append(completeBinaryWithZeroes(injectorCoefficient, 2))
                                .append("00");
                    break;
                case IMA11_1:
                case IMA11_2:
                case IMA11_3:
                case IMA11_4:
                    if (checkSum != -99) {

                        resultString.append(testPoint1Value != -99 ? completeBinaryWithZeroes(testPoint1Value, TP1.fieldLength(injectorIntCodeType, injectorCoefficient)) : "")
                                .append(completeBinaryWithZeroes(checkSum, CHECK_SUM.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                .append(completeBinaryWithZeroes(injectorCoefficient, 2));

                        if (testPoint5Value != -99 && testPoint6Value != -99 && testPoint7Value != -99) {
                            resultString.append(completeBinaryWithZeroes(testPoint5Value, TP5.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                    .append(completeBinaryWithZeroes(testPoint6Value, TP6.fieldLength(injectorIntCodeType, injectorCoefficient)))
                                    .append(completeBinaryWithZeroes(testPoint7Value, TP7.fieldLength(injectorIntCodeType, injectorCoefficient)));
                        }
                        resultString.append(completeBinaryWithZeroes(0, ADD_0.fieldLength(injectorIntCodeType, injectorCoefficient)));
                    }
                    break;
            }
            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
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
        List<ISAResult> isaResult = getIsaResult();
        switch (injectorCodeType) {

            case ONE:
            case TWO:
            case ZERO:
            case THREE:
            case FOUR:
            case IMA11_1:
            case IMA11_2:
            case IMA11_3:
            case IMA11_4:
                resultList.addAll(previousResultList);
                for (int i = 0; i < codeResult.size(); i++) {
                    if (activeLEDs.contains(i + 1)) {
                        resultList.set(i, codeResult.get(i).equals(NO_CODING) ? "Coding impossible" : prepareStringResult(codeResult.get(i), injectorCodeType));
                    }
                }
                break;
            case FIVE:
                for (int i = 0; i < codeResult.size(); i++) {
                    if (codeResult.get(i).contains(NO_CODING))
                        resultList.add("Coding impossible");
                    else {
                        switch (isaResult.get(i).getIsaResultState()) {
                            case INVALID:
                                resultList.add("Injector is out of parameters. ISA detection impossible.");
                                break;
                            case VALID:
                                resultList.add(prepareStringResult(codeResult.get(i)
                                        + completeBinaryWithZeroes(MASK.indexOf(getIsaResult().get(i).getIsa_char()), 5), injectorCodeType));
                                break;
                            case OFF:
                                    resultList.add(previousResultList.get(i));
                                break;
                        }
                    }
                }
                break;
            case SIX:
                for (int i = 0; i < codeResult.size(); i++) {
                    if (codeResult.get(i).contains(NO_CODING))
                        resultList.add("Coding impossible");
                    else {
                        switch (isaResult.get(i).getIsaResultState()) {
                            case INVALID:
                                resultList.add("Injector is out of parameters. ISA detection impossible.");
                                break;
                            case VALID:
                                resultList.add(prepareStringResult(codeResult.get(i)
                                        + completeBinaryWithZeroes(MASK.indexOf(getIsaResult().get(i).getIsa_char()), 5)
                                        + "000", injectorCodeType));
                                break;
                            case OFF:
                                    resultList.add(previousResultList.get(i));
                                break;
                        }
                    }
                }
                break;
        }

        logger.info("6. results list: {}", resultList);

        return resultList;

    }
    private static String prepareStringResult(String binaryResult, CodeTypes codeType) {

        logger.info("5. final binary string: {}", binaryResult);

        StringBuilder result = new StringBuilder();

        int codeSize = codeType.codeSize(injectorCoefficient);
        int codeStep = codeType.getStep();
        boolean isHEX = codeType.isHex();
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

    private static void addLostTests(Map<InjectorTest, List<Double>> testsMap, List<String> toExclude) {

        List<String> baseTestNames = injectorCodeType.getTestsList().stream().filter(name -> !toExclude.contains(name)).collect(Collectors.toList());
        List<String> factTestNames = testsMap.keySet().stream().map(t -> t.getTestName().toString()).collect(Collectors.toList());
        baseTestNames.stream()
                .filter(name -> !factTestNames.contains(name))
                .forEach(name -> testsMap.put(new InjectorTest(new TestName(name), 0d, 100d), getRandomFlowsList()));
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
    //FIVE(5, 5, 35)
    //SIX(6, 4, 40)
    ZERO(0, 5) {
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
    ONE(1, 5) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    },
    TWO(2, 5) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff) + PRE_INJ_2.fieldLength(getCodeType(), k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString(), PRE_INJ_2.toString());}
    },
    THREE(3, 4) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff) + ADD_00.fieldLength(getCodeType(), k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    },
    FOUR(4, 4) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff)
                + PRE_INJ_2.fieldLength(getCodeType(), k_coeff)
                + ADD_0.fieldLength(getCodeType(), k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString(), PRE_INJ_2.toString());}
    },
    FIVE(5, 5) {
        @Override final double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override final int codeSize(int k_coeff) { return commonPart(k_coeff) + U_CHAR.fieldLength(getCodeType(), k_coeff); }
        @Override final List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    },
    SIX(6, 4){
        @Override double divider(int k_coeff) {
            return getDivider(k_coeff);
        }
        @Override int codeSize(int k_coeff) {
            return commonPart(k_coeff)
                    + ADD_00.fieldLength(getCodeType(), k_coeff)
                    + U_CHAR.fieldLength(getCodeType(), k_coeff)
                    + ADD_000.fieldLength(getCodeType(), k_coeff);
        }
        @Override List<String> getTestsList() {
            return Arrays.asList(EMISSION_POINT.toString(), IDLE.toString(), MAX_LOAD.toString(), PRE_INJ.toString());}
    },
    IMA11_1(111, 5) {
        @Override double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override int codeSize(int k_coeff) {
            return commonPartIMA(k_coeff)
                    + TP5.fieldLength(getCodeType(), k_coeff)
                    + TP6.fieldLength(getCodeType(), k_coeff)
                    + TP7.fieldLength(getCodeType(), k_coeff)
                    + ADD_0.fieldLength(getCodeType(), k_coeff);
        }
        @Override List<String> getTestsList() {
            return Arrays.asList(TP4.toString(), TP2.toString(), TP1.toString(), PRE_INJ.toString(), TP5.toString(), TP6.toString(), TP7.toString());
        }
    },
    IMA11_2(112, 5) {
        @Override double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override int codeSize(int k_coeff) {
            return commonPartIMA(k_coeff)
                    + TP5.fieldLength(getCodeType(), k_coeff)
                    + TP6.fieldLength(getCodeType(), k_coeff)
                    + TP7.fieldLength(getCodeType(), k_coeff)
                    + ADD_0.fieldLength(getCodeType(), k_coeff);
        }
        @Override List<String> getTestsList() {
            return Arrays.asList(TP4.toString(), TP2.toString(), TP1.toString(), PRE_INJ.toString(), TP5.toString(), TP6.toString(), TP7.toString());
        }
    },
    IMA11_3(113, 5) {
        @Override double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override int codeSize(int k_coeff) {
            return commonPartIMA(k_coeff)
                    + TP5.fieldLength(getCodeType(), k_coeff)
                    + TP6.fieldLength(getCodeType(), k_coeff)
                    + TP7.fieldLength(getCodeType(), k_coeff)
                    + ADD_0.fieldLength(getCodeType(), k_coeff);
        }
        @Override List<String> getTestsList() {
            return Arrays.asList(TP4.toString(), TP2.toString(), TP1.toString(), PRE_INJ.toString(), TP5.toString(), TP6.toString(), TP7.toString());
        }
    },
    IMA11_4(114, 5) {
        @Override double divider(int k_coeff) { return getDivider(k_coeff); }
        @Override int codeSize(int k_coeff) {
            return commonPartIMA(k_coeff)
                    + TP5.fieldLength(getCodeType(), k_coeff)
                    + TP6.fieldLength(getCodeType(), k_coeff)
                    + TP7.fieldLength(getCodeType(), k_coeff)
                    + ADD_0.fieldLength(getCodeType(), k_coeff);
        }
        @Override List<String> getTestsList() {
            return Arrays.asList(TP4.toString(), TP2.toString(), TP1.toString(), PRE_INJ.toString(), TP5.toString(), TP6.toString(), TP7.toString());
        }
    };

    private final int codeType;
    private final int step;

    CodeTypes(int codeType, int step) {

        this.codeType = codeType;
        this.step = step;
    }

    public static CodeTypes getCodeType(int codeType) {

        for (CodeTypes codeTypes : values()) {
            if (codeTypes.codeType == codeType)
                return codeTypes;
        }
        return ONE;
    }

    public int getStep() {
        return step;
    }
    public boolean isHex() {
        return step == 4;
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

    private static int commonPartIMA(int k_coeff) {
        return
                TP4.fieldLength(0, k_coeff)
                + ADD_00000.fieldLength(0, k_coeff)
                + TP2.fieldLength(0, k_coeff)
                + TP1.fieldLength(0, k_coeff)
                + CHECK_SUM.fieldLength(0, k_coeff)
                + 2;
    }

    abstract int codeSize(int k_coeff);
    abstract List<String> getTestsList();

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
    CHECK_SUM ("Check Sum") {final int fieldLength(int codeType, int k){return 4;}},
    U_CHAR ("U_char") {final int fieldLength(int codeType, int k){return 5;}},
    ADD_0("Add_0") {final int fieldLength(int codeType, int k){return 1;}},
    ADD_00("Add_00") {final int fieldLength(int codeType, int k){return 2;}},
    ADD_000("Add_000"){final int fieldLength(int codeType, int k){return 3;}},
    ADD_00000("Add_000"){final int fieldLength(int codeType, int k){return 5;}},
    TP1("Test Point 01") {int fieldLength(int codeType, int k) { return 5; }},
    TP2("Test Point 02") {int fieldLength(int codeType, int k) { return 7; }},
    TP4("Test Point 04") {int fieldLength(int codeType, int k) { return 7; }},
    TP5("Test Point 05") {int fieldLength(int codeType, int k) { return 5; }},
    TP6("Test Point 06") {int fieldLength(int codeType, int k) { return 7; }},
    TP7("Test Point 07") {int fieldLength(int codeType, int k) { return 7; }};

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

