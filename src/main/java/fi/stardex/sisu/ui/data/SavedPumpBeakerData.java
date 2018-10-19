package fi.stardex.sisu.ui.data;

import fi.stardex.sisu.persistence.orm.PumpTest;
import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.pumps.tests.EfficiencyTest;
import fi.stardex.sisu.pumps.tests.Test3;
import fi.stardex.sisu.pumps.tests.Test4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class SavedPumpBeakerData implements SavedData, ResultsStorage {

    private static final Logger logger = LoggerFactory.getLogger(SavedPumpBeakerData.class);

    private Map<Model, Map<Test, ArrayList<Double>>> savedDataForBeakers = new HashMap<>();

    @Override
    public Map<Model, Map<Test, ArrayList<Double>>> getSavedDataForBeakers() {
        return savedDataForBeakers;
    }

    @Override
    public List<TestResult> getTestResult(Model model) {

        Map<Test, ArrayList<Double>> value = savedDataForBeakers.get(model);
        List<TestResult> result = new ArrayList<>();

        for (Map.Entry<Test, ArrayList<Double>> entry2 : value.entrySet()) {
            Test testKey = entry2.getKey();
            if (testKey instanceof EfficiencyTest) {
                PumpTest key2 = null;
                if (testKey instanceof Test3) {
                    key2 = ((Test3) testKey).getEfficiencyTest1();
                }
                if (testKey instanceof Test4) {
                    key2 = ((Test4) testKey).getEfficiencyTest2();
                }
                if (Objects.isNull(key2)) {
                    logger.error("key2 is null. testKey is of class {}", testKey.getClass());
                    return result;
                }
                ArrayList<Double> value2 = entry2.getValue();
                result.add(TestResult.makeTestResultsForPumps(key2.toString(), "Delivery", key2.getDelivery().doubleValue(),
                            value2.get(0), key2.getDeltaMinus().doubleValue(), key2.getDeltaPlus().doubleValue(), key2.getRpm().doubleValue(),
                            key2.getPressure().doubleValue()));

                result.add(TestResult.makeTestResultsForPumps(key2.toString(), "Back Flow", key2.getRecovery().doubleValue(),
                            value2.get(1), key2.getDeltaMinus().doubleValue(), key2.getDeltaPlus().doubleValue(), key2.getRpm().doubleValue(),
                            key2.getPressure().doubleValue()));
            }
        }
        return result;
    }

    @Override
    public List<? extends Result> getResultsList(Model model) {
        return null;
    }

    @Override
    public Boolean containsKey(Model model) {
        return null;
    }
}
