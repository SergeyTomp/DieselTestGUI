package fi.stardex.sisu.coding.siemens;

import fi.stardex.sisu.coding.Coder;
import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.CHECK_SUM;

public abstract class SiemensCoder implements Coder {

    final Logger logger = LoggerFactory.getLogger(SiemensCoder.class);

    private final String MASK = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";
    private final String NO_CODING = "NO_CODING";
    private final int CHANNELS_QTY = 4;
    private final Map<InjectorTest, List<Double>> mapOfResults = new HashMap<>();
    private List<Integer> activeLEDs;
    private List<Result> oldCodes;
    private List<String> previousResultList;
    final Map<CodeField, Integer> TEST_COEFF = new HashMap<>();
    ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults;

    SiemensCoder(List<Integer> activeLEDs, List<Result> oldCodes) {

        this.oldCodes = oldCodes;
        this.activeLEDs = activeLEDs;
        makePreviousResultsList();
    }

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
        mapOfResults.clear();
        mapOfResults.putAll(temp);
        return temp;
    }

    private void makePreviousResultsList() {

        previousResultList = new LinkedList<>();
        if (oldCodes.isEmpty()) {
            previousResultList.add("");
            previousResultList.add("");
            previousResultList.add("");
            previousResultList.add("");
        } else {
            previousResultList.add(oldCodes.get(0) != null ? oldCodes.get(0).getSubColumn1() : "");
            previousResultList.add(oldCodes.get(1) != null ? oldCodes.get(1).getSubColumn1() : "");
            previousResultList.add(oldCodes.get(2) != null ? oldCodes.get(2).getSubColumn1() : "");
            previousResultList.add(oldCodes.get(3) != null ? oldCodes.get(3).getSubColumn1() : "");
        }
    }

    protected Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert) {

        Map<String, List<Integer>> convertedMap = new HashMap<>();

        for (Map.Entry<InjectorTest, List<Double>> entry : mapToConvert.entrySet()) {

            Double flowRange = entry.getKey().getFlowRange();
            List<Integer> convertedList = entry.getValue().stream()
                    .map(value -> convertToInt(value, entry.getKey().getNominalFlow(), flowRange))
                    .collect(Collectors.toList());
            convertedMap.put(entry.getKey().toString(), convertedList);
        }
        logger.info("1. Converted to int: {}", convertedMap);

        shiftValues(convertedMap);
        logger.info("2. Shifted by 15: {}", convertedMap);
        return convertedMap;
    }

    private int convertToInt(double value, Double nominalFlow, Double flowRange) {

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

    protected List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap) {

        List<Integer> resultCheckSum = new ArrayList<>();
        Map<String, Integer> testFlows = new HashMap<>();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            int value;
            testFlows.clear();

            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

                testFlows.put(entry.getKey(), entry.getValue().get(i));
            }

            value = testFlows.entrySet()
                    .stream()
                    .filter(e -> e.getValue() != -99)
                    .peek(e -> e.setValue(e.getValue() * TEST_COEFF.get(CodeField.getField(e.getKey()))))
                    .map(Map.Entry::getValue)
                    .reduce(Integer::sum).orElse(-1);

            resultCheckSum.add(value);
        }

        logger.info("3. Check sum list: {}", resultCheckSum);
        return resultCheckSum;
    }

    private void shiftValues(Map<String, List<Integer>> convertedMap) {

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

    protected List<String> getCodeResult(Map<String, List<Integer>> preparedMap){

        List<String> codeResult = new ArrayList<>();

        TreeMap<String, List<Integer>> sortedMap = new TreeMap<>(Comparator.comparingInt(key -> TEST_COEFF.get(CodeField.getField(key))));
        sortedMap.putAll(preparedMap);
        sortedMap.remove(CHECK_SUM.toString());
        StringBuilder codeString = new StringBuilder();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            codeString.append(getCheckSumCode(preparedMap.get(CHECK_SUM.toString()).get(i)));

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

    private boolean allTestPassed(int ledNumber) {

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

    private String completeBinaryWithZeroes(final int decimal) {

        int size = 10;
        if (decimal < 0 || Integer.toBinaryString(decimal).length() > size){
            return NO_CODING;
        }
        logger.info("decimal: {}, size: {}", decimal, size);

        String binary = Integer.toBinaryString(decimal);
        logger.info("binary: {}", binary);

        StringBuilder result = new StringBuilder(binary);

        for (int j = result.length(); j < size; j++) {
            result.insert(0, "0");
        }
        logger.info("5. completed binary: {}", result.toString());
        return result.toString();
    }

    private String getCheckSumCode(Integer checkSum) {

        if (checkSum < 0) { return ""; }

        StringBuilder temp = new StringBuilder();

        String binaryString = completeBinaryWithZeroes(checkSum);
        int binaryValue = Integer.parseInt(binaryString, 2);
        binaryValue = ((binaryValue & 0b1111110000) << 1) | (binaryValue & 0b0000001111);
        char firstSymbol = getResultSymbol((binaryValue & 0b1111100000) >> 5);
        char secondSymbol = getResultSymbol(binaryValue & 0b0000011111);

        return temp.append(firstSymbol).append(secondSymbol).toString();
    }

    private char getResultSymbol(Integer value) { return MASK.charAt(value); }

    protected List<String> getCode(List<String> codeResult) {

        List<String> resultList = new ArrayList<>(previousResultList);
        for (int i = 0; i < codeResult.size(); i++) {
            String result = codeResult.get(i);
            if (activeLEDs.contains(i + 1)) {
                resultList.set(i, (result.length() <= 7 && result.length() > 2 && result.contains("*"))? "Coding impossible" : result.length() <= 2 ? "" : result);
            }
        }
        logger.info("6. results list: {}", resultList);
        return resultList;
    }
}
