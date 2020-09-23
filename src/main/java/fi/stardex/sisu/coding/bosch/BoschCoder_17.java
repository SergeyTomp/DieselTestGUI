package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.*;

public class BoschCoder_17 extends BoschCoder {

    private final List<String> testList;

    public BoschCoder_17(Injector injector, List<Integer> activeLEDs, List<Result> oldCodes) {
        super(oldCodes);
        super.codeStep = 4;
        super.isHEX =  true;
        super.valueDivider = getDivider(injector.getCoefficient());
        super.fieldLengthMap.put(TP4, 7);
        super.fieldLengthMap.put(TP3, 5);
        super.fieldLengthMap.put(TP2, 7);
        super.fieldLengthMap.put(TP1, 5);
        super.fieldLengthMap.put(CHECK_SUM, 4);
        super.fieldLengthMap.put(COEFF, 2);
        super.fieldLengthMap.put(ADD_00, 2);
        super.fieldLengthMap.put(TP5, 5);
        super.fieldLengthMap.put(TP6, 7);
        super.fieldLengthMap.put(TP7, 7);
        super.fieldLengthMap.put(ADD_1, 1);
        testList = super.fieldLengthMap.keySet().stream()
                .filter(CodeField::isTest)
                .map(CodeField::toString)
                .collect(Collectors.toList());
        super.baseTestsList = testList;
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

            int testPoint4Value = preparedMap.get(TP4.toString()).get(i);
            resultString.append(testPoint4Value != -99 ? completeBinaryWithZeroes(testPoint4Value, fieldLengthMap.get(TP4)) : "");

            int testPoint3Value = preparedMap.get(TP3.toString()).get(i);
            resultString.append(testPoint3Value != -99 ? completeBinaryWithZeroes(testPoint3Value, fieldLengthMap.get(TP3)) : "");

            int testPoint2Value = preparedMap.get(TP2.toString()).get(i);
            resultString.append(testPoint2Value != -99 ? completeBinaryWithZeroes(testPoint2Value, fieldLengthMap.get(TP2)) : "");

            int testPoint1Value = preparedMap.get(TP1.toString()).get(i);
            resultString.append(testPoint1Value != -99 ? completeBinaryWithZeroes(testPoint1Value, fieldLengthMap.get(TP1)) : "");

            int testPoint5Value = preparedMap.get(TP5.toString()).get(i);
            int testPoint6Value = preparedMap.get(TP6.toString()).get(i);
            int testPoint7Value = preparedMap.get(TP7.toString()).get(i);

            if (checkSum != -99) {

                resultString.append(completeBinaryWithZeroes(checkSum, fieldLengthMap.get(CHECK_SUM)))
                        .append(completeBinaryWithZeroes(injectorCoefficient, fieldLengthMap.get(COEFF)))
                        .append(completeBinaryWithZeroes(0, fieldLengthMap.get(ADD_00)));

                if (testPoint5Value != -99 && testPoint6Value != -99 && testPoint7Value != -99) {
                    resultString.append(completeBinaryWithZeroes(testPoint5Value, fieldLengthMap.get(TP5)))
                            .append(completeBinaryWithZeroes(testPoint6Value, fieldLengthMap.get(TP6)))
                            .append(completeBinaryWithZeroes(testPoint7Value, fieldLengthMap.get(TP7)));
                }
                resultString.append(completeBinaryWithZeroes(1, fieldLengthMap.get(ADD_1)));
            }

            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
        }
        return getCode(codeResult);
    }
}
