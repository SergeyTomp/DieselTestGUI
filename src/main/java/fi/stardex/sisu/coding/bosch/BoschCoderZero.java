package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.*;
import static fi.stardex.sisu.coding.CodeField.CHECK_SUM;
import static fi.stardex.sisu.coding.CodeField.COEFF;

public class BoschCoderZero extends BoschCoder {

    public BoschCoderZero(Injector injector,
                          List<Integer> activeLEDs,
                          List<Result> oldCodes,
                          ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        super(oldCodes);
        super.codeStep = 5;
        super.isHEX =  false;
        super.valueDivider = injector.getCoefficient() == 0 ? 0.1 : 0.25;
        super.injectorCoefficient = injector.getCoefficient();
        super.activeLEDs = activeLEDs;
        super.fieldLengthMap.put(EMISSION_POINT, 7);
        super.fieldLengthMap.put(IDLE, 5);
        super.fieldLengthMap.put(MAX_LOAD, 7);
        super.fieldLengthMap.put(PRE_INJ, 5);
        super.fieldLengthMap.put(CHECK_SUM, 4);
        super.fieldLengthMap.put(COEFF, 2);
        super.fieldLengthMap.put(PRE_INJ_2, injector.getCoefficient() != 0 ? 4 : 6);
        if (injectorCoefficient == 0) {
            super.fieldLengthMap.put(RESERVE, 7);
            super.fieldLengthMap.put(REST, 2);
        }else{
            super.fieldLengthMap.put(KEM, 7);
            super.fieldLengthMap.put(PRE_INJ_3, 4);
        }
        super.baseTestsList = super.fieldLengthMap.keySet().stream()
                .filter(CodeField::isTest)
                .map(CodeField::toString)
                .collect(Collectors.toList());
        super.codeSize = fieldLengthMap.values().stream().reduce(Integer::sum).orElse(0);
        super.mapOfFlowTestResults = mapOfFlowTestResults;
    }

    @Override
    public List<String> buildCode() {

        Map<InjectorTest, List<Double>> temp = getSourceMap(mapOfFlowTestResults);

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

            int preInjection2 = preparedMap.get(PRE_INJ_2.toString()).get(i);

            if (checkSum != -99)
                resultString
                        .append(completeBinaryWithZeroes(checkSum, fieldLengthMap.get(CHECK_SUM)))
                        .append(completeBinaryWithZeroes(injectorCoefficient, fieldLengthMap.get(COEFF)));

            if (preInjection2 != -99)
                resultString.append(completeBinaryWithZeroes(preInjection2, fieldLengthMap.get(PRE_INJ_2)));

            switch (injectorCoefficient) {
                case 0:
                    int reserve = preparedMap.get(RESERVE.toString()).get(i);
                    int rest = preparedMap.get(REST.toString()).get(i);
                    resultString
                            .append(completeBinaryWithZeroes(reserve, fieldLengthMap.get(RESERVE)))
                            .append(completeBinaryWithZeroes(rest, fieldLengthMap.get(REST)));
                    break;
                default:
                    int kem = preparedMap.get(KEM.toString()).get(i);
                    int preInjection3 = preparedMap.get(PRE_INJ_3.toString()).get(i);
                    resultString
                            .append(completeBinaryWithZeroes(kem, fieldLengthMap.get(KEM)))
                            .append(completeBinaryWithZeroes(preInjection3, fieldLengthMap.get(PRE_INJ_3)));
            }

            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
        }
        return getCode(codeResult);
    }
}
