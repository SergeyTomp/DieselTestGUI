package fi.stardex.sisu.coding.siemens;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Map;

import static fi.stardex.sisu.coding.CodeField.*;

public class SiemensCoderOne extends SiemensCoder {

    public SiemensCoderOne(List<Integer> activeLEDs,
                           List<Result> oldCodes,
                           ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        super(activeLEDs, oldCodes);
        super.TEST_COEFF.put(MAX_LOAD, 1);
        super.TEST_COEFF.put(PART_LOAD, 2);
        super.TEST_COEFF.put(IDLE, 3);
        super.TEST_COEFF.put(PRE_INJ, 4);
        super.TEST_COEFF.put(CHECK_SUM, 5);
        super.mapOfFlowTestResults = mapOfFlowTestResults;
    }

    @Override
    public List<String> buildCode() {

        Map<InjectorTest, List<Double>> temp = getSourceMap(mapOfFlowTestResults);
        Map<String, List<Integer>> preparedMap = convert(temp);
        List<Integer> checkSum = addCheckSum(preparedMap);
        preparedMap.put(CHECK_SUM.toString(), checkSum);
        logger.info("4. Check Sum added: {}", preparedMap);
        return getCodeResult(preparedMap);
    }
}
