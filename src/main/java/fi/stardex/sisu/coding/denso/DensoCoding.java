package fi.stardex.sisu.coding.denso;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class DensoCoding {

    private static Logger logger = LoggerFactory.getLogger(DensoCoding.class);

    public static List<String> calculate() {

        List<String> resultList = new LinkedList<>();

        Injector injector = CurrentInjectorObtainer.getInjector();

        String calibrationId = injector.getCalibrationId();

        String initialString = makeTwoChars(calibrationId);

        logger.error("1. Two chars: {}", initialString);

        StringBuilder resultString = new StringBuilder();

        for (int i = 1; i < 5; i++) {

            List<Integer> deltasList = DensoCodingDataHandler.getDeltasForBeaker(i);

            logger.error("9. deltasList: {}", deltasList);

            if (deltasList.isEmpty())
                resultList.add("");
            else {

                int checkSum = Integer.parseInt(calibrationId, 16);

                logger.error("10. checkSum: {}", checkSum);

                resultString.setLength(0);

                resultString.append(initialString);

                for (int j = deltasList.size() - 1; j >= 0; j--)
                    resultString.append(intToHexString(deltasList.get(j)));

                logger.error("11. resultString: {}", resultString);

                for (int j = resultString.length(); j < injector.getCoefficient() - 2; j++)
                    resultString.append("0");

                logger.error("12. resultString: {}", resultString);

                for (Integer number : deltasList)
                    checkSum ^= number;

                resultString.append(intToHexString(checkSum));

                logger.error("13. resultString: {}", resultString);

                if (injector.getCodetype() == 4)
                    resultString.delete(0, 2);

                logger.error("14. resultString: {}", resultString);

                resultList.add(resultString.toString());

            }

        }

        return resultList;

    }

    private static String makeTwoChars(String calibration_ID) {

        return calibration_ID.length() == 1 ? "0" + calibration_ID : calibration_ID;

    }

    private static String intToHexString(Integer number) {

        String hex = Integer.toHexString(number);

        if (hex.length() == 1)
            hex = "0" + hex;

        return hex.substring(hex.length() - 2).toUpperCase();

    }

}
