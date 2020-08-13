package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.*;
import static fi.stardex.sisu.coding.CodeField.CHECK_SUM;
import static fi.stardex.sisu.coding.CodeField.COEFF;

public class BoschCoderTwo extends BoschCoder {

    public BoschCoderTwo(Injector injector, List<Integer> activeLEDs, List<Result> oldCodes) {
        super(oldCodes);
        super.codeStep = 5;
        super.isHEX =  false;
        super.valueDivider = getDivider(injector.getCoefficient());
        super.fieldLengthMap.put(EMISSION_POINT, 7);
        super.fieldLengthMap.put(IDLE, 5);
        super.fieldLengthMap.put(MAX_LOAD, 7);
        super.fieldLengthMap.put(PRE_INJ, 5);
        super.fieldLengthMap.put(CHECK_SUM, 4);
        super.fieldLengthMap.put(COEFF, 2);
        super.fieldLengthMap.put(PRE_INJ_2, 5);
        super.baseTestsList = super.fieldLengthMap.keySet().stream()
                .filter(CodeField::isTest)
                .map(CodeField::toString)
                .collect(Collectors.toList());
        super.injectorCoefficient = injector.getCoefficient();
        super.codeSize = fieldLengthMap.values().stream().reduce(Integer::sum).orElse(0);
        super.activeLEDs = activeLEDs;
    }

    @Override
    public List<String> buildCode(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {

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

            if (preparedMap.containsKey(PRE_INJ_2.toString())) {
                int preInjection2 = preparedMap.get(PRE_INJ_2.toString()).get(i);
                if (checkSum != -99)
                    resultString
                            .append(completeBinaryWithZeroes(checkSum, fieldLengthMap.get(CHECK_SUM)))
                            .append(completeBinaryWithZeroes(injectorCoefficient, fieldLengthMap.get(COEFF)));
                if (preInjection2 != -99)
                    resultString
                            .append(completeBinaryWithZeroes(preInjection2, fieldLengthMap.get(PRE_INJ_2)));
            }

            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
        }
        return getCode(codeResult);
    }
}
