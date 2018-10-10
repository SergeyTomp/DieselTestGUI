package fi.stardex.sisu.coding.other;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CodingDataHandler {

    private static Logger logger = LoggerFactory.getLogger(DensoCoding.class);

    private static final List<Integer> DELTAS = new ArrayList<>();

    private static final Random RANDOM = new Random();

    public static List<Integer> getDeltasForBeaker(int beakerNumber) {

        DELTAS.clear();

        switch (beakerNumber) {
            case 1:
                Optional.ofNullable(CodingDataStorage.getLed1DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
            case 2:
                Optional.ofNullable(CodingDataStorage.getLed2DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
            case 3:
                Optional.ofNullable(CodingDataStorage.getLed3DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
            case 4:
                Optional.ofNullable(CodingDataStorage.getLed4DataStorage()).ifPresent(data -> data.entrySet().forEach(entry -> DELTAS.add(getCodingPoint(entry))));
                break;
        }

        return DELTAS;

    }

    public static int getCodingPoint(Map.Entry<InjectorTest, Map<Integer, Double>> entry) {

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

        logger.error("4. widthInitial: {}", widthInitial);

        logger.error("5. flowInitial: {}", flowInitial);

        while (valuesMapIterator.hasNext()) {

            Map.Entry<Integer, Double> nextValues = valuesMapIterator.next();

            Integer widthToCompare = nextValues.getKey();

            Double flowToCompare = nextValues.getValue();

            logger.error("5. widthToCompare: {}", widthToCompare);

            logger.error("6. flowToCompare: {}", flowToCompare);

            if (flowInitial < nominalFlow && flowToCompare > nominalFlow) {
                logger.error("If statement");
                width1 = widthInitial;
                width2 = widthToCompare;
                flow1 = flowInitial;
                flow2 = flowToCompare;
            }
            widthInitial = widthToCompare;
            flowInitial = flowToCompare;

        }

        if (width1 == null || width2 == null) {
            logger.error("Return");
            return RANDOM.nextInt(15) - 7;
        }

        int codingPoint = (int) (width1 + (nominalFlow - flow1) * (width2 - width1) / (flow2 - flow1)) - totalPulseTime;

        logger.error("7. codingPoint: {}", codingPoint);

        int range = 60;

        codingPoint = codingPoint > range ? range : codingPoint;

        codingPoint = codingPoint < -range ? -range : codingPoint;

        logger.error("8. codingPoint: {}", codingPoint);

        return codingPoint;

    }

}
