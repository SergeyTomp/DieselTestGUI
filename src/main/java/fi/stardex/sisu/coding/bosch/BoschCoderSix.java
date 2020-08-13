package fi.stardex.sisu.coding.bosch;

import fi.stardex.sisu.coding.CodeField;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController;
import javafx.collections.ObservableMap;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.CodeField.*;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState.INVALID;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.ISAResultState.OFF;
import static fi.stardex.sisu.ui.controllers.cr.windows.ISADetectionController.getIsaResult;

public class BoschCoderSix extends BoschCoder {

    public BoschCoderSix(Injector injector, List<Integer> activeLEDs, List<Result> oldCodes) {
        super(oldCodes);
        super.codeStep = 4;
        super.isHEX =  true;
        super.valueDivider = getDivider(injector.getCoefficient());
        super.fieldLengthMap.put(EMISSION_POINT, 7);
        super.fieldLengthMap.put(IDLE, 5);
        super.fieldLengthMap.put(MAX_LOAD, 7);
        super.fieldLengthMap.put(PRE_INJ, 5);
        super.fieldLengthMap.put(CHECK_SUM, 4);
        super.fieldLengthMap.put(COEFF, 2);
        super.fieldLengthMap.put(ADD_00, 2);
        super.fieldLengthMap.put(U_CHAR, 5);
        super.fieldLengthMap.put(ADD_000, 3);
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

            if (checkSum != -99)
                resultString
                        .append(completeBinaryWithZeroes(checkSum, fieldLengthMap.get(CHECK_SUM)))
                        .append(completeBinaryWithZeroes(injectorCoefficient, fieldLengthMap.get(COEFF)))
                        .append(completeBinaryWithZeroes(0, fieldLengthMap.get(ADD_00)));

            codeResult.add(resultString.toString());
            logger.info("4. result string {}: {}", i, resultString.toString());
        }
        return getCode(codeResult);
    }

    @Override
    protected List<String> getCode(List<String> codeResult) {
        List<String> resultList = new ArrayList<>();
        List<ISADetectionController.ISAResult> isaResult = getIsaResult();

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
                                + completeBinaryWithZeroes(0, fieldLengthMap.get(ADD_000))));
                        break;
                    case OFF:
                        resultList.add(previousResultList.get(i));
                        break;
                }
            }
        }
        logger.info("6. results list: {}", resultList);
        return resultList;
    }

    @Override
    protected List<Integer> addCheckSum(Map<String, List<Integer>> convertedMap) {

        List<Integer> resultCheckSum = new ArrayList<>();
        List <Integer> injectorFlows = new ArrayList<>();

        for (int i = 0; i < CHANNELS_QTY; i++) {

            int value = 0;
            int isaCharIndex;
            injectorFlows.clear();

            for (Map.Entry<String, List<Integer>> entry : convertedMap.entrySet()) {

                injectorFlows.add(entry.getValue().get(i));
            }
            value += injectorFlows.stream().filter(element -> element != -99).reduce(Integer::sum).orElse(0);

            if (value != -99) {
                value += injectorCoefficient;

                ISADetectionController.ISAResultState isaResultState = getIsaResult().get(i).getIsaResultState();
                if (isaResultState != INVALID && isaResultState != OFF ) {

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
}
