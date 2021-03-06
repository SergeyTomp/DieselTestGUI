package fi.stardex.sisu.coding.denso;

import fi.stardex.sisu.model.cr.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DensoCodingDataStorage {

    private static Map<InjectorTest, Map<Integer, Double>> led1DataStorage;
    private static Map<InjectorTest, Map<Integer, Double>> led2DataStorage;
    private static Map<InjectorTest, Map<Integer, Double>> led3DataStorage;
    private static Map<InjectorTest, Map<Integer, Double>> led4DataStorage;

    static Map<InjectorTest, Map<Integer, Double>> getLed1DataStorage() {
        return led1DataStorage;
    }
    static Map<InjectorTest, Map<Integer, Double>> getLed2DataStorage() {
        return led2DataStorage;
    }
    static Map<InjectorTest, Map<Integer, Double>> getLed3DataStorage() {
        return led3DataStorage;
    }
    static Map<InjectorTest, Map<Integer, Double>> getLed4DataStorage() {
        return led4DataStorage;
    }

    public static void initialize(List<Integer> activeLEDs, List<InjectorTest> codingTests) {

        activeLEDs.forEach(activeLed -> {

            switch (activeLed) {
                case 1:
                    led1DataStorage = new LinkedHashMap<>();
                    codingTests.forEach(codingTest -> led1DataStorage.put(codingTest, new LinkedHashMap<>()));
                    break;
                case 2:
                    led2DataStorage = new LinkedHashMap<>();
                    codingTests.forEach(codingTest -> led2DataStorage.put(codingTest, new LinkedHashMap<>()));
                    break;
                case 3:
                    led3DataStorage = new LinkedHashMap<>();
                    codingTests.forEach(codingTest -> led3DataStorage.put(codingTest, new LinkedHashMap<>()));
                    break;
                case 4:
                    led4DataStorage = new LinkedHashMap<>();
                    codingTests.forEach(codingTest -> led4DataStorage.put(codingTest, new LinkedHashMap<>()));
                    break;
            }
        });
    }

    public static void store(int width, FlowResult flowTestResult, List<Integer> activeLEDs) {

        InjectorTest injectorTest = flowTestResult.getInjectorTest();

        activeLEDs.forEach(activeLed -> {

            switch (activeLed) {
                case 1:
                    Optional.ofNullable(led1DataStorage).ifPresent(data -> data.get(injectorTest).put(width, flowTestResult.getDoubleValue_1()));
                    break;
                case 2:
                    Optional.ofNullable(led2DataStorage).ifPresent(data -> data.get(injectorTest).put(width, flowTestResult.getDoubleValue_2()));
                    break;
                case 3:
                    Optional.ofNullable(led3DataStorage).ifPresent(data -> data.get(injectorTest).put(width, flowTestResult.getDoubleValue_3()));
                    break;
                case 4:
                    Optional.ofNullable(led4DataStorage).ifPresent(data -> data.get(injectorTest).put(width, flowTestResult.getDoubleValue_4()));
                    break;
            }
        });
    }

    public static void clean() {

        led1DataStorage = null;
        led2DataStorage = null;
        led3DataStorage = null;
        led4DataStorage = null;
    }
}
