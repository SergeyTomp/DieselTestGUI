package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.*;

public class BoschCoderOne extends BoschCoder {

    private final List<String> testList;

    public BoschCoderOne(Injector injector,
                         List<Integer> activeLEDs,
                         CodingReportModel codingReportModel,
                         ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        super(codingReportModel);
        super.codeStep = 5;
        super.isHEX =  false;
        super.valueDivider = getDivider(injector.getCoefficient());
        super.fieldLengthMap.put(EMISSION_POINT, 7);
        super.fieldLengthMap.put(IDLE, 5);
        super.fieldLengthMap.put(MAX_LOAD, 7);
        super.fieldLengthMap.put(PRE_INJ, 5);
        super.fieldLengthMap.put(CHECK_SUM, 4);
        super.fieldLengthMap.put(COEFF, 2);
        testList = super.fieldLengthMap.keySet().stream()
                .filter(CodeField::isTest)
                .map(CodeField::toString)
                .collect(Collectors.toList());
        super.injectorCoefficient = injector.getCoefficient();
        super.baseTestsList = testList;
        super.codeSize = fieldLengthMap.values().stream().reduce(Integer::sum).orElse(0);
        super.activeLEDs = activeLEDs;
        super.mapOfFlowTestResults = mapOfFlowTestResults;
    }

    @Override
    public List<String> buildCode() {

        makePreviousResultsList();

        Map<InjectorTest, List<Double>> temp = getSourceMap(mapOfFlowTestResults);
        addLostTests(temp, Collections.emptyList());

        Map<String, List<Integer>> preparedMap = convert(temp);
        List<Integer> checkSum = addCheckSum(preparedMap);
        convertNegativeValues(preparedMap);
        preparedMap.put("Check Sum", checkSum);
        logger.info("3. Converted map: {}", preparedMap);
        return getCodeResult(preparedMap);
    }

    @Override
    protected List<String> getCodeResult(Map<String, List<Integer>> preparedMap) {
        List<String> codeResult = new ArrayList<>();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            StringBuilder resultString = new StringBuilder();

            int checkSum = preparedMap.get(CHECK_SUM.toString()).get(i);

            int emissionPointValue = preparedMap.get(EMISSION_POINT.toString()).get(i);
            resultString.append(emissionPointValue != -99 ? completeBinaryWithZeroes(emissionPointValue, fieldLengthMap.get(EMISSION_POINT)) : "");

            int idleValue = preparedMap.get(IDLE.toString()).get(i);
            resultString.append(idleValue != -99 ? completeBinaryWithZeroes(idleValue, fieldLengthMap.get(IDLE)) : "");

            int maximumLoadValue = preparedMap.get(MAX_LOAD.toString()).get(i);
            resultString.append(maximumLoadValue != -99 ? completeBinaryWithZeroes(maximumLoadValue, fieldLengthMap.get(MAX_LOAD)) : "");

            int preInjectionValue = preparedMap.get(PRE_INJ.toString()).get(i);
            resultString.append(preInjectionValue != -99 ? completeBinaryWithZeroes(preInjectionValue, fieldLengthMap.get(PRE_INJ)) : "");

            if (checkSum != -99) {
                resultString
                        .append(completeBinaryWithZeroes(checkSum, fieldLengthMap.get(CHECK_SUM)))
                        .append(completeBinaryWithZeroes(injectorCoefficient, fieldLengthMap.get(COEFF)));
            }

            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
        }
        return getCode(codeResult);
    }

    @Override
    protected Map<String, List<Integer>> convert(Map<InjectorTest, List<Double>> mapToConvert) {

        Set<Map.Entry<InjectorTest, List<Double>>> filteredSet = mapToConvert.entrySet()
                .stream()
                .filter(es -> testList.contains(es.getKey().getTestName().toString()))
                .collect(Collectors.toSet());

        Map<String, List<Integer>> convertedMap = new HashMap<>();

        boolean isCoeff2andPreInjection;
        for (Map.Entry<InjectorTest, List<Double>> entry : filteredSet) {

            isCoeff2andPreInjection = entry.getKey().getTestName().toString().equals(PRE_INJ.toString()) && injectorCoefficient == 2;

            double nominalFlow = isCoeff2andPreInjection ? 0d : entry.getKey().getNominalFlow();
            Double codingRange = entry.getKey().getCodingRange() == null ? 0 : entry.getKey().getCodingRange();

            if (isCoeff2andPreInjection) {
                entry.setValue(entry.getValue().stream()
                        .map(v -> { if (v != -99) { v = 0d; }return v; })
                        .collect(Collectors.toList()));
            }

            int fieldLength = getFieldLength(entry.getKey().getTestName().toString());
            List<Integer> convertedList = entry.getValue().stream()
                    .map(value -> convertToInt(value, nominalFlow, fieldLength, codingRange))
                    .collect(Collectors.toList());
            convertedMap.put(entry.getKey().toString(), convertedList);
        }
        logger.info("1. Converted to int: {}", convertedMap);
        return convertedMap;
    }
}
