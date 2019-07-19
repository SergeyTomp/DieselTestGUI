package fi.stardex.sisu.coding.delphi.c3i;

import fi.stardex.sisu.model.cr.FlowReportModel.FlowResult;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

import java.util.List;
import java.util.Optional;

import static fi.stardex.sisu.coding.delphi.c3i.DelphiC3ICodingDataHandler.storeCoefficient;

public class DelphiC3ICodingDataStorage {

    private static int[] led1DataStorage;

    private static int[] led2DataStorage;

    private static int[] led3DataStorage;

    private static int[] led4DataStorage;

    static int[] getLed1DataStorage() {
        return led1DataStorage;
    }

    static int[] getLed2DataStorage() {
        return led2DataStorage;
    }

    static int[] getLed3DataStorage() {
        return led3DataStorage;
    }

    static int[] getLed4DataStorage() {
        return led4DataStorage;
    }

    public static void initialize(List<Integer> activeLEDs) {

        activeLEDs.forEach(activeLed -> {

            switch (activeLed) {
                case 1:
                    led1DataStorage = new int[15];
                    fillWithDefaultValues(led1DataStorage);
                    break;
                case 2:
                    led2DataStorage = new int[15];
                    fillWithDefaultValues(led2DataStorage);
                    break;
                case 3:
                    led3DataStorage = new int[15];
                    fillWithDefaultValues(led3DataStorage);
                    break;
                case 4:
                    led4DataStorage = new int[15];
                    fillWithDefaultValues(led4DataStorage);
                    break;

            }

        });

    }

    private static void fillWithDefaultValues(int[] storage) {

        storage[2] = 63;
        storage[3] = 17;
        storage[4] = 15;
        storage[5] = 19;

    }

    public static void store(FlowResult flowTestResult) {

        InjectorTest injectorTest = flowTestResult.getInjectorTest();

        Optional.ofNullable(led1DataStorage).ifPresent(data -> storeCoefficient(injectorTest, flowTestResult.getFlow1_double(), data));
        Optional.ofNullable(led2DataStorage).ifPresent(data -> storeCoefficient(injectorTest, flowTestResult.getFlow2_double(), data));
        Optional.ofNullable(led3DataStorage).ifPresent(data -> storeCoefficient(injectorTest, flowTestResult.getFlow3_double(), data));
        Optional.ofNullable(led4DataStorage).ifPresent(data -> storeCoefficient(injectorTest, flowTestResult.getFlow4_double(), data));

    }

    public static void clean() {

        led1DataStorage = null;
        led2DataStorage = null;
        led3DataStorage = null;
        led4DataStorage = null;

    }

}
