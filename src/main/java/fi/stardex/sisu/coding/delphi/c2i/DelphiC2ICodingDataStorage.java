package fi.stardex.sisu.coding.delphi.c2i;

import fi.stardex.sisu.model.cr.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fi.stardex.sisu.coding.delphi.c2i.DelphiC2ICodingDataHandler.calculateCoefficient;

public class DelphiC2ICodingDataStorage {

    private static Map<String, Integer> led1DataStorage;

    private static Map<String, Integer> led2DataStorage;

    private static Map<String, Integer> led3DataStorage;

    private static Map<String, Integer> led4DataStorage;

    static Map<String, Integer> getLed1DataStorage() {
        return led1DataStorage;
    }

    static Map<String, Integer> getLed2DataStorage() {
        return led2DataStorage;
    }

    static Map<String, Integer> getLed3DataStorage() {
        return led3DataStorage;
    }

    static Map<String, Integer> getLed4DataStorage() {
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

    public static void store(FlowResult flowTestResult) {

        InjectorTest injectorTest = flowTestResult.getInjectorTest();

        String testName = injectorTest.getTestName().toString();

        Optional.ofNullable(led1DataStorage).ifPresent(data -> data.put(testName, calculateCoefficient(injectorTest, flowTestResult.getDoubleValue_1())));
        Optional.ofNullable(led2DataStorage).ifPresent(data -> data.put(testName, calculateCoefficient(injectorTest, flowTestResult.getDoubleValue_2())));
        Optional.ofNullable(led3DataStorage).ifPresent(data -> data.put(testName, calculateCoefficient(injectorTest, flowTestResult.getDoubleValue_3())));
        Optional.ofNullable(led4DataStorage).ifPresent(data -> data.put(testName, calculateCoefficient(injectorTest, flowTestResult.getDoubleValue_4())));

    }

    public static void clean() {

        led1DataStorage = null;
        led2DataStorage = null;
        led3DataStorage = null;
        led4DataStorage = null;

    }

}
