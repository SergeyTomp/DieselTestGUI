package fi.stardex.sisu.ui.data;

import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.util.enums.Measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedInjectorBeakerData implements SavedData {

    private Map<Model, Map<Test, ArrayList<Double>>> savedDataForBeakers = new HashMap<>();

    public void saveDataForBeakers(Model model, Map<Test, ArrayList<Double>> resultList){
        savedDataForBeakers.put(model, resultList);
    }

    public void storeResults(Model model, Test test, ArrayList<Double> results){
        savedDataForBeakers.computeIfAbsent(model, k -> new HashMap<>());
        savedDataForBeakers.get(model).put(test, results);
    }

    @Override
    public Map<Model, Map<Test, ArrayList<Double>>> getSavedDataForBeakers() {
        return savedDataForBeakers;
    }

    @Override
    public List<TestResult> getTestResult(Model model) {
        Map<Test, ArrayList<Double>> value = savedDataForBeakers.get(model);
        List<TestResult> result = new ArrayList<>();

        for (Map.Entry<Test, ArrayList<Double>> entry2 : value.entrySet()) {
            InjectorTest key2 = (InjectorTest) entry2.getKey();
            ArrayList<Double> value2 = entry2.getValue();
            if(key2.getTestName().getMeasurement() == Measurement.DELIVERY) {
                result.add(TestResult.makeTestResultsForInjectors(key2.toString(), "Delivery", key2.getNominalFlow(),
                        key2.getFlowRange(), value2.get(0), value2.get(1), value2.get(2), value2.get(3)));
            }
            if(key2.getTestName().getMeasurement() == Measurement.BACK_FLOW) {
                result.add(TestResult.makeTestResultsForInjectors(key2.toString(), "Back Flow", key2.getNominalFlow(),
                        key2.getFlowRange(), value2.get(0), value2.get(1), value2.get(2), value2.get(3)));
            }

        }
        return result;
    }
}
