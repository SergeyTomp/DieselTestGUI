package fi.stardex.sisu.coding.delphi.c4i;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DelphiC4ICodingDataStorage {

    private static Map<String, Double> led1DataStorage;
    private static Map<String, Double> led2DataStorage;
    private static Map<String, Double> led3DataStorage;
    private static Map<String, Double> led4DataStorage;

    static Map<String, Double> getLed1DataStorage() {
        return led1DataStorage;
    }
    static Map<String, Double> getLed2DataStorage() {
        return led2DataStorage;
    }
    static Map<String, Double> getLed3DataStorage() {
        return led3DataStorage;
    }
    static Map<String, Double> getLed4DataStorage() {
        return led4DataStorage;
    }

    public static void initialize(List<Integer> activeLEDs, List<String> codingTestNames) {

        activeLEDs.forEach(activeLed -> {

            switch (activeLed) {
                case 1:
                    led1DataStorage = new LinkedHashMap<>();
                    codingTestNames.forEach(codingTestName -> led1DataStorage.put(codingTestName, null));
                    break;
                case 2:
                    led2DataStorage = new LinkedHashMap<>();
                    codingTestNames.forEach(codingTestName -> led2DataStorage.put(codingTestName, null));
                    break;
                case 3:
                    led3DataStorage = new LinkedHashMap<>();
                    codingTestNames.forEach(codingTestName -> led3DataStorage.put(codingTestName, null));
                    break;
                case 4:
                    led4DataStorage = new LinkedHashMap<>();
                    codingTestNames.forEach(codingTestName -> led4DataStorage.put(codingTestName, null));
                    break;
            }
        });
    }
    public static void store(FlowReportModel.FlowResult flowTestResult, List<Integer> activeLEDs) {

        InjectorTest injectorTest = flowTestResult.getInjectorTest();
        String testName = injectorTest.getTestName().toString();

        if (activeLEDs.contains(1)) {
            Optional.ofNullable(led1DataStorage).ifPresent(data -> data.put(testName, flowTestResult.getDoubleValue_1() - injectorTest.getNominalFlow()));
        }
        if (activeLEDs.contains(2)) {
            Optional.ofNullable(led2DataStorage).ifPresent(data -> data.put(testName, flowTestResult.getDoubleValue_2() - injectorTest.getNominalFlow()));
        }
        if (activeLEDs.contains(3)) {
            Optional.ofNullable(led3DataStorage).ifPresent(data -> data.put(testName, flowTestResult.getDoubleValue_3() - injectorTest.getNominalFlow()));
        }
        if (activeLEDs.contains(4)) {
            Optional.ofNullable(led4DataStorage).ifPresent(data -> data.put(testName, flowTestResult.getDoubleValue_3() - injectorTest.getNominalFlow()));
        }
    }

    public static void clean() {

        led1DataStorage = null;
        led2DataStorage = null;
        led3DataStorage = null;
        led4DataStorage = null;
    }
}
