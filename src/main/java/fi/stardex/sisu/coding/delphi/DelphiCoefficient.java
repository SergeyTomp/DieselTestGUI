package fi.stardex.sisu.coding.delphi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelphiCoefficient {

    private static Logger logger = LoggerFactory.getLogger(DelphiCoefficient.class);

    public static int getDelphiC2ICoefficient(String testName) {

        switch (testName) {

            case "Test Point 01":
            case "Test Point 03":
            case "Test Point 05":
            case "Test Point 07":
                return 5;
            case "Test Point 02":
            case "Test Point 04":
                return 8;
            case "Test Point 06":
            case "Test Point 08":
                return 7;
            default:
                logger.error("Wrong test for Delphi C2I Coefficient!");
                return 0;

        }

    }

    public static int getDelphiC3ICoefficient(String testName) {

        return 0;

    }

}
