package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.*;
import static fi.stardex.sisu.coding.CodeField.ADD_0;

public class BoschCoderIMA_112 extends BoschCoder {

    public BoschCoderIMA_112(Injector injector, List<Integer> activeLEDs, List<Result> oldCodes) {
        super(oldCodes);
        super.codeStep = 5;
        super.isHEX =  false;
        super.injectorCoefficient = injector.getCoefficient();
        super.activeLEDs = activeLEDs;
        super.valueDivider = getDivider(injector.getCoefficient());
        super.fieldLengthMap.put(TP4, 7);
        super.fieldLengthMap.put(ADD_00000, 5);
        super.fieldLengthMap.put(TP2, 7);
        super.fieldLengthMap.put(TP1, 5);
        super.fieldLengthMap.put(PRE_INJ, 5);
        super.fieldLengthMap.put(CHECK_SUM, 4);
        super.fieldLengthMap.put(COEFF, 2);
        super.fieldLengthMap.put(TP5, 5);
        super.fieldLengthMap.put(TP6, 7);
        super.fieldLengthMap.put(TP7, 7);
        super.fieldLengthMap.put(ADD_0, 1);
        super.baseTestsList = super.fieldLengthMap.keySet().stream()
                .filter(CodeField::isTest)
                .map(CodeField::toString)
                .collect(Collectors.toList());
        super.codeSize = fieldLengthMap.keySet().stream()
                .filter(k -> k != PRE_INJ)
                .map(k -> fieldLengthMap.get(k))
                .reduce(Integer::sum).orElse(0);
    }

    @Override
    public List<String> buildCode(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {

        Map<InjectorTest, List<Double>> temp = getSourceMap(mapOfFlowTestResults);

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

            if(testName.equals(TP7.toString()) || testName.equals(TP5.toString())){
                temp.put(entrySet.getKey(), List.of(nominal, nominal, nominal, nominal));
            }
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

            int testPoint4Value = preparedMap.get(TP4.toString()).get(i);
            resultString.append(testPoint4Value != -99 ? completeBinaryWithZeroes(testPoint4Value, fieldLengthMap.get(TP4)) : "");

            resultString.append(completeBinaryWithZeroes(0, fieldLengthMap.get(ADD_00000)));

            int testPoint2value = preparedMap.get(TP2.toString()).get(i);
            resultString.append(testPoint2value != -99 ? completeBinaryWithZeroes(testPoint2value, fieldLengthMap.get(TP2)) : "");

            int testPoint1Value = preparedMap.get(TP1.toString()) != null ? preparedMap.get(TP1.toString()).get(i) : preparedMap.get(PRE_INJ.toString()).get(i);
            int testPoint5Value = preparedMap.get(TP5.toString()).get(i);
            int testPoint6Value = preparedMap.get(TP6.toString()).get(i);
            int testPoint7Value = preparedMap.get(TP7.toString()).get(i);

            if (checkSum != -99) {

                resultString.append(testPoint1Value != -99 ? completeBinaryWithZeroes(testPoint1Value, fieldLengthMap.get(TP1)) : "")
                        .append(completeBinaryWithZeroes(checkSum, fieldLengthMap.get(CHECK_SUM)))
                        .append(completeBinaryWithZeroes(injectorCoefficient, fieldLengthMap.get(COEFF)));

                if (testPoint5Value != -99 && testPoint6Value != -99 && testPoint7Value != -99) {
                    resultString.append(completeBinaryWithZeroes(testPoint5Value, fieldLengthMap.get(TP5)))
                            .append(completeBinaryWithZeroes(testPoint6Value, fieldLengthMap.get(TP6)))
                            .append(completeBinaryWithZeroes(testPoint7Value, fieldLengthMap.get(TP7)));
                }
                resultString.append(completeBinaryWithZeroes(0, fieldLengthMap.get(ADD_0)));
            }
            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
        }
        return getCode(codeResult);
    }
}
