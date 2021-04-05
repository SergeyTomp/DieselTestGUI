package fi.stardex.sisu.coding.denso;

import fi.stardex.sisu.coding.Coder;
import fi.stardex.sisu.model.cr.CoilOnePulseParametersModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.util.enums.Measurement;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DensoCoder implements Coder {

    private Logger logger = LoggerFactory.getLogger(DensoCoder.class);

    private Injector injector;
    private ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults;
    private List<Integer> activeLEDs;
    private List<InjectorTest> codingTests;
    private CoilOnePulseParametersModel coilOnePulseParametersModel;

    private final List<Integer> DELTAS = new ArrayList<>();
    private final Random RANDOM = new Random();

    private Map<InjectorTest, Map<Integer, Double>> led1DataStorage;
    private Map<InjectorTest, Map<Integer, Double>> led2DataStorage;
    private Map<InjectorTest, Map<Integer, Double>> led3DataStorage;
    private Map<InjectorTest, Map<Integer, Double>> led4DataStorage;

    public DensoCoder(MainSectionModel mainSectionModel,
                      List<Integer> activeLEDs,
                      FlowReportModel flowReportModel,
                      CoilOnePulseParametersModel coilOnePulseParametersModel) {
        this.injector = mainSectionModel.injectorProperty().get();
        this.activeLEDs = activeLEDs;
        this.mapOfFlowTestResults = flowReportModel.getResultObservableMap();
        this.codingTests = mainSectionModel.getInjectorTests().stream()
                .filter(injectorTest -> injectorTest.getTestName().getMeasurement() != Measurement.VISUAL)
                .collect(Collectors.toList());
        this.coilOnePulseParametersModel = coilOnePulseParametersModel;
        initialize();
        flowReportModel.resultMapChangedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!flowReportModel.isDensoResultRecovery()) {
                store(mapOfFlowTestResults.get(mainSectionModel.injectorTestProperty().get()));
            }
        });
    }

    private void initialize() {

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

    private void store(FlowReportModel.FlowResult flowTestResult) {

        int width = coilOnePulseParametersModel.widthProperty().get();
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

    @Override
    public List<String> buildCode() {

        List<String> resultList = new LinkedList<>();
        String calibrationId = injector.getCalibrationId();
        String initialString = makeTwoChars(calibrationId);

        logger.error("1. Two chars: {}", initialString);
        StringBuilder resultString = new StringBuilder();

        for (int i = 1; i < 5; i++) {

            List<Integer> deltasList = getDeltasForBeaker(i);
            logger.error("9. deltasList: {}", deltasList);

            if (deltasList.isEmpty()){ resultList.add(""); }
            else {
                int checkSum = Integer.parseInt(calibrationId, 16);
                logger.error("10. checkSum: {}", checkSum);

                resultString.setLength(0);
                resultString.append(initialString);
                for (int j = deltasList.size() - 1; j >= 0; j--){
                    resultString.append(intToHexString(deltasList.get(j)));
                }
                logger.error("11. resultString: {}", resultString);

                for (int j = resultString.length(); j < injector.getCoefficient() - 2; j++){ resultString.append("0"); }
                logger.error("12. resultString: {}", resultString);

                for (Integer number : deltasList){ checkSum ^= number; }
                resultString.append(intToHexString(checkSum));
                logger.error("13. resultString: {}", resultString);

                if (injector.getCodetype() == 4){ resultString.delete(0, 2); }
                logger.error("14. resultString: {}", resultString);
                resultList.add(resultString.toString());
            }
        }
        return resultList;
    }

    private String makeTwoChars(String calibration_ID) {
        return calibration_ID.length() == 1 ? "0" + calibration_ID : calibration_ID;
    }

    private String intToHexString(Integer number) {
        String hex = Integer.toHexString(number);
        if (hex.length() == 1)
            hex = "0" + hex;
        return hex.substring(hex.length() - 2).toUpperCase();
    }

    private List<Integer> getDeltasForBeaker(int beakerNumber) {

        DELTAS.clear();
        switch (beakerNumber) {
            case 1:
                Optional.ofNullable(DensoCodingDataStorage.getLed1DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
            case 2:
                Optional.ofNullable(DensoCodingDataStorage.getLed2DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
            case 3:
                Optional.ofNullable(DensoCodingDataStorage.getLed3DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
            case 4:
                Optional.ofNullable(DensoCodingDataStorage.getLed4DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
        }
        return DELTAS;
    }

    private int getCodingPoint(Map.Entry<InjectorTest, Map<Integer, Double>> entry) {

        Integer width1 = null, width2 = null;
        Double flow1 = null, flow2 = null;
        InjectorTest injectorTest = entry.getKey();
        Double nominalFlow = injectorTest.getNominalFlow();
        Integer totalPulseTime = injectorTest.getTotalPulseTime();

        logger.error("2.nominal flow: {}", nominalFlow);
        logger.error("3. totalPulseTime: {}", totalPulseTime);

        Iterator<Map.Entry<Integer, Double>> valuesMapIterator = entry.getValue().entrySet().iterator();
        Map.Entry<Integer, Double> initialValues = valuesMapIterator.hasNext() ? valuesMapIterator.next() : null;

        if (initialValues == null || !valuesMapIterator.hasNext()) {
            logger.error("Return");
            return RANDOM.nextInt(15) - 7;
        }

        Integer widthInitial = initialValues.getKey();
        Double flowInitial = initialValues.getValue();
        int minWidth = widthInitial;
        boolean overFlow = false;
        logger.error("4. widthInitial: {}", widthInitial);
        logger.error("5. flowInitial: {}", flowInitial);

        while (valuesMapIterator.hasNext()) {

            Map.Entry<Integer, Double> nextValues = valuesMapIterator.next();
            Integer widthToCompare = nextValues.getKey();
            Double flowToCompare = nextValues.getValue();
            logger.error("5. widthToCompare: {}", widthToCompare);
            logger.error("6. flowToCompare: {}", flowToCompare);

            if (flowInitial <= nominalFlow && flowToCompare >= nominalFlow) {
                logger.error("If statement");
                width1 = widthInitial;
                width2 = widthToCompare;
                flow1 = flowInitial;
                flow2 = flowToCompare;
            }
            widthInitial = widthToCompare;
            flowInitial = flowToCompare;

            if (flowToCompare > nominalFlow) { overFlow = true; }
        }

        int codingPoint;
        if (width1 == null || width2 == null) {
            codingPoint = totalPulseTime - minWidth;
            if (overFlow) {
                codingPoint = - codingPoint;
            }
        }else {
            codingPoint = (int) (width1 + (nominalFlow - flow1) * (width2 - width1) / (flow2 - flow1)) - totalPulseTime;
        }

        logger.error("7. codingPoint: {}", codingPoint);
        int range = 60;
        codingPoint = codingPoint > range ? range : codingPoint;
        codingPoint = codingPoint < -range ? -range : codingPoint;
        logger.error("8. codingPoint: {}", codingPoint);
        return codingPoint;
    }
}
