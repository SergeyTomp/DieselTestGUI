package fi.stardex.sisu.coding.other;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.store.FlowReport;
import javafx.collections.ObservableList;

import java.util.*;

public class CodingDataStorage {

    private static Map<InjectorTest, Map<Integer, Double>> led1DataStorage;

    private static Map<InjectorTest, Map<Integer, Double>> led2DataStorage;

    private static Map<InjectorTest, Map<Integer, Double>> led3DataStorage;

    private static Map<InjectorTest, Map<Integer, Double>> led4DataStorage;

    public static Map<InjectorTest, Map<Integer, Double>> getLed1DataStorage() {
        return led1DataStorage;
    }

    public static Map<InjectorTest, Map<Integer, Double>> getLed2DataStorage() {
        return led2DataStorage;
    }

    public static Map<InjectorTest, Map<Integer, Double>> getLed3DataStorage() {
        return led3DataStorage;
    }

    public static Map<InjectorTest, Map<Integer, Double>> getLed4DataStorage() {
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

    public static void store(int width, FlowReport.FlowTestResult flowTestResult) {

        InjectorTest injectorTest = flowTestResult.getInjectorTest();

        Optional.ofNullable(led1DataStorage).ifPresent(led1DataStorage -> led1DataStorage.get(injectorTest).put(width, flowTestResult.getFlow1_double()));
        Optional.ofNullable(led2DataStorage).ifPresent(led2DataStorage -> led2DataStorage.get(injectorTest).put(width, flowTestResult.getFlow2_double()));
        Optional.ofNullable(led3DataStorage).ifPresent(led3DataStorage -> led3DataStorage.get(injectorTest).put(width, flowTestResult.getFlow3_double()));
        Optional.ofNullable(led4DataStorage).ifPresent(led4DataStorage -> led4DataStorage.get(injectorTest).put(width, flowTestResult.getFlow4_double()));

    }

    public static void clean() {

        led1DataStorage = null;
        led2DataStorage = null;
        led3DataStorage = null;
        led4DataStorage = null;

    }

}
